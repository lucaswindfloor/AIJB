<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="getTitle" @ok="handleSubmit" width="1200px" @cancel="handleCancel" :keyboard="false" destroyOnClose>
    <a-row :gutter="16">
      <a-col :span="10">
        <BasicForm @register="registerForm" />
      </a-col>
      <a-col :span="14">
        <div ref="mapContainer" style="height: 500px; width: 100%;"></div>
        <div class="mt-2">
          <a-button @click="startDraw" type="primary">开始绘制</a-button>
          <a-button @click="clearDraw(true)" class="ml-2" danger>清除当前围栏</a-button>
        </div>
        <div class="text-secondary mt-2">
          操作提示：点击“开始绘制”后，在地图上单击绘制多边形，双击或单击第一个点结束绘制。
        </div>
      </a-col>
    </a-row>
  </BasicModal>
</template>
<script lang="ts" setup>
  import { ref, computed, unref, nextTick } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { formSchema } from '../fence.data';
  import { saveOrUpdate } from '../fence.api';
  import { useScript } from '/@/hooks/web/useScript';
  import { Row as ARow, Col as ACol, Button as AButton } from 'ant-design-vue';

  const A_MAP_URL = 'https://webapi.amap.com/maps?v=2.0&key=06313eb9c6563b674a8fd789db0692c3&plugin=AMap.MouseTool';

  const emit = defineEmits(['success', 'register']);

  const isUpdate = ref(true);
  const mapContainer = ref<HTMLDivElement | null>(null);
  let mapInstance: any = null;
  let mouseTool: any = null;
  let currentPolygon: any = null;
  
  const { toPromise } = useScript({ src: A_MAP_URL });

  const [registerForm, { setFieldsValue, resetFields, validate }] = useForm({
    labelWidth: 100,
    schemas: formSchema,
    showActionButtonGroup: false,
    baseColProps: { span: 24 },
  });

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    resetFields();
    setModalProps({ confirmLoading: false });
    isUpdate.value = !!data?.isUpdate;
    
    await nextTick();
    await initMap();

    if (unref(isUpdate) && data.record) {
      const record = { ...data.record };
      // JDictSelectTag (Radio类型) 需要字符串来匹配value，因此在这里进行强制转换
      if (record.status !== undefined && record.status !== null) {
        record.status = String(record.status);
      }
      setFieldsValue(record);
      
      if (record.points) {
        drawSavedFence(record.points);
      }
    }
  });

  const getTitle = computed(() => (unref(isUpdate) ? '编辑电子围栏' : '新增电子围栏'));

  async function initMap() {
    await toPromise();
    if (!mapContainer.value) return;

    const AMap = (window as any).AMap;
    mapInstance = new AMap.Map(mapContainer.value, {
      zoom: 13,
      center: [112.85126, 28.246939], // 默认中心点
      viewMode: '2D',
    });
    mouseTool = new AMap.MouseTool(mapInstance);

    mouseTool.on('draw', (event) => {
      clearDraw(false); // 先清除上一个, 但不清除表单数据
      currentPolygon = event.obj;
      const path = event.obj.getPath().map(p => [p.lng, p.lat]);
      setFieldsValue({ points: JSON.stringify(path) });
      mouseTool.close();
    });
  }
  
  function handleCancel() {
    if(mapInstance) {
      mapInstance.destroy();
      mapInstance = null;
      currentPolygon = null;
      mouseTool = null;
    }
  }

  function startDraw() {
    if (mouseTool) {
      clearDraw(false);
      mouseTool.polygon({
        strokeColor: "#FF33FF",
        strokeWeight: 6,
        strokeOpacity: 0.2,
        fillOpacity: 0.4,
        fillColor: '#1791fc',
        zIndex: 50,
      });
    }
  }

  function clearDraw(clearFormData: boolean) {
    if (currentPolygon) {
      mapInstance.remove(currentPolygon);
      currentPolygon = null;
    }
    if (clearFormData) {
      setFieldsValue({ points: undefined });
    }
  }

  function drawSavedFence(pointsStr: string) {
    clearDraw(false);
    if (!pointsStr) {
      return;
    }
    try {
      const path = JSON.parse(pointsStr);
      if (path && path.length > 0) {
        const AMap = (window as any).AMap;
        currentPolygon = new AMap.Polygon({
          path: path,
          strokeColor: "#FF33FF",
          strokeWeight: 6,
          strokeOpacity: 0.2,
          fillOpacity: 0.4,
          fillColor: '#1791fc',
          zIndex: 50,
        });
        mapInstance.add(currentPolygon);
        mapInstance.setFitView([currentPolygon]);
      }
    } catch (e) {
      console.error("解析并回显围栏坐标失败", e);
    }
  }

  async function handleSubmit() {
    try {
      const values = await validate();
      setModalProps({ confirmLoading: true });
      await saveOrUpdate(values, isUpdate.value);
      closeModal();
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>
