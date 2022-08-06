package com.roccoshi.micro.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Chassis.
 */
@Entity
@Table(name = "chassis")
public class Chassis implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "info")
    private String info;

    @OneToMany(mappedBy = "chassis")
    private Set<LineCard> lineCards = new HashSet<>();
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

    public Chassis name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public Chassis info(String info) {
        this.info = info;
        return this;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Set<LineCard> getLineCards() {
        return lineCards;
    }

    public Chassis lineCards(Set<LineCard> lineCards) {
        this.lineCards = lineCards;
        return this;
    }

    public Chassis addLineCard(LineCard lineCard) {
        this.lineCards.add(lineCard);
        lineCard.setChassis(this);
        return this;
    }

    public Chassis removeLineCard(LineCard lineCard) {
        this.lineCards.remove(lineCard);
        lineCard.setChassis(null);
        return this;
    }

    public void setLineCards(Set<LineCard> lineCards) {
        this.lineCards = lineCards;
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
        Chassis chassis = (Chassis) o;
        if (chassis.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), chassis.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Chassis{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", info='" + getInfo() + "'" +
            "}";
    }
}
