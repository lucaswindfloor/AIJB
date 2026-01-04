<template>
  <div>
    <!-- 使用JeecgBoot的BasicTable组件，它封装了所有列表页的基础功能 -->
    <BasicTable @register="registerTable">
      <!-- 操作列 -->
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <TableAction :actions="getTableAction(record)" />
        </template>
      </template>
    </BasicTable>

    <!-- TODO: 新增/编辑的弹窗或抽屉组件 -->
    <!-- <CapsuleModal @register="registerModal" @success="handleSuccess" /> -->
  </div>
</template>

<script setup lang="ts" name="animal_husbandry-rumen_capsule">
  import { reactive } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { getList, getLatestTelemetry } from './capsule.api';
  import { columns, searchFormSchema, RumenCapsule } from './capsule.data';
  // import { useModal } from '/@/components/Modal';

  // 定义组件名称，必须与后端菜单路径一致，用于权限匹配
  defineOptions({ name: 'animal_husbandry/rumen_capsule' });
  
  // const [registerModal, { openModal }] = useModal();

  const [registerTable, { reload, getDataSource, setTableData, setLoading }] = useTable({
    title: '瘤胃胶囊列表',
    api: getList,
    columns,
    formConfig: {
      labelWidth: 120,
      schemas: searchFormSchema,
    },
    useSearchForm: true,
    showTableSetting: true,
    bordered: true,
    actionColumn: {
      width: 120,
      title: '操作',
      dataIndex: 'action',
      fixed: 'right',
    },
    // 核心逻辑：在表格数据加载完成后，触发遥测数据获取
    afterFetch: async (data: RumenCapsule[]) => {
      if (data && data.length > 0) {
        setLoading(true);
        try {
          await fetchTelemetryForAll(data);
        } finally {
          setLoading(false);
        }
      }
    },
  });

  /**
   * 异步为列表中的每一行数据获取其遥测信息
   */
  async function fetchTelemetryForAll(tableData: RumenCapsule[]) {
    const promises = tableData.map(async (record) => {
      // 为遥测数据设置初始加载状态
      setInitialTelemetryState(record);

      if (record.tbDeviceId) {
        try {
          const res = await getLatestTelemetry(record.tbDeviceId);
          // 将获取到的遥测数据合并到当前行记录中
          if (res) {
            Object.assign(record, res);
            // 修正：手动将API返回的loRaSNR赋值给组件使用的snr字段
            if (res.loRaSNR !== undefined) {
              record.snr = res.loRaSNR;
            }
          } else {
            setTelemetryErrorState(record);
          }
        } catch (error) {
          console.error(`获取设备 ${record.capsuleName} 的遥测数据失败`, error);
          setTelemetryErrorState(record);
        }
      } else {
         setTelemetryErrorState(record, '无ID');
      }
    });

    await Promise.all(promises);
    // 使用 setTableData 刷新整个表格视图，确保合并后的数据能被渲染出来
    setTableData(tableData);
  }
  
  function setInitialTelemetryState(record: RumenCapsule){
    record.temperature = '加载中...';
    record.gastricMomentum = '加载中...';
  }

  function setTelemetryErrorState(record: RumenCapsule, text = '获取失败') {
    record.temperature = text;
    record.gastricMomentum = text;
    record.rssi = text;
    record.snr = text;
  }

  /**
   * 定义表格操作列
   * @param record
   */
  function getTableAction(record: RumenCapsule) {
    return [
      {
        label: '编辑',
        // onClick: handleEdit.bind(null, record),
      },
      {
        label: '删除',
        color: 'error',
        popConfirm: {
          title: '是否确认删除',
          // onConfirm: handleDelete.bind(null, record),
        },
      },
    ];
  }

</script> 