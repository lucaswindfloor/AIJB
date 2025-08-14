import type { AppRouteModule } from '/@/router/types';
import { LAYOUT } from '/@/router/constant';

const animalHusbandry: AppRouteModule = {
  path: '/animal-husbandry',
  name: 'AnimalHusbandry',
  component: LAYOUT,
  redirect: '/animal-husbandry/dashboard',
  meta: {
    orderNo: 20,
    icon: 'ant-design:appstore-outlined',
    title: '智慧畜牧',
  },
  children: [
    {
      path: 'dashboard',
      name: 'animal_husbandry/dashboard',
      component: () => import('/@/views/animal_husbandry/dashboard/index.vue'),
      meta: {
        title: '牧场驾驶舱',
        icon: 'ant-design:dashboard-outlined',
      },
    },
    {
      path: 'fence',
      name: 'animal_husbandry/fence',
      component: () => import('/@/views/animal_husbandry/fence/FenceList.vue'),
      meta: {
        title: '电子围栏管理',
        icon: 'ant-design:gateway-outlined',
      },
    },
  ],
};

export default animalHusbandry; 