package com.freetsinghua.redisdemo2.util;

import com.freetsinghua.redisdemo2.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author z.tsinghua
 * @date 2018/10/31
 */
@Slf4j
class CacheUtils {
    private static final JdbcTemplate JDBC_TEMPLATE = BeanManager.getJdbcTemplate();
    private static final RedisHashUtils REDIS_HASH_UTILS = RedisUtils.getRedisHashUtils();

    /** 从Redis刷新到MySQL中 */
    static synchronized void redisToMySql(String pattern) {

        Map<String, Message> entries = REDIS_HASH_UTILS.entries(pattern);

        if (JDBC_TEMPLATE == null) {
            log.error("jdbcTemplate还没有初始化.");
            return;
        }

        log.info("开始从Redis刷新数据到MySQL!");
        String sql = "INSERT INTO messages(id, src, dest, msg, remark) VALUES(?, ?, ?, ?, ?)";
        List<Object> params = new ArrayList<>();
        // 计数
        long count = 0L;
        // 使用批量删除，但
        // 有一个问题，就是，若是在刷入的时候，同时存入数据，那么那些存入的数据，就只能等待下一次
        // 这个问题需要解决，否则，若是又再次检测，那么那些数据还是在Redis中，会再次刷入
        for (Map.Entry<String, Message> entry : entries.entrySet()) {
            Message value = entry.getValue();

            // 清除之前那一次的数据
            params.clear();
            params.add(value.getId());
            params.add(value.getSrc());
            params.add(value.getDest());
            params.add(value.getMsg());
            params.add(value.getRemark());

            try {
                JDBC_TEMPLATE.update(sql, params.toArray(new Object[] {}));
                REDIS_HASH_UTILS.delete(entry.getKey());
                log.info("成功从Redis刷入【{}】条数据到MySQL!", ++count);
            } catch (Exception e) {
                // 如果是主键冲突，不再尝试，直接从Redis中删除，毕竟是同一个对象实例
                if (e.getMessage().contains("Duplicate")) {
                    REDIS_HASH_UTILS.delete(entry.getKey());
                } else { // 如果不是主键冲突，那么等待下一次刷入，打印错误日志
                    log.error("从Redis刷新到MySQL失败: 【{}】", e.getMessage());
                }
            }
        }
    }
}
