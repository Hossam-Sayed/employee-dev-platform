package com.edp.tag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
        basePackages = {
                "com.edp.tag",
                "com.edp.shared.security.jwt"
        },
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.CUSTOM,
                        classes = {TypeExcludeFilter.class}
                ),
                @ComponentScan.Filter(
                        type = FilterType.CUSTOM,
                        classes = {AutoConfigurationExcludeFilter.class}
                )
        }
)
public class TagServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TagServiceApplication.class, args);
    }

}
