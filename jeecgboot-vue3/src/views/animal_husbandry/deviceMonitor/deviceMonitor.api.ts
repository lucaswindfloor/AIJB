import { defHttp } from '/@/utils/http/axios';

const api = {
  getKpis: '/animal_husbandry/deviceMonitor/kpis',
  getProblematicList: '/animal_husbandry/deviceMonitor/problematicList',
  getTelemetryHistory: '/animal_husbandry/deviceMonitor/telemetryHistory',
  sendRpc: '/animal_husbandry/deviceMonitor/rpc',
  createMaintenance: '/animal_husbandry/deviceMonitor/createMaintenanceTask',
};

/**
 * @description: 获取设备监控KPI
 */
export const getKpis = () => {
  return defHttp.get({ url: api.getKpis });
};

/**
 * @description: 分页查询有问题的设备列表
 */
export const getProblematicList = (params) => {
  return defHttp.get({ url: api.getProblematicList, params });
};

/**
 * @description: 获取历史遥测数据
 */
export const getTelemetryHistory = (params) => {
  return defHttp.get({ url: api.getTelemetryHistory, params });
};

/**
 * @description: 发送RPC指令
 */
export const sendRpc = (data) => {
  return defHttp.post({ url: api.sendRpc, data });
};

/**
 * @description: 创建维保任务
 */
export const createMaintenance = (data) => {
  return defHttp.post({ url: api.createMaintenance, data });
}; 