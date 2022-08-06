package com.roccoshi.micro.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A LineCard.
 */
@Entity
@Table(name = "line_card")
public class LineCard implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "info")
    private String info;

    @OneToMany(mappedBy = "lineCard")
    private Set<Port> ports = new HashSet<>();
    @ManyToOne
    @JsonIgnoreProperties("lineCards")
    private Chassis chassis;

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

    public LineCard name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public LineCard info(String info) {
        this.info = info;
        return this;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Set<Port> getPorts() {
        return ports;
    }

    public LineCard ports(Set<Port> ports) {
        this.ports = ports;
        return this;
    }

    public LineCard addPort(Port port) {
        this.ports.add(port);
        port.setLineCard(this);
        return this;
    }

    public LineCard removePort(Port port) {
        this.ports.remove(port);
        port.setLineCard(null);
        return this;
    }

    public void setPorts(Set<Port> ports) {
        this.ports = ports;
    }

    public Chassis getChassis() {
        return chassis;
    }

    public LineCard chassis(Chassis chassis) {
        this.chassis = chassis;
        return this;
    }

    public void setChassis(Chassis chassis) {
        this.chassis = chassis;
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
        LineCard lineCard = (LineCard) o;
        if (lineCard.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), lineCard.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "LineCard{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", info='" + getInfo() + "'" +
            "}";
    }
}
