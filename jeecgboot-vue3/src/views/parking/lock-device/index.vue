<template>
  <div>
    <!--引用表格-->
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
      <!--自定义单个列-->
      <template #battery="{ record }">
        <a-progress :percent="record.batteryLevel" :strokeColor="getBatteryColor(record.batteryLevel)" size="small" />
      </template>
    </BasicTable>
    <!-- 表单区域 -->
    <LockDeviceDrawer @register="registerDrawer" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" setup>
  //ts引入
  import { ref, computed, unref } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useDrawer } from '/@/components/Drawer';
  import { useListPage } from '/@/hooks/system/useListPage';
  import LockDeviceDrawer from './LockDeviceDrawer.vue';
  import { columns, searchFormSchema } from './lockDevice.data';
  import { getList, deleteDevice, deleteBatch, lockUp, lockDown, refreshDeviceStatus } from './lockDevice.api';
  import { useMessage } from '/@/hooks/web/useMessage';

  const { createMessage, createConfirm } = useMessage();
  // 列表页面公共参数、方法
  const { prefixCls, tableContext } = useListPage({
    tableProps: {
      title: '车位锁设备管理',
      api: getList,
      columns: columns,
      formConfig: {
        labelWidth: 120,
        schemas: searchFormSchema,
      },
      scroll: { x: 1500 },
      actionColumn: {
        width: 180,
        title: '操作',
        dataIndex: 'action',
        slots: { customRender: 'action' },
        fixed: 'right',
      },
    },
  });

  //注册table数据
  const [registerTable, { reload }, { rowSelection, selectedRowKeys }] = tableContext;
  //注册drawer
  const [registerDrawer, { openDrawer }] = useDrawer();

  //新增事件
  function handleCreate() {
    openDrawer(true, {
      isUpdate: false,
      showFooter: true,
    });
  }
  
  //编辑事件
  function handleEdit(record: Recordable) {
    openDrawer(true, {
      record,
      isUpdate: true,
      showFooter: true,
    });
  }

  //删除事件
  async function handleDelete(record) {
    await deleteDevice({ id: record.id });
    handleSuccess();
  }
  
  //批量删除事件
  async function batchHandleDelete() {
    await deleteBatch({ ids: selectedRowKeys.value });
    handleSuccess();
  }
  
  //成功回调
  function handleSuccess() {
    reload();
  }

  // 操作栏
  function getTableAction(record) {
    return [
      {
        label: '升锁',
        onClick: handleLockUp.bind(null, record),
      },
      {
        label: '降锁',
        onClick: handleLockDown.bind(null, record),
      },
      {
        label: '刷新',
        color: 'success',
        onClick: handleRefresh.bind(null, record),
      },
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

  // 升锁
  function handleLockUp(record) {
    createConfirm({
        title: '确认升锁',
        content: `是否要升起设备 ${record.deviceNo} 的车位锁?`,
        iconType: 'warning',
        onOk: async () => {
            await lockUp({ deviceId: record.id });
            createMessage.success('升锁指令已发送');
            reload();
        }
    })
  }
  // 降锁
  function handleLockDown(record) {
    createConfirm({
        title: '确认降锁',
        content: `是否要降下设备 ${record.deviceNo} 的车位锁?`,
        iconType: 'warning',
        onOk: async () => {
            await lockDown({ deviceId: record.id });
            createMessage.success('降锁指令已发送');
            reload();
        }
    })
  }

  // 刷新状态
  async function handleRefresh(record) {
    try {
      await refreshDeviceStatus({ deviceId: record.id });
      createMessage.success(`设备 ${record.deviceNo} 状态刷新成功`);
      reload(); // 重新加载表格数据
    } catch (e) {
      // 错误消息会由全局处理器捕获并显示，这里可以只在控制台打印
      console.error(e);
    }
  }

  // 电池颜色
  function getBatteryColor(level) {
      if(level > 50) return '#52C41A'; // 绿色
      if(level > 20) return '#FAAD14'; // 黄色
      return '#F5222D'; // 红色
  }
</script> 