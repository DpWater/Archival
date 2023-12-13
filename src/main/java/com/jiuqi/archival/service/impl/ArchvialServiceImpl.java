package com.jiuqi.archival.service.impl;

import com.jiuqi.archival.constdata.MQConstants;
import com.jiuqi.archival.dto.ArchivalDoJobDto;
import com.jiuqi.archival.dto.ArchivalDoJobMqDto;
import com.jiuqi.archival.mapper.mysqlsource.GDMapper;
import com.jiuqi.archival.service.ArchivalService;
import com.jiuqi.archival.utils.MapperJson;
import com.jiuqi.archival.utils.StringUtil;
import com.jiuqi.archival.vo.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ArchvialServiceImpl implements ArchivalService {

    private static final Logger logger = LogManager.getLogger(ArchvialServiceImpl.class);


    @Autowired
    GDMapper gdMapper;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    RedisTemplate redisTemplate;

    private static final String TABLE_EXISTS_JOB_KEY = "tables_exists_job_hash_";


    @Override
    public Result doArchivalJob(ArchivalDoJobDto archivalDoJobDto) {
        List<ArchivalDoJobDto.ArchivalTask> tableNames = archivalDoJobDto.getTableNames();
//        todo 校验，如果redis中有正在轮训的数据则不能进行同步
        for (ArchivalDoJobDto.ArchivalTask table : archivalDoJobDto.getTableNames()) {
            table.getTableName();
            if (redisTemplate.hasKey(table.getTableName())) {
                return new Result("失败，存在正在同步的表" + table.getTableName());
            }
        }
//        todo 校验 验证索引
        for (ArchivalDoJobDto.ArchivalTask archivalTask : tableNames) {
//            根据表名获取总量
            int num = gdMapper.getTotalNum(archivalTask.getTableName());
//            todo 根据表名获取合理批次数 没实现前定位一千
            int batchNume = 5000;
//            根据批次发放
            int i = 0;
            String index = StringUtil.isNullOrEmpty(archivalTask.getIndex()) ? "id" : archivalTask.getIndex();
            redisTemplate.opsForValue().set(archivalTask.getTableName(), true, 30, TimeUnit.MINUTES);
            while (i < num) {
//              todo   发送过程中要获取rabbitmq的条目数，如果超过则睡眠一阵子
                ArchivalDoJobMqDto bak = new ArchivalDoJobMqDto();
                bak.setStartIndex(i);
                bak.setEndIndex(i + batchNume);
                bak.setIndexName(index);
                bak.setTableName(archivalTask.getTableName());
//                todo 发送失败处理逻辑
                rabbitTemplate.convertAndSend(MQConstants.EXCHANGE_GD, MQConstants.QUEUE_GD, MapperJson.beanToJson(bak));
                i += batchNume;
            }
        }
        return null;
    }

}
