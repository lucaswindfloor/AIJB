package org.jeecg.modules.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("parking_occupancy")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ParkingOccupancy {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    @TableField("parking_lot_id")
    private String parkingLotId;
    private String date;
    private Integer hour;
    @TableField("occupancy_rate")
    private BigDecimal occupancyRate;
}
