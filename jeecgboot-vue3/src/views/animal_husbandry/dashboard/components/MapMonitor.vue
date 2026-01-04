
<template>
  <div ref="wrapRef" :style="{ height, width }"></div>
</template>
<script lang="ts" setup>
  import { defineComponent, ref, nextTick, unref, onMounted, watch, PropType } from 'vue';
  import { useScript } from '/@/hooks/web/useScript';
  import { MapAnimalDataVo, FenceVo } from '../dashboard.model';
  import { useMessage } from '/@/hooks/web/useMessage';

  // --- 高德地图JS API配置 ---
  // 提示: 请将Key替换为您自己申请的高德Web JS API的Key
  const A_MAP_URL = 'https://webapi.amap.com/maps?v=2.0&key=06313eb9c6563b674a8fd789db0692c3';

  const props = defineProps({
    width: {
      type: String,
      default: '100%',
    },
    height: {
      type: String,
      default: '600px', // 根据原型调整默认高度
    },
    mapData: {
      type: Array as PropType<MapAnimalDataVo[]>,
      default: () => [],
    },
    fenceData: {
      type: Array as PropType<FenceVo[]>,
      default: () => [],
    },
  });

  const emit = defineEmits(['marker-click']);

  const wrapRef = ref<HTMLDivElement | null>(null);
  const { createMessage } = useMessage();
  const { toPromise } = useScript({ src: A_MAP_URL });

  let mapInstance: any = null; // 地图实例
  let markers: any[] = []; // 地图上的标记点实例
  let polygons: any[] = []; // [新增] 地图上的围栏多边形实例
  let infoWindow: any = null; // 信息窗体实例

  // --- SVG 图标 ---
  // 一个可以直接在代码中使用的小牛SVG图标
  const cowIconSvg = `
    <svg t="1667825946957" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2554" width="28" height="28">
      <path d="M784.914286 597.165714q24.685714 0 42.057143 17.371429t17.371428 42.057143-17.371428 42.057143-42.057143 17.371428h-530.102857q-24.685714 0-42.057143-17.371428t-17.371428-42.057143 17.371428-42.057143 42.057143-17.371429h18.857143v-102.4q0-101.44 71.634286-173.074286t173.074286-71.634286 173.074286 71.634286 71.634286 173.074286v102.4h18.857143zM512 309.44q-81.874286 0-140.022857 58.148571t-58.148571 140.022857v83.565714h198.171428v-83.565714q0-81.874286-58.148571-140.022857t-140.022857-58.148571zM890.194286 354.422857q0-22.125714-15.908571-38.034286t-38.034286-15.908571-38.034286 15.908571-15.908571 38.034286 15.908571 38.034286 38.034286 15.908571 38.034286-15.908571 15.908571-38.034286zM133.805714 354.422857q0-22.125714 15.908572-38.034286t38.034286-15.908571 38.034286 15.908571 15.908571 38.034286-15.908571 38.034286-38.034286 15.908571-38.034286-15.908571-15.908572-38.034286z" p-id="2555" fill="#333333"></path>
    </svg>
  `;

  // --- 地图标记点样式 ---
  const getMarkerIcon = (status: string) => {
    let color = '#52c41a'; // 默认绿色-健康
    if (status === 'SUB_HEALTHY') {
      color = '#faad14'; // 黄色-亚健康
    } else if (status === 'ALARM') {
      color = '#f5222d'; // 红色-告警
    }

    // [关键修复] 创建一个真实的DOM元素作为图标内容，而不是HTML字符串，以提高渲染的稳定性
    const markerContainer = document.createElement('div');
    markerContainer.style.display = 'flex';
    markerContainer.style.alignItems = 'center';

    const iconDiv = document.createElement('div');
    iconDiv.style.width = '16px';
    iconDiv.style.height = '16px';
    iconDiv.style.borderRadius = '50%';
    iconDiv.style.backgroundColor = color;
    iconDiv.style.border = '2px solid white';
    iconDiv.style.boxShadow = '0 2px 4px rgba(0,0,0,0.4)';

    const cowDiv = document.createElement('div');
    cowDiv.innerHTML = cowIconSvg;
    cowDiv.style.marginLeft = '-8px'; // 让牛图标和圆圈有部分重叠
    cowDiv.style.filter = 'drop-shadow(0px 2px 2px rgba(0,0,0,0.3))'; // 给牛也加上阴影

    markerContainer.appendChild(cowDiv);
    markerContainer.appendChild(iconDiv);

    return markerContainer;
  };

  // --- 地图核心逻辑 ---
  async function initMap() {
    try {
      await toPromise();
      await nextTick();
      const wrapEl = unref(wrapRef);
      if (!wrapEl) return;

      const AMap = (window as any).AMap;
      mapInstance = new AMap.Map(wrapEl, {
        zoom: 13, // 调整缩放级别以获得更合适的初始视野
        center: [112.85126, 28.246939], // 更新为新坐标点的中心区域
        viewMode: '2D',
        // [修复] 移除 grayscale 风格，恢复地图默认色彩
        // mapStyle: 'amap://styles/whitesmoke',
      });
      mapInstance.on('complete', () => {
        createMessage.success('地图加载完成');
        // [新增] 初始化信息窗体
        infoWindow = new AMap.InfoWindow({
          autoMove: true,
          closeWhenClickMap: true,
          offset: new AMap.Pixel(0, -25),
        });
        updateMarkers(props.mapData); // 初始加载标记
        drawFences(props.fenceData); // [新增] 初始加载围栏
      });
    } catch (e) {
      createMessage.error('地图加载失败，请检查网络或API Key配置');
      console.error(e);
    }
  }

  // --- 更新地图标记 ---
  function updateMarkers(data: MapAnimalDataVo[]) {
    if (!mapInstance) return;

    // 1. 清除旧的标记
    mapInstance.remove(markers);
    markers = [];

    if (!data || data.length === 0) {
      return;
    }

    const AMap = (window as any).AMap;

    // 2. 添加新的标记
    data.forEach((item) => {
      if (item.lon && item.lat) {
        const marker = new AMap.Marker({
          position: new AMap.LngLat(item.lon, item.lat),
          content: getMarkerIcon(item.healthStatus),
          offset: new AMap.Pixel(-14, -34), // 根据新的组合图标大小调整偏移量
          extData: {
            animalId: item.animalId,
            earTagId: item.earTagId, // 将耳标号和状态也存入
            healthStatus: item.healthStatus,
          },
        });

        // 原始的点击事件保持不变
        marker.on('click', (e) => {
          emit('marker-click', e.target.getExtData().animalId);
        });

        // [新增] 鼠标滑过显示信息窗体
        marker.on('mouseover', (e) => {
          const animalInfo = e.target.getExtData();
          const healthMap = { HEALTHY: '健康', SUB_HEALTHY: '亚健康', ALARM: '告警' };
          const healthText = healthMap[animalInfo.healthStatus] || '未知';

          const title = `<div style="font-weight: bold; font-size: 14px;">${animalInfo.earTagId}</div>`;
          const content = `<div>健康状态: ${healthText}</div>`;

          infoWindow.setContent(`${title}${content}`);
          infoWindow.open(mapInstance, e.target.getPosition());
        });

        // [新增] 鼠标移出关闭信息窗体
        marker.on('mouseout', () => {
          infoWindow.close();
        });

        markers.push(marker);
      }
    });

    // 3. 将新标记批量添加到地图
    mapInstance.add(markers);

    // 4. (可选) 自动调整地图视野以包含所有点
    // 备注：为了保证地图始终以牧场为中心，我们注释掉此功能。
    // 如果需要地图根据数据点自动聚焦，取消下一行的注释即可。
    // mapInstance.setFitView();
  }

  // --- [新增] 绘制电子围栏 ---
  function drawFences(data: FenceVo[]) {
    if (!mapInstance) return;

    // 1. 清除旧的多边形
    mapInstance.remove(polygons);
    polygons = [];

    if (!data || data.length === 0) {
      return;
    }

    const AMap = (window as any).AMap;

    // 2. 添加新的多边形
    data.forEach((item) => {
      try {
        const path = JSON.parse(item.points);
        if (path && path.length > 0) {
          const polygon = new AMap.Polygon({
            path: path,
            strokeColor: '#FF33FF', // 描边颜色
            strokeWeight: 6, // 线条宽度
            strokeOpacity: 0.6, // 描边透明度
            fillOpacity: 0.2, // 填充透明度
            fillColor: '#1791fc', // 填充颜色
            zIndex: 40, // 层级，比Marker低
          });
          // [新增] 可以为围栏也添加一些交互，例如显示名称
          polygon.on('mouseover', () => {
            infoWindow.setContent(`<div style="font-weight: bold; font-size: 14px;">围栏: ${item.name}</div>`);
            infoWindow.open(mapInstance, polygon.getBounds().getCenter());
          });
          polygon.on('mouseout', () => {
            infoWindow.close();
          });

          polygons.push(polygon);
        }
      } catch (e) {
        console.error(`解析围栏[${item.name}]坐标失败`, e);
      }
    });

    // 3. 将新多边形批量添加到地图
    mapInstance.add(polygons);
  }


  onMounted(() => {
    initMap();
  });

  // --- 监听数据变化，动态更新地图 ---
  watch(
    () => props.mapData,
    (newData) => {
      updateMarkers(newData);
    },
    { deep: true }
  );

  // [新增] 监听围栏数据变化，动态更新地图
  watch(
    () => props.fenceData,
    (newData) => {
      drawFences(newData);
    },
    { deep: true }
  );
</script> 