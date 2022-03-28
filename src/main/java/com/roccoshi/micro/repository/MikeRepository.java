package com.roccoshi.micro.repository;

import com.roccoshi.micro.domain.Mike;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Mike entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MikeRepository extends JpaRepository<Mike, Long>, JpaSpecificationExecutor<Mike> {

}
