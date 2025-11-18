package br.com.gym.management.gymapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class GymApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GymApiApplication.class, args);
	}

}
