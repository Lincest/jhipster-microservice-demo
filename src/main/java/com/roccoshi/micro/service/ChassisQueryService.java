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

import com.roccoshi.micro.domain.Chassis;
import com.roccoshi.micro.domain.*; // for static metamodels
import com.roccoshi.micro.repository.ChassisRepository;
import com.roccoshi.micro.service.dto.ChassisCriteria;
import com.roccoshi.micro.service.dto.ChassisDTO;
import com.roccoshi.micro.service.mapper.ChassisMapper;

/**
 * Service for executing complex queries for Chassis entities in the database.
 * The main input is a {@link ChassisCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ChassisDTO} or a {@link Page} of {@link ChassisDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ChassisQueryService extends QueryService<Chassis> {

    private final Logger log = LoggerFactory.getLogger(ChassisQueryService.class);

    private final ChassisRepository chassisRepository;

    private final ChassisMapper chassisMapper;

    public ChassisQueryService(ChassisRepository chassisRepository, ChassisMapper chassisMapper) {
        this.chassisRepository = chassisRepository;
        this.chassisMapper = chassisMapper;
    }

    /**
     * Return a {@link List} of {@link ChassisDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ChassisDTO> findByCriteria(ChassisCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Chassis> specification = createSpecification(criteria);
        return chassisMapper.toDto(chassisRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ChassisDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ChassisDTO> findByCriteria(ChassisCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Chassis> specification = createSpecification(criteria);
        return chassisRepository.findAll(specification, page)
            .map(chassisMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ChassisCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Chassis> specification = createSpecification(criteria);
        return chassisRepository.count(specification);
    }

    /**
     * Function to convert ChassisCriteria to a {@link Specification}
     */
    private Specification<Chassis> createSpecification(ChassisCriteria criteria) {
        Specification<Chassis> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Chassis_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Chassis_.name));
            }
            if (criteria.getInfo() != null) {
                specification = specification.and(buildStringSpecification(criteria.getInfo(), Chassis_.info));
            }
            if (criteria.getLineCardId() != null) {
                specification = specification.and(buildSpecification(criteria.getLineCardId(),
                    root -> root.join(Chassis_.lineCards, JoinType.LEFT).get(LineCard_.id)));
            }
        }
        return specification;
    }
}
