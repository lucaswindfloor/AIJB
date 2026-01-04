<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="getTitle" @ok="handleSubmit" width="700px">
    <BasicForm @register="registerForm">
      <!-- 自定义dev_eui输入框，实现“验证并获取”功能 -->
      <template #dev_eui="{ model, field }">
        <a-input-group compact>
          <a-input v-model:value="model[field]" :disabled="isUpdate" placeholder="请输入DevEUI" style="width: calc(100% - 120px)" />
          <a-button type="primary" @click="handleVerify" :loading="verifying" :disabled="isUpdate"> 验证并获取 </a-button>
        </a-input-group>
      </template>
    </BasicForm>
  </BasicModal>
</template>
<script lang="ts" setup>
  import { ref, computed, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form';
  import { deviceFormSchema } from '../device.data';
  import { syncFromThingsboard, editDevice, verifyFromThingsboard } from '../device.api';

  const emit = defineEmits(['success', 'register']);

  const isUpdate = ref(true);
  const verifying = ref(false);

  const [registerForm, { setFieldsValue, updateSchema, resetFields, validate }] = useForm({
    labelWidth: 120,
    baseColProps: { span: 24 },
    schemas: deviceFormSchema,
    showActionButtonGroup: false,
    // 将后端返回的 YYYY-MM-DD 字符串转换为 DatePicker 可识别的格式
    transformDateFunc: (date) => (date ? date.format('YYYY-MM-DD') : ''),
  });

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    resetFields();
    isUpdate.value = !!data?.isUpdate;

    // 根据是否为更新模式，动态显示或隐藏“验证”按钮
    updateSchema([
      {
        field: 'dev_eui',
        slot: !isUpdate.value ? 'dev_eui' : '',
      },
    ]);

    if (unref(isUpdate)) {
      setFieldsValue({
        ...data.record,
      });
    }
    setModalProps({ confirmLoading: false });
  });

  const getTitle = computed(() => (unref(isUpdate) ? '编辑设备' : '从ThingsBoard同步设备'));

  // “验证并获取” 按钮点击事件
  async function handleVerify() {
    try {
      const values = await validate(['devEui']);
      verifying.value = true;
      const tbInfo = await verifyFromThingsboard({ devEui: values.devEui });
      if (tbInfo) {
        // [修正] 增加数据转换层，适配前端表单
        const formattedInfo = {
          name: tbInfo.name, // 设备别名，可使用TB的name作为默认值
          tbDeviceId: tbInfo.id?.id, // ThingsBoard ID
          deviceType: tbInfo.deviceProfileName?.includes('追踪器') ? 'TRACKER' : 'CAPSULE',
          firmwareVersion: tbInfo.softwareId || 'N/A', // 固件版本
          // 其他需要从TB回填的字段...
        };
        // 将获取到的信息回填到表单
        setFieldsValue(formattedInfo);
      }
    } finally {
      verifying.value = false;
    }
  }

  // 提交表单
  async function handleSubmit() {
    try {
      const values = await validate();
      setModalProps({ confirmLoading: true });
      // [修正] 根据状态调用不同的接口
      if (unref(isUpdate)) {
        await editDevice(values);
      } else {
        await syncFromThingsboard(values);
      }
      closeModal();
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script> 