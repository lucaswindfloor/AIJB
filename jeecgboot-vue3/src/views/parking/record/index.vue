<template>
  <div>
    <!-- 表格 -->
    <BasicTable @register="registerTable">
      <!-- 工具栏 -->
      <template #toolbar>
        <a-button type="primary" @click="handleCreate">人工补录</a-button>
        <a-button type="primary" @click="handleExport"> 导出</a-button>
        <a-button type="primary" danger @click="handleBatchDelete"> 批量删除</a-button>
      </template>
      <!-- 操作列 -->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" :dropDownActions="getDropDownAction(record)" />
      </template>
    </BasicTable>

    <!-- 新增/编辑抽屉 -->
    <RecordDrawer @register="registerDrawer" @success="handleSuccess" />

    <!-- 其他弹窗 placeholder -->
    <!-- <FeeModal @register="registerFeeModal" /> -->
    <!-- <SettleModal @register="registerSettleModal" /> -->
    <!-- <ImageModal @register="registerImageModal" /> -->
  </div>
</template>

<script lang="ts" setup name="ParkingRecordManagement">
  import { BasicTable, useTable, TableAction, ActionItem } from '/@/components/Table';
  import { useDrawer } from '/@/components/Drawer';
  import { useModal } from '/@/components/Modal';
  import { getList, deleteRecord, deleteBatch, exportXls } from './record.api';
  import { columns, searchFormSchema } from './record.data';
  import RecordDrawer from './RecordDrawer.vue';

  // 页面名称
  defineOptions({ name: 'ParkingRecordManagement' });

  // 注册组件
  const [registerDrawer, { openDrawer }] = useDrawer();
  // const [registerFeeModal, { openModal: openFeeModal }] = useModal();
  // const [registerSettleModal, { openModal: openSettleModal }] = useModal();
  // const [registerImageModal, { openModal: openImageModal }] = useModal();

  // 表格配置
  const [registerTable, { reload, getSelectRowKeys, getForm }] = useTable({
    title: '停车记录列表',
    api: getList,
    columns,
    formConfig: {
      labelWidth: 120,
      schemas: searchFormSchema,
      autoSubmitOnEnter: true,
      showAdvancedButton: true,
      baseColProps: {
        span: 6,
      },
    },
    useSearchForm: true,
    showTableSetting: true,
    bordered: true,
    showIndexColumn: true,
    // 启用横向滚动，并为操作列提供足够宽度
    scroll: { x: 1800 }, 
    actionColumn: {
      width: 180,
      title: '操作',
      dataIndex: 'action',
      slots: { customRender: 'action' },
      fixed: 'right', // 固定在右侧
    },
  });

  // 人工补录
  function handleCreate() {
    openDrawer(true, { isUpdate: false });
  }

  // 编辑
  function handleEdit(record: Recordable) {
    openDrawer(true, { record, isUpdate: true });
  }

  // 删除
  async function handleDelete(record: Recordable) {
    await deleteRecord({ id: record.id }, reload);
  }

  // 批量删除
  async function handleBatchDelete() {
    const ids = getSelectRowKeys();
    if (ids.length > 0) {
      await deleteBatch({ ids: ids.join(',') }, reload);
    }
  }
  
  // 导出
  async function handleExport() {
    await exportXls({ ...getForm().getFieldsValue() });
  }

  // 操作完成后的回调
  function handleSuccess() {
    reload();
  }
  
  // 主操作
  function getTableAction(record: Recordable): ActionItem[] {
    return [
      {
        label: '编辑',
        onClick: handleEdit.bind(null, record),
      },
      {
        label: '费用',
        // onClick: () => openFeeModal(true, { record }),
      },
    ];
  }
  
  // 下拉菜单操作
  function getDropDownAction(record: Recordable): ActionItem[] {
      return [
          {
            label: '结算',
            // onClick: () => openSettleModal(true, { record }),
          },
          {
            label: '图片',
            // onClick: () => openImageModal(true, { record }),
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