package com.ait.userdata_project_springboot_camel.service;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.Resource;
import org.apache.camel.spi.ResourceLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

@Service
public class YamlRouteLoaderService {

    // 定義多個路由來源
    private static final List<String> ROUTE_DIRECTORIES = List.of(
            "classpath:routes",  // Spring Boot 專案的 routes 資料夾
            "/mnt"              // VM 機器的 /mnt 資料夾
    );

    private final CamelContext camelContext;

    public YamlRouteLoaderService(CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    /**
     * 加載所有路徑下的 YAML 路由。
     */
    public void loadYamlRoutes() {
        System.out.println("do loadYamlRoutes");
        ROUTE_DIRECTORIES.forEach(this::loadYamlRoutesFromPath);
    }

    /**
     * 從單一路徑加載 YAML 路由。
     *
     * @param path 路徑（可以是 classpath 或文件系統路徑）
     */
    private void loadYamlRoutesFromPath(String path) {
        try {
            if (path.startsWith("classpath:")) {
                // 處理 classpath 資源
                String resourcePath = path.substring("classpath:".length());
                Enumeration<URL> resources = getClass().getClassLoader().getResources(resourcePath);

                while (resources.hasMoreElements()) {
                    URL url = resources.nextElement();
                    loadYamlRouteFromResource(url.toString());
                }
            } else {
                // 處理文件系統路徑
                File routeDir = new File(path);
                if (!routeDir.exists() || !routeDir.isDirectory()) {
                    System.out.println("Route directory does not exist or is not a directory: " + path);
                    return;
                }

                File[] yamlFiles = routeDir.listFiles((dir, name) -> name.endsWith(".yaml"));
                if (yamlFiles != null) {
                    for (File file : yamlFiles) {
                        loadYamlRouteFromResource("file:" + file.getAbsolutePath());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load routes from path: " + path);
            e.printStackTrace();
        }
    }

    /**
     * 加載單一 YAML 文件為路由。
     *
     * @param resourcePath 資源路徑
     */
    private void loadYamlRouteFromResource(String resourcePath) {
        try {
            ResourceLoader resourceLoader = camelContext.getCamelContextExtension().getContextPlugin(ResourceLoader.class);
            Resource resource = resourceLoader.resolveResource(resourcePath);

             // 檢查是否已經存在相同的路由
            if (camelContext.getRoutes().stream()
                    .anyMatch(route -> route.getRouteId().equals(resourcePath))) {
                System.out.println("Route already exists: " + resourcePath);
                return;
            }

            // 動態加載 YAML 路由
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(resource.getLocation())
                            .routeId(resource.getLocation())
                            .log("Loaded route from YAML: " + resource.getLocation());
                }
            });

            System.out.println("Successfully loaded route from: " + resourcePath);
        } catch (Exception e) {
            System.err.println("Failed to load YAML route: " + resourcePath);
            e.printStackTrace();
        }
    }

    /**
     * 清除現有路由。
     */
    public void clearExistingRoutes() {
        System.out.println("do clearExistingRoutes");
        camelContext.getRoutes().forEach(route -> {
            try {
                camelContext.getRouteController().stopRoute(route.getRouteId());
                camelContext.removeRoute(route.getRouteId());
                System.out.println("Removed route: " + route.getRouteId());
            } catch (Exception e) {
                System.err.println("Failed to remove route: " + route.getRouteId());
                e.printStackTrace();
            }
        });
    }

    /**
     * 一次性刷新所有路由（清除並重新加載）。
     */
    public void reloadRoutes() {
        clearExistingRoutes();
        loadYamlRoutes();
    }
}
