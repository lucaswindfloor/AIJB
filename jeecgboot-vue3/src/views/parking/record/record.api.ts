import { defHttp } from '/@/utils/http/axios';

enum Api {
  List = '/parking/pParkingRecord/list',
  Save = '/parking/pParkingRecord/add',
  Edit = '/parking/pParkingRecord/edit',
  Delete = '/parking/pParkingRecord/delete',
  DeleteBatch = '/parking/pParkingRecord/deleteBatch',
  ExportXls = '/parking/pParkingRecord/exportXls',
  ImportExcel = '/parking/pParkingRecord/importExcel',
  CalculateFee = '/parking/pParkingRecord/calculateFee',
  ManualSettle = '/parking/pParkingRecord/manualSettle',
}

/**
 * 查询列表
 * @param params
 */
export const getList = (params) => defHttp.get({ url: Api.List, params });

/**
 * 导出
 * @param params
 */
export const exportXls = (params) => defHttp.get({ url: Api.ExportXls, params, responseType: 'blob' });

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
 * 删除
 * @param params
 */
export const deleteRecord = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.Delete, params }, { joinParamsToUrl: true, successMessage: '删除成功' }).then(() => {
    handleSuccess();
  });
};

/**
 * 批量删除
 * @param params
 */
export const deleteBatch = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.DeleteBatch, params }, { joinParamsToUrl: true, successMessage: '批量删除成功' }).then(() => {
    handleSuccess();
  });
};

/**
 * 计算费用
 * @param params
 */
export const calculateFee = (params) => defHttp.get({ url: Api.CalculateFee, params });

/**
 * 人工结算
 * @param params
 */
export const manualSettle = (params) => defHttp.post({ url: Api.ManualSettle, params }); 