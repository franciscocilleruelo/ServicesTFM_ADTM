package es.uned.master.software.tfm.adtm.microservice.order.service;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.uned.master.software.tfm.adtm.entity.Transaction;
import es.uned.master.software.tfm.adtm.entity.TransactionExecutor;
import es.uned.master.software.tfm.adtm.manager.DistributedTransactionManager;
import es.uned.master.software.tfm.adtm.microservice.order.jpa.entity.Order;
import es.uned.master.software.tfm.adtm.microservice.order.jpa.repository.OrderRepository;
import es.uned.master.software.tfm.adtm.microservice.order.thread.NewOrderCommitThread;
import es.uned.master.software.tfm.adtm.microservice.order.thread.NewOrderRollbackThread;

@Service
@Transactional
public class OrderService implements Serializable{

	private static final long serialVersionUID = -8757659633801168330L;

	private static final Logger log = LoggerFactory.getLogger(OrderService.class);
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private DistributedTransactionManager transactionManager;
	
	@Value("${queue.orders.name}")
	private String ordersQueueName;
	
	@Value("${queue.customers.name}")
	private String customersQueueName;
	
	public void insertExampleData(){
		orderRepository.save(new Order("OPEN", 25));
		orderRepository.save(new Order("OPEN", 250));
		log.info("Inicializado repositorio de pedidos con datos de ejemplo");
	}
	
	public List<Order> findAll(){
		log.info("Busqueda de todos los pedidos");
		return orderRepository.findAll();
	}
	
	public Order createOrder(Order order){
		log.info("Se inicializa el estado del pedido a crear como nuevo (NEW)");
		order.setStatus("NEW");
		log.info("Se guarda el nuevo pedido");
		orderRepository.save(order);
		log.info("Se envia el pedido a la cola {} para ser procesado por el servicio de clientes", ordersQueueName);
		NewOrderCommitThread threadCommit = new NewOrderCommitThread(order, this);
		NewOrderRollbackThread threadRollback = new NewOrderRollbackThread(order, this);
		TransactionExecutor<NewOrderCommitThread, NewOrderRollbackThread> executor = new TransactionExecutor<NewOrderCommitThread, NewOrderRollbackThread>(threadCommit, threadRollback);
		Transaction<Order> transaction = new Transaction(order, executor, ordersQueueName, customersQueueName, 0);
		transactionManager.sendTransaction(transaction);
		return order;
	}
	
	public void update(Order order){
		log.info("Se actualiza el pedido");
		orderRepository.save(order);
	}

}
