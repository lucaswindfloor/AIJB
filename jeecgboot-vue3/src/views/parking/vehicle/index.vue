<template>
  <div>
    <!-- 表格区域 -->
    <BasicTable @register="registerTable">
      <!-- 操作按钮 -->
      <template #tableTitle>
        <a-button type="primary" @click="handleCreate">新增</a-button>
        <a-button type="primary" @click="handleExportXls('车辆信息')">导出</a-button>
        <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
           <a-button type="primary">导入</a-button>
        </a-upload>
        <a-dropdown v-if="checkedKeys.length > 0">
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" @click="batchHandleDelete">
                <Icon icon="ant-design:delete-outlined"></Icon>
                删除
              </a-menu-item>
            </a-menu>
          </template>
          <a-button>
            批量操作
            <Icon icon="mdi:chevron-down"></Icon>
          </a-button>
        </a-dropdown>
      </template>
      <!-- 操作列 -->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <!-- 表单弹窗 -->
    <VehicleDrawer @register="registerDrawer" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" setup>
  import { ref, computed, unref } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useDrawer } from '/@/components/Drawer';
  import VehicleDrawer from './VehicleDrawer.vue';
  import { columns, searchFormSchema } from './vehicle.data';
  import { getList, deleteVehicle, deleteBatchVehicle, approveVehicle } from './vehicle.api';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useListPage } from '/@/hooks/system/useListPage';

  const { createMessage } = useMessage();
  const [registerDrawer, { openDrawer }] = useDrawer();

  // 列表页面公共参数、方法
  const { tableContext } = useListPage({
      tableProps: {
      title: '车辆信息列表',
      api: getList,
      columns: columns,
      formConfig: {
        labelWidth: 120,
        schemas: searchFormSchema,
      },
      actionColumn: {
        width: 120,
        title: '操作',
        dataIndex: 'action',
        slots: { customRender: 'action' },
      },
    },
  });

  const [registerTable, { reload, getSelectRows, clearSelectedRowKeys }, { rowSelection, selectedRowKeys }] = tableContext;
  const checkedKeys = selectedRowKeys;

  /**
   * 新增事件
   */
  function handleCreate() {
    openDrawer(true, {
      isUpdate: false,
    });
  }

  /**
   * 编辑事件
   */
  function handleEdit(record: Recordable) {
    openDrawer(true, {
      record,
      isUpdate: true,
    });
  }

  /**
   * 删除事件
   */
  async function handleDelete(record: Recordable) {
    await deleteVehicle({ id: record.id });
    createMessage.success('删除成功！');
    reload();
  }

  /**
   * 批量删除事件
   */
  async function batchHandleDelete() {
    await deleteBatchVehicle({ ids: checkedKeys.value.join(',') });
    createMessage.success('批量删除成功！');
    reload();
  }

  /**
   * 审核事件
   */
  async function handleApprove(record: Recordable) {
    await approveVehicle({ id: record.id, status: '2' }); // Set status to 'normal'
    createMessage.success('审核成功！');
    reload();
  }

  /**
   * 成功回调
   */
  function handleSuccess() {
    reload();
  }

  /**
   * 操作列定义
   */
  function getTableAction(record) {
    const actions = [
      {
        label: '编辑',
        onClick: handleEdit.bind(null, record),
      },
    ];
    // Add approve action if status is 'auditing'
    if (record.status === '1') {
      actions.push({
        label: '审核',
        color: 'success',
        popConfirm: {
          title: '是否确认审核通过？',
          confirm: handleApprove.bind(null, record),
        },
      });
    }
    actions.push({
      label: '删除',
      color: 'error',
      popConfirm: {
        title: '是否确认删除？',
        confirm: handleDelete.bind(null, record),
      },
    });
    return actions;
  }
</script> 