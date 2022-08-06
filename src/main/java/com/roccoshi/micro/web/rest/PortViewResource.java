package com.roccoshi.micro.web.rest;
import com.roccoshi.micro.domain.PortView;
import com.roccoshi.micro.service.PortViewService;
import com.roccoshi.micro.web.rest.errors.BadRequestAlertException;
import com.roccoshi.micro.web.rest.util.HeaderUtil;
import com.roccoshi.micro.web.rest.util.PaginationUtil;
import com.roccoshi.micro.service.dto.PortViewCriteria;
import com.roccoshi.micro.service.PortViewQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing PortView.
 */
@RestController
@RequestMapping("/api")
public class PortViewResource {

    private final Logger log = LoggerFactory.getLogger(PortViewResource.class);

    private static final String ENTITY_NAME = "microNamePortView";

    private final PortViewService portViewService;

    private final PortViewQueryService portViewQueryService;

    public PortViewResource(PortViewService portViewService, PortViewQueryService portViewQueryService) {
        this.portViewService = portViewService;
        this.portViewQueryService = portViewQueryService;
    }

    /**
     * POST  /port-views : Create a new portView.
     *
     * @param portView the portView to create
     * @return the ResponseEntity with status 201 (Created) and with body the new portView, or with status 400 (Bad Request) if the portView has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/port-views")
    public ResponseEntity<PortView> createPortView(@RequestBody PortView portView) throws URISyntaxException {
        log.debug("REST request to save PortView : {}", portView);
        if (portView.getId() != null) {
            throw new BadRequestAlertException("A new portView cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PortView result = portViewService.save(portView);
        return ResponseEntity.created(new URI("/api/port-views/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /port-views : Updates an existing portView.
     *
     * @param portView the portView to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated portView,
     * or with status 400 (Bad Request) if the portView is not valid,
     * or with status 500 (Internal Server Error) if the portView couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/port-views")
    public ResponseEntity<PortView> updatePortView(@RequestBody PortView portView) throws URISyntaxException {
        log.debug("REST request to update PortView : {}", portView);
        if (portView.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        PortView result = portViewService.save(portView);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, portView.getId().toString()))
            .body(result);
    }

    /**
     * GET  /port-views : get all the portViews.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of portViews in body
     */
    @GetMapping("/port-views")
    public ResponseEntity<List<PortView>> getAllPortViews(PortViewCriteria criteria, Pageable pageable) {
        log.debug("REST request to get PortViews by criteria: {}", criteria);
        Page<PortView> page = portViewQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/port-views");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * GET  /port-views/count : count all the portViews.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/port-views/count")
    public ResponseEntity<Long> countPortViews(PortViewCriteria criteria) {
        log.debug("REST request to count PortViews by criteria: {}", criteria);
        return ResponseEntity.ok().body(portViewQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /port-views/:id : get the "id" portView.
     *
     * @param id the id of the portView to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the portView, or with status 404 (Not Found)
     */
    @GetMapping("/port-views/{id}")
    public ResponseEntity<PortView> getPortView(@PathVariable Long id) {
        log.debug("REST request to get PortView : {}", id);
        Optional<PortView> portView = portViewService.findOne(id);
        return ResponseUtil.wrapOrNotFound(portView);
    }

    /**
     * DELETE  /port-views/:id : delete the "id" portView.
     *
     * @param id the id of the portView to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/port-views/{id}")
    public ResponseEntity<Void> deletePortView(@PathVariable Long id) {
        log.debug("REST request to delete PortView : {}", id);
        portViewService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
