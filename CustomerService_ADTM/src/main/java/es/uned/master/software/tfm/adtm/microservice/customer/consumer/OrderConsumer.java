package es.uned.master.software.tfm.adtm.microservice.customer.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.uned.master.software.tfm.adtm.amqp.receiver.ReceiverConsumer;
import es.uned.master.software.tfm.adtm.microservice.customer.jpa.entity.Customer;
import es.uned.master.software.tfm.adtm.microservice.customer.jpa.entity.Order;
import es.uned.master.software.tfm.adtm.microservice.customer.jpa.entity.ReservedCredit;
import es.uned.master.software.tfm.adtm.microservice.customer.jpa.entity.ReservedCreditId;
import es.uned.master.software.tfm.adtm.microservice.customer.jpa.repository.CustomerRepository;
import es.uned.master.software.tfm.adtm.microservice.customer.jpa.repository.ReservedCreditRepository;

@Component
public class OrderConsumer extends ReceiverConsumer<Order> {
	
	private static final Logger log = LoggerFactory.getLogger(OrderConsumer.class);
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private ReservedCreditRepository reservedCreditRepository;

	@Autowired
	public OrderConsumer(RabbitTemplate rabbitTemplate) {
		super(rabbitTemplate);
	}

	@Override
	public boolean processRequest(Order requestObject) {
		Customer customer = customerRepository.findOne(requestObject.getCustomerId());
		if (customer != null && customer.getCreditLimit()>=requestObject.getTotal()){
			log.info("El limite de credito es superior a la cantidad solicitada por el pedido");
			ReservedCreditId reservedCreditId = new ReservedCreditId(requestObject.getOrderId(), requestObject.getCustomerId());
			ReservedCredit reservedCredit = new ReservedCredit(reservedCreditId, requestObject.getTotal());
			log.info("Se reserva el credito {} para el pedido {} del cliente {}", reservedCredit.getTotalReserved(), 
					reservedCredit.getReservedCreditId().getOrderId(), reservedCredit.getReservedCreditId().getCustomerId());
			reservedCreditRepository.save(reservedCredit);
			return true;
		} else { // No existe el cliente o la cantidad del pedido supera el credito
			log.info("El limite de credito es inferior a la cantidad solicitada por el pedido");
			return false;
		}
		
	}

}
