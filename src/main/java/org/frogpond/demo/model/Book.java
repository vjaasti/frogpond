package org.frogpond.demo.model;

import org.frogpond.annotation.*;
import org.frogpond.model.Primitive;

import java.util.ArrayList;
import java.util.List;

@LilyRecord(1L)
@LilyRecordIndex
public class Book {
    @LilyFieldIndex
    @LilyField
    @LilyId
    private String title;

    @LilyField
    private Integer pages;

    @LilyField
    @LilyFieldIndex
    @LilyVariant("language")
    private String language;

    @LilyFieldIndex
    @LilyField(primitive = Primitive.Link, javaType = Author.class)
    private List<Author> authors = new ArrayList<Author>();

    @LilyFieldIndex
    @LilyField(primitive=Primitive.Link)
    private Publisher publisher;

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", pages=" + pages +
                ", language='" + language + '\'' +
                ", publisher='" + publisher + '\'' +
                ", authors=" + authors +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        if (authors != null ? !authors.equals(book.authors) : book.authors != null) return false;
        if (language != null ? !language.equals(book.language) : book.language != null) return false;
        if (pages != null ? !pages.equals(book.pages) : book.pages != null) return false;
        if (title != null ? !title.equals(book.title) : book.title != null) return false;
        if (publisher != null ? !publisher.equals(book.publisher) : book.publisher != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (pages != null ? pages.hashCode() : 0);
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (authors != null ? authors.hashCode() : 0);
        result = 31 * result + (publisher != null ? publisher.hashCode() : 0);
        return result;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }
}
