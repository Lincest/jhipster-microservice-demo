package com.roccoshi.micro.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.roccoshi.micro.domain.PortView;
import com.roccoshi.micro.domain.*; // for static metamodels
import com.roccoshi.micro.repository.PortViewRepository;
import com.roccoshi.micro.service.dto.PortViewCriteria;

/**
 * Service for executing complex queries for PortView entities in the database.
 * The main input is a {@link PortViewCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PortView} or a {@link Page} of {@link PortView} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PortViewQueryService extends QueryService<PortView> {

    private final Logger log = LoggerFactory.getLogger(PortViewQueryService.class);

    private final PortViewRepository portViewRepository;

    public PortViewQueryService(PortViewRepository portViewRepository) {
        this.portViewRepository = portViewRepository;
    }

    /**
     * Return a {@link List} of {@link PortView} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PortView> findByCriteria(PortViewCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<PortView> specification = createSpecification(criteria);
        return portViewRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link PortView} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PortView> findByCriteria(PortViewCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PortView> specification = createSpecification(criteria);
        return portViewRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PortViewCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<PortView> specification = createSpecification(criteria);
        return portViewRepository.count(specification);
    }

    /**
     * Function to convert PortViewCriteria to a {@link Specification}
     */
    private Specification<PortView> createSpecification(PortViewCriteria criteria) {
        Specification<PortView> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), PortView_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), PortView_.name));
            }
            if (criteria.getInfo() != null) {
                specification = specification.and(buildStringSpecification(criteria.getInfo(), PortView_.info));
            }
            if (criteria.getChassisId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getChassisId(), PortView_.chassisId));
            }
            if (criteria.getChassisName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getChassisName(), PortView_.chassisName));
            }
            if (criteria.getLineCardId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLineCardId(), PortView_.lineCardId));
            }
            if (criteria.getLineCardName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLineCardName(), PortView_.lineCardName));
            }
        }
        return specification;
    }
}
