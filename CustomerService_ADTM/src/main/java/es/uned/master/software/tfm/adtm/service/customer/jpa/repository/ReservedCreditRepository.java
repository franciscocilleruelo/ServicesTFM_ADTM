package es.uned.master.software.tfm.adtm.service.customer.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import es.uned.master.software.tfm.adtm.service.customer.jpa.entity.ReservedCredit;
import es.uned.master.software.tfm.adtm.service.customer.jpa.entity.ReservedCreditId;

@RepositoryRestResource
public interface ReservedCreditRepository extends JpaRepository<ReservedCredit, ReservedCreditId> {
	
}
