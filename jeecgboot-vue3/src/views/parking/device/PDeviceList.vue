<template>
  <div>
    <!--引用表格-->
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button
          type="primary"
          @click="handleAdd"
          preIcon="ant-design:plus-outlined"
        >
          新增
        </a-button>
        <a-button 
          type="primary" 
          preIcon="ant-design:export-outlined" 
          @click="onExportXls"
        > 
          导出
        </a-button>
        <j-upload-button 
          type="primary" 
          preIcon="ant-design:import-outlined" 
          @click="onImportXls"
        >
          导入
        </j-upload-button>
        <a-dropdown v-if="selectedRowKeys.length > 0">
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" @click="batchHandleDelete">
                <Icon icon="ant-design:delete-outlined" />
                删除
              </a-menu-item>
            </a-menu>
          </template>
          <a-button>
            批量操作
            <Icon icon="mdi:chevron-down" />
          </a-button>
        </a-dropdown>
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction
          :actions="[
            {
              label: '远程控制',
              onClick: handleRemoteControl.bind(null, record),
              color: 'warning',
            },
            {
              label: '编辑',
              onClick: handleEdit.bind(null, record),
            },
            {
              label: '删除',
              color: 'error',
              popConfirm: {
                title: '是否确认删除',
                placement: 'left',
                confirm: handleDelete.bind(null, record),
              },
            },
          ]"
        />
      </template>
    </BasicTable>
    <!-- 表单区域 -->
    <PDeviceModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" name="parking-pDevice" setup>
  import { ref, computed, unref } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import PDeviceModal from './components/PDeviceModal.vue'
  import { columns, searchFormSchema, DEVICE_BRAND_MAP, DEVICE_STATUS_MAP, DEVICE_TYPE_MAP } from './pDevice.data';
  import { list, deleteOne, batchDelete, getExportUrl, getImportUrl, remoteControl } from './pDevice.api';
  import { downloadByUrl } from '/@/utils/file/download';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { Modal } from 'ant-design-vue';
  
  const checkedKeys = ref<Array<string | number>>([]);
  const { createMessage } = useMessage();
  const { createConfirm } = useMessage();
  
  //注册model
  const [registerModal, { openModal }] = useModal();
  
  //注册table数据
  const { prefixCls, tableContext, onExportXls, onImportXls } = useListPage({
    tableProps: {
      title: '硬件设备',
      api: list,
      columns,
      canResize: false,
      formConfig: {
        //labelWidth: 120,
        schemas: searchFormSchema,
        autoSubmitOnEnter: true,
        showAdvancedButton: true,
        fieldMapToNumber: [],
        fieldMapToTime: [],
      },
      actionColumn: {
        width: 120,
        fixed: 'right',
      },
      beforeFetch: (params) => {
        return Object.assign(params);
      },
      afterFetch: (dataSource) => {
        // 处理关联数据显示，保持与旧系统一致的业务逻辑
        return dataSource.map(record => {
          // 品牌映射处理
          if (record.brand && DEVICE_BRAND_MAP[record.brand]) {
            record.brandName = DEVICE_BRAND_MAP[record.brand];
          }
          
          // 状态映射处理  
          if (record.status && DEVICE_STATUS_MAP[record.status]) {
            record.statusName = DEVICE_STATUS_MAP[record.status];
          }
          
          // 类型映射处理
          if (record.deviceType && DEVICE_TYPE_MAP[record.deviceType]) {
            record.deviceTypeName = DEVICE_TYPE_MAP[record.deviceType];
          }
          
          // 关联数据处理：停车场名称和关卡名称
          record.checkpointName = record.checkpoint?.name || '-';
          record.parkingLotName = record.checkpoint?.parkingLot?.name || '-';
          
          return record;
        });
      },
    },
    exportConfig: {
      name: '硬件设备列表',
      url: getExportUrl,
      params: {},
    },
    importConfig: {
      url: getImportUrl,
      success: handleSuccess,
    },
  });

  const [registerTable, { reload }, { rowSelection, selectedRowKeys }] = tableContext;

  /**
   * 新增事件
   */
  function handleAdd() {
    openModal(true, {
      isUpdate: false,
      showFooter: true,
    });
  }
  
  /**
   * 编辑事件
   */
  function handleEdit(record: Recordable) {
    openModal(true, {
      record,
      isUpdate: true,
      showFooter: true,
    });
  }
  
  /**
   * 详情
   */
  function handleDetail(record: Recordable) {
    openModal(true, {
      record,
      isUpdate: true,
      showFooter: false,
    });
  }
  
  /**
   * 删除事件
   */
  async function handleDelete(record) {
    await deleteOne({ id: record.id }, handleSuccess);
  }
  
  /**
   * 批量删除事件
   */
  async function batchHandleDelete() {
    await batchDelete({ ids: selectedRowKeys.value }, handleSuccess);
  }
  
  /**
   * 远程控制事件 - 实现旧系统的开闸/关闸逻辑
   */
  function handleRemoteControl(record) {
    // 检查设备是否有关联的关卡
    if (!record.checkpointId) {
      createMessage.warning('该设备未设置默认通道，无法进行远程控制');
      return;
    }
    
    // 显示确认对话框，与旧系统逻辑完全一致
    Modal.confirm({
      title: '远程控制确认',
      content: '此处可自由控制该硬件开闸关闸，请注意关闸时是否有车辆行人经过，如未正常开关闸，请检查闸机是否正常接线',
      okText: '开闸',
      cancelText: '关闸',
      onOk: async () => {
        try {
          // 开闸操作
          const result = await remoteControl({
            checkPointId: record.checkpointId,
            applicationReason: '人工确认',
            RedisPreFix: 'release_',
            type: 1,
          });
          
          if (result.success || result.code === 10002) {
            createMessage.success(result.message || '开闸成功');
          } else {
            createMessage.error(result.message || '开闸失败');
          }
        } catch (error) {
          createMessage.error('开闸操作失败');
        }
      },
      onCancel: async () => {
        try {
          // 关闸操作
          const result = await remoteControl({
            checkPointId: record.checkpointId,
            applicationReason: '人工确认',
            RedisPreFix: 'checkouPoint_off_',
            type: 1,
          });
          
          if (result.success || result.code === 10002) {
            createMessage.success(result.message || '关闸成功');
          } else {
            createMessage.error(result.message || '关闸失败');
          }
        } catch (error) {
          createMessage.error('关闸操作失败');
        }
      },
    });
  }
  
  /**
   * 成功回调
   */
  function handleSuccess() {
    (selectedRowKeys.value = []) && reload();
  }
  
  /**
   * 操作栏
   */
  function getTableAction(record) {
    return [
      {
        label: '远程控制',
        onClick: handleRemoteControl.bind(null, record),
        color: 'warning',
      },
      {
        label: '编辑',
        onClick: handleEdit.bind(null, record),
      },
    ];
  }
</script>

<style scoped></style> 