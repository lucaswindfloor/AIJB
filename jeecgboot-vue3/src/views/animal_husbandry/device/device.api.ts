import { defHttp } from '/@/utils/http/axios';

// API枚举，定义所有相关的后端接口地址
enum Api {
  List = '/animal_husbandry/device/list',
  Save = '/animal_husbandry/device/add',
  Edit = '/animal_husbandry/device/edit',
  Delete = '/animal_husbandry/device/delete',
  DeleteBatch = '/animal_husbandry/device/deleteBatch',
  // [核心] 从ThingsBoard同步设备的接口
  SyncFromTb = '/animal_husbandry/device/syncFromThingsboard',
  // [核心] 在同步前，先验证DevEUI有效性的接口
  VerifyFromTb = '/animal_husbandry/device/getThingsboardDeviceByDevEui',
  // [核心] 绑定设备到牲畜
  BindToAnimal = '/animal_husbandry/device/bind',
  // [核心] 将设备从牲畜解绑
  UnbindFromAnimal = '/animal_husbandry/device/unbind',
  // [核心] 获取可绑定的牲畜列表（用于ApiSelect）
  GetAvailableAnimals = '/animal_husbandry/animal/listAvailableForBinding',
}

/**
 * 查询设备台账列表
 * @param params
 */
export const getList = (params) => defHttp.get({ url: Api.List, params });

/**
 * [新增] 从Thingsboard同步设备
 * @param params
 */
export const syncFromThingsboard = (params) => defHttp.post({ url: Api.SyncFromTb, params });

/**
 * [编辑] 更新设备业务信息
 * @param params
 */
export const editDevice = (params) => defHttp.put({ url: Api.Edit, params });

/**
 * 验证DevEUI并从ThingsBoard获取信息
 * @param params
 */
export const verifyFromThingsboard = (params) => {
  return defHttp.get({ url: Api.VerifyFromTb, params }, { errorMessageMode: 'message' });
};

/**
 * 删除设备
 * @param params
 */
export const deleteDevice = (params) => defHttp.delete({ url: Api.Delete, params }, { joinParamsToUrl: true });

/**
 * 批量删除设备
 * @param params
 */
export const deleteBatch = (params) => defHttp.delete({ url: Api.DeleteBatch, params });

/**
 * 绑定设备到牲畜
 * @param data
 */
export const bindToAnimal = (data) => defHttp.post({ url: Api.BindToAnimal, data });

/**
 * 解绑设备
 * @param params
 */
export const unbindFromAnimal = (params) => defHttp.post({ url: Api.UnbindFromAnimal, params });

/**
 * 获取可绑定的牲畜列表
 * @param params
 */
export const getAvailableAnimals = (params) => defHttp.get({ url: Api.GetAvailableAnimals, params }); 