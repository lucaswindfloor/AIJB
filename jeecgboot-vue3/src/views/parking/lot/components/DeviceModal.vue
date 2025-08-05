<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="getTitle" @ok="handleSubmit" width="800px">
    <BasicTable @register="registerTable" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicTable, useTable } from '/@/components/Table';
  import { getDevices } from '../lot.api';

  const checkpointId = ref('');
  const checkpointName = ref('');

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    checkpointId.value = data.record.id;
    checkpointName.value = data.record.name;
    setModalProps({ confirmLoading: false });
    reload();
  });
  
  const getTitle = computed(() => `${checkpointName.value} - 硬件管理`);

  const columns = [
    { title: '设备序列号', dataIndex: 'deviceNo' },
    { title: '设备类型', dataIndex: 'deviceType', customRender: ({text}) => (text === 'CAMERA' ? '摄像头' : '道闸') },
    { title: '品牌', dataIndex: 'brand' },
    { title: 'IP地址', dataIndex: 'ipAddress' },
    { title: '状态', dataIndex: 'status' },
  ];

  const [registerTable, { reload }] = useTable({
    api: getDevices,
    columns,
    searchInfo: { checkpointId: checkpointId },
    pagination: false,
    showIndexColumn: false,
    canResize: false,
  });

  async function handleSubmit() {
    closeModal();
  }
</script> 