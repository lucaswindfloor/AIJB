<template>
  <BasicDrawer v-bind="$attrs" @register="registerDrawer" showFooter :title="getTitle" width="50%">
    <BasicForm @register="registerForm" />
  </BasicDrawer>
</template>
<script lang="ts" setup>
  import { ref, computed, unref } from 'vue';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { formSchema } from './lockDevice.data';
  import { BasicDrawer, useDrawerInner } from '/@/components/Drawer';
  import { addDevice, editDevice } from './lockDevice.api';

  // 声明emits
  const emit = defineEmits(['success', 'register']);
  const isUpdate = ref(true);

  //表单配置
  const [registerForm, { resetFields, setFieldsValue, validate }] = useForm({
    labelWidth: 120,
    schemas: formSchema,
    showActionButtonGroup: false,
  });

  // Dailog/Drawer配置
  const [registerDrawer, { setDrawerProps, closeDrawer }] = useDrawerInner(async (data) => {
    // 重置表单
    await resetFields();
    setDrawerProps({ confirmLoading: false });
    isUpdate.value = !!data?.isUpdate;

    if (unref(isUpdate)) {
      //回显
      await setFieldsValue({
        ...data.record,
      });
    }
  });

  // 获取标题
  const getTitle = computed(() => (!unref(isUpdate) ? '新增设备' : '编辑设备'));

  //提交事件
  async function handleSubmit() {
    try {
      const values = await validate();
      setDrawerProps({ confirmLoading: true });
      if (unref(isUpdate)) {
        await editDevice(values);
      } else {
        await addDevice(values);
      }
      // 关闭弹窗
      closeDrawer();
      // 触发父组件的success事件
      emit('success');
    } finally {
      setDrawerProps({ confirmLoading: false });
    }
  }
</script> 