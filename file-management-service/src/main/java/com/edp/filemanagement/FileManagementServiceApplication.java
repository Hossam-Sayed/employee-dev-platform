package com.edp.filemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.edp.shared.client.auth")
@ComponentScan(basePackages = {"com.edp.filemanagement",
		"com.edp.shared.security.jwt"},
		excludeFilters = {@ComponentScan.Filter(
				type = FilterType.CUSTOM,
				classes = {TypeExcludeFilter.class}
		), @ComponentScan.Filter(
				type = FilterType.CUSTOM,
				classes = {AutoConfigurationExcludeFilter.class}
		)}
)
public class FileManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileManagementServiceApplication.class, args);
	}
}
