-- =====================================================================================
-- Description: 将模拟牲畜的地理坐标更新到内蒙古呼伦贝尔市海拉尔国家森林公园范围内
-- Coordinates Range:
-- Longitude: 119°31′08″ E to 119°44′02″ E (approx. 119.5189° to 119.7339°)
-- Latitude:  49°06′36″ N to 49°15′49″ N (approx. 49.1100° to 49.2636°)
-- =====================================================================================

UPDATE `ah_animal` SET `last_location_lon` = 119.52, `last_location_lat` = 49.26 WHERE `ear_tag_id` = 'animal_001';
UPDATE `ah_animal` SET `last_location_lon` = 119.55, `last_location_lat` = 49.25 WHERE `ear_tag_id` = 'animal_002';
UPDATE `ah_animal` SET `last_location_lon` = 119.58, `last_location_lat` = 49.24 WHERE `ear_tag_id` = 'animal_003';
UPDATE `ah_animal` SET `last_location_lon` = 119.60, `last_location_lat` = 49.23 WHERE `ear_tag_id` = 'animal_004';
UPDATE `ah_animal` SET `last_location_lon` = 119.62, `last_location_lat` = 49.22 WHERE `ear_tag_id` = 'animal_005';
UPDATE `ah_animal` SET `last_location_lon` = 119.65, `last_location_lat` = 49.21 WHERE `ear_tag_id` = 'animal_006';
UPDATE `ah_animal` SET `last_location_lon` = 119.68, `last_location_lat` = 49.20 WHERE `ear_tag_id` = 'animal_007';
UPDATE `ah_animal` SET `last_location_lon` = 119.70, `last_location_lat` = 49.18 WHERE `ear_tag_id` = 'animal_008';
UPDATE `ah_animal` SET `last_location_lon` = 119.72, `last_location_lat` = 49.15 WHERE `ear_tag_id` = 'animal_009';
UPDATE `ah_animal` SET `last_location_lon` = 119.73, `last_location_lat` = 49.12 WHERE `ear_tag_id` = 'animal_010';

-- End of script 