package com.roccoshi.micro.service.mapper;

import com.roccoshi.micro.domain.*;
import com.roccoshi.micro.service.dto.MikeDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Mike and its DTO MikeDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface MikeMapper extends EntityMapper<MikeDTO, Mike> {



    default Mike fromId(Long id) {
        if (id == null) {
            return null;
        }
        Mike mike = new Mike();
        mike.setId(id);
        return mike;
    }
}
