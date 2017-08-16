package es.uned.master.software.tfm.adtm.microservice.customer.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.uned.master.software.tfm.adtm.microservice.customer.jpa.entity.Customer;
import es.uned.master.software.tfm.adtm.microservice.customer.jpa.repository.CustomerRepository;


@Service
@Transactional
public class CustomerService {
	
	private static final Logger log = LoggerFactory.getLogger(CustomerService.class);
	
	@Autowired
	private CustomerRepository customerRepository;
	
	public void insertExampleData(){
		customerRepository.save(new Customer(300));
		customerRepository.save(new Customer(8000));
		log.info("Inicializado repositorio de clientes con datos de ejemplo");
	}
	
	public List<Customer> findAll(){
		log.info("Busqueda de todos los clientes");
		return customerRepository.findAll();
	}

}
