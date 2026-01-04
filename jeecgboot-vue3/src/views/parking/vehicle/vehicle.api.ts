import { defHttp } from '/@/utils/http/axios';
import { ErrorMessageMode } from '/#/axios';

enum Api {
  List = '/parking/pVehicle/list',
  Save = '/parking/pVehicle/add',
  Edit = '/parking/pVehicle/edit',
  Delete = '/parking/pVehicle/delete',
  DeleteBatch = '/parking/pVehicle/deleteBatch',
  Approve = '/parking/pVehicle/approve',
}

/**
 * 查询车辆列表
 */
export const getList = (params) => {
  return defHttp.get({ url: Api.List, params });
};

/**
 * 保存车辆
 */
export const saveVehicle = (params, mode: ErrorMessageMode = 'message') => {
  return defHttp.post({ url: Api.Save, params: params }, { errorMessageMode: mode });
}

/**
 * 编辑车辆
 */
export const editVehicle = (params, mode: ErrorMessageMode = 'message') => {
  return defHttp.put({ url: Api.Edit, params: params }, { errorMessageMode: mode });
}

/**
 * 删除车辆
 */
export const deleteVehicle = (params, mode: ErrorMessageMode = 'message') => {
  return defHttp.delete({ url: Api.Delete, params: params }, { errorMessageMode: mode });
}

/**
 * 批量删除车辆
 */
export const deleteBatchVehicle = (params, mode: ErrorMessageMode = 'message') => {
  return defHttp.delete({ url: Api.DeleteBatch, params: params }, { errorMessageMode: mode });
}

/**
 * 审核车辆
 */
export const approveVehicle = (params, mode: ErrorMessageMode = 'message') => {
  return defHttp.post({ url: Api.Approve, params: params }, { errorMessageMode: mode });
} 