import { BasicColumn, FormSchema } from '/@/components/Table';
import { useGlobSetting } from '/@/hooks/setting';
const globSetting = useGlobSetting();

export const columns: BasicColumn[] = [
  {
    title: '停车场',
    dataIndex: 'parkingLotName',
    width: 180,
  },
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
    title: '车辆类型',
    dataIndex: 'vehicleType_dictText',
    width: 100,
  },
  {
    title: '入场时间',
    dataIndex: 'entryTime',
    width: 160,
  },
  {
    title: '入口关卡',
    dataIndex: 'entryCheckpointName',
    width: 120,
  },
  {
    title: '出场时间',
    dataIndex: 'exitTime',
    width: 160,
  },
  {
    title: '出口关卡',
    dataIndex: 'exitCheckpointName',
    width: 120,
  },
  {
    title: '停车时长',
    dataIndex: 'duration',
    width: 100,
  },
  {
    title: '应收金额(元)',
    dataIndex: 'payableAmount',
    width: 120,
  },
  {
    title: '实收金额(元)',
    dataIndex: 'actualAmount',
    width: 120,
  },
  {
    title: '支付方式',
    dataIndex: 'paymentMethod_dictText',
    width: 100,
  },
  {
    title: '支付时间',
    dataIndex: 'payTime',
    width: 160,
  },
  {
    title: '支付状态',
    dataIndex: 'status_dictText',
    width: 100,
    slots: { customRender: 'status' },
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    label: '车牌号',
    field: 'licensePlate',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    label: '停车场',
    field: 'parkingLotId',
    component: 'JSearchSelect',
    componentProps: {
      dict: 'p_parking_lot,name,id',
    },
    colProps: { span: 6 },
  },
  {
    label: '停车状态',
    field: 'status',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'p_record_status',
      placeholder: '请选择停车状态',
      stringToNumber: true,
    },
    colProps: { span: 6 },
  },
  {
    label: '车主姓名',
    field: 'ownerName',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    label: '车辆类型',
    field: 'vehicleType',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'vehicle_type',
      placeholder: '请选择车辆类型',
    },
    colProps: { span: 6 },
  },
  {
    label: '支付方式',
    field: 'paymentMethod',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'payment_method',
      placeholder: '请选择支付方式',
    },
    colProps: { span: 6 },
  },
  {
    label: '入场时间',
    field: 'entryTime',
    component: 'RangePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
      showTime:true,
    },
    colProps: { span: 8 },
  },
  {
    label: '应付金额',
    field: 'payableAmount',
    component: 'InputGroup',
    colProps: { span: 8 },
    children: [
        {
            field: 'payableAmount_begin',
            component: 'InputNumber',
            componentProps: {
              placeholder: '最小值',
            },
        },
        {
            field: 'payableAmount_end',
            component: 'InputNumber',
            componentProps: {
              placeholder: '最大值',
            },
        },
    ],
  },
  {
    label: '实付金额',
    field: 'actualAmount',
    component: 'InputGroup',
    colProps: { span: 8 },
    children: [
        {
            field: 'actualAmount_begin',
            component: 'InputNumber',
            componentProps: {
              placeholder: '最小值',
            },
        },
        {
            field: 'actualAmount_end',
            component: 'InputNumber',
            componentProps: {
              placeholder: '最大值',
            },
        },
    ],
  },
];

export const formSchema: FormSchema[] = [
  {
    label: '车牌号',
    field: 'licensePlate',
    component: 'Input',
    required: true,
  },
  {
    label: '停车场',
    field: 'parkingLotId',
    component: 'JSearchSelect',
    componentProps: {
      dict: 'p_parking_lot,name,id',
    },
    required: true,
  },
  {
    label: '入场时间',
    field: 'entryTime',
    component: 'DatePicker',
    componentProps:{
      showTime:true,
      valueFormat:'YYYY-MM-DD HH:mm:ss'
    },
    required: true,
  },
  {
    label: '出场时间',
    field: 'exitTime',
    component: 'DatePicker',
    componentProps:{
      showTime:true,
      valueFormat:'YYYY-MM-DD HH:mm:ss'
    }
  },
  {
    label: '入口',
    field: 'entryCheckpointId',
    component: 'JSearchSelect',
    componentProps:{
        dict: 'p_checkpoint,name,id'
    }
  },
  {
    label: '出口',
    field: 'exitCheckpointId',
    component: 'JSearchSelect',
    componentProps:{
        dict: 'p_checkpoint,name,id'
    }
  },
  {
    label: '备注',
    field: 'remarks',
    component: 'InputTextArea',
  },
]; 