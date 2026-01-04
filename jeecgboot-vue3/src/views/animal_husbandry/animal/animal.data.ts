import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import { h } from 'vue';
import { Tag } from 'ant-design-vue';
import { getDictItems } from '/@/api/common/api';
import { getAvailableDevices } from './animal.api';

export const columns: BasicColumn[] = [
  {
    title: '耳标号',
    dataIndex: 'earTagId',
    width: 120,
  },
  {
    title: '牲畜昵称',
    dataIndex: 'name',
    width: 120,
  },
  {
    title: '瘤胃胶囊',
    dataIndex: ['deviceMap', 'CAPSULE', 'name'],
    width: 150,
  },
  {
    title: '动物追踪器',
    dataIndex: ['deviceMap', 'TRACKER', 'name'],
    width: 150,
  },
  {
    title: '牲畜类型',
    dataIndex: 'type_dictText',
    width: 100,
  },
  {
    title: '所属畜群',
    dataIndex: 'herdId_dictText', // Assuming herdId will have dict support
    width: 120,
  },
  {
    title: '健康状态',
    dataIndex: 'healthStatus',
    width: 100,
    customRender: ({ text }) => {
      const color = text === 'HEALTHY' ? 'green' : text === 'SUB_HEALTHY' ? 'warning' : 'error';
      const statusText = text === 'HEALTHY' ? '健康' : text === 'SUB_HEALTHY' ? '亚健康' : '告警';
      return h(Tag, { color: color }, () => statusText);
    },
  },
  {
    title: '健康评分',
    dataIndex: 'healthScore',
    width: 80,
  },
  {
    title: '最新AI结论',
    dataIndex: 'aiConclusion',
    width: 400,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    field: 'earTagId',
    label: '耳标号',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'name',
    label: '牲畜昵称',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'healthStatus',
    label: '健康状态',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'health_status',
    },
    colProps: { span: 6 },
  },
];

export const formSchema: FormSchema[] = [
  {
    field: 'id',
    label: 'ID',
    component: 'Input',
    show: false,
  },
  {
    field: 'earTagId',
    label: '耳标号',
    component: 'Input',
    required: true,
  },
  {
    field: 'name',
    label: '牲畜昵称',
    component: 'Input',
  },
  {
    field: 'type',
    label: '牲畜类型',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'animal_type',
    },
    required: true,
  },
  {
    field: 'herdId',
    label: '所属畜群',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'ah_herd,name,id', // Example for dict table
    },
    required: true,
  },
  {
    field: 'gender',
    label: '性别',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'sex',
    },
  },
  {
    field: 'birthDate',
    label: '出生日期',
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD',
      disabledDate: (current) => {
        // Can not select days after today
        return current && current > Date.now();
      },
    },
  },
  {
    field: 'weightKg',
    label: '体重(KG)',
    component: 'InputNumber',
  },
];

// Schema for binding a device
export const bindDeviceFormSchema: FormSchema[] = [
  {
    field: 'deviceId',
    label: '选择设备',
    component: 'ApiSelect',
    componentProps: {
      api: getAvailableDevices,
      labelField: 'name',
      valueField: 'id',
      params: {
        // We can pass additional params here if needed
      },
      resultField: 'records',
    },
    required: true,
  },
];

// Schema for adding a lifecycle event
export const lifecycleEventFormSchema: FormSchema[] = [
  {
    field: 'event_type',
    label: '事件类型',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'animal_event_type',
    },
    required: true,
  },
  {
    field: 'event_time',
    label: '事件时间',
    component: 'DatePicker',
    componentProps: {
      showTime: true,
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
    },
    required: true,
  },
  {
    field: 'description',
    label: '事件描述',
    component: 'InputTextArea',
  },
]; 