package com.roccoshi.micro.web.rest;
import com.roccoshi.micro.domain.NorthNotificationEvents;
import com.roccoshi.micro.repository.NorthNotificationEventsRepository;
import com.roccoshi.micro.web.rest.errors.BadRequestAlertException;
import com.roccoshi.micro.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing NorthNotificationEvents.
 */
@RestController
@RequestMapping("/api")
public class NorthNotificationEventsResource {

    private final Logger log = LoggerFactory.getLogger(NorthNotificationEventsResource.class);

    private static final String ENTITY_NAME = "microNameNorthNotificationEvents";

    private final NorthNotificationEventsRepository northNotificationEventsRepository;

    public NorthNotificationEventsResource(NorthNotificationEventsRepository northNotificationEventsRepository) {
        this.northNotificationEventsRepository = northNotificationEventsRepository;
    }

    /**
     * POST  /north-notification-events : Create a new northNotificationEvents.
     *
     * @param northNotificationEvents the northNotificationEvents to create
     * @return the ResponseEntity with status 201 (Created) and with body the new northNotificationEvents, or with status 400 (Bad Request) if the northNotificationEvents has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/north-notification-events")
    public ResponseEntity<NorthNotificationEvents> createNorthNotificationEvents(@RequestBody NorthNotificationEvents northNotificationEvents) throws URISyntaxException {
        log.debug("REST request to save NorthNotificationEvents : {}", northNotificationEvents);
        if (northNotificationEvents.getId() != null) {
            throw new BadRequestAlertException("A new northNotificationEvents cannot already have an ID", ENTITY_NAME, "idexists");
        }
        NorthNotificationEvents result = northNotificationEventsRepository.save(northNotificationEvents);
        return ResponseEntity.created(new URI("/api/north-notification-events/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /north-notification-events : Updates an existing northNotificationEvents.
     *
     * @param northNotificationEvents the northNotificationEvents to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated northNotificationEvents,
     * or with status 400 (Bad Request) if the northNotificationEvents is not valid,
     * or with status 500 (Internal Server Error) if the northNotificationEvents couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/north-notification-events")
    public ResponseEntity<NorthNotificationEvents> updateNorthNotificationEvents(@RequestBody NorthNotificationEvents northNotificationEvents) throws URISyntaxException {
        log.debug("REST request to update NorthNotificationEvents : {}", northNotificationEvents);
        if (northNotificationEvents.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        NorthNotificationEvents result = northNotificationEventsRepository.save(northNotificationEvents);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, northNotificationEvents.getId().toString()))
            .body(result);
    }

    /**
     * GET  /north-notification-events : get all the northNotificationEvents.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of northNotificationEvents in body
     */
    @GetMapping("/north-notification-events")
    public List<NorthNotificationEvents> getAllNorthNotificationEvents() {
        log.debug("REST request to get all NorthNotificationEvents");
        return northNotificationEventsRepository.findAll();
    }

    /**
     * GET  /north-notification-events/:id : get the "id" northNotificationEvents.
     *
     * @param id the id of the northNotificationEvents to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the northNotificationEvents, or with status 404 (Not Found)
     */
    @GetMapping("/north-notification-events/{id}")
    public ResponseEntity<NorthNotificationEvents> getNorthNotificationEvents(@PathVariable Long id) {
        log.debug("REST request to get NorthNotificationEvents : {}", id);
        Optional<NorthNotificationEvents> northNotificationEvents = northNotificationEventsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(northNotificationEvents);
    }

    /**
     * DELETE  /north-notification-events/:id : delete the "id" northNotificationEvents.
     *
     * @param id the id of the northNotificationEvents to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/north-notification-events/{id}")
    public ResponseEntity<Void> deleteNorthNotificationEvents(@PathVariable Long id) {
        log.debug("REST request to delete NorthNotificationEvents : {}", id);
        northNotificationEventsRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
