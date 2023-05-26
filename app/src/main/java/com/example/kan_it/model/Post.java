package com.example.kan_it.model;

import java.util.ArrayList;
import java.util.List;

public class Post {
    private String ID;
    private String title;
    private String desc;
    private int view;
    private int vote;
    private int book_mark_count = 0;
    private String userID;
    private long created_at;
    private long updated_at;

    private boolean isVerify = false;

    public boolean isVerify() {
        return isVerify;
    }

    public void setVerify(boolean verify) {
        isVerify = verify;
    }

    private List<String> tags = new ArrayList<>();

    public int post_comment_count = 0;

    public Post() {
    }

    public int getBook_mark_count() {
        return book_mark_count;
    }

    public void setBook_mark_count(int book_mark_count) {
        this.book_mark_count = book_mark_count;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public long getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(long updated_at) {
        this.updated_at = updated_at;
    }

    public List<String> getTagIds() {
        return tags;
    }

    public void setTagIds(List<String> tagIds) {
        this.tags = tagIds;
    }
}
