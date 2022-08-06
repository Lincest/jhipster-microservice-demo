package com.roccoshi.micro.service.dto;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the LineCard entity.
 */
public class LineCardDTO implements Serializable {

    private Long id;

    private String name;

    private String info;


    private Long chassisId;

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

    public Long getChassisId() {
        return chassisId;
    }

    public void setChassisId(Long chassisId) {
        this.chassisId = chassisId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LineCardDTO lineCardDTO = (LineCardDTO) o;
        if (lineCardDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), lineCardDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "LineCardDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", info='" + getInfo() + "'" +
            ", chassis=" + getChassisId() +
            "}";
    }
}
