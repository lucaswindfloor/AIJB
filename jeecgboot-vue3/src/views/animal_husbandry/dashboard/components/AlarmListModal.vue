
<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="modalTitle" :width="800">
    <BasicTable @register="registerTable" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicTable, useTable, BasicColumn } from '/@/components/Table';
  import { RecentAlarmVo } from '../dashboard.model';

  const modalTitle = ref('');
  const tableData = ref<RecentAlarmVo[]>([]);

  const tableColumns: BasicColumn[] = [
    {
      title: '耳标号',
      dataIndex: 'earTagId',
      width: 150,
    },
    {
      title: '告警内容',
      dataIndex: 'alarmContent',
    },
    {
      title: '告警级别',
      dataIndex: 'alarmLevel',
      width: 100,
      customRender: ({ text }) => text === 'CRITICAL' ? '严重' : '警告',
    },
    {
      title: '告警时间',
      dataIndex: 'alarmTime',
      width: 180,
    },
  ];

  const [registerTable, { setTableData }] = useTable({
    columns: tableColumns,
    dataSource: tableData,
    pagination: {
      pageSize: 10,
    },
    canResize: false,
    showIndexColumn: true,
  });

  const [registerModal] = useModalInner(async (data) => {
    modalTitle.value = `告警列表 - ${data.title}`;
    tableData.value = data.records;
    setTableData(data.records);
  });
</script>
