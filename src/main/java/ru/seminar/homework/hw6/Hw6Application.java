package ru.seminar.homework.hw6;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Clock;

@EnableScheduling
@SpringBootApplication
public class Hw6Application implements WebMvcConfigurer {

	@Bean
	public Clock clock() {
		return Clock.systemUTC();
	}

	public static void main(String[] args) {
		SpringApplication.run(Hw6Application.class, args);
	}
}
