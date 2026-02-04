package br.sst.auditoria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SstApplication {

	public static void main(String[] args) {
		SpringApplication.run(SstApplication.class, args);
	}

}
