import type { AppRouteModule } from '/@/router/types';

import { LAYOUT } from '/@/router/constant';
import { t } from '/@/hooks/web/useI18n';

const animalHusbandry: AppRouteModule = {
  path: '/animal-husbandry',
  name: 'AnimalHusbandry',
  component: LAYOUT,
  // 将驾驶舱设置为该模块的默认（重定向）页面
  redirect: '/animal-husbandry/dashboard', 
  meta: {
    orderNo: 20,
    icon: 'ant-design:appstore-outlined',
    title: '畜牧管理',
  },
  children: [
    {
      path: 'dashboard',
      name: 'AnimalHusbandryDashboard',
      // 指向我们刚刚创建的 index.vue
      component: () => import('/@/views/animal_husbandry/dashboard/index.vue'),
      meta: {
        // 子菜单标题
        title: '牧场驾驶舱',
        icon: 'ant-design:dashboard-outlined',
      },
    },
    {
      path: 'device',
      name: 'AnimalHusbandryDevice',
      // 这里指向我们刚刚创建的DeviceList.vue
      component: () => import('/@/views/animal_husbandry/device/DeviceList.vue'),
      meta: {
        // 子菜单标题
        title: '设备台账管理',
        icon: 'ant-design:database-outlined',
      },
    },
    // 未来可以扩展其他子菜单，如牲畜档案、告警中心等
    // {
    //   path: 'animal',
    //   name: 'AnimalHusbandryAnimal',
    //   component: () => import('...'),
    //   meta: {
    //     title: '牲畜档案管理',
    //     icon: '...',
    //   },
    // },
  ],
};

export default animalHusbandry; 