package com.roccoshi.micro.service.dto;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Mike entity.
 */
public class MikeDTO implements Serializable {

    private Long id;

    private String name;

    private Integer age;

    private String details;


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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MikeDTO mikeDTO = (MikeDTO) o;
        if (mikeDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), mikeDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "MikeDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", age=" + getAge() +
            ", details='" + getDetails() + "'" +
            "}";
    }
}
