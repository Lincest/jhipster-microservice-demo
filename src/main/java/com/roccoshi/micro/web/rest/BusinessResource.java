package com.roccoshi.micro.web.rest;
import com.roccoshi.micro.service.BusinessService;
import com.roccoshi.micro.web.rest.errors.BadRequestAlertException;
import com.roccoshi.micro.web.rest.util.HeaderUtil;
import com.roccoshi.micro.web.rest.util.PaginationUtil;
import com.roccoshi.micro.service.dto.BusinessDTO;
import com.roccoshi.micro.service.dto.BusinessCriteria;
import com.roccoshi.micro.service.BusinessQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Business.
 */
@RestController
@RequestMapping("/api")
public class BusinessResource {

    private final Logger log = LoggerFactory.getLogger(BusinessResource.class);

    private static final String ENTITY_NAME = "microNameBusiness";

    private final BusinessService businessService;

    private final BusinessQueryService businessQueryService;

    public BusinessResource(BusinessService businessService, BusinessQueryService businessQueryService) {
        this.businessService = businessService;
        this.businessQueryService = businessQueryService;
    }

    /**
     * POST  /businesses : Create a new business.
     *
     * @param businessDTO the businessDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new businessDTO, or with status 400 (Bad Request) if the business has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/businesses")
    public ResponseEntity<BusinessDTO> createBusiness(@Valid @RequestBody BusinessDTO businessDTO) throws URISyntaxException {
        log.debug("REST request to save Business : {}", businessDTO);
        if (businessDTO.getId() != null) {
            throw new BadRequestAlertException("A new business cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BusinessDTO result = businessService.save(businessDTO);
        return ResponseEntity.created(new URI("/api/businesses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /businesses : Updates an existing business.
     *
     * @param businessDTO the businessDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated businessDTO,
     * or with status 400 (Bad Request) if the businessDTO is not valid,
     * or with status 500 (Internal Server Error) if the businessDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/businesses")
    public ResponseEntity<BusinessDTO> updateBusiness(@Valid @RequestBody BusinessDTO businessDTO) throws URISyntaxException {
        log.debug("REST request to update Business : {}", businessDTO);
        if (businessDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        BusinessDTO result = businessService.save(businessDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, businessDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /businesses : get all the businesses.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of businesses in body
     */
    @GetMapping("/businesses")
    public ResponseEntity<List<BusinessDTO>> getAllBusinesses(BusinessCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Businesses by criteria: {}", criteria);
        Page<BusinessDTO> page = businessQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/businesses");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * GET  /businesses/count : count all the businesses.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/businesses/count")
    public ResponseEntity<Long> countBusinesses(BusinessCriteria criteria) {
        log.debug("REST request to count Businesses by criteria: {}", criteria);
        return ResponseEntity.ok().body(businessQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /businesses/:id : get the "id" business.
     *
     * @param id the id of the businessDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the businessDTO, or with status 404 (Not Found)
     */
    @GetMapping("/businesses/{id}")
    public ResponseEntity<BusinessDTO> getBusiness(@PathVariable Long id) {
        log.debug("REST request to get Business : {}", id);
        Optional<BusinessDTO> businessDTO = businessService.findOne(id);
        return ResponseUtil.wrapOrNotFound(businessDTO);
    }

    /**
     * DELETE  /businesses/:id : delete the "id" business.
     *
     * @param id the id of the businessDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/businesses/{id}")
    public ResponseEntity<Void> deleteBusiness(@PathVariable Long id) {
        log.debug("REST request to delete Business : {}", id);
        businessService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
