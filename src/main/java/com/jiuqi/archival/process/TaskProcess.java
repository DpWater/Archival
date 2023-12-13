package com.jiuqi.archival.process;

import com.jiuqi.archival.interfaces.DataReader;
import com.jiuqi.archival.interfaces.DataTransform;
import com.jiuqi.archival.interfaces.DataWriter;
import com.jiuqi.archival.utils.ThreadUtils;
import com.jiuqi.archival.vo.TableDesc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Component
public class TaskProcess {

    private static final int CORE_POOL_SIZE = 50; // 核心线程数
    private static final int MAX_POOL_SIZE = 100; // 最大线程数
    private static final long KEEP_ALIVE_TIME = 1L; // 非核心线程空闲存活时间
    private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES; // 存活时间的时间单位
    private static final BlockingQueue<Runnable> WORK_QUEUE = new LinkedBlockingQueue<>(300); // 任务队列

    private static final Logger logger = LogManager.getLogger(TaskProcess.class);


    // 全局线程池实例
    private static ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TIME_UNIT,
            WORK_QUEUE,
//            todo 拒绝策略需要自定义，等满的时候需要延时等待
            new RejectionHandler()
    );
    ;


    private static void addTask(Runnable task) {
        THREAD_POOL_EXECUTOR.submit(task);
    }


    /**
     * @param dataReader
     * @param dataWriter
     * @param map
     */
    public static void doTaskItem(DataReader dataReader, DataWriter dataWriter, DataTransform dataTransform, Map map) {
//        这个该如何保证让一个批次走一个数值
        TableDesc tableDasc = dataReader.getColumn(map.get("tableName").toString());
        tableDasc = dataTransform.transFormData(tableDasc);
        TableDesc finalTableDasc = tableDasc;
//        int dynamicMaxPoolSize = ThreadUtils.getDynamicMaxPoolSize();
//        动态获取可用最大线程数
//        dynamicMaxPoolSize = Math.max(dynamicMaxPoolSize, THREAD_POOL_EXECUTOR.getCorePoolSize());
//        adjustThreadPoolSize(dynamicMaxPoolSize);
        addTask(new Runnable() {
            @Override
            public void run() {
                List list = dataReader.readData(map);
                dataWriter.checkAndInitTable(map.get("tableName").toString(), finalTableDasc);
                dataWriter.writeData(map, list, finalTableDasc);
                Logger logger1 = logger;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String timestamp = dateFormat.format(new Date());
                logger1.info(timestamp + "----------------------------查勘完成----------------------------------------");
            }
        });
    }

    public static void adjustThreadPoolSize(int newMaxPoolSize) {
        // 动态调整线程池的最大线程数
        THREAD_POOL_EXECUTOR.setMaximumPoolSize(newMaxPoolSize);
        THREAD_POOL_EXECUTOR.setCorePoolSize(newMaxPoolSize);
    }

    //自定义拒绝策略
    private static class RejectionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                // 等待一段时间再次尝试
                Thread.sleep(5000);
                // 再次提交任务
                executor.submit(r);
//                todo 需要做个累计 超过几次后即进入延时队列
            } catch (InterruptedException e) {
                // 重试过程中被中断，根据需要进行处理
                Thread.currentThread().interrupt();
                throw new RejectedExecutionException("Task interrupted during retry.", e);
            }
        }
    }
}
