package org.fis2021.models;

import org.dizitart.no2.objects.Id;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ForumThread {
    @Id
    private String title;
    private String content;
    private String author;
    private Date creationDate;
    private ArrayList<ThreadReply> replies;
    private boolean isClosed = false;

    public ForumThread(){
    }

    public ForumThread(String title, String content, String author, Date creationDate){
        this.title = title;
        this.content = content;
        this.author = author;
        this.creationDate = creationDate;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public ArrayList<ThreadReply> getReplies() {
        return replies;
    }

    public void setReplies(ArrayList<ThreadReply> replies) {
        this.replies = replies;
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
