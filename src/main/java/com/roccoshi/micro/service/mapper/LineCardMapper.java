package com.roccoshi.micro.service.mapper;

import com.roccoshi.micro.domain.*;
import com.roccoshi.micro.service.dto.LineCardDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity LineCard and its DTO LineCardDTO.
 */
@Mapper(componentModel = "spring", uses = {ChassisMapper.class})
public interface LineCardMapper extends EntityMapper<LineCardDTO, LineCard> {

    @Mapping(source = "chassis.id", target = "chassisId")
    LineCardDTO toDto(LineCard lineCard);

    @Mapping(target = "ports", ignore = true)
    @Mapping(source = "chassisId", target = "chassis")
    LineCard toEntity(LineCardDTO lineCardDTO);

    default LineCard fromId(Long id) {
        if (id == null) {
            return null;
        }
        LineCard lineCard = new LineCard();
        lineCard.setId(id);
        return lineCard;
    }
}
