
import { defHttp } from '/@/utils/http/axios';
import {
  DashboardKpiVo,
  MapAnimalDataVo,
  RecentAlarmVo,
} from './dashboard.model';

enum Api {
  GetKpiData = '/animal_husbandry/dashboard/kpi',
  GetMapData = '/animal_husbandry/dashboard/map-data',
  GetRecentAlarms = '/animal_husbandry/dashboard/recent-alarms',
  GetFences = '/animal_husbandry/dashboard/fences',
  GetOutOfBoundsAlarms = '/animal_husbandry/dashboard/out-of-bounds-alarms',
  GetAllHealthAlarms = '/animal_husbandry/dashboard/all-health-alarms',
}

/**
 * @description: Get kpi data
 */
export const getKpiData = () => {
  return defHttp.get({ url: Api.GetKpiData });
};

/**
 * @description: Get animal map data
 */
export const getMapData = () => {
  return defHttp.get({ url: Api.GetMapData });
};

/**
 * @description: Get recent alarms
 */
export const getRecentAlarms = () => {
  return defHttp.get({ url: Api.GetRecentAlarms });
};

/**
 * @description: Get all enabled fences
 */
export const getFences = () => {
  return defHttp.get({ url: Api.GetFences });
};

/**
 * @description: Get out of bounds alarms
 */
export const getOutOfBoundsAlarms = () => {
  return defHttp.get({ url: Api.GetOutOfBoundsAlarms });
};

/**
 * @description: Get all health alarms
 */
export const getAllHealthAlarms = () => {
  return defHttp.get({ url: Api.GetAllHealthAlarms });
};

// 备注：根据设计文档，点击地图点位后弹窗需要更详细信息
// 此处预留接口，在V1.1迭代中实现弹窗时使用
/**
 * @description: 获取单个牲畜的弹窗摘要信息
 */
// export const getAnimalDetailsForMapTip = (animalId: string) => {
//   return defHttp.get({ url: Api.GetAnimalDetailsForMapTip, params: { animalId } });
// }; 