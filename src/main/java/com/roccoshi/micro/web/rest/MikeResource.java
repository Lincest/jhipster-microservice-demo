package com.roccoshi.micro.web.rest;

import com.roccoshi.micro.domain.Mike;
import com.roccoshi.micro.service.MikeService;
import com.roccoshi.micro.service.mapper.MikeMapper;
import com.roccoshi.micro.web.rest.errors.BadRequestAlertException;
import com.roccoshi.micro.web.rest.util.HeaderUtil;
import com.roccoshi.micro.service.dto.MikeDTO;
import com.roccoshi.micro.service.dto.MikeCriteria;
import com.roccoshi.micro.service.MikeQueryService;
import com.roccoshi.micro.web.rest.util.PaginationUtil;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.web.util.ResponseUtil;
import org.hibernate.Criteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Mike.
 */
@RestController
@RequestMapping("/api")
public class MikeResource {

    private final Logger log = LoggerFactory.getLogger(MikeResource.class);

    private static final String ENTITY_NAME = "microNameMike";

    private final MikeService mikeService;

    private final MikeQueryService mikeQueryService;

    public MikeResource(MikeService mikeService, MikeQueryService mikeQueryService) {
        this.mikeService = mikeService;
        this.mikeQueryService = mikeQueryService;
    }

    /**
     * POST  /mikes : Create a new mike.
     *
     * @param mikeDTO the mikeDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new mikeDTO, or with status 400 (Bad Request) if the mike has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/mikes")
    public ResponseEntity<MikeDTO> createMike(@RequestBody MikeDTO mikeDTO) throws URISyntaxException {
        log.debug("REST request to save Mike : {}", mikeDTO);
        log.debug("new to mike");
        if (mikeDTO.getId() != null) {
            throw new BadRequestAlertException("A new mike cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MikeDTO result = mikeService.save(mikeDTO);
        return ResponseEntity.created(new URI("/api/mikes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /mikes : Updates an existing mike.
     *
     * @param mikeDTO the mikeDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated mikeDTO,
     * or with status 400 (Bad Request) if the mikeDTO is not valid,
     * or with status 500 (Internal Server Error) if the mikeDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/mikes")
    public ResponseEntity<MikeDTO> updateMike(@RequestBody MikeDTO mikeDTO) throws URISyntaxException {
        log.debug("REST request to update Mike : {}", mikeDTO);
        if (mikeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        MikeDTO result = mikeService.save(mikeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, mikeDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /mikes : get all the mikes.
     *
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of mikes in body
     */
    @GetMapping("/mikes")
    public ResponseEntity<List<MikeDTO>> getAllMikes(MikeCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Mikes by criteria: {}", criteria);
        Page<MikeDTO> entityList = mikeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(entityList, ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.ok().headers(headers).body(entityList.getContent());
    }

    /**
     * GET  /mikes/count : count all the mikes.
     *
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the count in body
     */
    @GetMapping("/mikes/count")
    public ResponseEntity<Long> countMikes(MikeCriteria criteria) {
        log.debug("REST request to count Mikes by criteria: {}", criteria);
        return ResponseEntity.ok().body(mikeQueryService.countByCriteria(criteria));
    }

    /**
     * get mikes by name
     *
     * @return mikes
     */
    @GetMapping(value = "/mikes/find")
    public ResponseEntity<List<MikeDTO>> getMikesByName(@RequestParam(name = "name") String name) {
        log.debug("Request Mikes by name = {}", name);
        MikeCriteria criteria = new MikeCriteria();
        StringFilter stringFilter = new StringFilter();
        stringFilter.setContains(name);
        criteria.setName(stringFilter);
        return ResponseEntity.ok().body(mikeQueryService.findByCriteria(criteria));
    }


    /**
     * GET  /mikes/:id : get the "id" mike.
     *
     * @param id the id of the mikeDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the mikeDTO, or with status 404 (Not Found)
     */
    @GetMapping("/mikes/{id}")
    public ResponseEntity<MikeDTO> getMike(@PathVariable Long id) {
        log.debug("REST request to get Mike : {}", id);
        Optional<MikeDTO> mikeDTO = mikeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mikeDTO);
    }

    /**
     * DELETE  /mikes/:id : delete the "id" mike.
     *
     * @param id the id of the mikeDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/mikes/{id}")
    public ResponseEntity<Void> deleteMike(@PathVariable Long id) {
        log.debug("REST request to delete Mike : {}", id);
        mikeService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
