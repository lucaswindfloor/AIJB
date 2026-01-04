-- ----------------------------
-- JeecgBoot 停车场管理模块表结构
-- author: Gemini
-- date: 2024-08-09
-- ----------------------------

-- ----------------------------
-- 模块：基础信息模块
-- ----------------------------

-- 停车场核心信息表 (源表: cf_car_park)
CREATE TABLE `p_parking_lot` (
    `id` varchar(36) NOT NULL COMMENT '主键ID',
    `name` varchar(100) NOT NULL COMMENT '停车场名称',
    `image` varchar(255) DEFAULT '' COMMENT '停车场封面图',
    `address` varchar(255) DEFAULT '' COMMENT '详细地址',
    `longitude` decimal(10,7) DEFAULT NULL COMMENT '经度',
    `latitude` decimal(10,7) DEFAULT NULL COMMENT '纬度',
    `province_id` varchar(36) DEFAULT '' COMMENT '省份ID (关联 sys_area.id)',
    `city_id` varchar(36) DEFAULT '' COMMENT '城市ID (关联 sys_area.id)',
    `area_id` varchar(36) DEFAULT '' COMMENT '区/县ID (关联 sys_area.id)',
    `total_spaces` int(11) NOT NULL DEFAULT 0 COMMENT '车位总数',
    `contact_person` varchar(50) DEFAULT '' COMMENT '联系人',
    `contact_phone` varchar(20) DEFAULT '' COMMENT '联系电话',
    `business_hours_start` time DEFAULT NULL COMMENT '营业开始时间',
    `business_hours_end` time DEFAULT NULL COMMENT '营业结束时间',
    `score` decimal(3,2) DEFAULT '5.00' COMMENT '评分',
    `number_of_comments` int(11) DEFAULT '0' COMMENT '评论数',
    `status` varchar(20) NOT NULL DEFAULT 'NORMAL' COMMENT '状态 (NORMAL-正常, FULL-车位已满, CLOSED-停业)',
    `sys_org_code` varchar(64) DEFAULT NULL COMMENT '所属部门编码',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime(3) DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(50) DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime(3) DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='停车场核心信息表';

-- 停车场业务配置表 (源表: cf_car_park 部分字段)
CREATE TABLE `p_parking_lot_config` (
    `id` varchar(36) NOT NULL COMMENT '主键ID',
    `parking_lot_id` varchar(36) NOT NULL COMMENT '停车场ID',
    `free_duration` int(11) DEFAULT '15' COMMENT '免费停车时长(分钟)',
    `unit_duration_minutes` int(11) DEFAULT '60' COMMENT '计费单位时长(分钟)',
    `expired_car_free_duration` int(11) DEFAULT '0' COMMENT '过期月租车免费时长(分钟)',
    `billing_model` varchar(50) DEFAULT 'static' COMMENT '计费模式 (static-静态/dynamic-动态/24_static-24小时静态/24_dynamic-24小时动态)',
    `starting_price` decimal(10,2) DEFAULT '0.00' COMMENT '起步价',
    `allow_unlicensed_entry` tinyint(1) DEFAULT '0' COMMENT '允许无牌车入场 (0-否, 1-是)',
    `forbid_simultaneous_entry` tinyint(1) DEFAULT '0' COMMENT '禁止同组车辆同时在场 (0-否, 1-是)',
    `limit_on_full` tinyint(1) DEFAULT '1' COMMENT '车位已满时是否限制入场 (0-否, 1-是)',
    `auto_renew_subscription` tinyint(1) DEFAULT '0' COMMENT '自动续费月卡 (0-否, 1-是)',
    `temp_car_auto_pay` tinyint(1) DEFAULT '1' COMMENT '临时车是否自动支付 (0-否, 1-是)',
    `abnormal_auto_release` tinyint(1) DEFAULT '1' COMMENT '异常记录是否自动放行 (0-否, 1-是)',
    `allow_edit_record` tinyint(1) DEFAULT '0' COMMENT '允许收费员修改停车记录 (0-否, 1-是)',
    `auto_issue_whitelist` tinyint(1) DEFAULT '0' COMMENT '套餐车是否自动下发白名单 (0-否, 1-是)',
    `show_record_remarks` tinyint(1) DEFAULT '0' COMMENT '是否显示停车记录备注 (0-否, 1-是)',
    `visitor_approval_mode` varchar(20) DEFAULT 'DISABLED' COMMENT '访客审核模式 (DISABLED-关闭, OWNER-业主审核, PROPERTY-物业审核)',
    `payment_collection_lot_id` varchar(36) DEFAULT NULL COMMENT '费用代收停车场ID',
    `data_reporting_platform` varchar(255) DEFAULT '' COMMENT '数据上报平台',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime(3) DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(50) DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime(3) DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_lot_id` (`parking_lot_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='停车场业务配置表';

-- 停车场标签表 (源表: cf_car_park_tag)
CREATE TABLE `p_parking_lot_tag` (
    `id` varchar(36) NOT NULL COMMENT '主键ID',
    `name` varchar(50) NOT NULL COMMENT '标签名称',
    `sort_order` int(11) DEFAULT '99' COMMENT '排序值',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='停车场标签表';

-- 停车场-标签关联表 (源表: cf_car_park_link_tag)
CREATE TABLE `p_lot_tag_link` (
    `id` varchar(36) NOT NULL COMMENT '主键ID',
    `lot_id` varchar(36) NOT NULL COMMENT '停车场ID',
    `tag_id` varchar(36) NOT NULL COMMENT '标签ID',
    PRIMARY KEY (`id`),
    KEY `idx_lot_tag` (`lot_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='停车场-标签关联表';

-- 关卡/出入口表 (源表: cf_car_park_checkpoint)
CREATE TABLE `p_checkpoint` (
    `id` varchar(36) NOT NULL COMMENT '主键ID',
    `parking_lot_id` varchar(36) NOT NULL COMMENT '停车场ID',
    `name` varchar(100) NOT NULL COMMENT '关卡名称',
    `direction` varchar(10) NOT NULL COMMENT '方向 (IN-入口, OUT-出口)',
    `status` varchar(20) NOT NULL DEFAULT 'NORMAL' COMMENT '状态 (NORMAL-正常, CLOSED-关闭)',
    `remark` varchar(255) DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关卡/出入口表';

-- 硬件设备表 (源表: cf_car_park_device)
CREATE TABLE `p_device` (
    `id` varchar(36) NOT NULL COMMENT '主键ID',
    `checkpoint_id` varchar(36) NOT NULL COMMENT '关卡ID',
    `name` varchar(100) NOT NULL COMMENT '设备名称',
    `serial_number` varchar(100) DEFAULT NULL COMMENT '设备序列号 (SN/barcode)',
    `device_no` varchar(50) DEFAULT NULL COMMENT '设备业务编号',
    `mac_address` varchar(50) DEFAULT NULL COMMENT 'MAC地址',
    `device_type` varchar(20) NOT NULL COMMENT '设备类型 (CAMERA-相机, GATE-道闸)',
    `brand` varchar(50) DEFAULT '' COMMENT '品牌',
    `model` varchar(100) DEFAULT '' COMMENT '型号',
    `ip_address` varchar(45) DEFAULT NULL COMMENT 'IP地址',
    `username` varchar(100) DEFAULT NULL COMMENT '登录用户名',
    `password` varchar(255) DEFAULT NULL COMMENT '登录密码 (加密存储)',
    `stream_url` varchar(255) DEFAULT NULL COMMENT '视频流地址',
    `status` varchar(20) NOT NULL DEFAULT 'ONLINE' COMMENT '状态 (ONLINE-在线, OFFLINE-离线, FAULTY-故障, BUSY-占用)',
    `last_online_time` datetime(3) DEFAULT NULL COMMENT '最后在线时间',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime(3) DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(50) DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime(3) DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_serial_number` (`serial_number`),
    KEY `idx_checkpoint_id` (`checkpoint_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='硬件设备表';

CREATE TABLE `p_lock_device` (
  `id` varchar(36) NOT NULL COMMENT '主键',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新日期',
  `device_no` varchar(255) DEFAULT NULL COMMENT '设备编号',
  `device_name` varchar(255) DEFAULT NULL COMMENT '设备名称',
  `parking_lot_id` varchar(36) DEFAULT NULL COMMENT '所属停车场ID',
  `parking_space_id` varchar(36) DEFAULT NULL COMMENT '绑定车位ID',
  `lock_status` int(11) DEFAULT NULL COMMENT '锁状态;0-降下，1-升起',
  `is_occupied` int(11) DEFAULT NULL COMMENT '占用状态;0-空闲, 1-占用',
  `battery_level` int(11) DEFAULT NULL COMMENT '电池电量',
  `signal_strength` int(11) DEFAULT NULL COMMENT '信号强度',
  `status` varchar(255) DEFAULT NULL COMMENT '设备状态;0-停用,1-正常,2-故障',
  `last_heartbeat_time` datetime DEFAULT NULL COMMENT '最后心跳时间',
  `dev_eui` varchar(255) DEFAULT NULL COMMENT 'LoRaWAN DevEUI',
  `app_eui` varchar(255) DEFAULT NULL COMMENT 'LoRaWAN AppEUI',
  `app_key` varchar(255) DEFAULT NULL COMMENT 'LoRaWAN AppKey',
  `tb_device_id` varchar(255) DEFAULT NULL COMMENT 'ThingsBoard设备ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
-- ----------------------------
-- 模块：车辆与套餐模块
-- ----------------------------

-- 车辆信息主表 (源表: cf_car_park_car) - 经过重新设计的标准模型
CREATE TABLE `p_vehicle` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `license_plate` varchar(20) NOT NULL COMMENT '车牌号',
  `owner_id` varchar(36) DEFAULT NULL COMMENT '车主用户ID (关联 sys_user.id)',
  `owner_name` varchar(50) DEFAULT NULL COMMENT '车主姓名 (冗余)',
  `owner_phone` varchar(20) DEFAULT NULL COMMENT '车主手机号 (冗余)',
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态(DELETED-已删除, PENDING-审核中, ACTIVE-正常)',
  `vehicle_type` varchar(50) DEFAULT NULL COMMENT '车辆类型 (关联数据字典, 如: temporary_car)',
  `image_url` varchar(255) DEFAULT NULL COMMENT '车辆图片URL',
  `brand` varchar(50) DEFAULT NULL COMMENT '车辆品牌',
  `model` varchar(50) DEFAULT NULL COMMENT '车辆型号',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `sys_org_code` varchar(64) DEFAULT NULL COMMENT '所属部门/租户',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_license_plate` (`license_plate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='车辆信息主表';

-- 特殊车辆放行策略表 (源表: cf_car_park_special_car)
CREATE TABLE `p_special_vehicle_policy` (
    `id` varchar(36) NOT NULL COMMENT '主键ID',
    `vehicle_id` varchar(36) NOT NULL COMMENT '车辆ID',
    `parking_lot_id` varchar(36) NOT NULL COMMENT '停车场ID',
    `policy_type` varchar(20) NOT NULL COMMENT '策略类型 (FREE_PASS-免费放行, FIXED_FEE-固定费用)',
    `start_time` datetime(3) NOT NULL COMMENT '策略生效开始时间',
    `end_time` datetime(3) NOT NULL COMMENT '策略生效结束时间',
    `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态 (ACTIVE-有效, EXPIRED-过期, CANCELLED-取消)',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='特殊车辆放行策略表';

-- 计费规则表 (源表: cf_car_park_charging_rules)
CREATE TABLE `p_charging_rule` (
    `id` varchar(36) NOT NULL COMMENT '主键ID',
    `parking_lot_id` varchar(36) NOT NULL COMMENT '停车场ID',
    `name` varchar(100) NOT NULL COMMENT '规则名称',
    `vehicle_type_id` varchar(36) NOT NULL COMMENT '车辆类型ID (关联 p_vehicle_type.id)',
    `group_flag` varchar(32) DEFAULT NULL COMMENT '规则分组标识 (用于区分同一天内不同时段的规则集)',
    `rule_type` varchar(20) NOT NULL DEFAULT 'HOURLY' COMMENT '计费类型 (HOURLY-按时, FIXED-按次)',
    `billing_model` varchar(50) DEFAULT 'static' COMMENT '计费模式 (static-静态/dynamic-动态)',
    `time_segments_json` json DEFAULT NULL COMMENT '分时段收费规则 (JSON格式)',
    `rate` decimal(10,2) DEFAULT NULL COMMENT '单价 (按次收费时使用)',
    `cap_per_day` decimal(10,2) DEFAULT NULL COMMENT '每日收费上限(封顶)',
    `cap_per_stay` decimal(10,2) DEFAULT NULL COMMENT '单次停车收费上限(封顶)',
    `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态 (1-启用, 0-禁用)',
    PRIMARY KEY (`id`),
    KEY `idx_parking_lot_id` (`parking_lot_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计费规则表';

-- 套餐价格表 (源表: cf_car_park_package_price)
CREATE TABLE `p_parking_package_price` (
    `id` varchar(36) NOT NULL COMMENT '主键ID',
    `parking_lot_id` varchar(36) NOT NULL COMMENT '停车场ID',
    `name` varchar(100) NOT NULL COMMENT '套餐名称',
    `duration_days` int(11) NOT NULL COMMENT '套餐时长(天)',
    `price` decimal(10,2) NOT NULL COMMENT '价格',
    `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态 (ACTIVE-可售, INACTIVE-停售)',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='套餐价格表';

-- 车辆套餐表 (源表: cf_car_park_package)
CREATE TABLE `p_parking_package` (
    `id` varchar(36) NOT NULL COMMENT '主键ID',
    `vehicle_id` varchar(36) NOT NULL COMMENT '车辆ID',
    `package_price_id` varchar(36) NOT NULL COMMENT '套餐价格ID',
    `start_time` datetime(3) NOT NULL COMMENT '套餐生效开始时间',
    `end_time` datetime(3) NOT NULL COMMENT '套餐生效结束时间',
    `order_id` varchar(36) DEFAULT NULL COMMENT '关联的订单ID',
    `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态 (ACTIVE-有效, EXPIRED-过期)',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime(3) DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(50) DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime(3) DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆套餐表';

-- 车辆类型表 (源表: cf_car_park_car_type)
CREATE TABLE `p_vehicle_type` (
    `id` varchar(36) NOT NULL COMMENT '主键ID',
    `type_key` varchar(50) NOT NULL COMMENT '类型标识 (如: temp_car, monthly_car)',
    `name` varchar(100) NOT NULL COMMENT '类型名称 (如: 临时车, 月租车)',
    `remark` varchar(255) DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_type_key` (`type_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆类型表';

-- 停车场-车型限制表 (源表: cf_car_park_car_limit)
CREATE TABLE `p_vehicle_type_limit` (
    `id` varchar(36) NOT NULL COMMENT '主键ID',
    `parking_lot_id` varchar(36) NOT NULL COMMENT '停车场ID',
    `vehicle_type_id` varchar(36) NOT NULL COMMENT '车辆类型ID',
    `is_allowed` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否允许通行 (1-是, 0-否)',
    `billing_model` varchar(50) DEFAULT NULL COMMENT '专属计费模式, NULL表示继承停车场配置',
    `fee_upper_limit_per_day` decimal(10,2) DEFAULT NULL COMMENT '专属每日费用上限, NULL表示继承停车场配置',
    `free_duration` int(11) DEFAULT NULL COMMENT '专属免费时长(分钟), NULL表示遵循停车场默认配置',
    `auto_open_gate` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否自动抬杆 (1-是, 0-否)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_lot_vehicle_type` (`parking_lot_id`,`vehicle_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='停车场-车型限制表';


-- ----------------------------
-- 模块：访客与预约模块
-- ----------------------------

-- 访客预约记录表 (源表: cf_car_park_visit)
CREATE TABLE `p_visitor_appointment` (
    `id` varchar(36) NOT NULL COMMENT '主键ID',
    `parking_lot_id` varchar(36) NOT NULL COMMENT '停车场ID',
    `license_plate` varchar(20) NOT NULL COMMENT '访客车牌号',
    `appointment_time` datetime(3) NOT NULL COMMENT '预约到访时间',
    `resident_user_id` varchar(36) DEFAULT NULL COMMENT '受访的住户/员工ID',
    `applicant_user_id` varchar(36) DEFAULT NULL COMMENT '申请人ID (可能是访客自己或住户)',
    `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态 (PENDING-待审核, APPROVED-已批准, REJECTED-已拒绝, VISITED-已到访, EXPIRED-已过期)',
    `process_time` datetime(3) DEFAULT NULL COMMENT '审核时间',
    `processor_id` varchar(36) DEFAULT NULL COMMENT '审核人ID',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime(3) DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(50) DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime(3) DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='访客预约记录表';


-- ----------------------------
-- 模块：业务记录与日志
-- ----------------------------

-- 停车记录表 (源表: cf_car_park_use_log) - 已根据前端需求和旧系统功能进行增强
CREATE TABLE `p_parking_record` (
    `id` varchar(36) NOT NULL COMMENT '主键ID',
    `parking_lot_id` varchar(36) NOT NULL COMMENT '停车场ID',
    `vehicle_id` varchar(36) DEFAULT NULL COMMENT '车辆ID (关联p_vehicle.id)',
    `license_plate` varchar(20) NOT NULL COMMENT '车牌号 (冗余)',
    `owner_name` varchar(50) DEFAULT NULL COMMENT '车主姓名 (冗余)',
    `vehicle_type` varchar(50) DEFAULT NULL COMMENT '车辆类型 (冗余, 关联字典 vehicle_type)',

    `entry_time` datetime(3) NOT NULL COMMENT '入场时间',
    `entry_checkpoint_id` varchar(36) DEFAULT NULL COMMENT '入口关卡ID',
    `entry_release_type` varchar(20) DEFAULT NULL COMMENT '入场放行方式 (关联字典 release_type)',
    `entry_image_url` varchar(255) DEFAULT NULL COMMENT '入场抓拍图片URL',
    
    `exit_time` datetime(3) DEFAULT NULL COMMENT '出场时间',
    `exit_checkpoint_id` varchar(36) DEFAULT NULL COMMENT '出口关卡ID',
    `exit_release_type` varchar(20) DEFAULT NULL COMMENT '出场放行方式 (关联字典 release_type)',
    `exit_image_url` varchar(255) DEFAULT NULL COMMENT '出场抓拍图片URL',

    `space_no` varchar(50) DEFAULT NULL COMMENT '停放的车位编号',
    
    `status` varchar(20) NOT NULL DEFAULT 'IN_PROGRESS' COMMENT '状态 (关联字典 p_record_status)',
    
    `payable_amount` decimal(10,2) DEFAULT NULL COMMENT '应付金额',
    `actual_amount` decimal(10,2) DEFAULT NULL COMMENT '实付金额',
    `discount_amount` decimal(10,2) DEFAULT '0.00' COMMENT '优惠金额',
    `pay_time` datetime(3) DEFAULT NULL COMMENT '支付时间',
    `payment_method` varchar(20) DEFAULT NULL COMMENT '支付方式 (关联字典 payment_method)',
    
    `billing_rule_id` varchar(36) DEFAULT NULL COMMENT '应用的计费规则ID',
    `remarks` varchar(500) DEFAULT NULL COMMENT '备注',
    
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
    `create_time` datetime(3) DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
    `update_time` datetime(3) DEFAULT NULL COMMENT '更新时间',
    `del_flag` int(1) DEFAULT 0 COMMENT '逻辑删除标志',

    PRIMARY KEY (`id`),
    KEY `idx_entry_time` (`entry_time`),
    KEY `idx_exit_time` (`exit_time`),
    KEY `idx_license_plate` (`license_plate`),
    KEY `idx_parking_lot_id` (`parking_lot_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='停车记录表';

-- 手动抬杆放行日志 (源表: cf_car_park_release_log)
CREATE TABLE `p_manual_release_log` (
    `id` varchar(36) NOT NULL COMMENT '主键ID',
    `parking_record_id` varchar(36) DEFAULT NULL COMMENT '关联的停车记录ID',
    `checkpoint_id` varchar(36) NOT NULL COMMENT '操作的关卡ID',
    `operator_id` varchar(36) NOT NULL COMMENT '操作员ID',
    `operation_time` datetime(3) NOT NULL COMMENT '操作时间',
    `reason` varchar(255) NOT NULL COMMENT '放行原因',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='手动抬杆放行日志';

-- 套餐购买/续费日志 (源表: cf_car_park_package_log)
CREATE TABLE `p_package_log` (
    `id` varchar(36) NOT NULL COMMENT '主键ID',
    `parking_package_id` varchar(36) NOT NULL COMMENT '车辆套餐ID',
    `action_type` varchar(20) NOT NULL COMMENT '操作类型 (PURCHASE-购买, RENEW-续费)',
    `details_json` json DEFAULT NULL COMMENT '操作详情 (如续费前的到期时间)',
    `operator_id` varchar(36) DEFAULT NULL COMMENT '操作员ID (后台操作时)',
    `operation_time` datetime(3) NOT NULL COMMENT '操作时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='套餐购买/续费日志';

-- 白名单下发日志 (源表: cf_car_park_whitelist_issued_log)
CREATE TABLE `p_whitelist_issue_log` (
    `id` varchar(36) NOT NULL COMMENT '主键ID',
    `device_id` varchar(36) NOT NULL COMMENT '硬件设备ID',
    `license_plate` varchar(20) NOT NULL COMMENT '下发的车牌号',
    `issue_time` datetime(3) NOT NULL COMMENT '下发时间',
    `status` varchar(20) NOT NULL COMMENT '状态 (SUCCESS-成功, FAILED-失败)',
    `response_message` text DEFAULT NULL COMMENT '设备响应信息',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='白名单下发日志';


-- ----------------------------
-- 模块：关联与统计
-- ----------------------------

-- 停车场-用户关联表 (源表: cf_car_park_link_user)
CREATE TABLE `p_parking_lot_user_link` (
    `id` varchar(36) NOT NULL COMMENT '主键ID',
    `parking_lot_id` varchar(36) NOT NULL COMMENT '停车场ID',
    `user_id` varchar(36) NOT NULL COMMENT '用户ID (关联 sys_user.id)',
    `role` varchar(50) NOT NULL COMMENT '用户在此停车场扮演的角色 (如: CHARGE_ADMIN, PATROL_STAFF)',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime(3) DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(50) DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime(3) DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_lot_user` (`parking_lot_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='停车场-用户关联表';

-- 每日运营统计报表 (源表: cf_daily_parking_statistcs)
CREATE TABLE `p_daily_stats_report` (
    `id` varchar(36) NOT NULL COMMENT '主键ID',
    `parking_lot_id` varchar(36) NOT NULL COMMENT '停车场ID',
    `report_date` date NOT NULL COMMENT '统计日期',
    `total_entries` int(11) NOT NULL DEFAULT '0' COMMENT '总入场车次',
    `total_exits` int(11) NOT NULL DEFAULT '0' COMMENT '总出场车次',
    `total_revenue` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '总收入',
    `avg_occupancy_rate` decimal(5,2) DEFAULT NULL COMMENT '日均占用率(%)',
    `peak_usage_hour` time DEFAULT NULL COMMENT '使用高峰时段',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_lot_date` (`parking_lot_id`,`report_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日运营统计报表';


-- ----------------------------
-- 模块：支付与账号
-- ----------------------------

-- 收款账号配置表 (源表: cf_user_payment_agency)
CREATE TABLE `p_payment_agency` (
    `id` varchar(36) NOT NULL COMMENT '主键ID',
    `user_id` varchar(36) NOT NULL COMMENT '所属用户ID (关联 sys_user.id)',
    `agency_name` varchar(100) NOT NULL COMMENT '账户名称 (如: 微信官方支付)',
    `channel_type` varchar(20) NOT NULL COMMENT '支付渠道 (WECHAT, ALIPAY)',
    `provider_key` varchar(50) DEFAULT NULL COMMENT '支付渠道提供商标识(如: yisheng_pay)',
    `use_scenes` varchar(50) DEFAULT NULL COMMENT '使用场景(关联第三方登录平台, 如: weixin_mp)',
    `merchant_id` varchar(100) DEFAULT NULL COMMENT '商户号(MCH_ID)',
    `app_id` varchar(100) DEFAULT NULL COMMENT '应用ID(APPID)',
    `secret_key` text DEFAULT NULL COMMENT '应用密钥(Secret)',
    `cert_path` varchar(255) DEFAULT NULL COMMENT '证书路径',
    `notify_url` varchar(255) DEFAULT NULL COMMENT '支付回调地址',
    `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认 (1-是, 0-否)',
    `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态 (ACTIVE-启用, INACTIVE-停用)',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime(3) DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(50) DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime(3) DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收款账号配置表'; 