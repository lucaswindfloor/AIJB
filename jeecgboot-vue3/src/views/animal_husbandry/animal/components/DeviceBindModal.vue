<template>
  <BasicModal v-bind="$attrs" @register="registerModal" title="为牲畜绑定设备" @ok="handleSubmit">
    <BasicForm @register="registerForm" />
  </BasicModal>
</template>
<script lang="ts" setup>
  import { ref, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm, FormSchema } from '/@/components/Form';
  import { getAvailableDevicesByType, bindDevice } from '../animal.api';

  const emit = defineEmits(['success', 'register']);

  const animalId = ref('');
  const animalName = ref('');
  const deviceType = ref('');
  
  // 将Schema定义移到setup内部，以便动态修改
  const formSchemas: FormSchema[] = [
    {
      field: 'deviceId',
      label: '选择设备',
      component: 'ApiSelect',
      componentProps: ({ formModel }) => {
          return {
            api: getAvailableDevicesByType,
            params: { deviceType: deviceType.value },
            labelField: 'name',
            valueField: 'id',
            resultField: 'result', // 【修正】后端直接返回List，所以从result字段取
            placeholder: '请选择要绑定的设备',
          }
      },
      required: true,
    },
  ];

  const [registerForm, { setFieldsValue, resetFields, validate }] = useForm({
    labelWidth: 100,
    baseColProps: { span: 24 },
    schemas: formSchemas,
    showActionButtonGroup: false,
  });

  const [registerModal, { setModalProps, closeModal, changeLoading, changeOkLoading }] = useModalInner(async (data) => {
    await resetFields();
    setModalProps({ 
        confirmLoading: false,
        // 动态设置标题
        title: `为 [${data.record.name}] 绑定${data.deviceType === 'CAPSULE' ? '瘤胃胶囊' : '动物追踪器'}`
    });
    animalId.value = data.record.id;
    deviceType.value = data.deviceType;
    // 需要手动触发一次ApiSelect的依赖更新
    await setFieldsValue({ deviceId: undefined });
  });

  async function handleSubmit() {
    try {
      const values = await validate();
      changeOkLoading(true);
      const params = {
        animalId: unref(animalId),
        deviceId: values.deviceId,
      };
      await bindDevice(params);
      closeModal();
      emit('success');
    } finally {
      changeOkLoading(false);
    }
  }
</script> 