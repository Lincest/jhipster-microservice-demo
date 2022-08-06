package com.roccoshi.micro.service.dto;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Port entity.
 */
public class PortDTO implements Serializable {

    private Long id;

    private String name;

    private String info;

    private String chassisName;

    private String lineCardName;


    private Long lineCardId;

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

    public String getChassisName() {
        return chassisName;
    }

    public void setChassisName(String chassisName) {
        this.chassisName = chassisName;
    }

    public String getLineCardName() {
        return lineCardName;
    }

    public void setLineCardName(String lineCardName) {
        this.lineCardName = lineCardName;
    }

    public Long getLineCardId() {
        return lineCardId;
    }

    public void setLineCardId(Long lineCardId) {
        this.lineCardId = lineCardId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PortDTO portDTO = (PortDTO) o;
        if (portDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), portDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PortDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", info='" + getInfo() + "'" +
            ", chassisName='" + getChassisName() + "'" +
            ", lineCardName='" + getLineCardName() + "'" +
            ", lineCard=" + getLineCardId() +
            "}";
    }
}
