<template>
  <div>
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button type="primary" @click="handleSync"> <CloudSyncOutlined /> 从TB同步设备 </a-button>
        <a-button type="danger" @click="handleBatchDelete" :disabled="!hasSelected">批量删除</a-button>
      </template>
      <template #action="{ record }">
        <TableAction :actions="getTableActions(record)" :dropDownActions="getDropDownActions(record)" />
      </template>
    </BasicTable>
    <DeviceModal @register="registerModal" @success="handleSuccess" />
    <BindModal @register="registerBindModal" @success="handleSuccess" />
  </div>
</template>
<script lang="ts" setup name="animal_husbandry/device">
  import { computed } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { CloudSyncOutlined } from '@ant-design/icons-vue';

  import DeviceModal from './components/DeviceModal.vue';
  import BindModal from './components/BindModal.vue';
  import { columns, searchFormSchema } from './device.data';
  import { getList, deleteDevice, unbindFromAnimal, deleteBatch } from './device.api';

  // 组件命名，必须与后端菜单的组件路径匹配
  defineOptions({ name: 'animal_husbandry/device' });
  
  const { createMessage, createConfirm } = useMessage();
  const [registerModal, { openModal }] = useModal();
  const [registerBindModal, { openModal: openBindModal }] = useModal();

  const [registerTable, { reload, getSelectRowKeys }] = useTable({
    title: '设备台账列表',
    api: getList,
    columns,
    canResize: true,
    formConfig: {
      labelWidth: 120,
      schemas: searchFormSchema,
    },
    rowKey: 'id',
    rowSelection: {
      type: 'checkbox',
    },
    useSearchForm: true,
    showTableSetting: true,
    bordered: true,
    actionColumn: {
      width: 180,
      title: '操作',
      dataIndex: 'action',
      slots: { customRender: 'action' },
      fixed: 'right',
    },
  });

  const hasSelected = computed(() => getSelectRowKeys().length > 0);

  /**
   * 工具栏：从TB同步设备
   */
  function handleSync() {
    openModal(true, { isUpdate: false });
  }

  /**
   * 工具栏：批量删除
   */
  function handleBatchDelete() {
    createConfirm({
      iconType: 'error',
      title: '高危操作确认',
      content: `确定要删除选中的 ${getSelectRowKeys().length} 条设备记录吗？此操作不可恢复！`,
      onOk: async () => {
        await deleteBatch({ ids: getSelectRowKeys().join(',') });
        createMessage.success('批量删除成功');
        reload();
      },
    });
  }

  /**
   * 操作列：详情/编辑
   */
  function handleEdit(record: Recordable) {
    openModal(true, { record, isUpdate: true });
  }

  /**
   * 操作列：绑定
   */
  function handleBind(record: Recordable) {
    openBindModal(true, { record });
  }

  /**
   * 操作列：解绑
   */
  async function handleUnbind(record: Recordable) {
    createConfirm({
      iconType: 'warning',
      title: '确认解绑',
      content: `确定要将设备 [${record.name}] 从其关联的牲畜上解绑吗？`,
      onOk: async () => {
        await unbindFromAnimal({ deviceId: record.id });
        createMessage.success('解绑成功');
        reload();
      },
    });
  }

  /**
   * 操作列：删除
   */
  async function handleDelete(record: Recordable) {
    await deleteDevice({ id: record.id });
    createMessage.success('删除成功');
    reload();
  }

  /**
   * 定义主要操作
   */
  function getTableActions(record: Recordable): ActionItem[] {
    const actions: ActionItem[] = [
      {
        label: '编辑',
        onClick: handleEdit.bind(null, record),
      },
    ];

    if (record.status === 'IN_STOCK' || record.status === 'IDLE') {
      actions.push({
        label: '绑定',
        onClick: handleBind.bind(null, record),
      });
    }

    if (record.status === 'ACTIVE') {
      actions.push({
        label: '解绑',
        color: 'warning',
        onClick: handleUnbind.bind(null, record),
      });
    }

    return actions;
  }
  
  /**
   * 定义下拉菜单操作
   */
  function getDropDownActions(record: Recordable): ActionItem[] {
     const actions: ActionItem[] = [];
     if (record.status === 'IN_STOCK') {
      actions.push({
        label: '删除',
        color: 'error',
        popConfirm: {
            title: `确定要删除设备 [${record.name}] 吗?`,
            confirm: handleDelete.bind(null, record),
        }
      });
    }
    // 可以在此添加更多操作，如“变更状态到维保中”等
    return actions;
  }

  /**
   * 弹窗操作成功后的回调
   */
  function handleSuccess() {
    reload();
  }
</script> 