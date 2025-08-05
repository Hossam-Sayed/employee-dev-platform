package com.edp.shared.client.auth;

import com.edp.shared.client.auth.model.UserProfileDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;


@FeignClient(name = "auth-service", url = "${auth.service.url}")
public interface AuthServiceClient {

    @GetMapping("/api/users/{id}")
    UserProfileDto getUserById(@PathVariable("id") Long id, @RequestHeader("Authorization") String token);

    @GetMapping("/api/users/{managerId}/managed")
    List<UserProfileDto> getManagedUsers(@PathVariable("managerId") Long managerId, @RequestHeader("Authorization") String token);
}
