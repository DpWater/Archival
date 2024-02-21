package com.jiuqi.archival.plugs.writer;

import com.jiuqi.archival.interfaces.DataWriter;
import com.jiuqi.archival.mapper.gdtarget.GDTargetMapper;
import com.jiuqi.archival.utils.SpringContextUtil;
import com.jiuqi.archival.vo.TableDesc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

public class MysqlWriter implements DataWriter<Map<String, String>> {

    private static final Logger logger = LogManager.getLogger(MysqlWriter.class);

    private static final String LOCK_KEY = "table_lock";
    private static final String TABLE_EXISTS_KEY = "tables_exists_hash_";

    private GDTargetMapper gdTargetMapper;
    private RedisTemplate redisTemplate;
    private RedissonClient redissonClient;


    public MysqlWriter() {
        gdTargetMapper = SpringContextUtil.getBean(GDTargetMapper.class);
        redisTemplate = (RedisTemplate) SpringContextUtil.getBean("redisTemplate");
        redissonClient = SpringContextUtil.getBean(RedissonClient.class);
    }

    @Override
    public void writeData(Map map, List<Map<String, String>> data, TableDesc desc) {
//        通过mybatis迭代，速度较慢
//        writeFunction(map, data);
//        自己拼接，速度提两倍
        writeFunctionNew(map, data);
        logger.info("完成");
    }

    private void writeFunctionNew(Map map, List<Map<String, String>> data) {
        String tableName = map.get("tableName").toString();
        StringBuilder valuesBuilder = new StringBuilder();
        StringBuilder columnBuilder = new StringBuilder("(");
        StringJoiner columnJoiner = new StringJoiner(",");
        boolean columnFlag = true;
        for (Map<String, String> row : data) {
            if (columnFlag) {
                for (String columnName : row.keySet()) {
                    columnJoiner.add(columnName);
                }
            }
            columnFlag = false;

            StringJoiner valueJoiner = new StringJoiner(", ", "(", ")");
            for (String fieldValue : row.values()) {
                valueJoiner.add("'" + fieldValue + "'");
            }
            if (valuesBuilder.length() > 0) {
                valuesBuilder.append(", ");
            }
            valuesBuilder.append(valueJoiner.toString());
        }
        columnBuilder.append(columnJoiner).append(")");
        String valuesClause = valuesBuilder.toString();
        gdTargetMapper.insertDataByTableNameByString(tableName, columnBuilder.toString(), valuesClause,data);
    }

    private void writeFunction(Map map, List<Map<String, String>> data) {
        String tableName = map.get("tableName").toString();
        gdTargetMapper.insertDataByTableName(tableName, data);
        logger.info("tableName");
    }

    @Override
    public void checkAndInitTable(String tableName, TableDesc tableDasc) {
        // 检查Redis，看是否表已经存在
        if (!redisTemplate.opsForHash().hasKey(TABLE_EXISTS_KEY, tableName)) {
            RLock lock = redissonClient.getLock(LOCK_KEY);
            try {
                // 尝试获取锁，参数1: 等待时间, 参数2: 锁的持有时间, 参数3: 时间单位
                if (lock.tryLock(100, 300, TimeUnit.SECONDS)) {
                    try {
                        // 再次检查是否已经创建了表
                        if (!redisTemplate.opsForHash().hasKey(TABLE_EXISTS_KEY, tableName)) {
//                            创建表
//                            todo 还需要支持创建索引
                            try {
                                gdTargetMapper.createTable(tableName, tableDasc);
                            } catch (Exception e) {
                                logger.error(e.getMessage());
                            }
                            redisTemplate.opsForHash().put(TABLE_EXISTS_KEY, tableName, "true");
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
