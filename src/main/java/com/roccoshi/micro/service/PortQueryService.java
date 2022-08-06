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

import com.roccoshi.micro.domain.Port;
import com.roccoshi.micro.domain.*; // for static metamodels
import com.roccoshi.micro.repository.PortRepository;
import com.roccoshi.micro.service.dto.PortCriteria;
import com.roccoshi.micro.service.dto.PortDTO;
import com.roccoshi.micro.service.mapper.PortMapper;

/**
 * Service for executing complex queries for Port entities in the database.
 * The main input is a {@link PortCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PortDTO} or a {@link Page} of {@link PortDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PortQueryService extends QueryService<Port> {

    private final Logger log = LoggerFactory.getLogger(PortQueryService.class);

    private final PortRepository portRepository;

    private final PortMapper portMapper;

    public PortQueryService(PortRepository portRepository, PortMapper portMapper) {
        this.portRepository = portRepository;
        this.portMapper = portMapper;
    }

    /**
     * Return a {@link List} of {@link PortDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PortDTO> findByCriteria(PortCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Port> specification = createSpecification(criteria);
        return portMapper.toDto(portRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PortDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PortDTO> findByCriteria(PortCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Port> specification = createSpecification(criteria);
        return portRepository.findAll(specification, page)
            .map(portMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PortCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Port> specification = createSpecification(criteria);
        return portRepository.count(specification);
    }

    /**
     * Function to convert PortCriteria to a {@link Specification}
     */
    private Specification<Port> createSpecification(PortCriteria criteria) {
        Specification<Port> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Port_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Port_.name));
            }
            if (criteria.getInfo() != null) {
                specification = specification.and(buildStringSpecification(criteria.getInfo(), Port_.info));
            }
            if (criteria.getLineCardId() != null) {
                specification = specification.and(buildSpecification(criteria.getLineCardId(),
                    root -> root.join(Port_.lineCard, JoinType.LEFT).get(LineCard_.id)));
            }
        }
        return specification;
    }
}
