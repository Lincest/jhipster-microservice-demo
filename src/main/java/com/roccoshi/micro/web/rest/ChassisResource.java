package com.roccoshi.micro.web.rest;
import com.roccoshi.micro.service.ChassisService;
import com.roccoshi.micro.web.rest.errors.BadRequestAlertException;
import com.roccoshi.micro.web.rest.util.HeaderUtil;
import com.roccoshi.micro.web.rest.util.PaginationUtil;
import com.roccoshi.micro.service.dto.ChassisDTO;
import com.roccoshi.micro.service.dto.ChassisCriteria;
import com.roccoshi.micro.service.ChassisQueryService;
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
 * REST controller for managing Chassis.
 */
@RestController
@RequestMapping("/api")
public class ChassisResource {

    private final Logger log = LoggerFactory.getLogger(ChassisResource.class);

    private static final String ENTITY_NAME = "microNameChassis";

    private final ChassisService chassisService;

    private final ChassisQueryService chassisQueryService;

    public ChassisResource(ChassisService chassisService, ChassisQueryService chassisQueryService) {
        this.chassisService = chassisService;
        this.chassisQueryService = chassisQueryService;
    }

    /**
     * POST  /chassis : Create a new chassis.
     *
     * @param chassisDTO the chassisDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new chassisDTO, or with status 400 (Bad Request) if the chassis has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/chassis")
    public ResponseEntity<ChassisDTO> createChassis(@RequestBody ChassisDTO chassisDTO) throws URISyntaxException {
        log.debug("REST request to save Chassis : {}", chassisDTO);
        if (chassisDTO.getId() != null) {
            throw new BadRequestAlertException("A new chassis cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ChassisDTO result = chassisService.save(chassisDTO);
        return ResponseEntity.created(new URI("/api/chassis/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /chassis : Updates an existing chassis.
     *
     * @param chassisDTO the chassisDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated chassisDTO,
     * or with status 400 (Bad Request) if the chassisDTO is not valid,
     * or with status 500 (Internal Server Error) if the chassisDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/chassis")
    public ResponseEntity<ChassisDTO> updateChassis(@RequestBody ChassisDTO chassisDTO) throws URISyntaxException {
        log.debug("REST request to update Chassis : {}", chassisDTO);
        if (chassisDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ChassisDTO result = chassisService.save(chassisDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, chassisDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /chassis : get all the chassis.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of chassis in body
     */
    @GetMapping("/chassis")
    public ResponseEntity<List<ChassisDTO>> getAllChassis(ChassisCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Chassis by criteria: {}", criteria);
        Page<ChassisDTO> page = chassisQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/chassis");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * GET  /chassis/count : count all the chassis.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/chassis/count")
    public ResponseEntity<Long> countChassis(ChassisCriteria criteria) {
        log.debug("REST request to count Chassis by criteria: {}", criteria);
        return ResponseEntity.ok().body(chassisQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /chassis/:id : get the "id" chassis.
     *
     * @param id the id of the chassisDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the chassisDTO, or with status 404 (Not Found)
     */
    @GetMapping("/chassis/{id}")
    public ResponseEntity<ChassisDTO> getChassis(@PathVariable Long id) {
        log.debug("REST request to get Chassis : {}", id);
        Optional<ChassisDTO> chassisDTO = chassisService.findOne(id);
        return ResponseUtil.wrapOrNotFound(chassisDTO);
    }

    /**
     * DELETE  /chassis/:id : delete the "id" chassis.
     *
     * @param id the id of the chassisDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/chassis/{id}")
    public ResponseEntity<Void> deleteChassis(@PathVariable Long id) {
        log.debug("REST request to delete Chassis : {}", id);
        chassisService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
