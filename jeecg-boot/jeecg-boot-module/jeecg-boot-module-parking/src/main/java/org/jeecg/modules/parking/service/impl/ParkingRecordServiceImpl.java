package org.jeecg.modules.parking.service.impl;

import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.parking.entity.ParkingRecord;
import org.jeecg.modules.parking.mapper.ParkingRecordMapper;
import org.jeecg.modules.parking.service.IParkingRecordService;
import org.jeecg.modules.parking.vo.ParkingRecordPage;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

/**
 * @Description: 停车记录表
 * @Author: jeecg-boot
 * @Date:   2024-08-12
 * @Version: V1.0
 */
@Service
public class ParkingRecordServiceImpl extends ServiceImpl<ParkingRecordMapper, ParkingRecord> implements IParkingRecordService {

    @Override
    public IPage<ParkingRecordPage> queryParkingRecordPage(Page<ParkingRecordPage> page, Map<String, String[]> params) {
        QueryWrapper<ParkingRecord> queryWrapper = buildQueryWrapper(params);
        IPage<ParkingRecordPage> pageList = this.baseMapper.getParkingRecordPage(page, queryWrapper);

        // 数据后处理，计算停车时长
        pageList.getRecords().forEach(record -> {
            if (record.getEntryTime() != null && record.getExitTime() != null) {
                record.setDuration(formatDuration(record.getEntryTime(), record.getExitTime()));
            } else if (record.getEntryTime() != null) {
                record.setDuration(formatDuration(record.getEntryTime(), new Date()));
            }
        });

        return pageList;
    }

    private QueryWrapper<ParkingRecord> buildQueryWrapper(Map<String, String[]> params) {
        QueryWrapper<ParkingRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pr.del_flag", 0); // 默认查询未删除的记录

        // 遍历前端传递的参数，构建查询条件
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();

            if (values == null || values.length == 0 || StringUtils.isEmpty(values[0])) {
                continue;
            }
            String value = values[0];
            // 跳过分页参数
            if (key.equals("pageNo") || key.equals("pageSize") || key.equals("column") || key.equals("order")) {
                continue;
            }

            if (key.endsWith("_like")) {
                String field = oConvertUtils.camelToUnderline(key.substring(0, key.length() - 5));
                if ("parking_lot_name".equals(field)) {
                    queryWrapper.like("pl.name", value);
                } else {
                    queryWrapper.like("pr." + field, value);
                }
            } else if (key.endsWith("_begin")) {
                String field = oConvertUtils.camelToUnderline(key.substring(0, key.length() - 6));
                queryWrapper.ge("pr." + field, value);
            } else if (key.endsWith("_end")) {
                String field = oConvertUtils.camelToUnderline(key.substring(0, key.length() - 4));
                queryWrapper.le("pr." + field, value);
            }
            // 处理特殊的状态查询
            else if ("queryStatus".equals(key)) {
                switch (value) {
                    case "PARKING": // 在场中
                        queryWrapper.isNull("pr.exit_time");
                        break;
                    case "AWAITING_PAYMENT": // 待支付
                        queryWrapper.isNotNull("pr.exit_time").isNull("pr.pay_time").gt("pr.payable_amount", 0);
                        break;
                    case "COMPLETED": // 已完成
                        queryWrapper.isNotNull("pr.pay_time").or().eq("pr.payable_amount", 0);
                        break;
                    default: // 其他情况
                        break;
                }
            }
            else {
                // 默认等于查询
                String field = oConvertUtils.camelToUnderline(key);
                queryWrapper.eq("pr." + field, value);
            }
        }
        // 按入场时间倒序
        queryWrapper.orderByDesc("pr.entry_time");
        return queryWrapper;
    }

    private String formatDuration(Date start, Date end) {
        if (start == null || end == null) {
            return "";
        }
        LocalDateTime startTime = Instant.ofEpochMilli(start.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime endTime = Instant.ofEpochMilli(end.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        Duration duration = Duration.between(startTime, endTime);

        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("天");
        }
        if (hours > 0) {
            sb.append(hours).append("小时");
        }
        // 如果不足一分钟，显示0分钟
        if (sb.length() == 0 && minutes <= 0) {
             return "0分钟";
        }
        if (minutes > 0) {
            sb.append(minutes).append("分钟");
        }
        return sb.toString();
    }
} 