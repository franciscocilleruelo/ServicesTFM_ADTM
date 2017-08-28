package es.uned.master.software.tfm.adtm.microservice.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import es.ADTM;

@SpringBootApplication
@Import(ADTM.class)
public class OrderServiceAdtmApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceAdtmApplication.class, args);
	}

} 
