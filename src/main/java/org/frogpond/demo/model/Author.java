package org.frogpond.demo.model;

import org.frogpond.annotation.*;

@LilyRecord(1L)
@LilyRecordIndex
public class Author {
    @LilyField
    @LilyFieldIndex
    private String name;

    private String bio;

    @LilyFieldIndex
    private String email;

    public Author() {
    }

    public Author(String name, String bio, String email) {
        this.name = name;
        this.bio = bio;
        this.email = email;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Author");
        sb.append("{name='").append(name).append('\'');
        sb.append(", bio='").append(bio).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Author author = (Author) o;

        if (bio != null ? !bio.equals(author.bio) : author.bio != null) return false;
        if (name != null ? !name.equals(author.name) : author.name != null) return false;
        if (email != null ? !email.equals(author.email) : author.email != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (bio != null ? bio.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }

    @LilyId
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    @LilyField
    public void setBio(String bio) {
        this.bio = bio;
    }

    @LilyField
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
