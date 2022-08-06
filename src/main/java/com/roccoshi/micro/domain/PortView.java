package com.roccoshi.micro.domain;



import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A PortView.
 */
@Entity
@Table(name = "port_view")
public class PortView implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "info")
    private String info;

    @Column(name = "chassis_id")
    private Long chassisId;

    @Column(name = "chassis_name")
    private String chassisName;

    @Column(name = "line_card_id")
    private Long lineCardId;

    @Column(name = "line_card_name")
    private String lineCardName;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public PortView name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public PortView info(String info) {
        this.info = info;
        return this;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Long getChassisId() {
        return chassisId;
    }

    public PortView chassisId(Long chassisId) {
        this.chassisId = chassisId;
        return this;
    }

    public void setChassisId(Long chassisId) {
        this.chassisId = chassisId;
    }

    public String getChassisName() {
        return chassisName;
    }

    public PortView chassisName(String chassisName) {
        this.chassisName = chassisName;
        return this;
    }

    public void setChassisName(String chassisName) {
        this.chassisName = chassisName;
    }

    public Long getLineCardId() {
        return lineCardId;
    }

    public PortView lineCardId(Long lineCardId) {
        this.lineCardId = lineCardId;
        return this;
    }

    public void setLineCardId(Long lineCardId) {
        this.lineCardId = lineCardId;
    }

    public String getLineCardName() {
        return lineCardName;
    }

    public PortView lineCardName(String lineCardName) {
        this.lineCardName = lineCardName;
        return this;
    }

    public void setLineCardName(String lineCardName) {
        this.lineCardName = lineCardName;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PortView portView = (PortView) o;
        if (portView.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), portView.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PortView{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", info='" + getInfo() + "'" +
            ", chassisId=" + getChassisId() +
            ", chassisName='" + getChassisName() + "'" +
            ", lineCardId=" + getLineCardId() +
            ", lineCardName='" + getLineCardName() + "'" +
            "}";
    }
}
