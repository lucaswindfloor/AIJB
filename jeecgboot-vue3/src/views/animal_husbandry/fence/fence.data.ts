import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import { render } from '/@/utils/common/renderUtils';

//列表数据
export const columns: BasicColumn[] = [
  {
    title: '围栏名称',
    align: 'center',
    dataIndex: 'name',
  },
  {
    title: '描述',
    align: 'center',
    dataIndex: 'description',
  },
  {
    title: '状态',
    align: 'center',
    dataIndex: 'status_dictText',
  },
  {
    title: '创建时间',
    align: 'center',
    dataIndex: 'createTime',
  },
];

//查询数据
export const searchFormSchema: FormSchema[] = [
  {
    label: '围栏名称',
    field: 'name',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    label: '状态',
    field: 'status',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'fence_status',
      placeholder: '请选择状态',
    },
    colProps: { span: 6 },
  },
];

//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '围栏名称',
    field: 'name',
    component: 'Input',
    required: true,
  },
  {
    label: '描述',
    field: 'description',
    component: 'InputTextArea',
  },
  {
    label: '状态',
    field: 'status',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'fence_status',
      type: 'radio'
    },
    defaultValue: 1,
    required: true,
  },
  {
    label: '围栏坐标',
    field: 'points',
    component: 'InputTextArea',
    helpMessage: ['此项将在后续步骤中通过地图绘制自动生成。'],
    componentProps: {
      placeholder: '该坐标将由地图绘制自动生成',
      disabled: true,
    },
    required: true,
  },
  {
    label: '',
    field: 'id',
    component: 'Input',
    show: false,
  },
];
