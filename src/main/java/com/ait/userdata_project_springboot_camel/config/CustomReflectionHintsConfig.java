package com.ait.userdata_project_springboot_camel.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.camel.component.platform.http.springboot.SpringBootPlatformHttpConsumer;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CustomReflectionHintsConfig implements RuntimeHintsRegistrar {

    public CustomReflectionHintsConfig() {
        System.out.println("CustomReflectionHintsConfig instantiated...");
    }

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        System.out.println("do custom registerHints...");
        hints.reflection().registerType(SpringBootPlatformHttpConsumer.class, builder ->
                builder
                        .withMethod(
                                "service",
                                List.of(
                                        TypeReference.of(HttpServletRequest.class),
                                        TypeReference.of(HttpServletResponse.class)
                                ),
                                org.springframework.aot.hint.ExecutableMode.INVOKE
                        )
        );
    }
}