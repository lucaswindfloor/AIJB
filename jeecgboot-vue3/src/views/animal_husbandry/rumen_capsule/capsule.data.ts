import { BasicColumn, FormSchema } from '/@/components/Table';
import { h } from 'vue';
import { Tag } from 'ant-design-vue';
import { formatToDateTime } from '/@/utils/dateUtil';

// 定义实体类型，用于类型提示
export interface RumenCapsule {
  id: string;
  capsuleName: string;
  devEui: string;
  tbDeviceId: string;
  temperature?: number;
  gastricMomentum?: number;
  rssi?: number;
  snr?: number;
  status: string;
  lastHeartbeatTime: string;
  [key: string]: any; // 允许其他遥测数据
}

// 表格列定义
export const columns: BasicColumn[] = [
  {
    title: '胶囊名称',
    dataIndex: 'capsuleName',
    width: 180,
  },
  {
    title: 'DevEUI',
    dataIndex: 'devEui',
    width: 180,
  },
  {
    title: '绑定牲畜ID',
    dataIndex: 'bindAnimalId',
    width: 180,
  },
  {
    title: '最新温度(℃)',
    dataIndex: 'temperature',
    width: 120,
    customRender: ({ text }) => {
        if (typeof text !== 'number') return text;
        const color = text > 40 ? 'red' : 'green';
        return h(Tag, { color }, () => text);
    }
  },
  {
    title: '胃动量',
    dataIndex: 'gastric_momentum', // 注意: 此处key需要与后端返回的完全一致
    width: 120,
  },
  {
    title: '信号强度(RSSI)',
    dataIndex: 'rssi',
    width: 120,
  },
  {
    title: '信噪比(SNR)',
    dataIndex: 'snr',
    width: 120,
  },
  {
    title: '传感器状态',
    dataIndex: 'sensorStatus',
    width: 120,
  },
  {
    title: '最后心跳时间',
    dataIndex: 'lastHeartbeatTime',
    width: 180,
    customRender: ({ text }) => {
      if (!text || text === 'N/A') {
        return 'N/A';
      }
      return formatToDateTime(text);
    },
  },
  {
    title: '设备配置文件',
    dataIndex: 'deviceProfileName',
    width: 180,
  },
];

// 搜索表单配置
export const searchFormSchema: FormSchema[] = [
  {
    field: 'capsuleName',
    label: '胶囊名称',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'devEui',
    label: 'DevEUI',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'status',
    label: '设备状态',
    component: 'Select',
    componentProps: {
      options: [
        { label: '正常', value: 'NORMAL' },
        { label: '故障', value: 'FAULTY' },
        { label: '离线', value: 'OFFLINE' },
        { label: '未激活', value: 'INACTIVE' },
      ],
    },
    colProps: { span: 6 },
  },
]; 