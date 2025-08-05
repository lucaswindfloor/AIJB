import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createMessage } = useMessage();

/**
 * API定义，与旧系统接口保持一致
 */
enum Api {
  list = '/parking/pDevice/list',
  save = '/parking/pDevice/add',
  edit = '/parking/pDevice/edit',
  deleteOne = '/parking/pDevice/delete',
  deleteBatch = '/parking/pDevice/deleteBatch',
  importExcel = '/parking/pDevice/importExcel',
  exportXls = '/parking/pDevice/exportXls',
  // 远程控制接口 - 保持与旧系统业务逻辑一致
  openOrCloseDoor = '/parking/pDevice/openOrCloseDoor',
  // 关联数据查询
  checkpointList = '/parking/pCheckpoint/list',
  parkingLotList = '/parking/parkingLot/list',
}

/**
 * 导出api
 */
export const getExportUrl = Api.exportXls;

/**
 * 导入api
 */
export const getImportUrl = Api.importExcel;

/**
 * 列表接口
 */
export const list = (params) => defHttp.get({ url: Api.list, params });

/**
 * 删除单个
 */
export const deleteOne = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.deleteOne, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
};

/**
 * 批量删除
 */
export const batchDelete = (params, handleSuccess) => {
  createMessage.loading('正在删除...');
  return defHttp.delete({ url: Api.deleteBatch, data: params }, { joinParamsToUrl: true }).then(() => {
    createMessage.success('删除成功！');
    handleSuccess();
  });
};

/**
 * 新增设备
 */
export const addDevice = (params) => {
  return defHttp.post({ url: Api.save, params }, { isTransformResponse: false });
};

/**
 * 编辑设备
 */
export const editDevice = (params) => {
  return defHttp.post({ url: Api.edit, params }, { isTransformResponse: false });
};

/**
 * 保存或者更新 (兼容旧版, 可选保留)
 */
export const saveOrUpdate = (params, isUpdate) => {
  let url = isUpdate ? Api.edit : Api.save;
  return defHttp.post({ url: url, params }, { isTransformResponse: false });
};

/**
 * 远程控制开闸/关闸 - 与旧系统API保持完全一致
 */
export const remoteControl = (params) => {
  return defHttp.post({ 
    url: Api.openOrCloseDoor, 
    params: {
      checkPointId: params.checkPointId,
      applicationReason: params.applicationReason,
      RedisPreFix: params.RedisPreFix,
      type: params.type
    }
  });
};

/**
 * 获取关卡列表
 */
export const getCheckpointList = (params) => {
  return defHttp.get({ url: Api.checkpointList, params });
};

/**
 * 获取停车场列表  
 */
export const getParkingLotList = (params) => {
  return defHttp.get({ url: Api.parkingLotList, params });
}; 