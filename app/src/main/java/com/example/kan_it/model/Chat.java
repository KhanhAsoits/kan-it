package com.example.kan_it.model;

public class Chat {
    private String message;
    private String role;
    private String finish_reason;
    private int index;
    private long created_at;
    private int com_token;
    private int prompt_token;
    private int total_token;

    public Chat() {
    }

    public Chat(String message, String role, String finish_reason, int index, long created_at, int com_token, int prompt_token, int total_token) {
        this.message = message;
        this.role = role;
        this.finish_reason = finish_reason;
        this.index = index;
        this.created_at = created_at;
        this.com_token = com_token;
        this.prompt_token = prompt_token;
        this.total_token = total_token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFinish_reason() {
        return finish_reason;
    }

    public void setFinish_reason(String finish_reason) {
        this.finish_reason = finish_reason;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public int getCom_token() {
        return com_token;
    }

    public void setCom_token(int com_token) {
        this.com_token = com_token;
    }

    public int getPrompt_token() {
        return prompt_token;
    }

    public void setPrompt_token(int prompt_token) {
        this.prompt_token = prompt_token;
    }

    public int getTotal_token() {
        return total_token;
    }

    public void setTotal_token(int total_token) {
        this.total_token = total_token;
    }
}
