
<template>
  <div class="p-4">
    <!-- 1. 顶部KPI统计卡片 -->
    <StatCards :loading="loading" :kpi-data="kpiData" />

    <!-- 2. 主体区域：地图与告警列表 -->
    <a-row :gutter="24">
      <!-- 左侧地图监控 -->
      <a-col :xl="18" :lg="24" :md="24" :sm="24" :xs="24" style="margin-bottom: 24px">
        <MapMonitor
          :loading="loading"
          :map-data="mapData"
          :fence-data="fenceData"
          @marker-click="handleMarkerClick"
          height="620px"
        />
      </a-col>
      <!-- 右侧告警区 -->
      <a-col :xl="6" :lg="24" :md="24" :sm="24" :xs="24">
        <RealtimeAlarmList
          title="越界告警"
          :loading="loading"
          :alarm-data="outOfBoundsAlarmData"
          @item-click="handleAlarmItemClick"
          @more-click="handleMoreClick"
          class="mb-4"
        />
        <RealtimeAlarmList
          title="健康告警"
          :loading="loading"
          :alarm-data="alarmData"
          @item-click="handleAlarmItemClick"
          @more-click="handleMoreClick"
        />
      </a-col>
    </a-row>
    
    <AlarmListModal @register="registerAlarmModal" />
  </div>
</template>

<script lang="ts" setup name="AnimalHusbandryDashboard">
  import { ref, onMounted, onUnmounted, reactive } from 'vue';
  import { Row as ARow, Col as ACol } from 'ant-design-vue';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useModal } from '/@/components/Modal';

  import StatCards from './components/StatCards.vue';
  import MapMonitor from './components/MapMonitor.vue';
  import RealtimeAlarmList from './components/RealtimeAlarmList.vue';
  import AlarmListModal from './components/AlarmListModal.vue';

  import { getKpiData, getMapData, getRecentAlarms, getFences, getOutOfBoundsAlarms, getAllHealthAlarms } from './dashboard.api';
  import { DashboardKpiVo, MapAnimalDataVo, RecentAlarmVo, FenceVo } from './dashboard.model';

  // 组件命名，与后端菜单路径对应，用于权限
  defineOptions({ name: 'animal_husbandry/dashboard' });

  const { createMessage } = useMessage();

  const loading = ref(true);
  let timer: any = null;

  // --- 响应式数据 ---
  const kpiData = reactive<Partial<DashboardKpiVo>>({});
  const mapData = ref<MapAnimalDataVo[]>([]);
  const alarmData = ref<RecentAlarmVo[]>([]);
  const fenceData = ref<FenceVo[]>([]);
  const outOfBoundsAlarmData = ref<RecentAlarmVo[]>([]);

  // --- API 数据获取 ---
  async function fetchData() {
    try {
      loading.value = true;
      const [kpiRes, mapRes, alarmRes, fenceRes, outOfBoundsRes] = await Promise.all([
        getKpiData(),
        getMapData(),
        getRecentAlarms(),
        getFences(),
        getOutOfBoundsAlarms(),
      ]);

      Object.assign(kpiData, kpiRes);
      mapData.value = mapRes;
      alarmData.value = alarmRes;
      fenceData.value = fenceRes;
      outOfBoundsAlarmData.value = outOfBoundsRes;
    } catch (error) {
      console.error('获取驾驶舱数据失败', error);
      createMessage.error('数据加载失败，请刷新页面重试');
    } finally {
      loading.value = false;
    }
  }

  // --- 轮询刷新 ---
  // 根据设计文档，地图和告警数据需要定时刷新
  function startPolling() {
    fetchData(); // 立即执行一次
    timer = setInterval(async () => {
      // 轮询时只刷新地图和告警，KPI通常不需要高频刷新
      try {
        const [mapRes, alarmRes, fenceRes, outOfBoundsRes] = await Promise.all([getMapData(), getRecentAlarms(), getFences(), getOutOfBoundsAlarms()]);
        mapData.value = mapRes;
        alarmData.value = alarmRes;
        fenceData.value = fenceRes;
        outOfBoundsAlarmData.value = outOfBoundsRes;
      } catch (error) {
        console.error('轮询数据失败', error);
      }
    }, 15000); // 15秒刷新一次
  }

  // --- 事件处理 ---
  function handleMarkerClick(animalId: string) {
    console.log('点击了地图上的牲畜:', animalId);
    // V1.1 可在此处调用 getAnimalDetailsForMapTip 接口，并打开Modal显示详情
    createMessage.info(`点击了牲畜: ${animalId}`);
  }

  function handleAlarmItemClick(animalId: string) {
    console.log('点击了告警列表中的牲畜:', animalId);
    // V1.1 可在此处实现地图联动，高亮显示对应的Marker
    createMessage.info(`告警牲畜: ${animalId}，将在地图上高亮`);
  }
  
  const [registerAlarmModal, { openModal: openAlarmModal }] = useModal();

  async function handleMoreClick(type: string) {
    let records = [];
    if (type === '越界告警') {
      records = outOfBoundsAlarmData.value;
      openAlarmModal(true, { title: type, records });
    } else if (type === '健康告警') {
      records = await getAllHealthAlarms();
      openAlarmModal(true, { title: type, records });
    }
  }

  // --- 生命周期钩子 ---
  onMounted(() => {
    startPolling();
  });

  onUnmounted(() => {
    if (timer) {
      clearInterval(timer);
    }
  });
</script>

<style lang="less" scoped>
.mb-4 {
  margin-bottom: 1rem;
}
</style> 