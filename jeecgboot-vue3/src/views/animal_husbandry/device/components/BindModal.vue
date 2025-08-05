<template>
  <BasicModal v-bind="$attrs" @register="registerModal" title="将设备绑定到牲畜" @ok="handleSubmit" width="500px">
    <div class="pt-4 pr-4">
      <BasicForm @register="registerForm" />
    </div>
  </BasicModal>
</template>
<script lang="ts" setup>
  import { ref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm, FormSchema } from '/@/components/Form';
  import { bindToAnimal, getAvailableAnimals } from '../device.api';

  const emit = defineEmits(['success', 'register']);

  const deviceId = ref('');
  // 1. 创建一个响应式的ref来专门存放ApiSelect的参数
  const apiParams = ref({ deviceType: '' });
  
  // 2. 将表单定义移入组件内部，并直接绑定响应式的apiParams
  const formSchemas: FormSchema[] = [
    {
      field: 'animalId',
      label: '选择牲畜',
      component: 'ApiSelect',
      componentProps: {
        api: getAvailableAnimals,
        params: apiParams, // 直接绑定ref
        // 【修正】使用正确的牲畜字段作为标签和值
        labelField: 'earTagId',
        valueField: 'id',
        resultField: 'records',
        placeholder: '请输入耳标号搜索牲畜',
      },
      required: true,
    },
  ];

  const [registerForm, { resetFields, validate }] = useForm({
    labelWidth: 100,
    baseColProps: { span: 24 },
    schemas: formSchemas, // 使用在组件内定义的schema
    showActionButtonGroup: false,
  });

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    resetFields();
    setModalProps({ confirmLoading: false });
    
    deviceId.value = data?.record?.id;
    const currentDeviceType = data?.record?.deviceType;

    // 3. 不再调用updateSchema，只更新ref的值，让Vue的响应式系统自动触发ApiSelect更新
    apiParams.value.deviceType = currentDeviceType;
  });

  async function handleSubmit() {
    try {
      const values = await validate();
      setModalProps({ confirmLoading: true });
      await bindToAnimal({
        deviceId: deviceId.value,
        animalId: values.animalId,
      });
      closeModal();
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script> 