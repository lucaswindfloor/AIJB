import { h } from 'vue';
import { BasicColumn, FormSchema } from '/@/components/Table';
import { useMessage } from '/@/hooks/web/useMessage';
import { useCopyToClipboard, copyTextToClipboard } from '/@/hooks/web/useCopyToClipboard';
import { Tag } from 'ant-design-vue';

const { createMessage } = useMessage();

// Table columns
export const columns: BasicColumn[] = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 180,
    customRender: ({ text }) => {
      if (!text) return '';
      // Custom rendering for click-to-copy
      return h(
        'a',
        {
          onClick: () => {
            if (copyTextToClipboard(text)) {
              createMessage.success('ID已复制到剪贴板！');
            }
          },
        },
        text
      );
    },
  },
  {
    title: '名称',
    dataIndex: 'name',
    key: 'name',
    width: 150,
  },
  {
    title: '地址',
    dataIndex: 'address',
    key: 'address',
    width: 200,
  },
  {
    title: '手机号',
    dataIndex: 'phone',
    key: 'phone',
    width: 120,
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 80,
  },
  {
    title: '座机号',
    dataIndex: 'landline',
    key: 'landline',
    width: 120,
  },
  {
    title: '计费模式',
    dataIndex: 'billingModel',
    key: 'billingModel',
    width: 120,
  },
  {
    title: '车满限制',
    dataIndex: 'limitParkingSpaceNumber',
    key: 'limitParkingSpaceNumber',
    width: 80,
    customRender: ({ text }) => {
      const color = text === 1 ? 'green' : 'red';
      const displayText = text === 1 ? '是' : '否';
      return h(Tag, { color: color }, () => displayText);
    },
  },
  {
    title: '停车场图片',
    dataIndex: 'imageUrl',
    key: 'imageUrl',
    width: 100,
    component: 'ImagePreview',
  },
];

// Query form schema
export const searchFormSchema: FormSchema[] = [
  {
    field: 'name',
    label: '停车场名称',
    component: 'Input',
    colProps: { span: 5 },
  },
  {
    field: 'phone',
    label: '手机号',
    component: 'Input',
    colProps: { span: 5 },
  },
  {
    field: 'status',
    label: '状态',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'car_park_status',
      placeholder: '请选择状态',
    },
    colProps: { span: 5 },
  },
  {
    field: 'billingModel',
    label: '计费模式',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'billing_model',
      placeholder: '请选择计费模式',
    },
    colProps: { span: 5 },
  },
];

// Add/Edit form schema
export const formSchema: FormSchema[] = [
  {
    field: 'id',
    label: 'ID',
    component: 'Input',
    show: false,
  },
  {
    field: 'name',
    label: '停车场名称',
    component: 'Input',
    required: true,
  },
  {
    field: 'address',
    label: '地址',
    component: 'Input',
    required: true,
  },
  {
    field: 'phone',
    label: '手机号',
    component: 'Input',
    required: true,
  },
  {
    field: 'landline',
    label: '座机号',
    component: 'Input',
  },
  {
    field: 'status',
    label: '状态',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'car_park_status',
    },
    required: true,
  },
  {
    field: 'billingModel',
    label: '计费模式',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'billing_model',
    },
    required: true,
  },
  {
    field: 'limitParkingSpaceNumber',
    label: '车满限制',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'yn',
    },
    required: true,
  },
  {
    field: 'imageUrl',
    label: '停车场图片',
    component: 'JImageUpload',
  },
]; 