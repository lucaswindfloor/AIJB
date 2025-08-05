import { defHttp } from '/@/utils/http/axios';

enum Api {
  List = '/parking/lock-device/list',
  Add = '/parking/lock-device/add',
  Edit = '/parking/lock-device/edit',
  Delete = '/parking/lock-device/delete',
  DeleteBatch = '/parking/lock-device/deleteBatch',
  LockUp = '/parking/lock-device/lock-up',
  LockDown = '/parking/lock-device/lock-down',
  RefreshStatus = '/parking/lock-device/refresh-status',
  // For ApiSelect component
  ParkingLotList = '/parking/parkingLot/list',
}

/**
 * 获取设备列表
 */
export const getList = (params) => defHttp.get({ url: Api.List, params });

/**
 * 新增设备
 */
export const addDevice = (params) => defHttp.post({ url: Api.Add, params });

/**
 * 编辑设备
 */
export const editDevice = (params) => defHttp.put({ url: Api.Edit, params });

/**
 * 删除设备
 */
export const deleteDevice = (params) => defHttp.delete({ url: Api.Delete, params });

/**
 * 批量删除设备
 */
export const deleteBatch = (params) => defHttp.delete({ url: Api.DeleteBatch, params });

/**
 * 升锁
 */
export const lockUp = (params) => defHttp.get({ url: Api.LockUp, params });

/**
 * 降锁
 */
export const lockDown = (params) => defHttp.get({ url: Api.LockDown, params });

/**
 * 刷新设备状态
 */
export const refreshDeviceStatus = (params) => defHttp.get({ url: Api.RefreshStatus, params });

/**
 * 获取停车场列表 (用于下拉选择)
 */
export const getParkingLotList = (params) => defHttp.get({ url: Api.ParkingLotList, params }); 