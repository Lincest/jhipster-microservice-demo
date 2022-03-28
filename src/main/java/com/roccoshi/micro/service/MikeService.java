package com.roccoshi.micro.service;

import com.roccoshi.micro.domain.Mike;
import com.roccoshi.micro.repository.MikeRepository;
import com.roccoshi.micro.service.dto.MikeDTO;
import com.roccoshi.micro.service.mapper.MikeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Mike.
 */
@Service
@Transactional
public class MikeService {

    private final Logger log = LoggerFactory.getLogger(MikeService.class);

    private final MikeRepository mikeRepository;

    private final MikeMapper mikeMapper;

    public MikeService(MikeRepository mikeRepository, MikeMapper mikeMapper) {
        this.mikeRepository = mikeRepository;
        this.mikeMapper = mikeMapper;
    }

    /**
     * Save a mike.
     *
     * @param mikeDTO the entity to save
     * @return the persisted entity
     */
    public MikeDTO save(MikeDTO mikeDTO) {
        log.debug("Request to save Mike : {}", mikeDTO);
        Mike mike = mikeMapper.toEntity(mikeDTO);
        mike = mikeRepository.save(mike);
        return mikeMapper.toDto(mike);
    }

    /**
     * Get all the mikes.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<MikeDTO> findAll() {
        log.debug("Request to get all Mikes");
        return mikeRepository.findAll().stream()
            .map(mikeMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one mike by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<MikeDTO> findOne(Long id) {
        log.debug("Request to get Mike : {}", id);
        return mikeRepository.findById(id)
            .map(mikeMapper::toDto);
    }

    /**
     * Delete the mike by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Mike : {}", id);
        mikeRepository.deleteById(id);
    }
}
