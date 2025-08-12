import { defHttp } from '/@/utils/http/axios';
import { Modal } from 'ant-design-vue';

enum Api {
  list = '/animal_husbandry/animal/list',
  save = '/animal_husbandry/animal/add',
  edit = '/animal_husbandry/animal/edit',
  delete = '/animal_husbandry/animal/delete',
  deleteBatch = '/animal_husbandry/animal/deleteBatch',
  queryById = '/animal_husbandry/animal/queryById',
  
  // Custom APIs from design documents
  bindDevice = '/animal_husbandry/animal/bindDevice',
  unbindDevice = '/animal_husbandry/animal/unbindDevice',
  addLifecycleEvent = '/animal_husbandry/animal/addLifecycleEvent',
  
  // API to get available devices for binding
  // According to the design, we need devices that are 'IN_STOCK' or 'IDLE'
  listAvailableDevices = '/animal_husbandry/device/list', 
  // 【新】根据设备类型获取可绑定的设备列表
  listAvailableDevicesByType = '/animal_husbandry/device/listAvailableForBinding',
  
  // 【新】获取原始遥测日志
  getRawTelemetryLog = '/animal_husbandry/animal/get-raw-telemetry-log',
}

/**
 * 查询列表
 * @param params
 */
export const getList = (params) => defHttp.get({ url: Api.list, params });

/**
 * 通过id查询
 * @param params
 */
export const getById = (params) => defHttp.get({ url: Api.queryById, params });

/**
 * 保存或者更新
 * @param params
 * @param isUpdate
 */
export const saveOrUpdate = (params, isUpdate) => {
  const url = isUpdate ? Api.edit : Api.save;
  return defHttp.post({ url: url, params });
};

/**
 * 删除
 * @param params
 */
export const deleteAnimal = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.delete, params }).then(() => {
    handleSuccess();
  });
};

/**
 * 批量删除
 * @param params
 */
export const batchDeleteAnimal = (params, handleSuccess) => {
  Modal.confirm({
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({ url: Api.deleteBatch, params }, { joinParamsToUrl: true }).then(() => {
        handleSuccess();
      });
    },
  });
};

/**
 * 绑定设备
 * @param params
 */
export const bindDevice = (params) => defHttp.post({ url: Api.bindDevice, params });

/**
 * 解绑设备
 * @param params
 */
export const unbindDevice = (params) => defHttp.post({ url: Api.unbindDevice, params });

/**
 * 添加生命周期事件
 * @param params
 */
export const addLifecycleEvent = (params) => defHttp.post({ url: Api.addLifecycleEvent, params });

/**
 * 查询可绑定的设备列表
 * @param params
 */
export const getAvailableDevices = (params) => defHttp.get({ url: Api.listAvailableDevices, params: {...params, status_in: 'IN_STOCK,IDLE'} });

/**
 * 【新】根据设备类型查询可绑定的设备列表
 * @param params
 */
export const getAvailableDevicesByType = (params) => defHttp.get({ url: Api.listAvailableDevicesByType, params });


/**
 * 获取设备时序遥测数据
 */
export const getTimeSeriesTelemetry = (params: {
  deviceId: string;
  key: string;
  startTs: number;
  endTs: number;
}) => {
  return defHttp.get({ url: '/animal_husbandry/animal/get-timeseries-telemetry', params });
};

/**
 * 【新】获取设备原始遥测日志
 */
export const getRawTelemetryLog = (params: {
  deviceId: string;
  startTs: number;
  endTs: number;
}) => {
  return defHttp.get({ url: Api.getRawTelemetryLog, params });
}; 