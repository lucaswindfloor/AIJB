
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
  healthStatus: 'HEALTHY' | 'SUB_HEALTHY' | 'ALARM';
}

// 牧场驾驶舱 - /recent-alarms 接口VO
export interface RecentAlarmVo {
  animalId: string;
  earTagId: string;
  alarmContent: string;
  alarmTime: string;
}

export interface FenceVo {
  id: string;
  name: string;
  points: string; // JSON string of points
} 