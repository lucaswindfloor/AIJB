import { BasicColumn, FormSchema } from '/@/components/Table';
import { h } from 'vue';
import { Tag } from 'ant-design-vue';

/**
 * @description: 设备台账实体 (已根据接口文档修正)
 * 统一使用camelCase命名法
 */
export interface Device {
  id: string; // 本系统主键ID
  name: string; // 设备名称（别名）
  deviceType: string;
  devEui: string;
  tbDeviceId: string; // ThingsBoard设备ID
  model: string;
  purchaseDate: string;
  status: string; // 生命周期状态
  hardwareVersion: string; // 硬件版本
  // 关联的牲畜信息，可以为null
  boundAnimalInfo?: {
    earTagId: string;
    [key: string]: any;
  } | null;
  [key: string]: any;
}

// 数据字典: 设备类型
export const deviceTypeDict = [
  { label: '瘤胃胶囊', value: 'CAPSULE', color: 'blue' },
  { label: '动物追踪器', value: 'TRACKER', color: 'green' },
];

// 数据字典: 设备生命周期状态
export const deviceLifecycleStatusDict = [
  { label: '库存中', value: 'IN_STOCK', color: 'default' },
  { label: '在用', value: 'ACTIVE', color: 'success' },
  { label: '闲置', value: 'IDLE', color: 'processing' },
  { label: '维保中', value: 'MAINTENANCE', color: 'warning' },
  { label: '已报废', value: 'RETIRED', color: 'error' },
];

// 表格列定义
export const columns: BasicColumn[] = [
  {
    title: '设备名称',
    dataIndex: 'name',
    width: 150,
    sorter: true,
  },
  {
    title: '设备类型',
    dataIndex: 'deviceType', // 修正: device_type -> deviceType
    width: 120,
    customRender: ({ text }) => {
      const item = deviceTypeDict.find((i) => i.value === text);
      return h(Tag, { color: item?.color || 'default' }, () => item?.label || '未知');
    },
  },
  {
    title: 'DevEUI',
    dataIndex: 'devEui', // 修正: dev_eui -> devEui
    width: 180,
  },
  {
    title: '生命周期状态',
    dataIndex: 'status',
    width: 120,
    sorter: true,
    customRender: ({ text }) => {
      const item = deviceLifecycleStatusDict.find((i) => i.value === text);
      return h(Tag, { color: item?.color || 'default' }, () => item?.label || '未知');
    },
  },
  {
    title: '绑定牲畜',
    dataIndex: ['boundAnimalInfo', 'earTagId'], // 修正: 保持此精确路径
    width: 120,
    sorter: true,
    customRender: ({ text }) => {
      return text || '—';
    },
  },
  {
    title: '设备型号',
    dataIndex: 'model',
    width: 120,
  },
  {
    title: '采购日期',
    dataIndex: 'purchaseDate', // 修正: purchase_date -> purchaseDate
    width: 120,
    sorter: true,
  },
];

// 搜索表单配置 (已修正field)
export const searchFormSchema: FormSchema[] = [
  {
    field: 'name',
    label: '设备名称',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'devEui', // 修正: dev_eui -> devEui
    label: 'DevEUI',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'deviceType', // 修正: device_type -> deviceType
    label: '设备类型',
    component: 'Select',
    componentProps: {
      options: deviceTypeDict,
    },
    colProps: { span: 6 },
  },
  {
    field: 'status',
    label: '生命周期状态',
    component: 'Select',
    componentProps: {
      options: deviceLifecycleStatusDict,
    },
    colProps: { span: 6 },
  },
];

// “从TB同步/编辑” 表单配置 (已修正field)
export const deviceFormSchema: FormSchema[] = [
  {
    field: 'id',
    label: 'ID',
    component: 'Input',
    show: false,
  },
  {
    field: 'devEui', // 修正: dev_eui -> devEui
    label: 'DevEUI',
    component: 'Input',
    helpMessage: '请输入在ThingsBoard平台已注册的设备DevEUI',
    dynamicRules: ({ values }) => {
      const isUpdate = !!values.id;
      return [
        {
          required: !isUpdate, // 仅在新增时必填
          message: '请输入DevEUI',
        },
      ];
    },
    dynamicDisabled: ({ values }) => !!values.id, // 编辑时不可修改
  },
  {
    field: 'tbDeviceId', // 修正: tb_device_id -> tbDeviceId
    label: 'ThingsBoard ID',
    component: 'Input',
    componentProps: {
      disabled: true,
    },
    ifShow: ({ values }) => !!values.id, // 仅在编辑模式下显示
  },
  {
    field: 'name',
    label: '设备名称 (别名)',
    component: 'Input',
    required: true,
    helpMessage: '为方便管理，在本平台设置的业务名称或别名',
  },
  {
    field: 'deviceType', // 修正: device_type -> deviceType
    label: '设备类型',
    component: 'Select',
    componentProps: {
      options: deviceTypeDict,
    },
    dynamicDisabled: ({ values }) => !!values.id, // 编辑时不可修改
    required: true,
  },
  {
    field: 'status',
    label: '生命周期状态',
    component: 'Select',
    componentProps: {
      options: deviceLifecycleStatusDict,
    },
    ifShow: ({ values }) => !!values.id, // 仅在编辑模式下显示
    required: true,
  },
  {
    field: 'model',
    label: '设备型号',
    component: 'Input',
  },
  {
    field: 'purchaseDate', // 修正: purchase_date -> purchaseDate
    label: '采购日期',
    component: 'DatePicker',
  },
];

// "绑定到牲畜" 表单配置 (已修正field)
export const bindFormSchema: FormSchema[] = [
  {
    field: 'animalId',
    label: '选择牲畜',
    component: 'ApiSelect',
    componentProps: {
      api: () => Promise.resolve([]), // 在使用时由外部API填充
      labelField: 'label',
      valueField: 'value',
      placeholder: '请输入耳标号搜索牲畜',
    },
    required: true,
  },
]; 