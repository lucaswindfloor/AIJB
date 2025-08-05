
import { defHttp } from '/@/utils/http/axios';
import {
  DashboardKpiVo,
  MapAnimalDataVo,
  RecentAlarmVo,
} from './dashboard.model';

enum Api {
  GetKpiData = '/animal_husbandry/dashboard/kpi',
  GetMapAnimalData = '/animal_husbandry/dashboard/map-data',
  GetRecentAlarms = '/animal_husbandry/dashboard/recent-alarms',
  GetAnimalDetailsForMapTip = '/animal_husbandry/dashboard/map-tip-details',
}

/**
 * @description: 获取页面顶部的核心KPI统计数据
 */
export const getKpiData = () => {
  return defHttp.get<DashboardKpiVo>({ url: Api.GetKpiData });
};

/**
 * @description: 获取所有需要在地图上显示的牲畜数据列表
 */
export const getMapAnimalData = () => {
  return defHttp.get<MapAnimalDataVo[]>({ url: Api.GetMapAnimalData });
};

/**
 * @description: 获取最新的N条告警记录
 */
export const getRecentAlarms = () => {
  return defHttp.get<RecentAlarmVo[]>({ url: Api.GetRecentAlarms });
};

// 备注：根据设计文档，点击地图点位后弹窗需要更详细信息
// 此处预留接口，在V1.1迭代中实现弹窗时使用
/**
 * @description: 获取单个牲畜的弹窗摘要信息
 */
// export const getAnimalDetailsForMapTip = (animalId: string) => {
//   return defHttp.get({ url: Api.GetAnimalDetailsForMapTip, params: { animalId } });
// }; 