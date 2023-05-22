package com.roccoshi.micro.repository;

import com.roccoshi.micro.domain.NorthNotification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the NorthNotification entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NorthNotificationRepository extends JpaRepository<NorthNotification, Long> {
    Optional<NorthNotification> findByIdentifier(String identifier);

}
