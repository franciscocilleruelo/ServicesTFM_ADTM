package es.uned.master.software.tfm.adtm.microservice.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import es.uned.master.software.tfm.adtm.manager.TransactionManager;

@SpringBootApplication
public class OrderServiceAdtmApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceAdtmApplication.class, args);
	}
	
	@Bean
	public TransactionManager buildTransactionManager(){
		return new TransactionManager();
	}
} 
