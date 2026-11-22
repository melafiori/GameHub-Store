package com.gamehub.warranty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@org.springframework.cloud.openfeign.EnableFeignClients
@SpringBootApplication
public class WarrantyApplication {

	public static void main(String[] args) {
		SpringApplication.run(WarrantyApplication.class, args);
	}

}
