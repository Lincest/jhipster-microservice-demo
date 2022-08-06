package com.roccoshi.micro.service;

import com.roccoshi.micro.domain.Chassis;
import com.roccoshi.micro.repository.ChassisRepository;
import com.roccoshi.micro.service.dto.ChassisDTO;
import com.roccoshi.micro.service.mapper.ChassisMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing Chassis.
 */
@Service
@Transactional
public class ChassisService {

    private final Logger log = LoggerFactory.getLogger(ChassisService.class);

    private final ChassisRepository chassisRepository;

    private final ChassisMapper chassisMapper;

    public ChassisService(ChassisRepository chassisRepository, ChassisMapper chassisMapper) {
        this.chassisRepository = chassisRepository;
        this.chassisMapper = chassisMapper;
    }

    /**
     * Save a chassis.
     *
     * @param chassisDTO the entity to save
     * @return the persisted entity
     */
    public ChassisDTO save(ChassisDTO chassisDTO) {
        log.debug("Request to save Chassis : {}", chassisDTO);
        Chassis chassis = chassisMapper.toEntity(chassisDTO);
        chassis = chassisRepository.save(chassis);
        return chassisMapper.toDto(chassis);
    }

    /**
     * Get all the chassis.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ChassisDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Chassis");
        return chassisRepository.findAll(pageable)
            .map(chassisMapper::toDto);
    }


    /**
     * Get one chassis by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<ChassisDTO> findOne(Long id) {
        log.debug("Request to get Chassis : {}", id);
        return chassisRepository.findById(id)
            .map(chassisMapper::toDto);
    }

    /**
     * Delete the chassis by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Chassis : {}", id);
        chassisRepository.deleteById(id);
    }
}
