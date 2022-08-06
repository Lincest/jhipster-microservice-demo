package com.roccoshi.micro.repository;

import com.roccoshi.micro.domain.LineCard;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the LineCard entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LineCardRepository extends JpaRepository<LineCard, Long>, JpaSpecificationExecutor<LineCard> {

}
