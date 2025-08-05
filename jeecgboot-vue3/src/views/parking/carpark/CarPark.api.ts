import { defHttp } from '/@/utils/http/axios';

enum Api {
  list = '/parking/carPark/list',
  save = '/parking/carPark/add',
  edit = '/parking/carPark/edit',
  delete = '/parking/carPark/delete',
  deleteBatch = '/parking/carPark/deleteBatch',
  importExcel = '/parking/carPark/importExcel',
  exportXls = '/parking/carPark/exportXls',
}

/**
 * 导出api
 * @param params
 */
export const getExportUrl = Api.exportXls;

/**
 * 导入api
 */
export const getImportUrl = Api.importExcel;

/**
 * 列表接口
 * @param params
 */
export const getList = (params) => defHttp.get({ url: Api.list, params });

/**
 * 删除
 */
export const deleteParkingLot = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.delete, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
};

/**
 * 批量删除
 * @param params
 */
export const batchDeleteParkingLot = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.deleteBatch, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
};

/**
 * 保存或者更新
 * @param params
 */
export const saveOrUpdate = (params, isUpdate) => {
  const url = isUpdate ? Api.edit : Api.save;
  return defHttp.post({ url: url, params });
}; 