import { BasicColumn, FormSchema } from '/@/components/Table';
import { useGlobSetting } from '/@/hooks/setting';
import { getParkingLotList } from './lockDevice.api';

const globSetting = useGlobSetting();

// 表格列定义
export const columns = [
  {
    title: '设备编号',
    dataIndex: 'deviceNo',
    key: 'deviceNo',
    width: 150,
  },
  {
    title: '设备名称',
    dataIndex: 'deviceName',
    key: 'deviceName',
    width: 180,
  },
  {
    title: '所属停车场',
    dataIndex: 'parkingLotName',
    key: 'parkingLotName',
    width: 180,
  },
  {
    title: '绑定车位',
    dataIndex: 'parkingSpaceNo',
    key: 'parkingSpaceNo',
    width: 120,
  },
  {
    title: '锁状态',
    dataIndex: 'lockStatus',
    key: 'lockStatus',
    width: 80,
    customRender: ({ text }) => {
      return text === 1 ? '升起' : '降下';
    },
    filters: [
        { text: '升起', value: '1' },
        { text: '降下', value: '0' },
    ],
  },
  {
    title: '占用状态',
    dataIndex: 'isOccupied',
    key: 'isOccupied',
    width: 80,
    customRender: ({ text }) => {
      return text === 1 ? '占用' : '空闲';
    },
    filters: [
        { text: '占用', value: '1' },
        { text: '空闲', value: '0' },
    ],
  },
  {
    title: '电池电量',
    dataIndex: 'batteryLevel',
    key: 'batteryLevel',
    width: 100,
    customRender: ({ text }) => `${text}%`,
  },
  {
    title: '信号强度',
    dataIndex: 'signalStrength',
    key: 'signalStrength',
    width: 100,
    customRender: ({ text }) => `${text} dBm`,
  },
  {
    title: '设备状态',
    dataIndex: 'status',
    key: 'status',
    width: 100,
    customRender: ({ text }) => {
        if(text === '1') return '正常';
        if(text === '2') return '故障';
        return '停用';
    },
    filters: [
        { text: '正常', value: '1' },
        { text: '故障', value: '2' },
        { text: '停用', value: '0' },
    ],
  },
  {
    title: '最后心跳',
    dataIndex: 'lastHeartbeatTime',
    key: 'lastHeartbeatTime',
    width: 180,
  },
];

// 查询表单
export const searchFormSchema: FormSchema[] = [
  {
    field: 'deviceNo',
    label: '设备编号',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'parkingLotId',
    label: '所属停车场',
    component: 'ApiSelect',
    componentProps: {
      api: getParkingLotList,
      labelField: 'name',
      valueField: 'id',
    },
    colProps: { span: 6 },
  },
  {
    field: 'status',
    label: '设备状态',
    component: 'Select',
    componentProps: {
      options: [
        { label: '正常', value: '1' },
        { label: '故障', value: '2' },
        { label: '停用', value: '0' },
      ],
    },
    colProps: { span: 6 },
  },
];

// 新增/编辑表单
export const formSchema: FormSchema[] = [
  {
    field: 'id',
    label: 'ID',
    component: 'Input',
    show: false,
  },
  {
    field: 'deviceName',
    label: '设备名称',
    component: 'Input',
    required: true,
  },
  {
    field: 'deviceNo',
    label: '设备编号',
    component: 'Input',
    required: true,
  },
  {
    label: '所属停车场',
    field: 'parkingLotId',
    component: 'ApiSelect',
    componentProps: {
      api: getParkingLotList,
      labelField: 'name',
      valueField: 'id',
    },
    required: true,
  },
  {
    label: '绑定车位',
    field: 'parkingSpaceId',
    component: 'Input', // 可替换为根据停车场动态加载车位的ApiSelect
  },
  {
    field: 'divider-lorawan',
    label: 'LoRaWAN信息',
    component: 'Divider',
  },
  {
    field: 'devEui',
    label: 'Device EUI',
    component: 'Input',
    required: true,
    helpMessage: '设备的唯一标识符 (16个十六进制字符)',
  },
  {
    field: 'appEui',
    label: 'Application EUI',
    component: 'Input',
    helpMessage: '应用服务器的唯一标识符 (16个十六进制字符)',
  },
  {
    field: 'appKey',
    label: 'Application Key',
    component: 'Input',
    helpMessage: '用于设备激活的密钥 (32个十六进制字符)',
  },
  {
    field: 'divider-tb',
    label: 'ThingsBoard信息',
    component: 'Divider',
  },
    {
    field: 'tbDeviceId',
    label: 'ThingsBoard设备ID',
    component: 'Input',
    dynamicDisabled: true,
    helpMessage: '设备在ThingsBoard平台创建后自动生成',
  },
]; 