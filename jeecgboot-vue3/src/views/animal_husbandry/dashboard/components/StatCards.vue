
<template>
  <a-row :gutter="24">
    <a-col :span="6">
      <a-card :loading="loading" title="牲畜总数" :bordered="false" class="kpi-card">
        <div class="value">{{ kpiData.totalAnimals || 0 }} <span class="unit">头</span></div>
      </a-card>
    </a-col>
    <a-col :span="6">
      <a-card :loading="loading" title="设备在线率" :bordered="false" class="kpi-card">
        <div class="value">{{ deviceOnlineRate }} <span class="unit">%</span></div>
      </a-card>
    </a-col>
    <a-col :span="6">
      <a-card :loading="loading" title="健康状态分布" :bordered="false" class="kpi-card">
        <div class="value health-dist">
          <span class="healthy">{{ kpiData.healthyCount || 0 }}</span> / 
          <span class="sub-healthy">{{ kpiData.subHealthyCount || 0 }}</span> / 
          <span class="alarm">{{ kpiData.alarmCount || 0 }}</span>
        </div>
      </a-card>
    </a-col>
    <a-col :span="6">
      <a-card :loading="loading" title="今日新增告警" :bordered="false" class="kpi-card">
        <div class="value">{{ kpiData.newAlarmsToday || 0 }} <span class="unit">条</span></div>
      </a-card>
    </a-col>
  </a-row>
</template>

<script lang="ts" setup>
  import { computed, PropType } from 'vue';
  import { Row as ARow, Col as ACol, Card as ACard } from 'ant-design-vue';
  import { DashboardKpiVo } from '../dashboard.model';

  const props = defineProps({
    loading: {
      type: Boolean,
      default: true,
    },
    kpiData: {
      type: Object as PropType<Partial<DashboardKpiVo>>,
      default: () => ({}),
    },
  });

  const deviceOnlineRate = computed(() => {
    if (!props.kpiData.totalDevices || props.kpiData.totalDevices === 0) {
      return '100.0'; // 如果总数为0，则认为在线率为100%
    }
    const rate = (props.kpiData.onlineDevices / props.kpiData.totalDevices) * 100;
    return rate.toFixed(1); // 保留一位小数
  });
</script>

<style lang="less" scoped>
.kpi-card {
  .value {
    font-size: 30px;
    font-weight: bold;
    line-height: 1;
  }
  .unit {
    font-size: 16px;
    font-weight: normal;
    margin-left: 8px;
  }
  .health-dist {
    .healthy { color: #52c41a; }
    .sub-healthy { color: #faad14; }
    .alarm { color: #f5222d; }
  }
}
</style> 