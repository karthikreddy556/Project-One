package org.mifosplatform.organisation.address.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CityRepository extends JpaRepository<City,Long>,JpaSpecificationExecutor<City>{

}
