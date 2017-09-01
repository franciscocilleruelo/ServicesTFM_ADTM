package es.uned.master.software.tfm.adtm.microservice.customer.consumer;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.uned.master.software.tfm.adtm.amqp.receiver.ReceiverConsumer;
import es.uned.master.software.tfm.adtm.microservice.customer.jpa.entity.Order;
import es.uned.master.software.tfm.adtm.microservice.customer.service.CustomerService;

@Component
public class CustomerConsumer extends ReceiverConsumer<Order> implements Serializable{

	private static final long serialVersionUID = 1729892484009760697L;

	private static final Logger log = LoggerFactory.getLogger(CustomerConsumer.class);
	
	@Autowired
	private CustomerService customerService;

	public CustomerConsumer() {
		super(Order.class);
	}

	@Override
	public boolean processRequest(Order order) {
		log.info("Comprobamos la disponibilidad de credito para el cliente {}", order.getCustomerId());
		return customerService.checkCredit(order);
	}

}
