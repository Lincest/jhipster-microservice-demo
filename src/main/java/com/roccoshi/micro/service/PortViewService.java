package com.roccoshi.micro.service;

import com.roccoshi.micro.domain.PortView;
import com.roccoshi.micro.repository.PortViewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing PortView.
 */
@Service
@Transactional
public class PortViewService {

    private final Logger log = LoggerFactory.getLogger(PortViewService.class);

    private final PortViewRepository portViewRepository;

    public PortViewService(PortViewRepository portViewRepository) {
        this.portViewRepository = portViewRepository;
    }

    /**
     * Save a portView.
     *
     * @param portView the entity to save
     * @return the persisted entity
     */
    public PortView save(PortView portView) {
        log.debug("Request to save PortView : {}", portView);
        return portViewRepository.save(portView);
    }

    /**
     * Get all the portViews.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<PortView> findAll(Pageable pageable) {
        log.debug("Request to get all PortViews");
        return portViewRepository.findAll(pageable);
    }


    /**
     * Get one portView by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<PortView> findOne(Long id) {
        log.debug("Request to get PortView : {}", id);
        return portViewRepository.findById(id);
    }

    /**
     * Delete the portView by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete PortView : {}", id);
        portViewRepository.deleteById(id);
    }
}
