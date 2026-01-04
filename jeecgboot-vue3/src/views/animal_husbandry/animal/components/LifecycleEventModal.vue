<template>
  <BasicModal v-bind="$attrs" @register="registerModal" title="记录新的生命周期事件" @ok="handleSubmit">
    <BasicForm @register="registerForm" />
  </BasicModal>
</template>
<script lang="ts" setup>
  import { ref, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form';
  import { lifecycleEventFormSchema } from '../animal.data';
  import { addLifecycleEvent } from '../animal.api';

  const emit = defineEmits(['success', 'register']);

  const animalId = ref('');

  const [registerForm, { resetFields, validate }] = useForm({
    labelWidth: 100,
    baseColProps: { span: 24 },
    schemas: lifecycleEventFormSchema,
    showActionButtonGroup: false,
  });

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    resetFields();
    setModalProps({ confirmLoading: false });
    animalId.value = data.record.id;
  });

  async function handleSubmit() {
    try {
      const values = await validate();
      setModalProps({ confirmLoading: true });
      const params = {
        animalId: unref(animalId),
        ...values,
      };
      await addLifecycleEvent(params);
      closeModal();
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script> 