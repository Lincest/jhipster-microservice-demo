package com.roccoshi.micro.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the LineCard entity. This class is used in LineCardResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /line-cards?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class LineCardCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter info;

    private LongFilter portId;

    private LongFilter chassisId;

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getInfo() {
        return info;
    }

    public void setInfo(StringFilter info) {
        this.info = info;
    }

    public LongFilter getPortId() {
        return portId;
    }

    public void setPortId(LongFilter portId) {
        this.portId = portId;
    }

    public LongFilter getChassisId() {
        return chassisId;
    }

    public void setChassisId(LongFilter chassisId) {
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
        final LineCardCriteria that = (LineCardCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(info, that.info) &&
            Objects.equals(portId, that.portId) &&
            Objects.equals(chassisId, that.chassisId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        name,
        info,
        portId,
        chassisId
        );
    }

    @Override
    public String toString() {
        return "LineCardCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (info != null ? "info=" + info + ", " : "") +
                (portId != null ? "portId=" + portId + ", " : "") +
                (chassisId != null ? "chassisId=" + chassisId + ", " : "") +
            "}";
    }

}
