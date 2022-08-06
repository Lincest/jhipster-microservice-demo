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

import com.roccoshi.micro.domain.LineCard;
import com.roccoshi.micro.domain.*; // for static metamodels
import com.roccoshi.micro.repository.LineCardRepository;
import com.roccoshi.micro.service.dto.LineCardCriteria;
import com.roccoshi.micro.service.dto.LineCardDTO;
import com.roccoshi.micro.service.mapper.LineCardMapper;

/**
 * Service for executing complex queries for LineCard entities in the database.
 * The main input is a {@link LineCardCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link LineCardDTO} or a {@link Page} of {@link LineCardDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LineCardQueryService extends QueryService<LineCard> {

    private final Logger log = LoggerFactory.getLogger(LineCardQueryService.class);

    private final LineCardRepository lineCardRepository;

    private final LineCardMapper lineCardMapper;

    public LineCardQueryService(LineCardRepository lineCardRepository, LineCardMapper lineCardMapper) {
        this.lineCardRepository = lineCardRepository;
        this.lineCardMapper = lineCardMapper;
    }

    /**
     * Return a {@link List} of {@link LineCardDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<LineCardDTO> findByCriteria(LineCardCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<LineCard> specification = createSpecification(criteria);
        return lineCardMapper.toDto(lineCardRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link LineCardDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<LineCardDTO> findByCriteria(LineCardCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<LineCard> specification = createSpecification(criteria);
        return lineCardRepository.findAll(specification, page)
            .map(lineCardMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LineCardCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<LineCard> specification = createSpecification(criteria);
        return lineCardRepository.count(specification);
    }

    /**
     * Function to convert LineCardCriteria to a {@link Specification}
     */
    private Specification<LineCard> createSpecification(LineCardCriteria criteria) {
        Specification<LineCard> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), LineCard_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), LineCard_.name));
            }
            if (criteria.getInfo() != null) {
                specification = specification.and(buildStringSpecification(criteria.getInfo(), LineCard_.info));
            }
            if (criteria.getPortId() != null) {
                specification = specification.and(buildSpecification(criteria.getPortId(),
                    root -> root.join(LineCard_.ports, JoinType.LEFT).get(Port_.id)));
            }
            if (criteria.getChassisId() != null) {
                specification = specification.and(buildSpecification(criteria.getChassisId(),
                    root -> root.join(LineCard_.chassis, JoinType.LEFT).get(Chassis_.id)));
            }
        }
        return specification;
    }
}
