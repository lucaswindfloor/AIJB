<template>
  <BasicModal v-bind="$attrs" @register="registerModal" title="价格测算工具" @ok="handleCalculate" :ok-text="'开始计算'">
    <a-form :model="formState" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
      <a-form-item label="车辆类型">
        <a-select v-model:value="formState.carType" placeholder="请选择车辆类型">
          <a-select-option value="temp_car">临时车</a-select-option>
          <a-select-option value="monthly_car">月卡车</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="入场时间">
        <a-date-picker show-time v-model:value="formState.entryTime" style="width: 100%;"/>
      </a-form-item>
      <a-form-item label="出场时间">
        <a-date-picker show-time v-model:value="formState.exitTime" style="width: 100%;"/>
      </a-form-item>
      <a-divider />
      <div class="text-center text-red-500 text-xl">
        应付金额: {{ calculatedPrice }} 元
      </div>
    </a-form>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { reactive, ref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { Form as AForm, FormItem as AFormItem, Select as ASelect, SelectOption as ASelectOption, DatePicker as ADatePicker, Divider as ADivider } from 'ant-design-vue';

  interface FormState {
    carType: string;
    entryTime: any;
    exitTime: any;
  }
  
  const formState = reactive<FormState>({
      carType: 'temp_car',
      entryTime: null,
      exitTime: null,
  });
  const calculatedPrice = ref('0.00');

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async () => {
    formState.entryTime = null;
    formState.exitTime = null;
    calculatedPrice.value = '0.00';
    setModalProps({ confirmLoading: false });
  });
  
  async function handleCalculate() {
    // 模拟计算
    setModalProps({ confirmLoading: true });
    await new Promise(resolve => setTimeout(resolve, 500));
    calculatedPrice.value = '40.00'; // 模拟计算结果
    setModalProps({ confirmLoading: false });
  }
</script> 