package com.roccoshi.micro.service.dto;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Chassis entity.
 */
public class ChassisDTO implements Serializable {

    private Long id;

    private String name;

    private String info;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChassisDTO chassisDTO = (ChassisDTO) o;
        if (chassisDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), chassisDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ChassisDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", info='" + getInfo() + "'" +
            "}";
    }
}
