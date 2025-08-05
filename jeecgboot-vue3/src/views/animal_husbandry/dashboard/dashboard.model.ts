
// 牧场驾驶舱 - /kpi 接口VO
export interface DashboardKpiVo {
  totalAnimals: number;
  onlineDevices: number;
  healthyCount: number;
  subHealthyCount: number;
  alarmCount: number;
  newAlarmsToday: number;
}

// 牧场驾驶舱 - /map-data 接口VO
export interface MapAnimalDataVo {
  animalId: string;
  earTagId: string;
  lon: number;
  lat: number;
  healthStatus: 'HEALTHY' | 'SUB_HEALTHY' | 'ALARM' | string; // 健康状态
}

// 牧场驾驶舱 - /recent-alarms 接口VO
export interface RecentAlarmVo {
  id: string;
  animalId: string;
  earTagId: string; // 后端需要join查询提供
  alarmContent: string;
  alarmLevel: 'WARN' | 'CRITICAL' | string;
} 