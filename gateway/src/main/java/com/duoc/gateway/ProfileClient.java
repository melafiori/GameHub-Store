package com.duoc.gateway;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

@FeignClient(name = "msvc-user")
public interface ProfileClient {
    @GetMapping("/api/v1/users/{id}")
    Map<String, Object> getProfile(@PathVariable("id") long id);
}
