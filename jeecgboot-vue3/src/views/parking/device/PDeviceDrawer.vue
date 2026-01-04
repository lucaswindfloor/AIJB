<template>
  <BasicDrawer 
    v-bind="$attrs" 
    @register="registerDrawer" 
    showFooter 
    :title="getTitle" 
    width="50%" 
    @ok="handleSubmit"
  >
    <BasicForm @register="registerForm" />
  </BasicDrawer>
</template>

<script lang="ts" setup>
  import { ref, computed, unref } from 'vue';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { formSchema } from './pDevice.data';
  import { BasicDrawer, useDrawerInner } from '/@/components/Drawer';
  import { addDevice, editDevice } from './pDevice.api';
  import { useMessage } from '/@/hooks/web/useMessage';

  // Emits声明
  const emit = defineEmits(['success', 'register']);
  
  const { createMessage } = useMessage();
  const isUpdate = ref(true);
  const rowId = ref('');

  // 表单配置
  const [registerForm, { setFieldsValue, resetFields, clearValidate, validate }] = useForm({
    labelWidth: 100,
    schemas: formSchema,
    showActionButtonGroup: false,
    actionColOptions: {
      span: 23,
    },
  });

  // 抽屉配置
  const [registerDrawer, { setDrawerProps, closeDrawer }] = useDrawerInner(async (data) => {
    await resetFields();
    setDrawerProps({ confirmLoading: false });
    isUpdate.value = !!data?.id;

    if (unref(isUpdate)) {
      rowId.value = data.id;
      await setFieldsValue({
        ...data,
      });
    }
    
    // 清除验证
    await clearValidate();
  });

  // 标题
  const getTitle = computed(() => (!unref(isUpdate) ? '新增设备' : '编辑设备'));

  // 提交表单
  async function handleSubmit() {
    try {
      const values = await validate();
      setDrawerProps({ confirmLoading: true });

      // 处理表单数据
      const formData = { ...values };
      
      if (unref(isUpdate)) {
        // 编辑
        formData.id = rowId.value;
        await editDevice(formData);
        createMessage.success('修改成功');
      } else {
        // 新增
        await addDevice(formData);
        createMessage.success('新增成功');
      }

      closeDrawer();
      emit('success');
    } catch (error: any) {
      createMessage.error(error.message || '操作失败');
    } finally {
      setDrawerProps({ confirmLoading: false });
    }
  }
</script> 