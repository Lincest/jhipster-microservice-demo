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
 * Criteria class for the Port entity. This class is used in PortResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /ports?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PortCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter info;

    private StringFilter chassisName;

    private StringFilter lineCardName;

    private LongFilter lineCardId;

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

    public StringFilter getChassisName() {
        return chassisName;
    }

    public void setChassisName(StringFilter chassisName) {
        this.chassisName = chassisName;
    }

    public StringFilter getLineCardName() {
        return lineCardName;
    }

    public void setLineCardName(StringFilter lineCardName) {
        this.lineCardName = lineCardName;
    }

    public LongFilter getLineCardId() {
        return lineCardId;
    }

    public void setLineCardId(LongFilter lineCardId) {
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
        final PortCriteria that = (PortCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(info, that.info) &&
            Objects.equals(chassisName, that.chassisName) &&
            Objects.equals(lineCardName, that.lineCardName) &&
            Objects.equals(lineCardId, that.lineCardId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        name,
        info,
        chassisName,
        lineCardName,
        lineCardId
        );
    }

    @Override
    public String toString() {
        return "PortCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (info != null ? "info=" + info + ", " : "") +
                (chassisName != null ? "chassisName=" + chassisName + ", " : "") +
                (lineCardName != null ? "lineCardName=" + lineCardName + ", " : "") +
                (lineCardId != null ? "lineCardId=" + lineCardId + ", " : "") +
            "}";
    }

}
