package com.example.kan_it.model;

public class User {
    public static final String DEFAULT_USER_PHOTO = "https://firebasestorage.googleapis.com/v0/b/kan-it-15fb2.appspot.com/o/users%2Fdefault%2Fuser%20(1).png?alt=media&token=82e2a4bc-d363-45ac-90a5-091c6d40a88e";
    String UUID;
    String username;
    String email;
    String photo;
    long created_at;
    long updated_at;
    int star;
    boolean active;

    public User() {
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
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

    public int getStar() {
        return star;
    }

    public void setStar(int start) {
        this.star = start;
    }

    public void setActive(boolean b) {
        this.active = b;
    }

    public boolean getActive() {
        return this.active;
    }
}
