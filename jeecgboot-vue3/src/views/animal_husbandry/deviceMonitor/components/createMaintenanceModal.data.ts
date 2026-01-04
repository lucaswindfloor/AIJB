import { FormSchema } from '/@/components/Form';

export const formSchema: FormSchema[] = [
  {
    field: 'taskType',
    label: '任务类型',
    component: 'Select',
    required: true,
    componentProps: {
      options: [
        { label: '更换电池', value: 'battery_replacement' },
        { label: '固件升级', value: 'firmware_upgrade' },
        { label: '设备检修', value: 'device_repair' },
        { label: '其他', value: 'other' },
      ],
    },
    defaultValue: 'battery_replacement',
  },
  {
    field: 'assigneeId',
    label: '指派给',
    component: 'ApiSelect',
    componentProps: {
      // TODO: 对接真实的用户选择API
      api: () => new Promise(resolve => {
        resolve([
            { label: '张三 (技术员)', value: 'zhangsan' },
            { label: '李四 (技术员)', value: 'lisi' },
        ])
      }),
      labelField: 'label',
      valueField: 'value',
    },
  },
  {
    field: 'notes',
    label: '备注说明',
    component: 'InputTextArea',
    componentProps: {
      placeholder: '（选填）详细描述问题或操作要求',
      rows: 4,
    },
  },
]; 