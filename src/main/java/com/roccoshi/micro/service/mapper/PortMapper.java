package com.roccoshi.micro.service.mapper;

import com.roccoshi.micro.domain.*;
import com.roccoshi.micro.service.dto.PortDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Port and its DTO PortDTO.
 */
@Mapper(componentModel = "spring", uses = {LineCardMapper.class})
public interface PortMapper extends EntityMapper<PortDTO, Port> {

    @Mapping(source = "lineCard.id", target = "lineCardId")
    PortDTO toDto(Port port);

    @Mapping(source = "lineCardId", target = "lineCard")
    Port toEntity(PortDTO portDTO);

    default Port fromId(Long id) {
        if (id == null) {
            return null;
        }
        Port port = new Port();
        port.setId(id);
        return port;
    }
}
