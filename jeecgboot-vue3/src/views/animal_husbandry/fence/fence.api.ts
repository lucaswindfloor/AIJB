import { defHttp } from '/@/utils/http/axios';

enum Api {
  list = '/animal_husbandry/ahFence/list',
  save = '/animal_husbandry/ahFence/add',
  edit = '/animal_husbandry/ahFence/edit',
  deleteOne = '/animal_husbandry/ahFence/delete',
  deleteBatch = '/animal_husbandry/ahFence/deleteBatch',
}

/**
 * 列表接口
 * @param params
 */
export const list = (params) => defHttp.get({ url: Api.list, params });

/**
 * 删除
 */
export const deleteOne = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.deleteOne, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
};

/**
 * 批量删除
 * @param params
 */
export const batchDelete = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.deleteBatch, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
};

/**
 * 保存或者更新
 * @param params
 * @param isUpdate
 */
export const saveOrUpdate = (params, isUpdate) => {
  const url = isUpdate ? Api.edit : Api.save;
  return defHttp.post({ url: url, params });
};
