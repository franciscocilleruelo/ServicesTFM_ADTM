package es.uned.master.software.tfm.adtm.microservice.customer.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
	
	public void insertExampleData(){
		customerRepository.save(new Customer(300));
		customerRepository.save(new Customer(8000));
		log.info("Inicializado repositorio de clientes con datos de ejemplo");
	}
	
	public List<Customer> findAll(){
		log.info("Busqueda de todos los clientes");
		return customerRepository.findAll();
	}
	
	public boolean checkCredit(Order order){
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
