<template>
  <div>
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" @click="handleAdd" preIcon="ant-design:plus-outlined"> 新增</a-button>
        <a-dropdown v-if="selectedRowKeys.length > 0">
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" @click="batchHandleDelete">
                <Icon icon="ant-design:delete-outlined"></Icon>
                删除
              </a-menu-item>
            </a-menu>
          </template>
          <a-button>批量操作
            <Icon icon="mdi:chevron-down"></Icon>
          </a-button>
        </a-dropdown>
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)"/>
      </template>
    </BasicTable>
    <!-- 表单区域 -->
    <FenceModal @register="registerModal" @success="handleSuccess"></FenceModal>
  </div>
</template>

<script lang="ts" setup>
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useModal } from '/@/components/Modal';
  import FenceModal from './components/FenceModal.vue';
  import { columns, searchFormSchema } from './fence.data';
  import { list, deleteOne, batchDelete } from './fence.api';
  
  // 声明组件名称
  defineOptions({name:'animal_husbandry/fence'});

  const [registerModal, { openModal }] = useModal();

  // 列表页面公共参数、方法
  const { tableContext } = useListPage({
    tableProps: {
      title: '电子围栏管理',
      api: list,
      columns: columns,
      useSearchForm: true,
      formConfig: {
        labelWidth: 80,
        schemas: searchFormSchema,
      },
      canResize: false,
      actionColumn: {
        width: 120,
        title: '操作',
        dataIndex: 'action',
        slots: { customRender: 'action' },
        fixed: 'right',
      },
    },
  });

  const [registerTable, { reload, setSelectedRowKeys }, { rowSelection, selectedRowKeys }] = tableContext;

  /**
   * 新增事件
   */
  function handleAdd() {
    openModal(true, { isUpdate: false });
  }

  /**
   * 编辑事件
   */
  function handleEdit(record: Recordable) {
    openModal(true, { record, isUpdate: true });
  }

  /**
   * 删除事件
   */
  async function handleDelete(record) {
    await deleteOne({ id: record.id }, handleSuccess);
  }

  /**
   * 批量删除事件
   */
  async function batchHandleDelete() {
    await batchDelete({ ids: selectedRowKeys.value.join(',') }, handleSuccess);
  }

  /**
   * 成功回调
   */
  function handleSuccess() {
    (selectedRowKeys.value = []) && reload();
  }

   /**
   * 操作栏
   */
  function getTableAction(record) {
    return [
      {
        label: '编辑',
        onClick: handleEdit.bind(null, record),
      },
      {
        label: '删除',
        color: 'error',
        popConfirm: {
          title: '是否确认删除',
          confirm: handleDelete.bind(null, record),
        },
      },
    ];
  }
</script>
