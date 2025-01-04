package br.com.nemi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("file:${user.dir}/api/.env")
public class NemiApplication {

	public static void main(String[] args) {
		SpringApplication.run(NemiApplication.class, args);
	}

}
