package com.ait.userdata_project_springboot_camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.platform.http.springboot.SpringBootPlatformHttpConsumer;
import org.apache.camel.dsl.yaml.YamlRoutesBuilderLoader;
import org.apache.camel.model.ModelCamelContext;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.InputStream;
import java.util.Scanner;

@SpringBootApplication(scanBasePackages = "com.ait.userdata_project_springboot_camel")
@RegisterReflectionForBinding({
    RouteBuilder.class,
    ModelCamelContext.class,
    YamlRoutesBuilderLoader.class,
    SpringBootPlatformHttpConsumer.class,
    HttpServletRequest.class,
    HttpServletResponse.class
})
public class UserdataProjectSpringbootCamelApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(UserdataProjectSpringbootCamelApplication.class, args);
        // ApplicationContext context = SpringApplication.run(UserdataProjectSpringbootCamelApplication.class, args);
        // 验证 classpath 下 YAML 文件是否可加载
        // checkYamlFiles(context);

        // new ReflectionHintsConfig().registerHints(new RuntimeHints(), Thread.currentThread().getContextClassLoader());

        // 打印所有自动配置类
        // System.out.println("Loaded AutoConfigurations:");
        // for (String name : context.getBeanDefinitionNames()) {
        //     if (name.contains("config") || name.contains("ReflectionHintsConfig")) {
        //         System.out.println(name);
        //     }
        // }
        
        if (Boolean.getBoolean("exitAfterAgent")) {
            System.exit(0); 
        }
    }

    /**
     * 验证是否可以加载 classpath:routes/*.yaml 下的文件
     */
    public static void checkYamlFiles(ApplicationContext context) {
        try {
            // 使用 Spring 的资源加载机制匹配 classpath:routes/*.yaml
            String resourcePattern = "classpath:routes/*.yaml";
            Resource[] resources = context.getResources(resourcePattern);

            if (resources.length == 0) {
                System.out.println("No YAML files found in classpath:routes/");
                return;
            }

            // 遍历并打印 YAML 文件内容
            for (Resource resource : resources) {
                System.out.println("Found YAML file: " + resource.getFilename());
                try (InputStream inputStream = resource.getInputStream();
                     Scanner scanner = new Scanner(inputStream)) {
                    System.out.println("Content of " + resource.getFilename() + ":");
                    while (scanner.hasNextLine()) {
                        System.out.println(scanner.nextLine());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error occurred while loading YAML files: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
