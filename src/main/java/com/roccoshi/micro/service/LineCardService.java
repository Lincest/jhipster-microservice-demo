package com.roccoshi.micro.service;

import com.roccoshi.micro.domain.LineCard;
import com.roccoshi.micro.repository.LineCardRepository;
import com.roccoshi.micro.service.dto.LineCardDTO;
import com.roccoshi.micro.service.mapper.LineCardMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing LineCard.
 */
@Service
@Transactional
public class LineCardService {

    private final Logger log = LoggerFactory.getLogger(LineCardService.class);

    private final LineCardRepository lineCardRepository;

    private final LineCardMapper lineCardMapper;

    public LineCardService(LineCardRepository lineCardRepository, LineCardMapper lineCardMapper) {
        this.lineCardRepository = lineCardRepository;
        this.lineCardMapper = lineCardMapper;
    }

    /**
     * Save a lineCard.
     *
     * @param lineCardDTO the entity to save
     * @return the persisted entity
     */
    public LineCardDTO save(LineCardDTO lineCardDTO) {
        log.debug("Request to save LineCard : {}", lineCardDTO);
        LineCard lineCard = lineCardMapper.toEntity(lineCardDTO);
        lineCard = lineCardRepository.save(lineCard);
        return lineCardMapper.toDto(lineCard);
    }

    /**
     * Get all the lineCards.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<LineCardDTO> findAll(Pageable pageable) {
        log.debug("Request to get all LineCards");
        return lineCardRepository.findAll(pageable)
            .map(lineCardMapper::toDto);
    }


    /**
     * Get one lineCard by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<LineCardDTO> findOne(Long id) {
        log.debug("Request to get LineCard : {}", id);
        return lineCardRepository.findById(id)
            .map(lineCardMapper::toDto);
    }

    /**
     * Delete the lineCard by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete LineCard : {}", id);
        lineCardRepository.deleteById(id);
    }
}
