package com.roccoshi.micro.repository;

import com.roccoshi.micro.domain.PortView;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the PortView entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PortViewRepository extends JpaRepository<PortView, Long>, JpaSpecificationExecutor<PortView> {

}
