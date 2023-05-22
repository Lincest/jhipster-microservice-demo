package com.roccoshi.micro.repository;

import com.roccoshi.micro.domain.NorthNotificationEvents;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the NorthNotificationEvents entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NorthNotificationEventsRepository extends JpaRepository<NorthNotificationEvents, Long> {

}
