import { defHttp } from '/@/utils/http/axios';

// --- 基于`智能畜牧平台/瘤胃胶囊动物追踪器接口.md`的实际数据 ---

const REAL_TRACKER_DEVICE_ID = '929f0f60-500d-11f0-b064-af0707439256';

// 1. 列表中只包含文档中提到的那一个追踪器
const realDeviceList = [
  {
    id: '1', // 业务ID
    tbDeviceId: REAL_TRACKER_DEVICE_ID,
    name: '00956906000285d2', // from API doc
    type: '动物追踪器-OC-配置', // from API doc
    active: false, // from API doc
    customerName: '默认客户',
  },
];

// 2. 使用文档中为该追踪器提供的真实遥测数据
const realTelemetryData = {
  [REAL_TRACKER_DEVICE_ID]: {
    latitude: [{ ts: 1752643565510, value: '28.247117' }],
    longitude: [{ ts: 1752643565510, value: '112.851823' }],
    battery: [{ ts: 1752643565510, value: '99' }],
    hard_ver: [{ ts: 1752643565510, value: '4' }],
    soft_ver: [{ ts: 1752643565510, value: '3' }],
    step: [{ ts: 1752643565510, value: '0' }],
    work_mode: [{ ts: 1752643565510, value: '0' }],
    idle_interval: [{ ts: 1752643565510, value: '30' }],
  },
};

/**
 * @description: 获取设备列表，只返回真实的追踪器数据
 */
export const getList = (params) => {
  return Promise.resolve({
    records: realDeviceList,
    total: realDeviceList.length,
  });
};

/**
 * @description: 获取最新遥测数据，只返回对应追踪器的真实数据
 */
export const getLatestTelemetry = (deviceId: string) => {
  return new Promise((resolve) => {
    // 模拟网络延迟
    setTimeout(() => {
      const telemetry = realTelemetryData[deviceId];
      if (telemetry) {
        // 将ThingsBoard返回的数组格式转换为更易于使用的键值对格式
        const result = {};
        let lastTs = 0;
        for (const key in telemetry) {
          if (telemetry[key] && telemetry[key].length > 0) {
            result[key] = telemetry[key][0].value;
            lastTs = Math.max(lastTs, telemetry[key][0].ts);
          }
        }
        if (lastTs > 0) {
          result['lastUpdateTs'] = lastTs;
        }
        resolve(result);
      } else {
        resolve(null); // 如果ID不匹配，则没有数据
      }
    }, 100); // 固定一个小的延迟
  });
}; 