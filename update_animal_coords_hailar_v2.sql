-- =====================================================================================
-- Description: [V2] 将模拟牲畜的地理坐标更新到内蒙古呼伦贝尔市海拉尔国家森林公园范围内
-- Goal: 确保坐标点落在更符合牧场场景的林区或草原地带，避开城市建筑和主干道。
-- Coordinates Range (approx): Lon [119.519, 119.734], Lat [49.110, 49.264]
-- =====================================================================================

-- 坐标点经过微调，使其分布在公园更核心、自然的区域
UPDATE `ah_animal` SET `last_location_lon` = 119.6835, `last_location_lat` = 49.2140 WHERE `ear_tag_id` = 'EAR-001';
UPDATE `ah_animal` SET `last_location_lon` = 119.6910, `last_location_lat` = 49.2250 WHERE `ear_tag_id` = 'EAR-002';
UPDATE `ah_animal` SET `last_location_lon` = 119.6750, `last_location_lat` = 49.2080 WHERE `ear_tag_id` = 'EAR-003';
UPDATE `ah_animal` SET `last_location_lon` = 119.7050, `last_location_lat` = 49.1950 WHERE `ear_tag_id` = 'EAR-004';

-- End of script 