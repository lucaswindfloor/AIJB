<template>
  <div>
    <!--引用表格-->
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <!--插槽:table-action-->
      <template #tableTitle>
        <a-button type="primary" @click="handleAdd" preIcon="ant-design:plus-outlined"> 新增</a-button>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls"> 导出</a-button>
        <j-upload-button type="primary" preIcon="ant-design:import-outlined" @click="onImportXls">导入</j-upload-button>
        <a-dropdown v-if="selectedRowKeys.length > 0">
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" @click="batchHandleDelete">
                <Icon icon="ant-design:delete-outlined"></Icon>
                删除
              </a-menu-item>
            </a-menu>
          </template>
          <a-button
            >批量操作
            <Icon icon="mdi:chevron-down"></Icon>
          </a-button>
        </a-dropdown>
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" :dropDownActions="getDropDownAction(record)" />
      </template>
    </BasicTable>
    <!-- 表单区域 -->
    <AnimalModal @register="registerModal" @success="handleSuccess"></AnimalModal>
    <DeviceBindModal @register="registerBindModal" @success="handleSuccess"></DeviceBindModal>
    <AnimalDetailDrawer @register="registerDrawer"></AnimalDetailDrawer>
  </div>
</template>

<script lang="ts" setup>
  //ts语法
  import { ref, computed, unref } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useDrawer } from '/@/components/Drawer';
  import { useListPage } from '/@/hooks/system/useListPage';
  import AnimalModal from './components/AnimalModal.vue';
  import DeviceBindModal from './components/DeviceBindModal.vue';
  import AnimalDetailDrawer from './components/AnimalDetailDrawer.vue';
  import { columns, searchFormSchema } from './animal.data';
  import { getList, deleteAnimal, batchDeleteAnimal, unbindDevice as unbindDeviceApi } from './animal.api';
  import { downloadFile } from '/@/utils/common/renderUtils';
  import { useMessage } from '/@/hooks/web/useMessage';

  const [registerModal, { openModal }] = useModal();
  const [registerBindModal, { openModal: openBindModal }] = useModal();
  const [registerDrawer, { openDrawer }] = useDrawer();
  const { createMessage, createConfirm } = useMessage();

  //列表页面公共参数、方法
  const { tableContext, onExportXls, onImportXls } = useListPage({
    tableProps: {
      title: '牲畜档案管理',
      api: getList,
      columns,
      canResize: true,
      formConfig: {
        //labelWidth: 120,
        schemas: searchFormSchema,
        autoSubmitOnEnter: true,
        showAdvancedButton: true,
        fieldMapToNumber: [],
        fieldMapToTime: [],
      },
      actionColumn: {
        width: 360,
        title: '操作',
        dataIndex: 'action',
        slots: { customRender: 'action' },
        fixed: 'right',
      },
    },
    exportConfig: {
      name: '牲畜档案',
      url: '/animal_husbandry/animal/exportXls',
    },
    importConfig: {
      url: '/animal_husbandry/animal/importXls',
    }
  });

  const [registerTable, { reload, setProps }, { rowSelection, selectedRowKeys }] = tableContext;

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
    openDrawer(true, {
      record,
      isUpdate: true,
      showFooter: false,
    });
  }
  /**
   * 删除事件
   */
  async function handleDelete(record) {
    await deleteAnimal({ id: record.id }, handleSuccess);
  }
  /**
   * 批量删除事件
   */
  async function batchHandleDelete() {
    await batchDeleteAnimal({ ids: selectedRowKeys.value }, handleSuccess);
  }
  /**
   * 【新】处理列内绑定按钮点击
   */
  function handleBindDevice(record: Recordable, deviceType: string) {
    openBindModal(true, {
      record,
      deviceType, // 将设备类型传递给弹窗
    });
  }

  /**
   * 【新】处理列内解绑按钮点击
   */
  function handleUnbindDevice(device: any) {
    createConfirm({
        title: '确认解绑',
        content: `您确定要解绑设备 [${device.name}] 吗？`,
        iconType: 'warning',
        onOk: async () => {
            await unbindDeviceApi({ deviceId: device.id });
            createMessage.success('解绑成功');
            reload();
        }
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
    const actions = [
      {
        label: '详情',
        onClick: handleDetail.bind(null, record),
      },
    ];

    // 根据是否已绑定“瘤胃胶囊”来动态添加操作
    const capsule = record.deviceMap?.CAPSULE;
    if (capsule) {
      actions.push({
        label: '解绑胶囊',
        color: 'error',
        onClick: () => handleUnbindDevice(capsule),
      });
    } else {
      actions.push({
        label: '绑定胶囊',
        onClick: () => handleBindDevice(record, 'CAPSULE'),
      });
    }

    // 根据是否已绑定“动物追踪器”来动态添加操作
    const tracker = record.deviceMap?.TRACKER;
    if (tracker) {
      actions.push({
        label: '解绑追踪器',
        color: 'error',
        onClick: () => handleUnbindDevice(tracker),
      });
    } else {
      actions.push({
        label: '绑定追踪器',
        onClick: () => handleBindDevice(record, 'TRACKER'),
      });
    }

    return actions;
  }

  /**
   * 下拉操作栏
   */
  function getDropDownAction(record) {
    return [
      {
        label: '编辑',
        onClick: handleEdit.bind(null, record),
      },
      {
        label: '删除',
        color: 'error',
        popConfirm: {
          title: '是否确认删除',
          confirm: handleDelete.bind(null, record),
          placement: 'left',
        },
      },
    ];
  }
</script> 