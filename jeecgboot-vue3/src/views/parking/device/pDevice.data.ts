import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import { rules } from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { h } from 'vue';

/**
 * 列表页面列配置 - 完全基于旧系统业务逻辑和新系统p_device表结构
 * 对应旧系统的14个显示列，保持业务逻辑一致
 */
export const columns: BasicColumn[] = [
  {
    title: '序列号',
    dataIndex: 'serialNumber', // 新系统字段：serial_number，对应旧系统 bar_code
    fixed: 'left',
    width: 120,
  },
  {
    title: '设备编号', 
    dataIndex: 'deviceNo', // 新系统字段：device_no，对应旧系统 device_no
    width: 100,
  },
  {
    title: '设备名称',
    dataIndex: 'name', // 新系统字段：name，对应旧系统 device_name
    width: 120,
  },
  {
    title: 'IP地址',
    dataIndex: 'ipAddress', // 新系统字段：ip_address，对应旧系统 ip
    width: 120,
  },
  {
    title: 'MAC地址',
    dataIndex: 'macAddress', // 新系统字段：mac_address，对应旧系统 mac_address  
    width: 140,
  },
  {
    title: '出入口名称',
    dataIndex: 'checkpointName', // 关联查询p_checkpoint表获取name字段
    width: 120,
  },
  {
    title: '停车场名称',
    dataIndex: 'parkingLotName', // 通过checkpoint关联查询p_parking_lot表获取name字段
    width: 150,
  },
  {
    title: '登录账号',
    dataIndex: 'username', // 新系统字段：username，对应旧系统 username
    width: 100,
  },
  {
    title: '登录密码',
    dataIndex: 'password', // 新系统字段：password，对应旧系统 password
    width: 100,
    customRender: ({ text }) => {
      return text ? '******' : '';
    },
  },
  {
    title: '设备品牌',
    dataIndex: 'brand', // 新系统字段：brand，对应旧系统 brand
    width: 80,
    customRender: ({ text }) => {
      // 保持旧系统的品牌映射逻辑
      const brandMap = {
        'zhen_shi': '臻识',
        'hua_xia': '华夏', 
        'qian_yi': '千熠',
        'hk': '海康',
        'dh': '大华'
      };
      return brandMap[text] || text;
    },
  },
  {
    title: '设备类型',
    dataIndex: 'deviceType', // 新系统字段：device_type，对应旧系统 type
    width: 80,
    customRender: ({ text }) => {
      // 适配新系统的设备类型枚举值
      const typeMap = { 
        'CAMERA': '相机', 
        'GATE': '道闸',
        'DISPLAY': '显示屏'
      };
      return typeMap[text] || text;
    },
  },
  {
    title: '视频地址',
    dataIndex: 'streamUrl', // 新系统字段：stream_url，对应旧系统 video_address
    width: 150,
  },
  {
    title: '最后上线时间',
    dataIndex: 'lastOnlineTime', // 新系统字段：last_online_time，对应旧系统 last_online_time
    width: 150,
  },
  {
    title: '设备状态',
    dataIndex: 'status', // 新系统字段：status，对应旧系统 status
    width: 80,
    customRender: ({ text }) => {
      // 适配新系统的状态枚举值
      const statusMap = { 
        'OFFLINE': '离线', 
        'ONLINE': '在线',
        'FAULTY': '故障',
        'BUSY': '占用'
      };
      return statusMap[text] || text;
    },
  },
];

/**
 * 查询条件 - 基于旧系统完整查询功能和新系统表结构
 * 保持旧系统所有查询条件的完整性
 */
export const searchFormSchema: FormSchema[] = [
  {
    label: '停车场',
    field: 'parkingLotIdEQ', // 通过checkpoint关联查询
    component: 'JSearchSelect',
    componentProps: {
      dict: 'p_parking_lot,name,id',
      async: true,
    },
    colProps: { span: 6 },
  },
  {
    label: '设备序列号',
    field: 'serialNumberEQ', // 新系统字段：serial_number
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    label: '设备型号',
    field: 'modelEQ', // 新系统字段：model  
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    label: '关卡',
    field: 'checkpointIdEQ', // 新系统字段：checkpoint_id
    component: 'JSearchSelect',
    componentProps: {
      dict: 'p_checkpoint,name,id',
      async: true,
    },
    colProps: { span: 6 },
  },
  {
    label: '设备状态',
    field: 'statusEQ', // 新系统字段：status
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'device_status',
    },
    colProps: { span: 6 },
  },
  {
    label: '设备类型',
    field: 'deviceTypeEQ', // 新系统字段：device_type
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'device_type',
    },
    colProps: { span: 6 },
  },
  {
    label: '设备品牌',
    field: 'brandEQ', // 新系统字段：brand
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'device_brand',
    },
    colProps: { span: 6 },
  },
  {
    label: '最后上线时间',
    field: 'lastOnlineTimeGT', // 新系统字段：last_online_time
    component: 'DatePicker',
    componentProps: {
      showTime: true,
      format: 'YYYY-MM-DD HH:mm:ss',
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
    },
    colProps: { span: 6 },
  },
  {
    label: '最后上线时间止',
    field: 'lastOnlineTimeLT', // 新系统字段：last_online_time
    component: 'DatePicker',
    componentProps: {
      showTime: true,
      format: 'YYYY-MM-DD HH:mm:ss',
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
    },
    colProps: { span: 6 },
  },
];

/**
 * 表单页面配置 - 基于新系统表结构，保持旧系统表单功能完整性
 * 包含所有必要的设备信息字段
 */
export const formSchema: FormSchema[] = [
  {
    label: '',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    label: '关卡',
    field: 'checkpointId', // 新系统字段：checkpoint_id
    component: 'JSearchSelect',
    componentProps: {
      dict: 'p_checkpoint,name,id',
      async: true,
    },
    dynamicRules: ({ model, schema }) => rules.duplicateCheckRule('p_device', 'checkpointId', model, schema, true),
  },
  {
    label: '设备名称',
    field: 'name', // 新系统字段：name
    component: 'Input',
    dynamicRules: ({ model, schema }) => rules.duplicateCheckRule('p_device', 'name', model, schema, true),
  },
  {
    label: '设备编号',
    field: 'deviceNo', // 新系统字段：device_no
    component: 'Input',
  },
  {
    label: '设备序列号',
    field: 'serialNumber', // 新系统字段：serial_number
    component: 'Input',
    dynamicRules: ({ model, schema }) => rules.duplicateCheckRule('p_device', 'serialNumber', model, schema, true),
  },
  {
    label: '设备类型',
    field: 'deviceType', // 新系统字段：device_type
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'device_type',
    },
    dynamicRules: ({ model, schema }) => rules.duplicateCheckRule('p_device', 'deviceType', model, schema, true),
  },
  {
    label: '设备品牌',
    field: 'brand', // 新系统字段：brand
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'device_brand',
    },
  },
  {
    label: '设备型号',
    field: 'model', // 新系统字段：model
    component: 'Input',
  },
  {
    label: 'IP地址',
    field: 'ipAddress', // 新系统字段：ip_address
    component: 'Input',
    dynamicRules: ({ model, schema }) => rules.duplicateCheckRule('p_device', 'ipAddress', model, schema, true),
  },
  {
    label: 'MAC地址',
    field: 'macAddress', // 新系统字段：mac_address
    component: 'Input',
  },
  {
    label: '登录用户名',
    field: 'username', // 新系统字段：username
    component: 'Input',
  },
  {
    label: '登录密码',
    field: 'password', // 新系统字段：password
    component: 'InputPassword',
  },
  {
    label: '视频流地址',
    field: 'streamUrl', // 新系统字段：stream_url
    component: 'Input',
  },
  {
    label: '设备状态',
    field: 'status', // 新系统字段：status
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'device_status',
    },
  },
  {
    label: '最后上线时间',
    field: 'lastOnlineTime', // 新系统字段：last_online_time
    component: 'DatePicker',
    componentProps: {
      showTime: true,
      format: 'YYYY-MM-DD HH:mm:ss',
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
    },
  },
];

/**
 * 数据字典映射 - 用于数据处理逻辑
 * 保持与旧系统完全一致的业务逻辑
 */
export const DEVICE_BRAND_MAP = {
  'zhen_shi': '臻识',
  'hua_xia': '华夏', 
  'qian_yi': '千熠',
  'hk': '海康',
  'dh': '大华'
};

export const DEVICE_TYPE_MAP = { 
  'CAMERA': '相机', 
  'GATE': '道闸',
  'DISPLAY': '显示屏'
};

export const DEVICE_STATUS_MAP = { 
  'OFFLINE': '离线', 
  'ONLINE': '在线',
  'FAULTY': '故障',
  'BUSY': '占用'
};

export const DEVICE_DIRECTION_MAP = {
  'IN': '进场',
  'OUT': '出场',
  'BOTH': '双向'
};