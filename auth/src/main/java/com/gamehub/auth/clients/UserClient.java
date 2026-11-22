package com.gamehub.auth.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "msvc-user")
public interface UserClient {

    @PostMapping("/internal/registrations/{id}")
    Map<String, Object> register(@PathVariable("id") String id, @RequestHeader("X-Service-Secret") String secret,
                                 @RequestBody Map<String, Object> profile);

    @GetMapping("/api/v1/users/search")
    ResponseEntity<Object> getByEmail(@RequestParam("email") String email);
}
