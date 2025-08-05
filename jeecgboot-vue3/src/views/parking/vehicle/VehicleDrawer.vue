<template>
  <BasicDrawer v-bind="$attrs" @register="registerDrawer" showFooter :title="title" width="60%" @ok="handleSubmit">
    <BasicForm @register="registerForm" />
  </BasicDrawer>
</template>
<script lang="ts" setup>
  import { ref, computed, unref } from 'vue';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { formSchema } from './vehicle.data';
  import { BasicDrawer, useDrawerInner } from '/@/components/Drawer';
  import { saveVehicle, editVehicle } from './vehicle.api';

  const emit = defineEmits(['success', 'register']);
  const isUpdate = ref(true);
  
  const [registerForm, { setFieldsValue, resetFields, validate }] = useForm({
    labelWidth: 120,
    schemas: formSchema,
    showActionButtonGroup: false,
    baseColProps: {
      span: 24,
    }
  });

  const [registerDrawer, { setDrawerProps, closeDrawer }] = useDrawerInner(async (data) => {
    resetFields();
    setDrawerProps({ confirmLoading: false });
    isUpdate.value = !!data?.isUpdate;

    if (unref(isUpdate)) {
      setFieldsValue({
        ...data.record,
      });
    }
  });

  const title = computed(() => (unref(isUpdate) ? '编辑车辆' : '新增车辆'));

  async function handleSubmit() {
    try {
      const values = await validate();
      setDrawerProps({ confirmLoading: true });
      if (unref(isUpdate)) {
        await editVehicle(values);
      } else {
        await saveVehicle(values);
      }
      closeDrawer();
      emit('success');
    } finally {
      setDrawerProps({ confirmLoading: false });
    }
  }
</script> 