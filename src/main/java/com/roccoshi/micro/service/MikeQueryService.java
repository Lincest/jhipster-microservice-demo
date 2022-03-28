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

import com.roccoshi.micro.domain.Mike;
import com.roccoshi.micro.domain.*; // for static metamodels
import com.roccoshi.micro.repository.MikeRepository;
import com.roccoshi.micro.service.dto.MikeCriteria;
import com.roccoshi.micro.service.dto.MikeDTO;
import com.roccoshi.micro.service.mapper.MikeMapper;

/**
 * Service for executing complex queries for Mike entities in the database.
 * The main input is a {@link MikeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link MikeDTO} or a {@link Page} of {@link MikeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MikeQueryService extends QueryService<Mike> {

    private final Logger log = LoggerFactory.getLogger(MikeQueryService.class);

    private final MikeRepository mikeRepository;

    private final MikeMapper mikeMapper;

    public MikeQueryService(MikeRepository mikeRepository, MikeMapper mikeMapper) {
        this.mikeRepository = mikeRepository;
        this.mikeMapper = mikeMapper;
    }

    /**
     * Return a {@link List} of {@link MikeDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<MikeDTO> findByCriteria(MikeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Mike> specification = createSpecification(criteria);
        return mikeMapper.toDto(mikeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link MikeDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<MikeDTO> findByCriteria(MikeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Mike> specification = createSpecification(criteria);
        return mikeRepository.findAll(specification, page)
            .map(mikeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MikeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Mike> specification = createSpecification(criteria);
        return mikeRepository.count(specification);
    }

    /**
     * Function to convert MikeCriteria to a {@link Specification}
     */
    private Specification<Mike> createSpecification(MikeCriteria criteria) {
        Specification<Mike> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Mike_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Mike_.name));
            }
            if (criteria.getAge() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAge(), Mike_.age));
            }
            if (criteria.getDetails() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDetails(), Mike_.details));
            }
        }
        return specification;
    }
}
