package com.duoc.GameHubStore;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFeignClients
public class GameHubStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(GameHubStoreApplication.class, args);
	}

}
