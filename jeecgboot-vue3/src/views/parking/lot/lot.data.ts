import { BasicColumn, FormSchema } from '/@/components/Table';
import { h } from 'vue';
import { Tag } from 'ant-design-vue';
import { JAreaLinkage } from '/@/components/Form';

/**
 * 查询表单字段
 */
export const searchFormSchema: FormSchema[] = [
  {
    field: 'name',
    label: '停车场关键词',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'contact_phone',
    label: '联系人手机号',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    // 这个字段是自定义的，用于向后端传递组合查询意图
    field: 'queryStatus',
    label: '停车场状态',
    component: 'Select',
    colProps: { span: 6 },
    componentProps: {
      options: [
        { label: '正常运营', value: 'OPERATING' },
        { label: '车位已满', value: 'FULL' },
        { label: '暂停营业', value: 'CLOSED' },
      ],
      placeholder: '请选择停车场状态',
    },
  },
];

/**
 * 表格列定义
 */
export const columns: BasicColumn[] = [
  {
    title: '停车场名称',
    dataIndex: 'name',
    width: 200,
  },
  {
    title: '地址',
    dataIndex: 'address',
    width: 250,
  },
  {
    title: '车位(总/已用)',
    dataIndex: 'parkingSpaces', // 后端需要返回一个组合字段
    width: 120,
    customRender: ({ record }) => {
        return `${record.totalSpaces || 0} / ${record.usedSpaces || 0}`;
    }
  },
  {
    title: '联系电话',
    dataIndex: 'contactPhone',
    width: 120,
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 100,
    customRender: ({ record }) => {
        let color = 'green';
        let text = '正常运营';

        if (record.status === 'CLOSED') {
            color = 'grey';
            text = '暂停营业';
        } else if (record.totalSpaces > 0 && record.usedSpaces >= record.totalSpaces) {
            color = 'red';
            text = '车位已满';
        }
        return h(Tag, { color: color }, () => text);
    }
  },
];

// 新增/编辑表单定义
export const formSchema: FormSchema[] = [
  {
    field: 'id',
    label: 'ID',
    component: 'Input',
    show: false,
  },
  {
    label: '基础信息',
    field: 'divider-basic',
    component: 'Divider',
    colProps: {
      span: 24,
    },
  },
  {
    field: 'name',
    label: '停车场名称',
    component: 'Input',
    required: true,
    colProps: {
        span: 12,
    },
  },
  {
    field: 'image',
    label: '封面图',
    component: 'JImageUpload',
    colProps: {
      span: 12,
    },
  },
  {
    field: 'area',
    label: '省市区',
    component: 'JAreaLinkage',
    required: true,
    colProps: {
        span: 24,
    },
  },
  {
    field: 'address',
    label: '详细地址',
    component: 'Input',
    required: true,
    colProps: {
        span: 24,
    },
  },
  {
    field: 'totalSpaces',
    label: '车位总数',
    component: 'InputNumber',
    required: true,
    colProps: {
        span: 8,
    },
  },
  {
    field: 'contactPerson',
    label: '联系人',
    component: 'Input',
     colProps: {
        span: 8,
    },
  },
  {
    field: 'contactPhone',
    label: '联系电话',
    component: 'Input',
     colProps: {
        span: 8,
    },
  },
  {
    field: 'businessHours',
    label: '营业时间',
    component: 'RangePicker',
    componentProps: {
      valueFormat: 'HH:mm:ss',
      showTime: true,
    },
    colProps: {
        span: 8,
    },
  },
   {
    field: 'status',
    label: '状态',
    component: 'JDictSelectTag',
    defaultValue: '1',
    componentProps: {
      dictCode: 'p_parking_lot_status',
    },
    required: true,
     colProps: {
        span: 8,
    },
  },
  {
    label: '运营配置',
    field: 'divider-config',
    component: 'Divider',
     colProps: {
      span: 24,
    },
  },
  {
    field: 'config.paymentCollectionLotId',
    label: '代收费用停车场',
    component: 'JSearchSelect',
    componentProps: {
        dict: 'p_parking_lot,name,id',
    },
    colProps: { span: 8 },
  },
  {
    field: 'config.freeDuration',
    label: '免费停车时长(分钟)',
    component: 'InputNumber',
    defaultValue: 15,
     colProps: { span: 8 },
  },
  {
    field: 'config.expiredCarFreeDuration',
    label: '月租车过期后免费时长(分钟)',
    component: 'InputNumber',
    defaultValue: 30,
    colProps: { span: 8 },
  },
  {
    field: 'config.specialFeeExpiredCar',
    label: '对过期车启用特殊计费',
    component: 'Switch',
    defaultValue: true,
    colProps: { span: 8 },
  },
  {
    field: 'config.showExpiredCarType',
    label: '显示过期套餐车类型',
    component: 'Switch',
    defaultValue: false,
    colProps: { span: 8 },
  },
   {
    field: 'config.showRemarks',
    label: '显示停车记录备注',
    component: 'Switch',
    defaultValue: false,
    colProps: { span: 8 },
  },
  {
    field: 'config.limitOnFull',
    label: '启用严格车位控制',
    component: 'Switch',
    defaultValue: true,
    colProps: { span: 8 },
  },
  {
    field: 'config.allowEditRecord',
    label: '允许岗亭修改停车记录',
    component: 'Switch',
    defaultValue: false,
    colProps: { span: 8 },
  },
    {
    field: 'config.abnormalAutoRelease',
    label: '异常时自动放行',
    component: 'Switch',
    defaultValue: true,
    colProps: { span: 8 },
  },
  {
    field: 'config.autoIssueWhitelist',
    label: '自动下发白名单',
    component: 'Switch',
    defaultValue: false,
    colProps: { span: 8 },
  },
]; 