package com.jiuqi.archival.utils;

public class ThreadUtils {

    public static int getDynamicMaxPoolSize() {
        Runtime runtime = Runtime.getRuntime();
        // 获取可用的处理器核心数
        int availableProcessors = runtime.availableProcessors();
        // 获取JVM最大可用内存
        long maxMemory = runtime.maxMemory();
        // 假设每个线程栈的内存消耗为1MB
        long threadMemoryConsumption = 1024 * 1024;

        // 计算理论上能创建的最大线程数
        int maxThreads = (int) (maxMemory / threadMemoryConsumption);
        // 获取当前活跃线程的估计数
        int activeThreadCount = Thread.activeCount();

        // 计算线程池的理论最大线程数
        int poolMaxThreads = maxThreads - activeThreadCount;

        // 确保最大线程数不低于处理器核心数
        poolMaxThreads = Math.max(poolMaxThreads, availableProcessors);
        return poolMaxThreads;
    }


}
