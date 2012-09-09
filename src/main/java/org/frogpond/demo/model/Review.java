package org.frogpond.demo.model;

import org.frogpond.annotation.*;
import org.frogpond.model.Primitive;

import java.util.Date;

@LilyRecord(1L)
@LilyRecordIndex
public class Review {

    @LilyId
    private String id;

    @LilyField(primitive = Primitive.Link)
    private Book book;

    @LilyField
    @LilyFieldIndex
    private Date reviewDate;

    @LilyField
    @LilyFieldIndex
    private String reviewer;

    @LilyField
    private String comments;

    public Review() { }

    public Review(Book book, Date reviewDate, String reviewer, String comments) {
        this.book = book;
        this.reviewDate = reviewDate;
        this.reviewer = reviewer;
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Review review = (Review) o;

        if (comments != null ? !comments.equals(review.comments) : review.comments != null) return false;
        if (id != null ? !id.equals(review.id) : review.id != null) return false;
        if (book != null ? !book.equals(review.book) : review.book != null) return false;
        if (reviewDate != null ? !reviewDate.equals(review.reviewDate) : review.reviewDate != null) return false;
        if (reviewer != null ? !reviewer.equals(review.reviewer) : review.reviewer != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (book != null ? book.hashCode() : 0);
        result = 31 * result + (reviewDate != null ? reviewDate.hashCode() : 0);
        result = 31 * result + (reviewer != null ? reviewer.hashCode() : 0);
        result = 31 * result + (comments != null ? comments.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Review");
        sb.append("{id='").append(id).append('\'');
        sb.append(", book=").append(book);
        sb.append(", reviewDate=").append(reviewDate);
        sb.append(", reviewer='").append(reviewer).append('\'');
        sb.append(", comments='").append(comments).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @LilyId
    public String getId() {
        return id;
    }

    @LilyField(primitive = Primitive.Link)
    public Book getBook() {
        return book;
    }

    @LilyField(primitive = Primitive.Link)
    public void setBook(Book book) {
        this.book = book;
    }

    @LilyField
    public Date getReviewDate() {
        return reviewDate;
    }

    @LilyField
    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    @LilyField
    public String getReviewer() {
        return reviewer;
    }

    @LilyField
    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    @LilyField
    public String getComments() {
        return comments;
    }

    @LilyField
    public void setComments(String comments) {
        this.comments = comments;
    }
}
