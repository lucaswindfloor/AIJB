-- 停车场表
CREATE TABLE p_parking_lot (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    total_spaces INT NOT NULL,
    address VARCHAR(200),
    status INT DEFAULT 1,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 停车场占用率记录表
CREATE TABLE p_parking_occupancy_rate (
    id BIGINT PRIMARY KEY,
    parking_lot_id BIGINT NOT NULL,
    occupancy_rate DECIMAL(5,2) NOT NULL,
    total_spaces INT NOT NULL,
    occupied_spaces INT NOT NULL,
    record_time TIMESTAMP NOT NULL,
    granularity VARCHAR(10) NOT NULL, -- day/week/month
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parking_lot_id) REFERENCES p_parking_lot(id)
);

-- 创建索引
CREATE INDEX idx_p_parking_occupancy_rate_time ON p_parking_occupancy_rate(record_time);
CREATE INDEX idx_p_parking_occupancy_rate_parking_lot ON p_parking_occupancy_rate(parking_lot_id);

-- 插入示例数据
INSERT INTO p_parking_lot (id, name, total_spaces, address, status) VALUES
(1, '中心停车场', 200, '中心广场', 1),
(2, '东区停车场', 150, '东区入口', 1),
(3, '西区停车场', 180, '西区入口', 1);
