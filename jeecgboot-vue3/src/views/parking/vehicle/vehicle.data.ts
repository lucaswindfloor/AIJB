import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import { render } from '/@/utils/common/renderUtils';

// 表格列定义
export const columns: BasicColumn[] = [
  {
    title: '车牌号',
    dataIndex: 'licensePlate',
    width: 120,
  },
  {
    title: '车主姓名',
    dataIndex: 'ownerName',
    width: 100,
  },
    {
    title: '车辆品牌',
    dataIndex: 'brand',
    width: 120,
  },
  {
    title: '车辆型号',
    dataIndex: 'model',
    width: 120,
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 80,
    customRender: ({ text }) => {
      // Assuming a data dictionary 'vehicle_status' is created with items: {label: '正常', value: '2'}, {label: '审核中', value: '1'}, {label: '禁用', value: '0'}
      return render.renderDict(text, 'vehicle_status');
    },
  },
  {
    title: '所属套餐',
    dataIndex: 'group_id', // TBD: This may need to show a name based on the ID
    width: 150,
  },
   {
    title: '剩余次数',
    dataIndex: 'packageCounts',
    width: 100,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 180,
  },
];

// 搜索表单定义
export const searchFormSchema: FormSchema[] = [
  {
    field: 'licensePlate',
    label: '车牌号',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'vehicleType',
    label: '车辆类型',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'vehicle_type', // Assume a data dictionary is created for this
    },
    colProps: { span: 6 },
  },
  {
    field: 'status',
    label: '状态',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'vehicle_status',
    },
    colProps: { span: 6 },
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
    field: 'd1',
    component: 'Divider',
    colProps: {
        span: 24,
    },
  },
  {
    field: 'licensePlate',
    label: '车牌号',
    component: 'Input',
    required: true,
    colProps: { span: 8 },
  },
  {
    field: 'ownerName',
    label: '车主姓名',
    component: 'Input',
    colProps: { span: 8 },
  },
  {
    field: 'ownerPhone',
    label: '车主手机号',
    component: 'Input',
    colProps: { span: 8 },
  },
    {
    field: 'brand',
    label: '车辆品牌',
    component: 'Input',
    colProps: { span: 8 },
  },
  {
    field: 'model',
    label: '车辆型号',
    component: 'Input',
    colProps: { span: 8 },
  },
  {
    field: 'status',
    label: '状态',
    component: 'JDictSelectTag',
    defaultValue: '2',
    componentProps: {
      dictCode: 'vehicle_status',
    },
     colProps: { span: 8 },
  },
  {
    label: '资产与认证信息',
    field: 'd2',
    component: 'Divider',
    colProps: {
        span: 24,
    },
  },
  {
    field: 'purchaseTime',
    label: '购买时间',
    component: 'DatePicker',
    colProps: { span: 8 },
  },
  {
    field: 'price',
    label: '购买价格 (元)',
    component: 'InputNumber',
    colProps: { span: 8 },
  },
  {
    field: 'identityNumber',
    label: '证件号',
    component: 'Input',
    colProps: { span: 8 },
  },
    {
    field: 'identityImage',
    label: '证件照上传',
    component: 'JImageUpload',
    colProps: { span: 12 },
  },
  {
    field: 'image',
    label: '车辆照片上传',
    component: 'JImageUpload',
    colProps: { span: 12 },
  },
  {
    label: '套餐与有效期',
    field: 'd3',
    component: 'Divider',
    colProps: {
        span: 24,
    },
  },
  {
    field: 'vehicleType',
    label: '车辆类型',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'vehicle_type',
    },
    colProps: { span: 8 },
  },
  {
    field: 'vehicleTypeStartTime',
    label: '类型有效期 (起)',
    component: 'DatePicker',
    componentProps: {
        showTime: true,
        format: 'YYYY-MM-DD HH:mm:ss',
    },
    colProps: { span: 8 },
  },
    {
    field: 'vehicleTypeEndTime',
    label: '类型有效期 (止)',
    component: 'DatePicker',
     componentProps: {
        showTime: true,
        format: 'YYYY-MM-DD HH:mm:ss',
    },
    colProps: { span: 8 },
  },
  {
    field: 'groupId',
    label: '所属套餐/订阅组',
    component: 'Select', // This could be a remote search select, e.g., JSearchSelect
    colProps: { span: 8 },
  },
  {
    field: 'packageCounts',
    label: '套餐剩余次数',
    component: 'InputNumber',
    colProps: { span: 8 },
  },
]; 