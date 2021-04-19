package org.fis2021.models;

import java.util.Date;
import java.util.Objects;

public class ForumThread {
    private String title;
    private String content;
    private User author;
    private Date creationDate;

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public ForumThread(){
    }

    public ForumThread(String title, String content, User author, Date creationDate){
        this.title = title;
        this.content = content;
        this.author = author;
        this.creationDate = creationDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForumThread forumThread = (ForumThread) o;
        return Objects.equals(title, forumThread.title) &&
                Objects.equals(content, forumThread.content) &&
                Objects.equals(author, forumThread.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, content, author);
    }

    @Override
    public String toString() {
        return "ForumThread{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", author=" + author +
                '}';
    }
}
