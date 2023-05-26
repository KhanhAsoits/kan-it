package com.example.kan_it.model;

public class Bookmark {
    private String ID;
    private String userID;
    private String postId;
    private long bookmarked_at;
    private long updated_at;
    private boolean state;

    public Bookmark() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public long getBookmarked_at() {
        return bookmarked_at;
    }

    public void setBookmarked_at(long bookmarked_at) {
        this.bookmarked_at = bookmarked_at;
    }

    public long getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(long updated_at) {
        this.updated_at = updated_at;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
