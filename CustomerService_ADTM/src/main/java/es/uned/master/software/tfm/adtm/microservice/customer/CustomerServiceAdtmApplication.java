package es.uned.master.software.tfm.adtm.microservice.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import es.uned.master.software.tfm.adtm.manager.DistributedTransactionManager;
import es.uned.master.software.tfm.adtm.microservice.customer.consumer.OrderConsumer;

@SpringBootApplication
public class CustomerServiceAdtmApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceAdtmApplication.class, args);
	}
	
	@Value("${queue.customers.name}")
	private String customersQueueName;

	@Autowired
	private OrderConsumer orderConsumer;
	
	@Bean
	public DistributedTransactionManager buildTransactionManager(){
		DistributedTransactionManager transactionManager = new DistributedTransactionManager();
		transactionManager.recieveTransaction(customersQueueName, orderConsumer);
		return transactionManager;
	}
}
