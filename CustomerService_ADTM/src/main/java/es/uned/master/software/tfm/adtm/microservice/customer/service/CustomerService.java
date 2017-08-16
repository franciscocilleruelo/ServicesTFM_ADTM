package es.uned.master.software.tfm.adtm.microservice.customer.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.uned.master.software.tfm.adtm.microservice.customer.jpa.entity.Customer;
import es.uned.master.software.tfm.adtm.microservice.customer.jpa.entity.Order;
import es.uned.master.software.tfm.adtm.microservice.customer.jpa.entity.ReservedCredit;
import es.uned.master.software.tfm.adtm.microservice.customer.jpa.entity.ReservedCreditId;
import es.uned.master.software.tfm.adtm.microservice.customer.jpa.repository.CustomerRepository;
import es.uned.master.software.tfm.adtm.microservice.customer.jpa.repository.ReservedCreditRepository;


@Service
@Transactional
public class CustomerService {
	
	private static final Logger log = LoggerFactory.getLogger(CustomerService.class);
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private ReservedCreditRepository reservedCreditRepository;
	
	@Value("${queue.customers.name}")
	private String customersQueueName;
	
	public void insertExampleData(){
		customerRepository.save(new Customer(300));
		customerRepository.save(new Customer(8000));
		log.info("Inicializado repositorio de clientes con datos de ejemplo");
	}
	
	public List<Customer> findAll(){
		log.info("Busqueda de todos los clientes");
		return customerRepository.findAll();
	}
	
	public void checkLimit(Order order){
		Customer customer = customerRepository.findOne(order.getCustomerId());
		if (customer != null && customer.getCreditLimit()>=order.getTotal()){
			log.info("El limite de credito es superior a la cantidad solicitada por el pedido");
			log.info("Se establece el pedido como abierto (OPEN)");
			order.setStatus("OPEN");
			ReservedCreditId reservedCreditId = new ReservedCreditId(order.getOrderId(), order.getCustomerId());
			ReservedCredit reservedCredit = new ReservedCredit(reservedCreditId, order.getTotal());
			log.info("Se reserva el credito {} para el pedido {} del cliente {}", reservedCredit.getTotalReserved(), 
					reservedCredit.getReservedCreditId().getOrderId(), reservedCredit.getReservedCreditId().getCustomerId());
			reservedCreditRepository.save(reservedCredit);
		} else { // No existe el cliente o la cantidad del pedido supera el credito
			log.info("El limite de credito es inferior a la cantidad solicitada por el pedido");
			log.info("Se establece el pedido como rechazado (REJECTED)");
			order.setStatus("REJECTED");
		}
		log.info("Se envia el pedido con su estado modificado a la cola {} para ser procesador por el servicio de pedidos", customersQueueName);
	}

}
