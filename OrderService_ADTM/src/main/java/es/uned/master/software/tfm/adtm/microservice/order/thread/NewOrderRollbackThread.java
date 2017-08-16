package es.uned.master.software.tfm.adtm.microservice.order.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.uned.master.software.tfm.adtm.microservice.order.jpa.entity.Order;
import es.uned.master.software.tfm.adtm.microservice.order.service.OrderService;

public class NewOrderRollbackThread implements Runnable {
	
	private static final Logger log = LoggerFactory.getLogger(NewOrderRollbackThread.class);
	
	private Order order;
	private OrderService orderService;
	
	public NewOrderRollbackThread(Order order, OrderService orderService) {
		super();
		this.order = order;
		this.orderService = orderService;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public OrderService getOrderService() {
		return orderService;
	}

	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	@Override
	public void run() {
		log.info("Se actualiza el pedido a OPEN");
		order.setStatus("REJECTED");
		orderService.update(order);
	}

}
