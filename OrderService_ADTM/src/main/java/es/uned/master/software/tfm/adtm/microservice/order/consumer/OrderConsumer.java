package es.uned.master.software.tfm.adtm.microservice.order.consumer;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.uned.master.software.tfm.adtm.amqp.sender.SenderConsumer;
import es.uned.master.software.tfm.adtm.microservice.order.jpa.entity.Order;
import es.uned.master.software.tfm.adtm.microservice.order.service.OrderService;

@Component
public class OrderConsumer extends SenderConsumer<Order> implements Serializable{

	private static final long serialVersionUID = -6922109684903455774L;

	private static final Logger log = LoggerFactory.getLogger(OrderConsumer.class);
	
	@Autowired
	private OrderService orderService;
	
	public OrderConsumer() {
		super(Order.class);
	}

	@Override
	public void commit(Order order) {
		log.info("Se actualiza el pedido a OPEN");
		order.setStatus("OPEN");
		orderService.update(order);
	}

	@Override
	public void rollback(Order order) {
		log.info("Se actualiza el pedido a OPEN");
		order.setStatus("REJECTED");
		orderService.update(order);
	}

}
