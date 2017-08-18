package es.uned.master.software.tfm.adtm.microservice.customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import es.AdtmModule;
import es.uned.master.software.tfm.adtm.manager.DistributedTransactionManager;
import es.uned.master.software.tfm.adtm.microservice.customer.consumer.OrderConsumer;
import es.uned.master.software.tfm.adtm.microservice.customer.service.CustomerService;

@SpringBootApplication
@Import(AdtmModule.class)
public class CustomerServiceAdtmApplication {
	
	private static final Logger log = LoggerFactory.getLogger(CustomerServiceAdtmApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceAdtmApplication.class, args);
	}
	
	@Bean
	CommandLineRunner init(CustomerService customerService){
		return args -> {
			customerService.insertExampleData();
			customerService.findAll().forEach(entry -> log.info(entry.toString()));
		};
	}
	
	@Value("${queue.orders.name}")
	private String ordersQueueName;

	@Autowired
	private OrderConsumer orderConsumer;
	
	@Bean
	public DistributedTransactionManager buildTransactionManager(DistributedTransactionManager transactionManager){
		transactionManager.recieveTransaction(ordersQueueName, orderConsumer);
		return transactionManager;
	}
}
