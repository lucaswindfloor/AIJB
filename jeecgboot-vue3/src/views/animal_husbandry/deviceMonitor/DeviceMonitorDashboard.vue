<template>
  <div class="p-4">
    <!-- KPI Cards -->
    <a-row :gutter="16">
      <a-col :span="6">
        <a-card title="设备总数" :loading="loading">
          <span class="value">{{ kpis.total }}</span> <span class="unit">个</span>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card title="在线设备" :loading="loading">
          <span class="value">{{ kpis.online }}</span> <span class="unit">个</span>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card title="离线设备" :loading="loading">
          <span class="value text-red">{{ kpis.offline }}</span> <span class="unit">个</span>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card title="低电量设备" :loading="loading">
          <span class="value text-orange">{{ kpis.lowBattery }}</span> <span class="unit">个</span>
        </a-card>
      </a-col>
    </a-row>

    <!-- Problematic Devices Table -->
    <a-card title="有问题的设备 (离线超过12小时 / 电量低于20%)" class="!mt-4">
      <BasicTable @register="registerTable">
        <!-- 参照 2-frontend-guide.mdc 案例四，使用标准 #action 插槽 -->
        <template #action="{ record }">
          <TableAction :actions="getTableAction(record)" />
        </template>
      </BasicTable>
    </a-card>
    
    <DeviceTelemetryModal @register="registerDetailModal" />
    <CreateMaintenanceModal @register="registerMaintenanceModal" @success="handleRefresh" />
  </div>
</template>

<script lang="ts" setup>
  import { ref, onMounted } from 'vue';
  import { Row as ARow, Col as ACol, Card as ACard } from 'ant-design-vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { getKpis, getProblematicList, sendRpc } from './deviceMonitor.api';
  import { columns } from './deviceMonitor.data';
  import { useMessage } from '/@/hooks/web/useMessage';

  import DeviceTelemetryModal from './components/DeviceTelemetryModal.vue';
  import CreateMaintenanceModal from './components/CreateMaintenanceModal.vue';

  const { createMessage } = useMessage();
  const loading = ref(true);
  const kpis = ref({ total: 0, online: 0, offline: 0, lowBattery: 0 });

  const [registerTable, { reload }] = useTable({
    title: '问题设备列表',
    api: getProblematicList,
    columns: columns,
    showIndexColumn: false,
    bordered: true,
    // 参照 2-frontend-guide.mdc 案例四，添加 canResize: true 启用滚动条
    canResize: true,
    actionColumn: {
      width: 250,
      title: '操作',
      dataIndex: 'action',
      // 参照 2-frontend-guide.mdc 案例四，添加 fixed: 'right' 和标准插槽
      fixed: 'right',
      slots: { customRender: 'action' },
    },
  });
  
  const [registerDetailModal, { openModal: openDetailModal }] = useModal();
  const [registerMaintenanceModal, { openModal: openMaintenanceModal }] = useModal();

  /**
   * 获取操作栏按钮
   * @param record
   */
  function getTableAction(record: Recordable) {
    return [
      {
        label: '详情',
        onClick: handleDetail.bind(null, record),
      },
      {
        label: '重启指令',
        popConfirm: {
          title: '确认发送重启指令吗?',
          placement: 'left',
          confirm: handleReboot.bind(null, record),
        },
      },
      {
        label: '创建维保任务',
        onClick: handleCreateMaintenance.bind(null, record),
        ifShow: record.isLowBattery,
      },
    ];
  }

  async function loadKpis() {
    try {
      loading.value = true;
      kpis.value = await getKpis();
    } finally {
      loading.value = false;
    }
  }

  function handleDetail(record: Recordable) {
    openDetailModal(true, { record });
  }

  async function handleReboot(record: Recordable) {
    await sendRpc({ deviceId: record.id, method: 'reboot' });
    createMessage.success('重启指令已发送');
  }

  function handleCreateMaintenance(record: Recordable) {
    openMaintenanceModal(true, { record });
  }

  function handleRefresh() {
    reload();
    loadKpis();
  }

  onMounted(() => {
    loadKpis();
  });
</script>

<style lang="less" scoped>
  .value {
    font-size: 24px;
    font-weight: 500;
  }
  .unit {
    margin-left: 8px;
    color: #666;
  }
  .text-red {
    color: @error-color;
  }
  .text-orange {
    color: @warning-color;
  }
</style> 