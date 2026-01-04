<template>
  <BasicModal v-bind="$attrs" @register="registerModal" title="创建维保任务" @ok="handleSubmit">
    <BasicForm @register="registerForm" />
  </BasicModal>
</template>
<script lang="ts" setup>
  import { ref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form';
  import { createMaintenance } from '../deviceMonitor.api';
  import { formSchema } from './createMaintenanceModal.data';
  import { useMessage } from '/@/hooks/web/useMessage';

  const deviceId = ref('');
  const emit = defineEmits(['success', 'register']);
  const { createMessage } = useMessage();

  const [registerForm, { validate, resetFields }] = useForm({
    labelWidth: 100,
    baseColProps: { span: 24 },
    schemas: formSchema,
    showActionButtonGroup: false,
  });

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    resetFields();
    setModalProps({ confirmLoading: false });
    deviceId.value = data.record.id;
  });

  async function handleSubmit() {
    try {
      const values = await validate();
      setModalProps({ confirmLoading: true });
      
      const params = {
        deviceId: deviceId.value,
        ...values,
      };
      
      await createMaintenance(params);

      createMessage.success('维保任务创建成功');
      closeModal();
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>

<style scoped>
.warning-text {
  color: #f5222d;
  font-size: 13px;
  margin-top: 16px;
  text-align: left;
}
</style> 