package com.roccoshi.micro.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A NorthNotification.
 */
@Entity
@Table(name = "north_notification")
public class NorthNotification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "identifier", unique = true)
    private String identifier;

    @Column(name = "encoding")
    private String encoding;

    @Column(name = "topic")
    private String topic;

    @Column(name = "object_type")
    private String objectType;

    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<NorthNotificationEvents> events = new HashSet<>();
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public NorthNotification identifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getEncoding() {
        return encoding;
    }

    public NorthNotification encoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getTopic() {
        return topic;
    }

    public NorthNotification topic(String topic) {
        this.topic = topic;
        return this;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getObjectType() {
        return objectType;
    }

    public NorthNotification objectType(String objectType) {
        this.objectType = objectType;
        return this;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public Set<NorthNotificationEvents> getEvents() {
        return events;
    }

    public NorthNotification events(Set<NorthNotificationEvents> northNotificationEvents) {
        this.events = northNotificationEvents;
        return this;
    }

    public NorthNotification addEvents(NorthNotificationEvents northNotificationEvents) {
        this.events.add(northNotificationEvents);
        northNotificationEvents.setNotification(this);
        return this;
    }

    public NorthNotification removeEvents(NorthNotificationEvents northNotificationEvents) {
        this.events.remove(northNotificationEvents);
        northNotificationEvents.setNotification(null);
        return this;
    }

    public void setEvents(Set<NorthNotificationEvents> northNotificationEvents) {
        this.events = northNotificationEvents;
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
        NorthNotification northNotification = (NorthNotification) o;
        if (northNotification.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), northNotification.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "NorthNotification{" +
            "id=" + getId() +
            ", identifier='" + getIdentifier() + "'" +
            ", encoding='" + getEncoding() + "'" +
            ", topic='" + getTopic() + "'" +
            ", objectType='" + getObjectType() + "'" +
            "}";
    }
}
