package es.uned.master.software.tfm.adtm.microservice.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import es.AdtmModule;
import es.uned.master.software.tfm.adtm.microservice.order.service.OrderService;

@SpringBootApplication
@Import(AdtmModule.class)
public class OrderServiceAdtmApplication {
	
	private static final Logger log = LoggerFactory.getLogger(OrderServiceAdtmApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceAdtmApplication.class, args);
	}
	
	@Bean
	CommandLineRunner init(OrderService orderService){
		return args -> {
			orderService.insertExampleData();
			orderService.findAll().forEach(entry -> log.info(entry.toString()));
		};
	}

} 
