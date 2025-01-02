package com.ait.userdata_project_springboot_camel.service;

import org.apache.camel.CamelContext;
import org.apache.camel.spi.ShutdownStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import com.ait.userdata_project_springboot_camel.UserdataProjectSpringbootCamelApplication;

@Service
public class AppRestartService {

    private final ConfigurableApplicationContext applicationContext;
    private final CamelContext camelContext;

    public AppRestartService(ConfigurableApplicationContext applicationContext, CamelContext camelContext) {
        this.applicationContext = applicationContext;
        this.camelContext = camelContext;
    }

    public void restartApplication() {
        Thread restartThread = new Thread(() -> {
            try {
                System.out.println("Graceful shutdown starting...");
    
                // 配置 Camel 的强制关闭策略
                configureCamelShutdownStrategy();
    
                // 关闭当前应用上下文
                int exitCode = SpringApplication.exit(applicationContext, () -> 0);
                System.out.println("Graceful shutdown complete.");
    
                // 重新启动应用程序
                // SpringApplication.run(applicationContext.getClass(), new String[]{});
                SpringApplication.run(UserdataProjectSpringbootCamelApplication.class, new String[]{});
                System.out.println("Application restarted successfully.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    
        // 确保线程非守护线程
        restartThread.setDaemon(false);
        restartThread.start();
    }

    private void configureCamelShutdownStrategy() {
        try {
            ShutdownStrategy shutdownStrategy = camelContext.getShutdownStrategy();
            shutdownStrategy.setTimeout(5); // 设置超时时间为 5 秒
            shutdownStrategy.setShutdownNowOnTimeout(true); // 启用超时后的强制关闭
            System.out.println("Configured Camel shutdown strategy: Force shutdown enabled.");
        } catch (Exception e) {
            System.err.println("Failed to configure Camel shutdown strategy.");
            e.printStackTrace();
        }
    }
}
