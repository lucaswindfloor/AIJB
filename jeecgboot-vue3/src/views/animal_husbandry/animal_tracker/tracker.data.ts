import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import { h } from 'vue';
import { Tag } from 'ant-design-vue';

// 定义动物追踪器的数据结构，融合了基础信息和遥测信息
export interface AnimalTracker {
  id: string; // tbDeviceId
  name: string; // 设备名称/编号
  type: string; // 设备类型, e.g., '动物追踪器-OC-配置'
  active: boolean; // 是否在线
  customerName: string; // 所属客户
  
  // --- 以下为遥测数据 ---
  battery?: string | number;
  step?: string | number;
  latitude?: string | number;
  longitude?: string | number;
  lastUpdateTs?: number; // 遥测数据时间戳
}


// 表格列定义
export const columns: BasicColumn[] = [
  {
    title: '追踪器名称',
    dataIndex: 'name',
    width: 200,
  },
  {
    title: '设备类型',
    dataIndex: 'type',
    width: 180,
  },
  {
    title: '状态',
    dataIndex: 'active',
    width: 80,
    customRender: ({ record }) => {
      const active = record.active;
      const color = active ? 'green' : 'red';
      const text = active ? '在线' : '离线';
      return h(Tag, { color: color }, () => text);
    },
  },
  {
    title: '电量',
    dataIndex: 'battery',
    width: 100,
     customRender: ({ text }) => {
      return text ? `${text} %` : 'N/A';
    },
  },
  {
    title: '今日计步',
    dataIndex: 'step',
    width: 120,
  },
  {
    title: '最后位置(经,纬)',
    dataIndex: 'location',
    width: 220,
    customRender: ({ record }) => {
      const lat = record.latitude;
      const lon = record.longitude;
      if(lat && lon){
         return `${parseFloat(lon).toFixed(5)}, ${parseFloat(lat).toFixed(5)}`;
      }
      return 'N/A';
    }
  },
  {
      title: '最后上报时间',
      dataIndex: 'lastUpdateTs',
      width: 180,
      customRender:({text}) => {
          if(!text || text === 'N/A'){
              return 'N/A';
          }
          return new Date(text).toLocaleString();
      }
  }
];

// 搜索表单定义
export const searchFormSchema: FormSchema[] = [
  {
    field: 'name',
    label: '追踪器名称',
    component: 'Input',
    colProps: { span: 8 },
  },
  {
    field: 'active',
    label: '在线状态',
    component: 'Select',
    componentProps: {
      options: [
        { label: '在线', value: true },
        { label: '离线', value: false },
      ],
    },
    colProps: { span: 8 },
  },
]; 