package com.roccoshi.micro.service.mapper;

import com.roccoshi.micro.domain.*;
import com.roccoshi.micro.service.dto.BusinessDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Business and its DTO BusinessDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface BusinessMapper extends EntityMapper<BusinessDTO, Business> {

    @Mapping(source="name", target="userName")
    @Mapping(source="time", target="createTime")
    public BusinessDTO toDto(Business b);

    @Mapping(source="userName", target="name")
    @Mapping(source="createTime", target="time")
    public Business toEntity(BusinessDTO b);

    default Business fromId(Long id) {
        if (id == null) {
            return null;
        }
        Business business = new Business();
        business.setId(id);
        return business;
    }
}
