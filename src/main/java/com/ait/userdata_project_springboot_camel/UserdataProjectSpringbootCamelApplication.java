package com.ait.userdata_project_springboot_camel;

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RegisterReflectionForBinding({
    io.swagger.v3.oas.models.OpenAPI.class,
    io.swagger.v3.oas.models.info.Info.class
})
public class UserdataProjectSpringbootCamelApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserdataProjectSpringbootCamelApplication.class, args);
	}

}
