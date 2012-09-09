package org.frogpond.demo.model;

import org.frogpond.annotation.*;
import org.lilyproject.repository.api.RecordId;

@LilyRecord(1L)
@LilyRecordIndex
public class Publisher {
    @LilyId
    private RecordId id;

    @LilyField
    @LilyFieldIndex
    private String name;

    @LilyField
    private String website;

    public Publisher() { }

    public Publisher(String name, String website) {
        this.name = name;
        this.website = website;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Publisher publisher = (Publisher) o;

        if (id != null ? !id.equals(publisher.id) : publisher.id != null) return false;
        if (name != null ? !name.equals(publisher.name) : publisher.name != null) return false;
        if (website != null ? !website.equals(publisher.website) : publisher.website != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (website != null ? website.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Publisher");
        sb.append("{id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", website='").append(website).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public RecordId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
