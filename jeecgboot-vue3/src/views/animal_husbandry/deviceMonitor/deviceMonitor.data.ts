import { h } from 'vue';
import { BasicColumn } from '/@/components/Table';
import { Tag } from 'ant-design-vue';

export const columns: BasicColumn[] = [
  {
    title: '设备名称',
    dataIndex: 'name',
    width: 150,
  },
  {
    title: '设备类型',
    dataIndex: 'deviceTypeDictText',
    width: 120,
  },
  {
    title: 'DevEUI',
    dataIndex: 'devEui',
    width: 180,
  },
  {
    title: '状态',
    dataIndex: 'statusText',
    width: 100,
    customRender: ({ record }) => {
      const isOffline = record.isOffline;
      const color = isOffline ? 'red' : 'orange';
      const text = record.statusText;
      return h(Tag, { color: color }, () => text);
    },
  },
  {
    title: '电量',
    dataIndex: 'batteryLevel',
    width: 80,
    customRender: ({ text }) => {
      return text ? `${text}%` : '-';
    },
  },
  {
    title: '最后在线时间',
    dataIndex: 'updateTime',
    width: 180,
  },
]; 