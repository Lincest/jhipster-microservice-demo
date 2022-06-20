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

import com.roccoshi.micro.domain.Business;
import com.roccoshi.micro.domain.*; // for static metamodels
import com.roccoshi.micro.repository.BusinessRepository;
import com.roccoshi.micro.service.dto.BusinessCriteria;
import com.roccoshi.micro.service.dto.BusinessDTO;
import com.roccoshi.micro.service.mapper.BusinessMapper;

/**
 * Service for executing complex queries for Business entities in the database.
 * The main input is a {@link BusinessCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link BusinessDTO} or a {@link Page} of {@link BusinessDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BusinessQueryService extends QueryService<Business> {

    private final Logger log = LoggerFactory.getLogger(BusinessQueryService.class);

    private final BusinessRepository businessRepository;

    private final BusinessMapper businessMapper;

    public BusinessQueryService(BusinessRepository businessRepository, BusinessMapper businessMapper) {
        this.businessRepository = businessRepository;
        this.businessMapper = businessMapper;
    }

    /**
     * Return a {@link List} of {@link BusinessDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<BusinessDTO> findByCriteria(BusinessCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Business> specification = createSpecification(criteria);
        return businessMapper.toDto(businessRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link BusinessDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BusinessDTO> findByCriteria(BusinessCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Business> specification = createSpecification(criteria);
        return businessRepository.findAll(specification, page)
            .map(businessMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BusinessCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Business> specification = createSpecification(criteria);
        return businessRepository.count(specification);
    }

    /**
     * Function to convert BusinessCriteria to a {@link Specification}
     */
    private Specification<Business> createSpecification(BusinessCriteria criteria) {
        Specification<Business> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Business_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Business_.name));
            }
            if (criteria.getTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTime(), Business_.time));
            }
            if (criteria.getUuid() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUuid(), Business_.uuid));
            }
        }
        return specification;
    }
}
