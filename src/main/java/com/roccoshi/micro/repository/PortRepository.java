package com.roccoshi.micro.repository;

import com.roccoshi.micro.domain.Port;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Port entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PortRepository extends JpaRepository<Port, Long>, JpaSpecificationExecutor<Port> {

}
