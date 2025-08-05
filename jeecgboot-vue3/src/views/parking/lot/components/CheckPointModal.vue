<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="getTitle" @ok="handleSubmit" width="1200px">
    <div class="p-2">
      <BasicTable @register="registerTable">
        <template #toolbar>
            <a-button type="primary" @click="handleAddCheckpoint">新增关卡</a-button>
            <a-button type="info" @click="handleGenerateCode('in-situ')">生成场内码</a-button>
            <a-button type="info" @click="handleGenerateCode('applets')">生成小程序码</a-button>
        </template>
        <!-- 操作列 -->
        <template #action="{ record }">
          <TableAction :actions="createActions(record)" />
        </template>
      </BasicTable>
    </div>
    <!-- 硬件管理二级弹窗 -->
    <DeviceModal @register="registerDeviceModal" />
    <!-- 二维码生成弹窗 -->
    <CreateQrCodeModal @register="registerQrCodeModal" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed } from 'vue';
  import { BasicModal, useModal, useModalInner } from '/@/components/Modal';
  import { BasicTable, useTable, TableAction, ActionItem } from '/@/components/Table';
  import { Button as AButton } from 'ant-design-vue';
  import { getCheckpoints } from '../lot.api';
  import DeviceModal from './DeviceModal.vue';
  import CreateQrCodeModal from './CreateQrCodeModal.vue';

  const lotId = ref('');
  const lotName = ref('');

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    lotId.value = data.record.id;
    lotName.value = data.record.name;
    setModalProps({ confirmLoading: false });
    reload();
  });

  const [registerDeviceModal, { openModal: openDeviceModal }] = useModal();
  const [registerQrCodeModal, { openModal: openQrCodeModal }] = useModal();

  const getTitle = computed(() => `${lotName.value} - 关卡管理`);

  const columns = [
    { title: '名称', dataIndex: 'name' },
    { title: '方向', dataIndex: 'direction', customRender: ({text}) => (text === 'IN' ? '入口' : '出口') },
    { title: '模式', dataIndex: 'mode', customRender: () => '无人值守' }, // 示例
    { title: '值班人', dataIndex: 'dutyPersonName' }, // 示例字段，假设为 dutyPersonName
    { title: '状态', dataIndex: 'status', customRender: ({text}) => (text === 'NORMAL' ? '正常' : '关闭') },
  ];

  const [registerTable, { reload }] = useTable({
    api: getCheckpoints,
    columns,
    searchInfo: { parkingLotId: lotId },
    pagination: false,
    showIndexColumn: true,
    actionColumn: {
      width: 250,
      title: '操作',
      dataIndex: 'action',
      slots: { customRender: 'action' },
    },
  });

  function handleAddCheckpoint() {
    // 新增关卡逻辑
  }
  
  function handleGenerateCode(type: string, record?: Recordable){
     openQrCodeModal(true, { type, record });
  }

  function createActions(record): ActionItem[] {
    return [
      {
        label: '编辑',
        onClick: () => { /* 编辑逻辑 */ },
      },
      {
        label: '硬件管理',
        onClick: () => openDeviceModal(true, { record }),
      },
      {
        label: '生成入场码',
        onClick: () => { handleGenerateCode('entry', record) },
        ifShow: record.direction === 'IN'
      },
       {
        label: '生成付款码',
        onClick: () => { handleGenerateCode('payment', record) },
        ifShow: record.direction === 'OUT'
      },
      {
        label: '删除',
        color: 'error',
        popConfirm: {
          title: '是否确认删除',
          confirm: () => { /* 删除逻辑 */ },
        },
      },
    ];
  }

  async function handleSubmit() {
    closeModal();
  }
</script> 