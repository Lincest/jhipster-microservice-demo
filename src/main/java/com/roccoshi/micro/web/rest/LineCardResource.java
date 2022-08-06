package com.roccoshi.micro.web.rest;
import com.roccoshi.micro.service.LineCardService;
import com.roccoshi.micro.web.rest.errors.BadRequestAlertException;
import com.roccoshi.micro.web.rest.util.HeaderUtil;
import com.roccoshi.micro.web.rest.util.PaginationUtil;
import com.roccoshi.micro.service.dto.LineCardDTO;
import com.roccoshi.micro.service.dto.LineCardCriteria;
import com.roccoshi.micro.service.LineCardQueryService;
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
 * REST controller for managing LineCard.
 */
@RestController
@RequestMapping("/api")
public class LineCardResource {

    private final Logger log = LoggerFactory.getLogger(LineCardResource.class);

    private static final String ENTITY_NAME = "microNameLineCard";

    private final LineCardService lineCardService;

    private final LineCardQueryService lineCardQueryService;

    public LineCardResource(LineCardService lineCardService, LineCardQueryService lineCardQueryService) {
        this.lineCardService = lineCardService;
        this.lineCardQueryService = lineCardQueryService;
    }

    /**
     * POST  /line-cards : Create a new lineCard.
     *
     * @param lineCardDTO the lineCardDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new lineCardDTO, or with status 400 (Bad Request) if the lineCard has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/line-cards")
    public ResponseEntity<LineCardDTO> createLineCard(@RequestBody LineCardDTO lineCardDTO) throws URISyntaxException {
        log.debug("REST request to save LineCard : {}", lineCardDTO);
        if (lineCardDTO.getId() != null) {
            throw new BadRequestAlertException("A new lineCard cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LineCardDTO result = lineCardService.save(lineCardDTO);
        return ResponseEntity.created(new URI("/api/line-cards/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /line-cards : Updates an existing lineCard.
     *
     * @param lineCardDTO the lineCardDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated lineCardDTO,
     * or with status 400 (Bad Request) if the lineCardDTO is not valid,
     * or with status 500 (Internal Server Error) if the lineCardDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/line-cards")
    public ResponseEntity<LineCardDTO> updateLineCard(@RequestBody LineCardDTO lineCardDTO) throws URISyntaxException {
        log.debug("REST request to update LineCard : {}", lineCardDTO);
        if (lineCardDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        LineCardDTO result = lineCardService.save(lineCardDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, lineCardDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /line-cards : get all the lineCards.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of lineCards in body
     */
    @GetMapping("/line-cards")
    public ResponseEntity<List<LineCardDTO>> getAllLineCards(LineCardCriteria criteria, Pageable pageable) {
        log.debug("REST request to get LineCards by criteria: {}", criteria);
        Page<LineCardDTO> page = lineCardQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/line-cards");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * GET  /line-cards/count : count all the lineCards.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/line-cards/count")
    public ResponseEntity<Long> countLineCards(LineCardCriteria criteria) {
        log.debug("REST request to count LineCards by criteria: {}", criteria);
        return ResponseEntity.ok().body(lineCardQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /line-cards/:id : get the "id" lineCard.
     *
     * @param id the id of the lineCardDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the lineCardDTO, or with status 404 (Not Found)
     */
    @GetMapping("/line-cards/{id}")
    public ResponseEntity<LineCardDTO> getLineCard(@PathVariable Long id) {
        log.debug("REST request to get LineCard : {}", id);
        Optional<LineCardDTO> lineCardDTO = lineCardService.findOne(id);
        return ResponseUtil.wrapOrNotFound(lineCardDTO);
    }

    /**
     * DELETE  /line-cards/:id : delete the "id" lineCard.
     *
     * @param id the id of the lineCardDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/line-cards/{id}")
    public ResponseEntity<Void> deleteLineCard(@PathVariable Long id) {
        log.debug("REST request to delete LineCard : {}", id);
        lineCardService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
