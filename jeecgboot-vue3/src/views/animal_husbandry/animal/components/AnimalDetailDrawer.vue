<template>
  <BasicDrawer v-bind="$attrs" @register="registerDrawer" title="牲畜档案详情" width="70%">
    <Spin :spinning="loading" tip="加载中...">
      <div v-if="animalData" class="animal-detail-container">
        <!-- [V3方案] 1. 核心信息置顶，始终可见 -->
        <div class="section-title">基本信息</div>
        <Description :bordered="true" :column="4" :data="animalData" :schema="basicInfoSchema" />

        <div class="section-title">健康概览</div>
        <Description :bordered="true" :column="4" :data="animalData" :schema="healthInfoSchema" />

        <!-- [V3方案] 2. 使用页签承载所有详细数据 -->
        <div class="section-title">详细数据</div>
        <Tabs v-model:activeKey="activeKey">
          <!-- 页签一：数据概览 -->
          <TabPane key="DASHBOARD" tab="数据概览">
            <div class="chart-container">
              <TelemetryChart
                v-if="capsuleDevice"
                :device-id="capsuleDevice.id"
                telemetry-key="Temperature"
                title="体温核心趋势 (胶囊)"
                unit="℃"
                :y-axis-config="{ min: 20, max: 42 }"
              />
              <TelemetryChart
                v-if="capsuleDevice"
                :device-id="capsuleDevice.id"
                telemetry-key="Gastric_momentum"
                title="胃动力核心趋势 (胶囊)"
                unit=""
              />
              <TelemetryChart
                v-if="trackerDevice"
                :device-id="trackerDevice.id"
                telemetry-key="step"
                title="步数核心趋势 (追踪器)"
                unit="步"
              />
              <div v-if="!capsuleDevice && !trackerDevice" class="chart-placeholder">暂无图表数据</div>
            </div>
          </TabPane>
          
          <!-- 页签二：设备详情与遥测 -->
          <TabPane key="DEVICES" tab="设备详情与遥测">
            <div class="device-section">
              <div v-if="deviceDataSource.length > 0">
                 <!-- 时间范围选择器 -->
                <div class="toolbar">
                    <a-range-picker v-model:value="queryTimeRange" show-time />
                    <a-button type="primary" @click="handleQueryRawLog" :loading="rawLogLoading">查询所有设备日志</a-button>
                </div>
                <div class="log-viewer" v-if="!rawLogLoading">
                    <Collapse accordion>
                        <CollapsePanel v-for="deviceLog in rawLogData" :key="deviceLog.deviceId">
                            <template #header>
                                <span>设备: {{ deviceLog.deviceName }} ({{ deviceLog.deviceType }}) - 共 {{ deviceLog.logs.length }} 条日志</span>
                            </template>
                            <Collapse accordion>
                                <CollapsePanel v-for="(log, index) in deviceLog.logs" :key="index">
                                    <template #header>
                                        <span>{{ formatTimestamp(log.ts) }}</span>
                                    </template>
                                    <pre>{{ formatJson(log.value) }}</pre>
                                </CollapsePanel>
                            </Collapse>
                            <div v-if="deviceLog.logs.length === 0" class="chart-placeholder">在选定时间范围内无原始数据</div>
                        </CollapsePanel>
                    </Collapse>
                    <div v-if="rawLogData.length === 0" class="chart-placeholder">在选定时间范围内无原始数据</div>
                </div>
                <div v-else class="chart-placeholder">
                    <Spin />
                </div>
              </div>
              <div v-else class="chart-placeholder">暂无绑定设备，无法查询日志</div>
            </div>
          </TabPane>
          
          <!-- 页签三：告警历史 -->
          <TabPane key="ALARMS" tab="告警历史">
            <BasicTable @register="registerAlarmTable" />
          </TabPane>

          <!-- 页签四：生命周期 -->
          <TabPane key="LIFECYCLE" tab="生命周期">
            <a-button type="primary" @click="handleAddLifecycleEvent" class="mb-2"> 新增事件 </a-button>
            <BasicTable @register="registerLifecycleTable" />
          </TabPane>
        </Tabs>
      </div>
    </Spin>
    <LifecycleEventModal @register="registerLifecycleModal" @success="handleLifecycleSuccess" />
  </BasicDrawer>
</template>
<script lang="ts" setup>
  import { ref, computed } from 'vue';
  import { BasicDrawer, useDrawerInner } from '/@/components/Drawer';
  import { Description, DescItem } from '/@/components/Description';
  import { BasicTable, useTable } from '/@/components/Table';
  import { Spin, Tabs, TabPane, Collapse, CollapsePanel, Tag, Button as AButton, RangePicker as ARangePicker } from 'ant-design-vue';
  import { getById, getRawTelemetryLog } from '../animal.api';
  import { h } from 'vue';
  import { useModal } from '/@/components/Modal';
  import dayjs, { Dayjs } from 'dayjs';
  import TelemetryChart from './TelemetryChart.vue';
  import LifecycleEventModal from './LifecycleEventModal.vue';

  const animalData = ref<any>(null);
  const loading = ref(true);
  const activeKey = ref('DASHBOARD');

  // [V3方案] 新增原始日志相关状态
  const rawLogData = ref<any[]>([]);
  const rawLogLoading = ref(false);
  const queryTimeRange = ref<[Dayjs, Dayjs]>([dayjs().subtract(1, 'day'), dayjs()]);


  const deviceDataSource = ref<any[]>([]);
  const alarmDataSource = ref([]);
  const lifecycleDataSource = ref([]);
  
  // 计算属性，方便在模板中直接使用
  const capsuleDevice = computed(() => deviceDataSource.value.find(d => d.deviceType === 'CAPSULE'));
  const trackerDevice = computed(() => deviceDataSource.value.find(d => d.deviceType === 'TRACKER'));

  // [V3方案] 新增方法
  function formatTimestamp(ts) {
    return dayjs(ts).format('YYYY-MM-DD HH:mm:ss');
  }

  function formatJson(jsonString) {
      try {
          const obj = JSON.parse(jsonString);
          return JSON.stringify(obj, null, 2); // 格式化JSON，2个空格缩进
      } catch (e) {
          return jsonString; // 如果解析失败，返回原始字符串
      }
  }

  async function handleQueryRawLog() {
      if (deviceDataSource.value.length === 0) {
          return;
      }
      rawLogLoading.value = true;
      rawLogData.value = [];
      try {
          const [startTs, endTs] = [queryTimeRange.value[0].valueOf(), queryTimeRange.value[1].valueOf()];
          
          // 并行查询所有设备的日志
          const promises = deviceDataSource.value.map(device => 
              getRawTelemetryLog({ deviceId: device.id, startTs, endTs })
          );
          
          const results = await Promise.all(promises);
          
          // 组装数据
          rawLogData.value = deviceDataSource.value.map((device, index) => ({
              deviceId: device.id,
              deviceName: device.name,
              deviceType: device.deviceType_dictText,
              logs: results[index] || [],
          }));

      } catch (error) {
          console.error("查询原始遥测日志失败", error);
      } finally {
          rawLogLoading.value = false;
      }
  }


  // Schemas for descriptions (保持不变)
  const basicInfoSchema: DescItem[] = [
    { field: 'earTagId', label: '耳标号' },
    { field: 'name', label: '昵称' },
    { field: 'type_dictText', label: '类型' },
    { field: 'herdId_dictText', label: '所属畜群' },
    { field: 'gender_dictText', label: '性别' },
    { field: 'birthDate', label: '出生日期' },
    { field: 'weightKg', label: '最新体重(KG)' },
    { field: 'lastLocationLon', label: '最后位置', render: (val, data) => `${data.lastLocationLon || '-'}, ${data.lastLocationLat || '-'}` },
  ];

  const healthInfoSchema: DescItem[] = [
    {
      field: 'healthStatus_dictText',
      label: '健康状态',
      render: (val, data) => {
        const status = data.healthStatus;
        const color = status === 'ALARM' ? 'red' : status === 'SUB_HEALTHY' ? 'yellow' : 'green';
        return h(Tag, { color: color }, () => val);
      },
    },
    { field: 'healthScore', label: '健康评分' },
    { field: 'latestTemperature', label: '最新体温(℃)', render: (val) => val != null ? `${val.toFixed(2)}` : '-' },
    { field: 'latestGastricMomentum', label: '最新胃动量', render: (val) => val != null ? val : '-' },
    { field: 'aiConclusion', label: '最新AI分析结论', span: 4 },
  ];

  // Table for Alarm History (保持不变)
  const [registerAlarmTable] = useTable({
    columns: [
      { title: '告警时间', dataIndex: 'alarmTime', width: 180 },
      { title: '告警类型', dataIndex: 'alarmType_dictText', width: 120 },
      { title: '告警内容', dataIndex: 'alarmContent' },
      { title: '处理状态', dataIndex: 'status_dictText', width: 100 },
    ],
    dataSource: alarmDataSource,
    pagination: { pageSize: 5 },
    showIndexColumn: false,
    bordered: true,
  });

  // Table for Lifecycle Events (保持不变)
  const [registerLifecycleTable] = useTable({
     columns: [
      { title: '事件时间', dataIndex: 'eventTime', width: 180 },
      { title: '事件类型', dataIndex: 'eventType_dictText', width: 150 },
      { title: '事件描述', dataIndex: 'description' },
    ],
    dataSource: lifecycleDataSource,
    pagination: { pageSize: 5 },
    showIndexColumn: false,
    bordered: true,
  });

  const [registerDrawer] = useDrawerInner(async (data) => {
    loading.value = true;
    animalData.value = null;
    activeKey.value = 'DASHBOARD'; // 每次打开时重置到概览页签
    
    // 清空数据源
    deviceDataSource.value = [];
    alarmDataSource.value = [];
    lifecycleDataSource.value = [];
    rawLogData.value = []; // [V3方案] 清空原始日志

    try {
        const res = await getById({ id: data.record.id });
        animalData.value = res;
        
        deviceDataSource.value = res.deviceList || [];
        alarmDataSource.value = res.alarmRecordList || [];
        lifecycleDataSource.value = res.lifecycleEventList || [];
        
    } catch (e) {
        console.error("加载牲畜详情失败", e);
    } finally {
        loading.value = false;
    }
  });

  // Lifecycle Event Modal (保持不变)
  const [registerLifecycleModal, { openModal: openLifecycleModal }] = useModal();

  function handleAddLifecycleEvent() {
    openLifecycleModal(true, {
      // 传递animalId给弹窗
      animalId: animalData.value.id
    });
  }

  async function handleLifecycleSuccess() {
      // 刷新生命周期列表
      const res = await getById({ id: animalData.value.id });
      lifecycleDataSource.value = res.lifecycleEventList || [];
  }
</script>
<style lang="less" scoped>
  .animal-detail-container {
    padding: 16px;
  }
  .section-title {
    font-size: 16px;
    font-weight: 500;
    margin: 24px 0 16px 0;
  }
  .chart-container {
    display: flex;
    flex-direction: column;
    gap: 24px;
  }
  .chart-placeholder {
    height: 300px;
    background-color: #f0f2f5;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #999;
    border-radius: 4px;
  }
  .device-panel-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
  }
  .log-viewer {
    margin-top: 16px;
    max-height: 400px;
    overflow-y: auto;
    border: 1px solid #d9d9d9;
    padding: 8px;
    border-radius: 4px;
  }
  .toolbar {
      display: flex;
      align-items: center;
      gap: 8px;
  }
</style> 