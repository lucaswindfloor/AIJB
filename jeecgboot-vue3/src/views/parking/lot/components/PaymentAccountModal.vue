<template>
  <BasicModal v-bind="$attrs" @register="registerModal" title="收款账号管理" @ok="handleSubmit" width="900px">
    <BasicTable @register="registerTable" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicTable, useTable } from '/@/components/Table';

  // 假设有API获取收款账号
  // import { getPaymentAccounts } from '../lot.api';

  const lotId = ref('');

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    lotId.value = data.record.id;
    setModalProps({ confirmLoading: false });
    // reload();
  });
  
  const columns = [
    { title: '账户名称', dataIndex: 'accountName' },
    { title: '支付渠道', dataIndex: 'channel' },
    { title: '商户ID', dataIndex: 'merchantId' },
    { title: '是否默认', dataIndex: 'isDefault' },
    { title: '状态', dataIndex: 'status' },
  ];
  
  const mockData = [
      { id: '1', accountName: '微信支付官方商户', channel: '微信支付', merchantId: '1230000109', isDefault: '是', status: '启用' },
      { id: '2', accountName: '支付宝官方商户', channel: '支付宝', merchantId: '2088102179378152', isDefault: '否', status: '启用' },
  ];

  const [registerTable, { reload }] = useTable({
    // api: getPaymentAccounts, // 替换为真实API
    dataSource: mockData, // 使用模拟数据
    columns,
    searchInfo: { parkingLotId: lotId },
    pagination: false,
    showIndexColumn: false,
    canResize: false,
  });

  async function handleSubmit() {
    closeModal();
  }
</script> 