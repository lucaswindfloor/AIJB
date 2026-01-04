import { defHttp } from '/@/utils/http/axios';
import { Result } from '/#/axios';
import { RumenCapsule } from './capsule.data';

enum Api {
  List = '/animal_husbandry/rumen_capsule/list',
  GetLatestTelemetry = '/animal_husbandry/rumen_capsule/get-latest-telemetry',
  // ... 其他CRUD接口由JeecgBoot框架自动处理，此处无需定义
}

/**
 * 查询瘤胃胶囊列表
 * @param params
 */
export const getList = (params) => defHttp.get({ url: Api.List, params });

/**
 * 获取单个设备的最新遥测数据
 * @param tbDeviceId
 */
export const getLatestTelemetry = (tbDeviceId: string): Promise<Result<any>> => {
  return defHttp.get({ url: `${Api.GetLatestTelemetry}/${tbDeviceId}` });
}; 