<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="getTitle" @ok="handleSubmit" width="1000px">
    <a-tabs v-model:activeKey="activeKey">
      <a-tab-pane key="temp_car" tab="临时车">
        <BasicTable @register="registerTempCarTable">
          <template #toolbar>
            <a-button type="primary" @click="handleAddRule('temp_car')">新增规则</a-button>
          </template>
          <template #action="{ record }">
            <TableAction :actions="createRuleActions(record)" />
          </template>
        </BasicTable>
      </a-tab-pane>
      <a-tab-pane key="monthly_car" tab="月卡车" force-render>
        <BasicTable @register="registerMonthlyCarTable">
           <template #toolbar>
            <a-button type="primary" @click="handleAddRule('monthly_car')">新增规则</a-button>
          </template>
          <template #action="{ record }">
            <TableAction :actions="createRuleActions(record)" />
          </template>
        </BasicTable>
      </a-tab-pane>
      <a-tab-pane key="vip_car" tab="VIP免费车">
        <BasicTable @register="registerVipCarTable">
           <template #toolbar>
            <a-button type="primary" @click="handleAddRule('vip_car')">新增规则</a-button>
          </template>
          <template #action="{ record }">
            <TableAction :actions="createRuleActions(record)" />
          </template>
        </BasicTable>
      </a-tab-pane>
    </a-tabs>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicTable, useTable, TableAction, ActionItem } from '/@/components/Table';
  import { Tabs as ATabs, TabPane as ATabPane, Button as AButton } from 'ant-design-vue';
  import { getChargingRules } from '../lot.api';

  const lotId = ref('');
  const lotName = ref('');
  const activeKey = ref('temp_car');

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    lotId.value = data.record.id;
    lotName.value = data.record.name;
    setModalProps({ confirmLoading: false });
    reload();
  });

  const getTitle = computed(() => `${lotName.value} - 收费规则`);

  const ruleColumns = [
    { title: '规则名称', dataIndex: 'name' },
    { title: '生效时段', dataIndex: 'effectiveTime', customRender: ({ record }) => `${record.startTime} - ${record.endTime}` },
    { title: '计费模型', dataIndex: 'billingModel' },
    { title: '价格类型', dataIndex: 'type' },
    { title: '单价', dataIndex: 'feeAmount', customRender: ({ text }) => `${text}元` },
    { title: '周期上限', dataIndex: 'feeUpperLimit', customRender: ({ text }) => `${text}元` },
  ];
  
  const commonTableProps = {
    columns: ruleColumns,
    pagination: false,
    showIndexColumn: true,
    canResize: false,
    actionColumn: {
      width: 120,
      title: '操作',
      dataIndex: 'action',
      slots: { customRender: 'action' },
    },
  };

  const [registerTempCarTable, { reload }] = useTable({
    ...commonTableProps,
    api: getChargingRules,
    searchInfo: { carType: 'temporary_car', parkingLotId: lotId },
  });

  const [registerMonthlyCarTable] = useTable({
    ...commonTableProps,
    api: getChargingRules,
    searchInfo: { carType: 'monthly_car', parkingLotId: lotId },
  });

  const [registerVipCarTable] = useTable({
    ...commonTableProps,
    api: getChargingRules,
    searchInfo: { carType: 'vip_car', parkingLotId: lotId },
  });

  function handleAddRule(carType: string) {
    // 新增规则逻辑
    console.log('add rule for', carType);
  }

  function createRuleActions(record: Recordable): ActionItem[] {
    return [
      { label: '编辑' },
      { label: '删除', color: 'error', popConfirm: { title: '是否确认删除', confirm: () => console.log('delete', record) } },
    ]
  }

  async function handleSubmit() {
    closeModal();
  }
</script> 