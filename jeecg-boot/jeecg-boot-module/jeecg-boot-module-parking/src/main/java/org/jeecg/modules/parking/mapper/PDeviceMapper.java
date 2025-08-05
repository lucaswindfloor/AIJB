package org.jeecg.modules.parking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.parking.entity.PDevice;

import java.util.List;

/**
 * @Description: 硬件设备管理
 * @Author: jeecg-boot
 * @Date: 2024-01-01
 * @Version: V1.0
 */
public interface PDeviceMapper extends BaseMapper<PDevice> {

    /**
     * 分页查询设备列表（包含关联信息）
     * 此方法对应旧系统的 selectListByCondition 功能
     * 支持关联查询停车场、关卡信息，并提供字典翻译支持
     */
    @Select("SELECT " +
            "    d.id, " +
            "    d.checkpoint_id, " +
            "    d.name, " +
            "    d.serial_number, " +
            "    d.device_no, " +
            "    d.mac_address, " +
            "    d.device_type, " +
            "    d.brand, " +
            "    d.model, " +
            "    d.ip_address, " +
            "    d.username, " +
            "    d.password, " +
            "    d.stream_url, " +
            "    d.status, " +
            "    d.last_online_time, " +
            "    d.create_by, " +
            "    d.create_time, " +
            "    d.update_by, " +
            "    d.update_time, " +
            "    d.sys_org_code, " +
            "    c.name as checkpointName, " +
            "    c.direction, " +
            "    c.parking_lot_id as parkingLotId, " +
            "    l.name as parkingLotName " +
            "FROM p_device d " +
            "LEFT JOIN p_checkpoint c ON d.checkpoint_id = c.id " +
            "LEFT JOIN p_parking_lot l ON c.parking_lot_id = l.id " +
            "WHERE 1=1 " +
            "${ew.customSqlSegment}")
    IPage<PDevice> selectDevicePageWithJoin(Page<PDevice> page, @Param("ew") com.baomidou.mybatisplus.core.conditions.Wrapper<PDevice> queryWrapper);

    /**
     * 查询设备列表（包含关联信息） - 不分页
     */
    @Select("SELECT " +
            "    d.id, " +
            "    d.checkpoint_id, " +
            "    d.name, " +
            "    d.serial_number, " +
            "    d.device_no, " +
            "    d.mac_address, " +
            "    d.device_type, " +
            "    d.brand, " +
            "    d.model, " +
            "    d.ip_address, " +
            "    d.username, " +
            "    d.password, " +
            "    d.stream_url, " +
            "    d.status, " +
            "    d.last_online_time, " +
            "    d.create_by, " +
            "    d.create_time, " +
            "    d.update_by, " +
            "    d.update_time, " +
            "    d.sys_org_code, " +
            "    c.name as checkpointName, " +
            "    c.direction, " +
            "    c.parking_lot_id as parkingLotId, " +
            "    l.name as parkingLotName " +
            "FROM p_device d " +
            "LEFT JOIN p_checkpoint c ON d.checkpoint_id = c.id " +
            "LEFT JOIN p_parking_lot l ON c.parking_lot_id = l.id " +
            "WHERE 1=1 " +
            "${ew.customSqlSegment}")
    List<PDevice> selectDeviceListWithJoin(@Param("ew") com.baomidou.mybatisplus.core.conditions.Wrapper<PDevice> queryWrapper);

    /**
     * 根据序列号查询设备信息（用于远程控制）
     */
    @Select("SELECT " +
            "    d.*, " +
            "    c.name as checkpointName, " +
            "    c.direction, " +
            "    c.parking_lot_id as parkingLotId, " +
            "    l.name as parkingLotName " +
            "FROM p_device d " +
            "LEFT JOIN p_checkpoint c ON d.checkpoint_id = c.id " +
            "LEFT JOIN p_parking_lot l ON c.parking_lot_id = l.id " +
            "WHERE d.serial_number = #{serialNumber}")
    PDevice selectBySerialNumber(@Param("serialNumber") String serialNumber);

    /**
     * 根据MAC地址查询设备信息（用于设备注册）
     */
    @Select("SELECT " +
            "    d.*, " +
            "    c.name as checkpointName, " +
            "    c.direction, " +
            "    c.parking_lot_id as parkingLotId, " +
            "    l.name as parkingLotName " +
            "FROM p_device d " +
            "LEFT JOIN p_checkpoint c ON d.checkpoint_id = c.id " +
            "LEFT JOIN p_parking_lot l ON c.parking_lot_id = l.id " +
            "WHERE d.mac_address = #{macAddress}")
    PDevice selectByMacAddress(@Param("macAddress") String macAddress);

    /**
     * 批量更新设备最后在线时间
     */
    @Select("UPDATE p_device SET last_online_time = NOW() WHERE serial_number IN " +
            "<foreach collection='serialNumbers' item='serialNumber' open='(' separator=',' close=')'>" +
            "#{serialNumber}" +
            "</foreach>")
    int batchUpdateLastOnlineTime(@Param("serialNumbers") List<String> serialNumbers);

    /**
     * 查询指定时间内未上线的设备（用于设备监控）
     */
    @Select("SELECT " +
            "    d.*, " +
            "    c.name as checkpointName, " +
            "    c.direction, " +
            "    c.parking_lot_id as parkingLotId, " +
            "    l.name as parkingLotName " +
            "FROM p_device d " +
            "LEFT JOIN p_checkpoint c ON d.checkpoint_id = c.id " +
            "LEFT JOIN p_parking_lot l ON c.parking_lot_id = l.id " +
            "WHERE d.last_online_time < DATE_SUB(NOW(), INTERVAL #{minutes} MINUTE) " +
            "AND d.status = 'ONLINE'")
    List<PDevice> selectOfflineDevices(@Param("minutes") int minutes);

    /**
     * 根据停车场ID查询设备列表
     */
    @Select("SELECT " +
            "    d.*, " +
            "    c.name as checkpointName, " +
            "    c.direction, " +
            "    c.parking_lot_id as parkingLotId, " +
            "    l.name as parkingLotName " +
            "FROM p_device d " +
            "LEFT JOIN p_checkpoint c ON d.checkpoint_id = c.id " +
            "LEFT JOIN p_parking_lot l ON c.parking_lot_id = l.id " +
            "WHERE c.parking_lot_id = #{parkingLotId}")
    List<PDevice> selectByParkingLotId(@Param("parkingLotId") String parkingLotId);

    /**
     * 根据关卡ID查询设备列表
     */
    @Select("SELECT " +
            "    d.*, " +
            "    c.name as checkpointName, " +
            "    c.direction, " +
            "    c.parking_lot_id as parkingLotId, " +
            "    l.name as parkingLotName " +
            "FROM p_device d " +
            "LEFT JOIN p_checkpoint c ON d.checkpoint_id = c.id " +
            "LEFT JOIN p_parking_lot l ON c.parking_lot_id = l.id " +
            "WHERE d.checkpoint_id = #{checkpointId}")
    List<PDevice> selectByCheckpointId(@Param("checkpointId") String checkpointId);
} 