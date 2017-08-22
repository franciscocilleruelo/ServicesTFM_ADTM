package es.uned.master.software.tfm.adtm.microservice.customer.consumer;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import es.uned.master.software.tfm.adtm.amqp.receiver.ReceiverConsumer;
import es.uned.master.software.tfm.adtm.microservice.customer.jpa.entity.Customer;
import es.uned.master.software.tfm.adtm.microservice.customer.jpa.entity.Order;
import es.uned.master.software.tfm.adtm.microservice.customer.jpa.entity.ReservedCredit;
import es.uned.master.software.tfm.adtm.microservice.customer.jpa.entity.ReservedCreditId;
import es.uned.master.software.tfm.adtm.microservice.customer.jpa.repository.CustomerRepository;
import es.uned.master.software.tfm.adtm.microservice.customer.jpa.repository.ReservedCreditRepository;

@Component
public class OrderConsumer extends ReceiverConsumer<Order> implements Serializable{

	private static final long serialVersionUID = 1729892484009760697L;

	private static final Logger log = LoggerFactory.getLogger(OrderConsumer.class);
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private ReservedCreditRepository reservedCreditRepository;

	@Autowired
	public OrderConsumer(RabbitTemplate rabbitTemplate) {
		super(rabbitTemplate, Order.class);
	}

	@Override
	public boolean processRequest(Order order) {
		log.info("Recuperamos el cliente {} asociado al pedido", order.getCustomerId());
		Customer customer = customerRepository.findOne(order.getCustomerId());
		int reservedCreditNow = 0;
		String reservedCreditNowS = reservedCreditRepository.sumReserverCreditByCustomerId(order.getCustomerId());
		if (StringUtils.hasText(reservedCreditNowS)){
			reservedCreditNow = Integer.valueOf(reservedCreditNowS);
		}
		log.info("El credito reservado del cliente {} para otros pedidos es de {}", order.getCustomerId(), reservedCreditNow);
		if (customer != null && customer.getCreditLimit() >= order.getTotal() + reservedCreditNow){
			log.info("El limite de credito para el cliente {} es superior a la suma de la cantidad solicitada para el pedido ({}) mas el credito reservado para otros pedidos ({})"
					, order.getCustomerId(), order.getOrderId(), reservedCreditNow);
			ReservedCreditId reservedCreditId = new ReservedCreditId(order.getOrderId(), order.getCustomerId());
			ReservedCredit reservedCredit = new ReservedCredit(reservedCreditId, order.getTotal());
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
