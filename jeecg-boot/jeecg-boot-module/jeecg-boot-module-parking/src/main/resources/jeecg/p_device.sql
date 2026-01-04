-- 硬件设备管理建表脚本
-- 基于新系统p_device表结构，保持与旧系统业务逻辑兼容

-- 创建硬件设备表
DROP TABLE IF EXISTS `p_device`;
CREATE TABLE `p_device` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `checkpoint_id` varchar(36) DEFAULT NULL COMMENT '关卡ID，关联p_checkpoint表',
  `name` varchar(100) NOT NULL COMMENT '设备名称',
  `serial_number` varchar(50) NOT NULL COMMENT '设备序列号，唯一标识',
  `device_no` varchar(50) DEFAULT NULL COMMENT '设备编号',
  `mac_address` varchar(20) DEFAULT NULL COMMENT 'MAC地址',
  `device_type` varchar(20) NOT NULL DEFAULT 'CAMERA' COMMENT '设备类型：CAMERA-摄像头, GATE-道闸, LED-显示屏',
  `brand` varchar(50) DEFAULT NULL COMMENT '设备品牌：HIKVISION-海康威视, DAHUA-大华, OTHER-其他',
  `model` varchar(100) DEFAULT NULL COMMENT '设备型号',
  `ip_address` varchar(15) DEFAULT NULL COMMENT 'IP地址',
  `username` varchar(50) DEFAULT NULL COMMENT '设备登录用户名',
  `password` varchar(100) DEFAULT NULL COMMENT '设备登录密码',
  `stream_url` varchar(500) DEFAULT NULL COMMENT '视频流地址',
  `status` varchar(20) NOT NULL DEFAULT 'OFFLINE' COMMENT '设备状态：ONLINE-在线, OFFLINE-离线, FAULTY-故障, BUSY-忙碌',
  `last_online_time` datetime DEFAULT NULL COMMENT '最后在线时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `sys_org_code` varchar(64) DEFAULT NULL COMMENT '所属部门',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除状态：0-正常, 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_serial_number` (`serial_number`),
  UNIQUE KEY `uk_mac_address` (`mac_address`),
  KEY `idx_checkpoint_id` (`checkpoint_id`),
  KEY `idx_device_type` (`device_type`),
  KEY `idx_status` (`status`),
  KEY `idx_brand` (`brand`),
  KEY `idx_ip_address` (`ip_address`),
  KEY `idx_last_online_time` (`last_online_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='硬件设备管理表';

-- 添加外键约束（如果p_checkpoint表存在）
-- ALTER TABLE `p_device` ADD CONSTRAINT `fk_device_checkpoint` FOREIGN KEY (`checkpoint_id`) REFERENCES `p_checkpoint` (`id`);

-- 插入初始测试数据
INSERT INTO `p_device` (`id`, `checkpoint_id`, `name`, `serial_number`, `device_no`, `mac_address`, `device_type`, `brand`, `model`, `ip_address`, `username`, `password`, `stream_url`, `status`, `last_online_time`, `create_by`, `create_time`, `update_by`, `update_time`, `sys_org_code`, `del_flag`) VALUES
('1001', NULL, '入口摄像头1', 'HK001', 'DEV001', '00:11:22:33:44:55', 'CAMERA', 'HIKVISION', 'DS-2CD2347G1-LU', '192.168.1.100', 'admin', 'admin123', 'rtsp://192.168.1.100:554/h264/ch1/main/av_stream', 'ONLINE', '2024-01-01 10:00:00', 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 10:00:00', 'A01', 0),
('1002', NULL, '入口道闸1', 'HK002', 'DEV002', '00:11:22:33:44:56', 'GATE', 'HIKVISION', 'DS-TMG071-B', '192.168.1.101', 'admin', 'admin123', NULL, 'ONLINE', '2024-01-01 10:00:00', 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 10:00:00', 'A01', 0),
('1003', NULL, '出口摄像头1', 'DH001', 'DEV003', '00:11:22:33:44:57', 'CAMERA', 'DAHUA', 'IPC-HFW4831T-ASE', '192.168.1.102', 'admin', 'admin123', 'rtsp://192.168.1.102:554/cam/realmonitor?channel=1&subtype=0', 'OFFLINE', '2024-01-01 09:30:00', 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 09:30:00', 'A01', 0),
('1004', NULL, '出口道闸1', 'DH002', 'DEV004', '00:11:22:33:44:58', 'GATE', 'DAHUA', 'DHI-ASG2101A-P', '192.168.1.103', 'admin', 'admin123', NULL, 'ONLINE', '2024-01-01 10:00:00', 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 10:00:00', 'A01', 0),
('1005', NULL, 'LED显示屏1', 'LED001', 'DEV005', '00:11:22:33:44:59', 'LED', 'OTHER', 'LED-P10', '192.168.1.104', 'admin', 'admin123', NULL, 'ONLINE', '2024-01-01 10:00:00', 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 10:00:00', 'A01', 0);

-- 创建权限相关数据

-- 插入菜单权限
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_route`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `status`, `del_flag`, `rule_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `type`, `internal_or_external`) VALUES
('parking-device-menu', 'parking-main-menu', '硬件设备管理', '/parking/device', 'parking/device/PDeviceList', 'PDeviceList', NULL, 1, NULL, '1', 2, 0, 'ant-design:camera-outlined', 1, 1, 0, 0, 0, '硬件设备管理菜单', '1', 0, 0, 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 09:00:00', 0, 0);

-- 插入按钮权限
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_route`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `status`, `del_flag`, `rule_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `type`, `internal_or_external`) VALUES
('parking-device-add', 'parking-device-menu', '添加', NULL, NULL, NULL, NULL, 2, 'parking:p_device:add', '1', 1, 0, NULL, 0, 1, 0, 0, 0, '硬件设备添加', '1', 0, 0, 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 09:00:00', 0, 0),
('parking-device-edit', 'parking-device-menu', '编辑', NULL, NULL, NULL, NULL, 2, 'parking:p_device:edit', '1', 2, 0, NULL, 0, 1, 0, 0, 0, '硬件设备编辑', '1', 0, 0, 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 09:00:00', 0, 0),
('parking-device-delete', 'parking-device-menu', '删除', NULL, NULL, NULL, NULL, 2, 'parking:p_device:delete', '1', 3, 0, NULL, 0, 1, 0, 0, 0, '硬件设备删除', '1', 0, 0, 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 09:00:00', 0, 0),
('parking-device-deleteBatch', 'parking-device-menu', '批量删除', NULL, NULL, NULL, NULL, 2, 'parking:p_device:deleteBatch', '1', 4, 0, NULL, 0, 1, 0, 0, 0, '硬件设备批量删除', '1', 0, 0, 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 09:00:00', 0, 0),
('parking-device-exportXls', 'parking-device-menu', '导出', NULL, NULL, NULL, NULL, 2, 'parking:p_device:exportXls', '1', 5, 0, NULL, 0, 1, 0, 0, 0, '硬件设备导出', '1', 0, 0, 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 09:00:00', 0, 0),
('parking-device-importExcel', 'parking-device-menu', '导入', NULL, NULL, NULL, NULL, 2, 'parking:p_device:importExcel', '1', 6, 0, NULL, 0, 1, 0, 0, 0, '硬件设备导入', '1', 0, 0, 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 09:00:00', 0, 0),
('parking-device-remoteControl', 'parking-device-menu', '远程控制', NULL, NULL, NULL, NULL, 2, 'parking:p_device:remoteControl', '1', 7, 0, NULL, 0, 1, 0, 0, 0, '硬件设备远程控制', '1', 0, 0, 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 09:00:00', 0, 0),
('parking-device-publishLed', 'parking-device-menu', 'LED发布', NULL, NULL, NULL, NULL, 2, 'parking:p_device:publishLed', '1', 8, 0, NULL, 0, 1, 0, 0, 0, '硬件设备LED发布', '1', 0, 0, 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 09:00:00', 0, 0),
('parking-device-monitor', 'parking-device-menu', '设备监控', NULL, NULL, NULL, NULL, 2, 'parking:p_device:monitor', '1', 9, 0, NULL, 0, 1, 0, 0, 0, '硬件设备监控', '1', 0, 0, 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 09:00:00', 0, 0);

-- 为管理员角色分配权限
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`) VALUES
('device-role-perm-1', '1', 'parking-device-menu', NULL, '2024-01-01 09:00:00', '127.0.0.1'),
('device-role-perm-2', '1', 'parking-device-add', NULL, '2024-01-01 09:00:00', '127.0.0.1'),
('device-role-perm-3', '1', 'parking-device-edit', NULL, '2024-01-01 09:00:00', '127.0.0.1'),
('device-role-perm-4', '1', 'parking-device-delete', NULL, '2024-01-01 09:00:00', '127.0.0.1'),
('device-role-perm-5', '1', 'parking-device-deleteBatch', NULL, '2024-01-01 09:00:00', '127.0.0.1'),
('device-role-perm-6', '1', 'parking-device-exportXls', NULL, '2024-01-01 09:00:00', '127.0.0.1'),
('device-role-perm-7', '1', 'parking-device-importExcel', NULL, '2024-01-01 09:00:00', '127.0.0.1'),
('device-role-perm-8', '1', 'parking-device-remoteControl', NULL, '2024-01-01 09:00:00', '127.0.0.1'),
('device-role-perm-9', '1', 'parking-device-publishLed', NULL, '2024-01-01 09:00:00', '127.0.0.1'),
('device-role-perm-10', '1', 'parking-device-monitor', NULL, '2024-01-01 09:00:00', '127.0.0.1');

-- 创建数据字典配置

-- 设备类型字典
INSERT INTO `sys_dict` (`id`, `dict_name`, `dict_code`, `description`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `type`) VALUES
('device-type-dict', '设备类型', 'device_type', '硬件设备类型分类', 0, 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 09:00:00', 0);

INSERT INTO `sys_dict_item` (`id`, `dict_id`, `item_text`, `item_value`, `description`, `sort_order`, `status`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES
('device-type-1', 'device-type-dict', '摄像头', 'CAMERA', '车牌识别摄像头', 1, 1, 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 09:00:00'),
('device-type-2', 'device-type-dict', '道闸', 'GATE', '车辆通行道闸', 2, 1, 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 09:00:00'),
('device-type-3', 'device-type-dict', 'LED显示屏', 'LED', 'LED信息显示屏', 3, 1, 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 09:00:00');

-- 设备品牌字典
INSERT INTO `sys_dict` (`id`, `dict_name`, `dict_code`, `description`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `type`) VALUES
('device-brand-dict', '设备品牌', 'device_brand', '硬件设备品牌分类', 0, 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 09:00:00', 0);

INSERT INTO `sys_dict_item` (`id`, `dict_id`, `item_text`, `item_value`, `description`, `sort_order`, `status`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES
('device-brand-1', 'device-brand-dict', '海康威视', 'HIKVISION', '海康威视设备', 1, 1, 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 09:00:00'),
('device-brand-2', 'device-brand-dict', '大华', 'DAHUA', '大华设备', 2, 1, 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 09:00:00'),
('device-brand-3', 'device-brand-dict', '宇视', 'UNIVIEW', '宇视设备', 3, 1, 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 09:00:00'),
('device-brand-4', 'device-brand-dict', '其他', 'OTHER', '其他品牌设备', 99, 1, 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 09:00:00');

-- 设备状态字典
INSERT INTO `sys_dict` (`id`, `dict_name`, `dict_code`, `description`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `type`) VALUES
('device-status-dict', '设备状态', 'device_status', '硬件设备运行状态', 0, 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 09:00:00', 0);

INSERT INTO `sys_dict_item` (`id`, `dict_id`, `item_text`, `item_value`, `description`, `sort_order`, `status`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES
('device-status-1', 'device-status-dict', '在线', 'ONLINE', '设备正常在线', 1, 1, 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 09:00:00'),
('device-status-2', 'device-status-dict', '离线', 'OFFLINE', '设备离线', 2, 1, 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 09:00:00'),
('device-status-3', 'device-status-dict', '故障', 'FAULTY', '设备出现故障', 3, 1, 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 09:00:00'),
('device-status-4', 'device-status-dict', '忙碌', 'BUSY', '设备忙碌中', 4, 1, 'admin', '2024-01-01 09:00:00', 'admin', '2024-01-01 09:00:00');

-- 创建索引优化性能
-- 已在建表语句中包含

-- 创建视图（可选）
CREATE OR REPLACE VIEW `v_device_info` AS
SELECT 
    d.id,
    d.checkpoint_id,
    d.name,
    d.serial_number,
    d.device_no,
    d.mac_address,
    d.device_type,
    dt.item_text as device_type_text,
    d.brand,
    db.item_text as brand_text,
    d.model,
    d.ip_address,
    d.username,
    d.stream_url,
    d.status,
    ds.item_text as status_text,
    d.last_online_time,
    c.name as checkpoint_name,
    c.direction,
    l.name as parking_lot_name,
    d.create_by,
    d.create_time,
    d.update_by,
    d.update_time,
    d.sys_org_code,
    d.del_flag
FROM p_device d
LEFT JOIN p_checkpoint c ON d.checkpoint_id = c.id
LEFT JOIN p_parking_lot l ON c.parking_lot_id = l.id
LEFT JOIN sys_dict_item dt ON dt.dict_id = 'device-type-dict' AND dt.item_value = d.device_type
LEFT JOIN sys_dict_item db ON db.dict_id = 'device-brand-dict' AND db.item_value = d.brand
LEFT JOIN sys_dict_item ds ON ds.dict_id = 'device-status-dict' AND ds.item_value = d.status
WHERE d.del_flag = 0;

-- 添加注释
ALTER TABLE `p_device` COMMENT='硬件设备管理表，用于管理停车场的各类硬件设备包括摄像头、道闸、LED显示屏等';

-- 完成建表脚本
-- 此脚本完全兼容旧系统业务逻辑，支持新系统架构规范
-- 包含了完整的权限配置、数据字典和初始数据
-- 支持设备管理、远程控制、监控等功能 