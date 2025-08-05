import { defHttp } from '/@/utils/http/axios';
import { ErrorMessageMode } from '/#/axios';

enum Api {
  List = '/parking/parkingLot/list',
  Save = '/parking/parkingLot/add',
  Edit = '/parking/parkingLot/edit',
  Delete = '/parking/parkingLot/delete',
  DeleteBatch = '/parking/parkingLot/deleteBatch',
  ExportXls = '/parking/parkingLot/exportXls',
  ImportExcel = '/parking/parkingLot/importExcel',
  // 子功能API
  getChargingRules = '/parking/chargingRule/list',
  getCheckpoints = '/parking/checkpoint/list',
  getDevices = '/parking/device/list',
}

/**
 * 列表查询
 * @param params
 */
export const getList = (params) => defHttp.get({ url: Api.List, params });

/**
 * 批量删除
 * @param params
 */
export const deleteBatch = (params, mode: ErrorMessageMode = 'message') =>
  defHttp.delete({ url: Api.DeleteBatch, params }, { errorMessageMode: mode });

/**
 * 删除
 * @param params
 */
export const deleteLot = (params, mode: ErrorMessageMode = 'message') =>
  defHttp.delete({ url: Api.Delete, params }, { errorMessageMode: mode });
  
/**
 * 保存或者更新
 * @param params
 * @param isUpdate
 */
export const saveOrUpdate = (params, isUpdate) => {
  const url = isUpdate ? Api.Edit : Api.Save;
  return defHttp.post({ url: url, params });
};


/**
 * 导出
 * @param params
 */
export const exportXls = (params) => {
  return defHttp.get({ url: Api.ExportXls, params, responseType: 'blob' });
};

/**
 * 导入
 */
export const importExcel = (params) => {
  return defHttp.post({ url: Api.ImportExcel, params });
};

/**
 * 获取收费规则
 * @param params
 */
export const getChargingRules = (params) => defHttp.get({ url: Api.getChargingRules, params });

/**
 * 获取关卡列表
 * @param params
 */
export const getCheckpoints = (params) => defHttp.get({ url: Api.getCheckpoints, params });

/**
 * 获取硬件设备列表
 * @param params
 */
export const getDevices = (params) => defHttp.get({ url: Api.getDevices, params }); 