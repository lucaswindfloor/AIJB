<template>
  <div class="telemetry-chart">
    <div class="chart-header">
      <h4>{{ title }}</h4>
      <a-space>
        <a-select v-model:value="timeRange" @change="handleTimeRangeChange" style="width: 120px">
          <a-select-option value="1h">最近1小时</a-select-option>
          <a-select-option value="6h">最近6小时</a-select-option>
          <a-select-option value="24h">最近24小时</a-select-option>
          <a-select-option value="7d">最近7天</a-select-option>
        </a-select>
        <a-button @click="refreshData" :loading="loading" size="small">
          <template #icon><ReloadOutlined /></template>
          刷新
        </a-button>
      </a-space>
    </div>
    <div 
      ref="chartRef" 
      :style="{ width: '100%', height: chartHeight }"
      v-loading="loading"
    ></div>
    <div v-if="!loading && (!chartData || chartData.length === 0)" class="no-data">
      <a-empty description="暂无数据" />
    </div>
  </div>
</template>

<script lang="ts" setup>
  import { ref, onMounted, watch, nextTick, PropType } from 'vue';
  import { Select as ASelect, SelectOption as ASelectOption, Space as ASpace, Button as AButton, Empty as AEmpty } from 'ant-design-vue';
  import { ReloadOutlined } from '@ant-design/icons-vue';
  import { useECharts } from '/@/hooks/web/useECharts';
  import { getTimeSeriesTelemetry } from '../animal.api';
  import { useMessage } from '/@/hooks/web/useMessage';

  const props = defineProps({
    deviceId: {
      type: String,
      required: true,
    },
    telemetryKey: {
      type: String,
      required: true,
    },
    title: {
      type: String,
      required: true,
    },
    unit: {
      type: String,
      default: '',
    },
    chartHeight: {
      type: String,
      default: '300px',
    },
    yAxisConfig: {
      type: Object as PropType<any>,
      default: () => ({}),
    },
  });

  const { createMessage } = useMessage();
  const chartRef = ref<HTMLDivElement>();
  const { setOptions, getInstance } = useECharts(chartRef);
  
  const loading = ref(false);
  const timeRange = ref('24h');
  const chartData = ref<any[]>([]);

  // 时间范围映射
  const timeRangeMap = {
    '1h': 1 * 60 * 60 * 1000,
    '6h': 6 * 60 * 60 * 1000,
    '24h': 24 * 60 * 60 * 1000,
    '7d': 7 * 24 * 60 * 60 * 1000,
  };

  // 获取数据
  async function fetchData() {
    if (!props.deviceId || !props.telemetryKey) {
      return;
    }

    loading.value = true;
    try {
      const endTs = Date.now();
      const startTs = endTs - timeRangeMap[timeRange.value];
      
      const response = await getTimeSeriesTelemetry({
        deviceId: props.deviceId,
        key: props.telemetryKey,
        startTs,
        endTs,
      });

      chartData.value = response || [];
      renderChart();
    } catch (error) {
      console.error('获取时序数据失败:', error);
      createMessage.error('获取图表数据失败');
      chartData.value = [];
    } finally {
      loading.value = false;
    }
  }

  // 渲染图表
  function renderChart() {
    if (!chartData.value || chartData.value.length === 0) {
      return;
    }

    const option = {
      title: {
        show: false,
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'cross',
        },
        formatter: function (params: any) {
          const data = params[0];
          const time = new Date(data.value[0]).toLocaleString();
          const value = data.value[1];
          return `${time}<br/>${props.title}: ${value} ${props.unit}`;
        },
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true,
      },
      xAxis: {
        type: 'time',
        boundaryGap: false,
        axisLabel: {
          formatter: function (value: number) {
            const date = new Date(value);
            if (timeRange.value === '7d') {
              return date.toLocaleDateString();
            } else {
              return date.toLocaleTimeString();
            }
          },
        },
      },
      yAxis: {
        type: 'value',
        name: props.unit,
        ...props.yAxisConfig,
      },
      series: [
        {
          name: props.title,
          type: 'line',
          smooth: true,
          symbol: 'circle',
          symbolSize: 6,
          lineStyle: {
            width: 2,
          },
          areaStyle: {
            opacity: 0.1,
          },
          data: chartData.value,
        },
      ],
    };

    setOptions(option);
  }

  // 时间范围变化
  function handleTimeRangeChange() {
    fetchData();
  }

  // 刷新数据
  function refreshData() {
    fetchData();
  }

  // 监听设备ID变化
  watch(() => props.deviceId, () => {
    if (props.deviceId) {
      fetchData();
    }
  });

  onMounted(() => {
    nextTick(() => {
      if (props.deviceId) {
        fetchData();
      }
    });
  });
</script>

<style scoped>
  .telemetry-chart {
    border: 1px solid #e8e8e8;
    border-radius: 4px;
    overflow: hidden;
  }
  
  .chart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    background-color: #fafafa;
    border-bottom: 1px solid #e8e8e8;
  }
  
  .chart-header h4 {
    margin: 0;
    font-size: 14px;
    font-weight: 500;
  }
  
  .no-data {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 200px;
  }
</style> 