# 前端页面与接口调用关系

本文档梳理了 `jeecgboot-vue3` 前端项目中，每个菜单页面所对应的VUE组件以及其调用的后端API接口。

| 菜单名称 | 菜单路由 | VUE组件地址 | 接口地址 |
| --- | --- | --- | --- |
| **全局/认证接口** | (不适用) | `/src/store/modules/user.ts` | `POST /sys/login` (账号登录)<br>`POST /sys/phoneLogin` (手机号登录)<br>`GET /sys/logout` (登出)<br>`GET /sys/user/getUserInfo` (获取用户信息)<br>`GET /sys/permission/getPermCode` (获取权限码)<br>`GET /sys/randomImage` (图形验证码)<br>`POST /sys/sms` (短信验证码)<br>`POST /sys/user/register` (注册) |
| **畜牧管理** | | | |
| 设备台账管理 | `/animal-husbandry/device` | `/src/views/animal_husbandry/device/DeviceList.vue` | `GET /animal_husbandry/device/list`<br>`DELETE /animal_husbandry/device/delete`<br>`DELETE /animal_husbandry/device/deleteBatch`<br>`POST /animal_husbandry/device/unbind`<br>`POST /animal_husbandry/device/syncFromThingsboard`<br>`PUT /animal_husbandry/device/edit`<br>`GET /animal_husbandry/device/getThingsboardDeviceByDevEui`<br>`POST /animal_husbandry/device/bind`<br>`GET /animal_husbandry/animal/listAvailableForBinding` |
| **停车管理** | | | |
| 停车场管理 | (通常为 `/parking/carpark`) | `/src/views/parking/carpark/index.vue` | `GET /parking/carPark/list`<br>`POST /parking/carPark/add`<br>`PUT /parking/carPark/edit`<br>`DELETE /parking/carPark/delete`<br>`DELETE /parking/carPark/deleteBatch`<br>`POST /parking/carPark/importExcel`<br>`GET /parking/carPark/exportXls` |
| **仪表盘** | | | |
| 分析页 | `/dashboard/analysis` | `/src/views/dashboard/Analysis/index.vue` | `GET /sys/loginfo`<br>`GET /sys/visitInfo` |
| 工作台 | `/dashboard/workbench` | `/src/views/dashboard/workbench/index.vue` | (无) |
| **系统管理 (Demo)** | | | |
| 账号管理 | `/system/account` | `/src/views/demo/system/account/index.vue` | `GET /api/system/getAccountList` (Mock) |
| 角色管理 | `/system/role` | `/src/views/demo/system/role/index.vue` | `GET /api/system/getRoleListByPage` (Mock) |
| 菜单管理 | `/system/menu` | `/src/views/demo/system/menu/index.vue` | `GET /api/system/getMenuList` (Mock) |
| 部门管理 | `/system/dept` | `/src/views/demo/system/dept/index.vue` | `GET /api/system/getDeptList` (Mock) |
| **关于** | | | |
| 关于 | `/about/index` | `/src/views/sys/about/index.vue` | (无) |
| **其他Demo页面** | | | |
| 大部分位于`demo/`目录下的其他页面（如`feat`, `comp`, `page`等）都是前端UI组件功能展示，不涉及后端API调用，或者仅使用Mock接口。 | | | | 