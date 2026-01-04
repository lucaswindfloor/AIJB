<template>
  <BasicDrawer v-bind="$attrs" @register="registerDrawer" showFooter :title="getTitle" width="50%" @ok="handleSubmit">
    <BasicForm @register="registerForm" />
  </BasicDrawer>
</template>
<script lang="ts" setup>
  import { ref, computed, unref } from 'vue';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { formSchema } from './lot.data';
  import { BasicDrawer, useDrawerInner } from '/@/components/Drawer';
  import { saveOrUpdate } from './lot.api';

  const emit = defineEmits(['success', 'register']);
  const isUpdate = ref(true);

  const [registerForm, { setFieldsValue, resetFields, validate, updateSchema }] = useForm({
    labelWidth: 140,
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

  const getTitle = computed(() => (!unref(isUpdate) ? '新增停车场' : '编辑停车场'));

  async function handleSubmit() {
    try {
      const values = await validate();
      setDrawerProps({ confirmLoading: true });
      // 调用保存接口
      await saveOrUpdate(values, isUpdate.value);
      closeDrawer();
      emit('success');
    } finally {
      setDrawerProps({ confirmLoading: false });
    }
  }
</script> 