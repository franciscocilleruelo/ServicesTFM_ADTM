package es.uned.master.software.tfm.adtm.service.order.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import es.uned.master.software.tfm.adtm.service.order.jpa.entity.Order;

@RepositoryRestResource
public interface OrderRepository extends JpaRepository<Order, Long> {
	
}
