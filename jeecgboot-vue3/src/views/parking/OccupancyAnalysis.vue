<template>
  <div class="p-4">
    <a-card :bordered="false">
      
      <template #extra>
        <div class="extra-wrapper">
          <div class="extra-item">
            <span>停车场:</span>
            <a-select v-model:value="queryParam.parkingLotIds" placeholder="请选择停车场" mode="multiple" :options="parkingLotOptions" style="width: 200px"/>
          </div>
          <div class="extra-item">
            <span>查询粒度:</span>
            <a-radio-group v-model:value="queryParam.granularity">
              <a-radio-button value="day">按天</a-radio-button>
              <a-radio-button value="week">按周</a-radio-button>
              <a-radio-button value="month">按月</a-radio-button>
              <a-radio-button value="custom">自定义</a-radio-button>
            </a-radio-group>
          </div>
          <div class="extra-item" v-if="queryParam.granularity !== 'custom'">
             <span>选择日期:</span>
             <a-date-picker v-model:value="queryParam.date" placeholder="选择基准日期" valueFormat="YYYY-MM-DD" style="width: 150px"/>
          </div>
          <div class="extra-item" v-if="queryParam.granularity === 'custom'">
             <span>日期范围:</span>
             <a-range-picker v-model:value="queryParam.dateRange" valueFormat="YYYY-MM-DD" style="width: 250px"/>
          </div>
          <div class="extra-item">
            <a-button type="primary" @click="handleQuery">查询</a-button>
            <a-button @click="resetQuery" style="margin-left: 8px">重置</a-button>
          </div>
        </div>
      </template>
    </a-card>

    <a-row :gutter="16" class="!mt-5">
      <a-col :span="12">
        <a-card :bordered="false" title="停车费率示意">
          <div ref="feeChartRef" :style="{ height: '300px', width }"></div>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card :bordered="false" title="近期收入概览">
          <div ref="revenueChartRef" :style="{ height: '300px', width }"></div>
        </a-card>
      </a-col>
    </a-row>

    <a-card :bordered="false" class="!mt-5" :loading="loading">
       <div ref="chartRef" :key="chartKey" :style="{ height, width }"></div>
    </a-card>
  </div>
</template>

<script lang="ts">
  import { defineComponent, ref, onMounted, reactive, nextTick } from 'vue';
  import { useECharts } from '/@/hooks/web/useECharts';
  import { getParkingOccupancyRate, getParkingLotList } from './OccupancyAnalysis.api';
  import { SelectTypes } from 'ant-design-vue/lib/select';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { Card, Form, Row, Col, Select, Radio, DatePicker, Button } from 'ant-design-vue';
  import dayjs from 'dayjs';
  import * as echarts from 'echarts';

  export default defineComponent({
    name: 'OccupancyAnalysis',
    components: {
      ACard: Card,
      AForm: Form,
      AFormItem: Form.Item,
      ARow: Row,
      ACol: Col,
      ASelect: Select,
      ARadioGroup: Radio.Group,
      ARadioButton: Radio.Button,
      ADatePicker: DatePicker,
      ARangePicker: DatePicker.RangePicker,
      AButton: Button,
    },
    setup() {
      const width = ref('100%');
      const height = ref('500px');
      const chartRef = ref<HTMLDivElement | null>(null);
      const feeChartRef = ref<HTMLDivElement | null>(null);
      const revenueChartRef = ref<HTMLDivElement | null>(null);

      const { createMessage } = useMessage();
      const loading = ref(false);
      const chartKey = ref(0);

      const parkingLotOptions = ref<SelectTypes>([]);
      
      const queryParam = reactive({
        parkingLotIds: [],
        granularity: 'day',
        date: new Date().toISOString().split('T')[0],
        dateRange: [],
      });

      // 渲染费率图表
      function renderFeeChart() {
        const { setOptions: setFeeOptions } = useECharts(feeChartRef);

        const peakHoursStart = 8;
        const peakHoursEnd = 20;
        const peakRate = 5;
        const offPeakRate = 4;

        const hours = Array.from({ length: 24 }, (_, i) => i);
        const feeData = hours.map((hour) => {
          return hour >= peakHoursStart && hour < peakHoursEnd ? peakRate : offPeakRate;
        });

        const option: echarts.EChartsOption = {
          tooltip: {
            trigger: 'axis',
            formatter: '{b}:00 - {c} 元/小时',
          },
          grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
          xAxis: {
            type: 'category',
            data: hours,
            axisLabel: { formatter: '{value}:00' },
          },
          yAxis: { type: 'value', name: '元/小时' },
          series: [
            {
              name: '费率',
              type: 'bar',
              data: feeData,
              itemStyle: {
                color: (params) => (params.dataIndex >= peakHoursStart && params.dataIndex < peakHoursEnd ? '#FF9A2E' : '#3793FF'),
              },
            },
          ],
        };
        setFeeOptions(option);
      }

      // 渲染收入图表
      function renderRevenueChart() {
        const { setOptions: setRevenueOptions } = useECharts(revenueChartRef);
        const dates = Array.from({ length: 7 }, (_, i) => dayjs().subtract(6 - i, 'day').format('MM-DD'));
        const revenueData = dates.map(() => Math.floor(Math.random() * (2000 - 500 + 1) + 500));

        const option: echarts.EChartsOption = {
          tooltip: {
            trigger: 'axis',
            formatter: '{b}<br/>收入: {c} 元',
          },
          grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
          xAxis: { type: 'category', data: dates },
          yAxis: { type: 'value', name: '元' },
          series: [
            {
              name: '收入',
              type: 'line',
              smooth: true,
              data: revenueData,
              areaStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: 'rgba(58, 123, 255, 0.5)' },
                  { offset: 1, color: 'rgba(58, 123, 255, 0.1)' },
                ]),
              },
            },
          ],
        };
        setRevenueOptions(option);
      }

      // 获取停车场列表
      async function fetchParkingLots() {
        try {
          const res = await getParkingLotList();
          parkingLotOptions.value = res.map(item => ({
            label: item.name,
            value: item.id,
          }));
        } catch (e) {
          console.error('获取停车场列表失败', e);
        }
      }
      
      // 查询操作
      async function handleQuery() {
        if (!queryParam.parkingLotIds || queryParam.parkingLotIds.length === 0) {
          createMessage.warning('请选择至少一个停车场');
          return;
        }

        let queryDate = queryParam.date;
        if (!queryDate && queryParam.granularity !== 'custom') {
          queryDate = new Date().toISOString().split('T')[0];
        }

        const params = {
          granularity: queryParam.granularity,
          parkingLotIds: queryParam.parkingLotIds.join(','),
          date: queryDate,
          startDate: queryParam.granularity === 'custom' ? queryParam.dateRange[0] : undefined,
          endDate: queryParam.granularity === 'custom' ? queryParam.dateRange[1] : undefined,
        };

        loading.value = true;
        try {
          const res = await getParkingOccupancyRate(params);
          
          // 每次查询时重新初始化图表
          chartKey.value++;
          await nextTick(); // 等待DOM更新
          
          const { setOptions } = useECharts(chartRef);

          
          if (!res || res.length === 0) {
            createMessage.info('未查询到相关数据');
            // 清空图表
            setOptions({ series: [] });
          } else {
            // 数据转换逻辑
            const groupedData = res.reduce((acc, item) => {
              const { parkingLotName, time, occupancyRate } = item;
              if (!acc[parkingLotName]) {
                acc[parkingLotName] = [];
              }
              acc[parkingLotName].push([time, occupancyRate]);
              return acc;
            }, {});

            const series = Object.keys(groupedData).map(name => ({
              name,
              type: 'line',
              smooth: true,
              data: groupedData[name],
            }));

            const legendData = Object.keys(groupedData);
            const xAxisData = [...new Set(res.map(item => item.time))].sort();

            const chartOptions = {
              tooltip: {
                trigger: 'axis',
              },
              legend: {
                data: legendData,
                top: 'bottom'
              },
              grid: {
                left: '3%',
                right: '4%',
                bottom: '10%',
                containLabel: true,
              },
              xAxis: {
                type: 'category',
                boundaryGap: false,
                data: xAxisData,
              },
              yAxis: {
                type: 'value',
                axisLabel: {
                  formatter: '{value} %'
                }
              },
              series,
            };

            // 强制清空并重新设置图表选项
            setOptions(chartOptions, true);
          }
        } finally {
          loading.value = false;
        }
      }

      // 重置查询
      function resetQuery() {
        queryParam.parkingLotIds = [];
        queryParam.granularity = 'day';
        queryParam.date = new Date().toISOString().split('T')[0];
        queryParam.dateRange = [];
      }

      onMounted(() => {
        fetchParkingLots();
        renderFeeChart();
        renderRevenueChart();
      });

      return {
        width,
        height,
        chartRef,
        feeChartRef,
        revenueChartRef,
        queryParam,
        parkingLotOptions,
        handleQuery,
        resetQuery,
        loading,
        chartKey,
      }
    }
  });
</script>

<style lang="less" scoped>
  .extra-wrapper {
    line-height: 55px;
    padding-right: 24px;

    .extra-item {
      display: inline-block;
      margin-right: 24px;

      span {
        margin-right: 8px;
      }
    }
  }
</style>