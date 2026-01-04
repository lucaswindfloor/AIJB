<template>
  <div>
    <!-- Table area -->
    <BasicTable @register="registerTable">
      <!-- Action bar buttons -->
      <template #tableTitle>
        <a-button type="primary" @click="handleCreate">新建</a-button>
        <JUploadButton @handleSuccess="handleSuccess" biz-path="parkinglot">批量导入</JUploadButton>
        <a-button @click="handlePriceCalculation">价格测算</a-button>
        <a-button @click="handleHardwareList">硬件列表</a-button>
        <a-button @click="handleExportXls('停车场数据')">下载</a-button>
      </template>
      <!-- Action column -->
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <TableAction
            :actions="[
              {
                label: '操作',
                onClick: handleActions.bind(null, record),
              },
            ]"
          />
        </template>
      </template>
    </BasicTable>
    <!-- Add/Edit Modal -->
    <CarParkModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" setup>
  import { defineComponent } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useModal } from '/@/components/Modal';
  import { Button as AButton } from 'ant-design-vue';
  import JUploadButton from '/@/components/Button/src/JUploadButton.vue';

  import CarParkModal from './components/CarParkModal.vue';
  import { columns, searchFormSchema } from './CarPark.data';
  import { getList, getExportUrl, getImportUrl } from './CarPark.api';
  import { useMessage } from '/@/hooks/web/useMessage';

  // 使用 defineComponent 兼容旧版 setup 写法
  defineComponent({
    name: 'CarParkList',
  });

  const { createMessage } = useMessage();
  const [registerModal, { openModal }] = useModal();

  const { tableContext } = useListPage({
    tableProps: {
      title: '停车场列表',
      api: getList,
      columns: columns,
      formConfig: {
        labelWidth: 100,
        schemas: searchFormSchema,
        autoSubmitOnEnter: true,
        showAdvancedButton: false,
      },
      useSearchForm: true,
      actionColumn: {
        width: 100,
        title: '操作',
        dataIndex: 'action',
        slots: { customRender: 'action' },
      },
      rowKey: 'id',
      showTableSetting: true,
      bordered: true,
    },
    exportConfig: {
      name: '停车场数据',
      url: getExportUrl,
    },
    importConfig: {
      url: getImportUrl,
      success: handleSuccess,
    },
  });

  const [registerTable, { reload, handleExportXls }] = tableContext;

  function handleCreate() {
    openModal(true, { isUpdate: false });
  }

  function handleSuccess() {
    reload();
  }

  // Placeholder functions for new buttons
  function handlePriceCalculation() {
    createMessage.info('此功能待实现');
  }

  function handleHardwareList() {
    createMessage.info('此功能待实现');
  }

  function handleActions(record: Recordable) {
    // TODO: 打开包含所有操作项的弹窗
    console.log('点击了操作按钮，记录为:', record);
    createMessage.info('操作弹窗待实现');
  }
</script> 