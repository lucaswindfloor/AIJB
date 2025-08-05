<template>
  <div class="p-4">
    <a-card :bordered="false">
      <template #title>
        <div class="flex items-center">
          <span class="text-lg">牧场电子地图监控</span>
          <a-tag color="blue" class="ml-4">V1.0 - 原型</a-tag>
        </div>
      </template>

      <template #extra>
        <div class="extra-wrapper">
          <div class="extra-item">
            <span>图例:</span>
            <a-space>
              <a-tag color="green">健康</a-tag>
              <a-tag color="orange">关注</a-tag>
              <a-tag color="red">告警</a-tag>
            </a-space>
          </div>
          <div class="extra-item">
            <a-input-search placeholder="输入牛只编号搜索..." style="width: 200px" @search="onSearch" />
          </div>
        </div>
      </template>

      <div ref="mapContainer" style="height: 65vh"></div>
    </a-card>

    <!-- 使用 a-modal 展示牛只详细信息 -->
    <a-modal v-model:visible="modalVisible" :title="`牛只详情 - 编号: ${selectedCattle?.id}`" :footer="null" width="600px">
      <div v-if="selectedCattle">
        <a-descriptions bordered :column="2">
          <a-descriptions-item label="品种">{{ selectedCattle.breed }}</a-descriptions-item>
          <a-descriptions-item label="月龄">{{ selectedCattle.age }}</a-descriptions-item>
          <a-descriptions-item label="当前位置" :span="2">
            {{ selectedCattle.position[0].toFixed(5) }}, {{ selectedCattle.position[1].toFixed(5) }}
          </a-descriptions-item>
        </a-descriptions>

        <a-divider orientation="left">AI 健康状态分析</a-divider>
        <div class="p-4 bg-gray-100 rounded">
          <p class="text-base">{{ selectedCattle.aiAnalysis }}</p>
        </div>

        <a-divider orientation="left">关键物联数据</a-divider>
        <a-list item-layout="horizontal" :data-source="getIotDataList(selectedCattle)">
          <template #renderItem="{ item }">
            <a-list-item>
              <a-list-item-meta>
                <template #title>{{ item.label }}</template>
                <template #description>{{ item.desc }}</template>
              </a-list-item-meta>
              <div class="text-lg font-bold">{{ item.value }}</div>
            </a-list-item>
          </template>
        </a-list>
        <div class="text-right text-gray-500 mt-2">
            数据更新时间: {{ selectedCattle.iotData.lastUpdate }}
        </div>
      </div>
       <template #footer>
        <a-button key="back" @click="modalVisible = false">关闭</a-button>
        <a-button key="submit" type="primary" @click="handleViewDetails">查看完整历史</a-button>
      </template>
    </a-modal>
  </div>
</template>

<script setup lang="ts" name="AnimalHusbandryDashboard">
import { ref, onMounted, nextTick } from 'vue';
import {
  Card as ACard,
  Tag as ATag,
  InputSearch as AInputSearch,
  Space as ASpace,
  Modal as AModal,
  Descriptions as ADescriptions,
  DescriptionsItem as ADescriptionsItem,
  Divider as ADivider,
  List as AList,
  ListItem as AListItem,
  ListItemMeta as AListItemMeta,
  Button as AButton,
  message,
} from 'ant-design-vue';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';

// --- Mock Data (根据PRD文档) ---
const mockCattleData = [
  {
    id: 'N001',
    position: [49.215, 119.765],
    status: 'healthy',
    breed: '荷斯坦牛',
    age: 24,
    aiAnalysis: '健康状态良好，各项指标正常，活动量充足。',
    iotData: {
      temperature: { value: '38.5 °C', range: '38.0-39.5 °C' },
      heartRate: { value: '70 次/分', range: '60-80 次/分' },
      activity: { value: '11050 步', avg: '10000 步' },
      lastUpdate: '2025-06-13 11:20:00',
    },
  },
  {
    id: 'N002',
    position: [49.220, 119.770],
    status: 'warning',
    breed: '西门塔尔牛',
    age: 30,
    aiAnalysis: '体温略高于正常范围，但活动量正常，建议持续观察，可能是由于天气炎热导致。',
    iotData: {
      temperature: { value: '39.6 °C', range: '38.0-39.5 °C' },
      heartRate: { value: '75 次/分', range: '60-80 次/分' },
      activity: { value: '9500 步', avg: '10000 步' },
      lastUpdate: '2025-06-13 11:21:30',
    },
  },
    {
    id: 'N003',
    position: [49.210, 119.755],
    status: 'danger',
    breed: '荷斯坦牛',
    age: 18,
    aiAnalysis: '心率持续偏低，活动量严重不足，可能存在健康风险，建议立即安排兽医进行现场检查！',
    iotData: {
      temperature: { value: '38.2 °C', range: '38.0-39.5 °C' },
      heartRate: { value: '52 次/分', range: '60-80 次/分' },
      activity: { value: '2300 步', avg: '10000 步' },
      lastUpdate: '2025-06-13 11:19:45',
    },
  },
];

// --- Refs ---
const mapContainer = ref<HTMLElement | null>(null);
const modalVisible = ref(false);
const selectedCattle = ref<typeof mockCattleData[0] | null>(null);
let map: L.Map | null = null;
const markers: { [key: string]: L.Marker } = {};


// --- Leaflet Icon Fix & Custom Icons ---
// 修正Leaflet默认图标路径问题
import iconUrl from 'leaflet/dist/images/marker-icon.png';
import shadowUrl from 'leaflet/dist/images/marker-shadow.png';
L.Marker.prototype.options.icon = L.icon({ iconUrl, shadowUrl });

const getIcon = (status: 'healthy' | 'warning' | 'danger') => {
  const colorMap = {
    healthy: '#28a745',
    warning: '#ffc107',
    danger: '#dc3545',
  };
  // 使用一个更可靠的PNG图标源，解决图片不显示的问题
  const cowIconUrl = 'https://cdn-icons-png.flaticon.com/512/3503/3503834.png'; 

  const html = `
    <div style="position: relative; width: 32px; height: 32px; display: flex; align-items: center; justify-content: center;">
      <img src="${cowIconUrl}" style="width: 30px; height: 30px; filter: drop-shadow(2px 2px 2px rgba(0,0,0,0.5));">
      <div style="
        position: absolute;
        bottom: 0;
        right: 0;
        width: 12px;
        height: 12px;
        background-color: ${colorMap[status]};
        border-radius: 50%;
        border: 2px solid white;
        box-shadow: 0 0 3px rgba(0,0,0,0.6);
      "></div>
    </div>
  `;

  return L.divIcon({
    html: html,
    className: 'leaflet-custom-cow-icon',
    iconSize: [32, 32],
    iconAnchor: [16, 32], // Anchor at the bottom center of the icon
  });
};

// --- Map Initialization ---
onMounted(() => {
  nextTick(() => {
    if (mapContainer.value) {
      map = L.map(mapContainer.value).setView([49.215, 119.765], 14);
      
      // Use a satellite map layer for a more appropriate rural look
      L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
          attribution: 'Tiles &copy; Esri &mdash; Source: Esri, i-cubed, USDA, USGS, AEX, GeoEye, Getmapping, Aerogrid, IGN, IGP, UPR-EGP, and the GIS User Community'
      }).addTo(map);

      // 绘制电子围栏示例 (更新坐标)
      const bounds = L.latLngBounds([[49.200, 119.740], [49.230, 119.790]]);
      L.rectangle(bounds, { color: "#ff7800", weight: 2, fillOpacity: 0.1 }).addTo(map);
      map.fitBounds(bounds);

      renderCattle();
    }
  });
});

// --- Functions ---
const renderCattle = () => {
  if (!map) return;
  mockCattleData.forEach(cattle => {
    const marker = L.marker(cattle.position as L.LatLngExpression, { icon: getIcon(cattle.status) })
      .addTo(map)
      .on('click', () => {
        selectedCattle.value = cattle;
        modalVisible.value = true;
      });
    markers[cattle.id] = marker;
  });
};

const getIotDataList = (cattle) => {
    const iot = cattle.iotData;
    return [
        { label: '体温', value: iot.temperature.value, desc: `正常范围: ${iot.temperature.range}` },
        { label: '心率', value: iot.heartRate.value, desc: `正常范围: ${iot.heartRate.range}` },
        { label: '今日活动量', value: iot.activity.value, desc: `参考均值: ${iot.activity.avg}` },
    ];
}

const onSearch = (value: string) => {
  const cattleId = value.toUpperCase();
  const marker = markers[cattleId];
  if (marker && map) {
    map.setView(marker.getLatLng(), 18);
    marker.openPopup(); // 可以在此触发弹窗，或高亮显示
    // 模拟高亮
    const iconElement = marker.getElement();
    if (iconElement) {
        iconElement.style.transform = 'scale(1.5)';
        setTimeout(() => {
            iconElement.style.transform = 'scale(1)';
        }, 2000);
    }
    message.success(`已定位到牛只: ${cattleId}`);
  } else {
    message.warning(`未找到编号为 ${cattleId} 的牛只`);
  }
};

const handleViewDetails = () => {
    message.info('此功能将在后续版本中开放，用于查看完整历史数据追溯。');
    modalVisible.value = false;
}

</script>

<style scoped>
.extra-wrapper {
  display: flex;
  align-items: center;
  gap: 24px;
}

.extra-item {
  display: inline-block;
}

.extra-item > span {
    margin-right: 8px;
}

/* 覆盖 antd modal 的一些样式以获得更好的显示效果 */
:deep(.ant-modal-body) {
  padding: 16px 24px;
}
:deep(.ant-descriptions-item-label) {
  font-weight: 600;
}
.bg-gray-100 {
    background-color: #f7fafc;
}
.rounded {
    border-radius: 0.25rem;
}
</style> 