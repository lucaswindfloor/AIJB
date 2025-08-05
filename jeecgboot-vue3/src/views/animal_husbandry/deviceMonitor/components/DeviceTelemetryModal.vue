<template>
  <BasicModal v-bind="$attrs" @register="registerModal" title="设备运行时详情" :width="1000">
    <div class="pt-3 pr-3px">
        <a-row>
            <a-col :span="24">
                <div ref="chartRef" :style="{ width: '100%', height: '300px' }"></div>
            </a-col>
        </a-row>
    </div>
  </BasicModal>
</template>
<script lang="ts" setup>
  import { ref, onMounted } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { useECharts } from '/@/hooks/web/useECharts';
  import { getTelemetryHistory } from '../deviceMonitor.api';

  const chartRef = ref<HTMLDivElement | null>(null);
  const { setOptions } = useECharts(chartRef);

  const [registerModal, { setModalProps }] = useModalInner(async (data) => {
      setModalProps({ confirmLoading: true });
      
      // TODO: 替换为真实的数据获取逻辑
      // 模拟获取历史数据
      const mockData = {
          battery: Array.from({ length: 24 }, (_, i) => ({ ts: Date.now() - (24 - i) * 3600 * 1000, value: 100 - i * 2 - Math.random() * 5 })),
          rssi: Array.from({ length: 24 }, (_, i) => ({ ts: Date.now() - (24 - i) * 3600 * 1000, value: -80 - Math.random() * 20 })),
          snr: Array.from({ length: 24 }, (_, i) => ({ ts: Date.now() - (24 - i) * 3600 * 1000, value: 10 - Math.random() * 15 })),
      }
      
      const series = Object.keys(mockData).map(key => {
          return {
              name: key.toUpperCase(),
              type: 'line',
              smooth: true,
              data: mockData[key].map(item => [new Date(item.ts), item.value.toFixed(2)]),
              yAxisIndex: key === 'battery' ? 0 : 1,
          }
      });

      setOptions({
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            data: series.map(s => s.name)
        },
        xAxis: {
            type: 'time',
        },
        yAxis: [
            {
                type: 'value',
                name: '电量 (%)',
                min: 0,
                max: 100,
            },
            {
                type: 'value',
                name: '信号 (RSSI/SNR)',
            }
        ],
        series: series,
      });

      setModalProps({ confirmLoading: false });
  });
</script>

<style scoped>
.chart-container {
    background-color: #f0f2f5;
    padding: 16px;
    border-radius: 4px;
}
</style> 