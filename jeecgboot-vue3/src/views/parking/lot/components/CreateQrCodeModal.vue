<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="getTitle" :show-cancel-btn="false" :show-ok-btn="false">
    <div class="text-center p-4">
        <QrCode :value="qrCodeUrl" :width="200" />
        <div class="mt-2 text-lg">{{ qrCodeText }}</div>
    </div>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { QrCode } from '/@/components/Qrcode';

  const modalData = ref<any>(null);

  const [registerModal] = useModalInner(async (data) => {
    modalData.value = data;
  });

  const getTitle = computed(() => {
    switch (modalData.value?.type) {
      case 'in-situ': return '场内码';
      case 'applets': return '小程序码';
      case 'entry': return '入口码';
      case 'payment': return '付款码';
      default: return '生成二维码';
    }
  });

  const qrCodeUrl = computed(() => {
    // 模拟URL，实际应从后端API获取
    const recordId = modalData.value?.record?.id || 'park123';
    return `https://www.jeecg.com/parking?type=${modalData.value?.type}&id=${recordId}`;
  });

  const qrCodeText = computed(() => {
    return `请使用微信扫描上方二维码`;
  });

</script> 