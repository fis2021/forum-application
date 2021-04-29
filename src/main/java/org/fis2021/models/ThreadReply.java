package org.fis2021.models;

import java.util.Objects;

public class ThreadReply {

    private String author;
    private String content;
    private boolean isDeleted = false;

    public ThreadReply() {
    }

    public ThreadReply(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThreadReply that = (ThreadReply) o;
        return Objects.equals(author, that.author) && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, content);
    }

}
