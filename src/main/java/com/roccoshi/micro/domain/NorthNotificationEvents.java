package com.roccoshi.micro.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A NorthNotificationEvents.
 */
@Entity
@Table(name = "north_notification_events")
public class NorthNotificationEvents implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patch_id")
    private String patchId;

    @Column(name = "target_id")
    private String targetId;

    @Column(name = "oper_state")
    private String operState;

    @Column(name = "current_state")
    private String currentState;

    @ManyToOne
    @JsonIgnoreProperties("events")
    private NorthNotification notification;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatchId() {
        return patchId;
    }

    public NorthNotificationEvents patchId(String patchId) {
        this.patchId = patchId;
        return this;
    }

    public void setPatchId(String patchId) {
        this.patchId = patchId;
    }

    public String getTargetId() {
        return targetId;
    }

    public NorthNotificationEvents targetId(String targetId) {
        this.targetId = targetId;
        return this;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getOperState() {
        return operState;
    }

    public NorthNotificationEvents operState(String operState) {
        this.operState = operState;
        return this;
    }

    public void setOperState(String operState) {
        this.operState = operState;
    }

    public String getCurrentState() {
        return currentState;
    }

    public NorthNotificationEvents currentState(String currentState) {
        this.currentState = currentState;
        return this;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public NorthNotification getNotification() {
        return notification;
    }

    public NorthNotificationEvents notification(NorthNotification northNotification) {
        this.notification = northNotification;
        return this;
    }

    public void setNotification(NorthNotification northNotification) {
        this.notification = northNotification;
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
        NorthNotificationEvents northNotificationEvents = (NorthNotificationEvents) o;
        if (northNotificationEvents.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), northNotificationEvents.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "NorthNotificationEvents{" +
            "id=" + getId() +
            ", patchId='" + getPatchId() + "'" +
            ", targetId='" + getTargetId() + "'" +
            ", operState='" + getOperState() + "'" +
            ", currentState='" + getCurrentState() + "'" +
            "}";
    }
}
