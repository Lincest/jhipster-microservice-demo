package com.roccoshi.micro.web.rest;
import com.roccoshi.micro.domain.NorthNotification;
import com.roccoshi.micro.repository.NorthNotificationRepository;
import com.roccoshi.micro.web.rest.errors.BadRequestAlertException;
import com.roccoshi.micro.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing NorthNotification.
 */
@RestController
@RequestMapping("/api")
public class NorthNotificationResource {

    private final Logger log = LoggerFactory.getLogger(NorthNotificationResource.class);

    private static final String ENTITY_NAME = "microNameNorthNotification";

    private final NorthNotificationRepository northNotificationRepository;

    public NorthNotificationResource(NorthNotificationRepository northNotificationRepository) {
        this.northNotificationRepository = northNotificationRepository;
    }

    /**
     * POST  /north-notifications : Create a new northNotification.
     *
     * @param northNotification the northNotification to create
     * @return the ResponseEntity with status 201 (Created) and with body the new northNotification, or with status 400 (Bad Request) if the northNotification has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/north-notifications")
    public ResponseEntity<NorthNotification> createNorthNotification(@Valid @RequestBody NorthNotification northNotification) throws URISyntaxException {
        log.debug("REST request to save NorthNotification : {}", northNotification);
        if (northNotification.getId() != null) {
            throw new BadRequestAlertException("A new northNotification cannot already have an ID", ENTITY_NAME, "idexists");
        }
        NorthNotification result = northNotificationRepository.save(northNotification);
        return ResponseEntity.created(new URI("/api/north-notifications/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /north-notifications : Updates an existing northNotification.
     *
     * @param northNotification the northNotification to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated northNotification,
     * or with status 400 (Bad Request) if the northNotification is not valid,
     * or with status 500 (Internal Server Error) if the northNotification couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/north-notifications")
    public ResponseEntity<NorthNotification> updateNorthNotification(@Valid @RequestBody NorthNotification northNotification) throws URISyntaxException {
        log.debug("REST request to update NorthNotification : {}", northNotification);
        if (northNotification.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        NorthNotification result = northNotificationRepository.save(northNotification);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, northNotification.getId().toString()))
            .body(result);
    }

    /**
     * GET  /north-notifications : get all the northNotifications.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of northNotifications in body
     */
    @GetMapping("/north-notifications")
    public List<NorthNotification> getAllNorthNotifications() {
        log.debug("REST request to get all NorthNotifications");
        return northNotificationRepository.findAll();
    }

    /**
     * GET  /north-notifications/:id : get the "id" northNotification.
     *
     * @param id the id of the northNotification to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the northNotification, or with status 404 (Not Found)
     */
    @GetMapping("/north-notifications/{id}")
    public ResponseEntity<NorthNotification> getNorthNotification(@PathVariable Long id) {
        log.debug("REST request to get NorthNotification : {}", id);
        Optional<NorthNotification> northNotification = northNotificationRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(northNotification);
    }

    /**
     * DELETE  /north-notifications/:id : delete the "id" northNotification.
     *
     * @param id the id of the northNotification to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/north-notifications/{id}")
    public ResponseEntity<Void> deleteNorthNotification(@PathVariable Long id) {
        log.debug("REST request to delete NorthNotification : {}", id);
        northNotificationRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
