package dev.yaansz.MessageRegister;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MessageRegisterApplication {

	static void main(String[] args) {
		SpringApplication.run(MessageRegisterApplication.class, args);
	}

}
