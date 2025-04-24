package com.uasjava.tiketbioskop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TiketbioskopApplication {

	public static void main(String[] args) {
		SpringApplication.run(TiketbioskopApplication.class, args);
	}

}
