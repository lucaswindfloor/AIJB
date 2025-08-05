<template>
  <div>
    <!-- 表格 -->
    <BasicTable @register="registerTable">
      <!-- 工具栏 -->
      <template #toolbar>
        <a-button type="primary" @click="handleCreate"> 新增</a-button>
        <a-button type="primary" @click="handleExport"> 导出</a-button>
        <a-button type="primary" @click="handleImport"> 导入</a-button>
        <a-button type="primary" danger @click="handleBatchDelete"> 批量删除</a-button>
        <a-button type="primary" @click="handlePriceCalculation">价格测算</a-button>
      </template>
      <!-- 操作列 -->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" :dropDownActions="getDropDownAction(record)" />
      </template>
    </BasicTable>

    <!-- 新增/编辑抽屉 -->
    <LotDrawer @register="registerDrawer" @success="handleSuccess" />

    <!-- 子功能弹窗 -->
    <ChargingRuleModal @register="registerChargingRuleModal" />
    <PaymentAccountModal @register="registerPaymentAccountModal" />
    <CheckPointModal @register="registerCheckPointModal" />
    <PriceCalculationModal @register="registerPriceCalculationModal" />
  </div>
</template>

<script lang="ts" setup name="ParkingLotManagement">
  import { ref } from 'vue';
  import { BasicTable, useTable, TableAction, ActionItem } from '/@/components/Table';
  import { useDrawer } from '/@/components/Drawer';
  import { useModal } from '/@/components/Modal';
  import { getList, deleteLot, deleteBatch, exportXls } from './lot.api';
  import { columns, searchFormSchema } from './lot.data';

  import LotDrawer from './LotDrawer.vue';
  import ChargingRuleModal from './components/ChargingRuleModal.vue';
  import PaymentAccountModal from './components/PaymentAccountModal.vue';
  import CheckPointModal from './components/CheckPointModal.vue';
  import PriceCalculationModal from './components/PriceCalculationModal.vue';

  // 页面名称，用于keep-alive
  defineOptions({ name: 'ParkingLotManagement' });
  
  // 注册抽屉和弹窗
  const [registerDrawer, { openDrawer }] = useDrawer();
  const [registerChargingRuleModal, { openModal: openChargingRuleModal }] = useModal();
  const [registerPaymentAccountModal, { openModal: openPaymentAccountModal }] = useModal();
  const [registerCheckPointModal, { openModal: openCheckPointModal }] = useModal();
  const [registerPriceCalculationModal, { openModal: openPriceCalculationModal }] = useModal();

  // 表格配置
  const [registerTable, { reload, getSelectRowKeys, getForm }] = useTable({
    title: '停车场列表',
    api: getList,
    columns,
    formConfig: {
      labelWidth: 120,
      schemas: searchFormSchema,
    },
    useSearchForm: true,
    showTableSetting: true,
    bordered: true,
    showIndexColumn: true,
    actionColumn: {
      width: 120,
      title: '操作',
      dataIndex: 'action',
      slots: { customRender: 'action' },
      fixed: undefined,
    },
  });

  // 新增
  function handleCreate() {
    openDrawer(true, {
      isUpdate: false,
    });
  }

  // 编辑
  function handleEdit(record: Recordable) {
    openDrawer(true, {
      record,
      isUpdate: true,
    });
  }

  // 价格测算
  function handlePriceCalculation() {
    openPriceCalculationModal(true, {});
  }
  
  // 删除
  async function handleDelete(record: Recordable) {
    await deleteLot({ id: record.id });
    reload();
  }

  // 批量删除
  async function handleBatchDelete() {
    const ids = getSelectRowKeys();
    if (ids.length === 0) {
      return;
    }
    await deleteBatch({ ids });
    reload();
  }
  
  // 导出
  async function handleExport() {
    await exportXls({ ...getForm().getFieldsValue() });
  }

  // 导入
  function handleImport() {
    // 导入逻辑
  }

  // 操作完成后的回调
  function handleSuccess() {
    reload();
  }
  
  // 表格操作列
  function getTableAction(record: Recordable): ActionItem[] {
    return [
      {
        label: '编辑',
        onClick: handleEdit.bind(null, record),
      },
    ];
  }
  
  // 表格操作列 - 下拉菜单
  function getDropDownAction(record: Recordable): ActionItem[] {
      return [
          {
            label: '收费规则',
            onClick: () => openChargingRuleModal(true, { record }),
          },
          {
            label: '收款账号',
            onClick: () => openPaymentAccountModal(true, { record }),
          },
          {
            label: `关卡管理(${record.checkPointCounts || 0})`,
            onClick: () => openCheckPointModal(true, { record }),
          },
          {
            label: '删除',
            color: 'error',
            popConfirm: {
              title: '是否确认删除',
              confirm: handleDelete.bind(null, record),
            },
          },
      ]
  }

</script> 