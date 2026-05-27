package com.gamehub.auth.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "msvc-user", url ="localhost:8004/api/v1/users")
public interface UserClient {

    @GetMapping("/search")
    ResponseEntity<Object> getByEmail(@RequestParam("email") String email);
}
