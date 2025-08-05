package com.edp.filemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.edp.filemanagement.client")
public class FileManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileManagementServiceApplication.class, args);
	}
}
