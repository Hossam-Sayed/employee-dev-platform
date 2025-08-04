package com.edp.careerpackage.client;

import com.edp.careerpackage.client.model.UserProfileDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(name = "auth-service", url = "${auth.service.url}")
public interface AuthServiceClient {

    @GetMapping("/api/users/{id}")
    UserProfileDto getUserById(@PathVariable("id") Long id, @RequestHeader("Authorization") String bearerToken);

//    @GetMapping("/api/users/whatever")
//    List<Long> getManagedUserIds();
}
