<template>
  <BasicDrawer v-bind="$attrs" @register="registerDrawer" showFooter :title="getTitle" width="50%" @ok="handleSubmit">
    <BasicForm @register="registerForm" />
  </BasicDrawer>
</template>
<script lang="ts" setup>
  import { ref, computed, unref } from 'vue';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { formSchema } from './record.data';
  import { saveOrUpdate } from './record.api';
  import { BasicDrawer, useDrawerInner } from '/@/components/Drawer';

  const emit = defineEmits(['success', 'register']);
  const isUpdate = ref(true);
  
  //表单配置
  const [registerForm, { resetFields, setFieldsValue, validate }] = useForm({
    labelWidth: 100,
    schemas: formSchema,
    showActionButtonGroup: false,
  });

  //抽屉配置
  const [registerDrawer, { setDrawerProps, closeDrawer }] = useDrawerInner(async (data) => {
    //重置表单
    await resetFields();
    setDrawerProps({ confirmLoading: false });
    isUpdate.value = !!data?.isUpdate;
    if (unref(isUpdate)) {
      //编辑
      await setFieldsValue({
        ...data.record,
      });
    }
  });

  // 抽屉标题
  const getTitle = computed(() => (unref(isUpdate) ? '编辑停车记录' : '人工补录'));
  
  //提交
  async function handleSubmit() {
    try {
      const values = await validate();
      setDrawerProps({ confirmLoading: true });
      //提交表单
      await saveOrUpdate(values, isUpdate.value);
      //关闭抽屉
      closeDrawer();
      //刷新列表
      emit('success');
    } finally {
      setDrawerProps({ confirmLoading: false });
    }
  }
</script> 