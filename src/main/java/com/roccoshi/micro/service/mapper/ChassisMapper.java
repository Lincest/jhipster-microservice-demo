package com.roccoshi.micro.service.mapper;

import com.roccoshi.micro.domain.*;
import com.roccoshi.micro.service.dto.ChassisDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Chassis and its DTO ChassisDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ChassisMapper extends EntityMapper<ChassisDTO, Chassis> {


    @Mapping(target = "lineCards", ignore = true)
    Chassis toEntity(ChassisDTO chassisDTO);

    default Chassis fromId(Long id) {
        if (id == null) {
            return null;
        }
        Chassis chassis = new Chassis();
        chassis.setId(id);
        return chassis;
    }
}
