import { defHttp } from '/@/utils/http/axios';
import { ErrorMessageMode } from '/#/axios';

enum Api {
  ListAll = '/parking/listAll',
  Analysis = '/parking/analysis',
}

/**
 * @description: Get all parking lots
 */
export const getParkingLotList = () => {
  return defHttp.get({ url: Api.ListAll });
};

/**
 * @description: Get parking occupancy rate
 */
export const getParkingOccupancyRate = (params) => {
  return defHttp.get({ 
    url: Api.Analysis, 
    params 
  }, {
    ignoreCancelToken: true
  });
}; 