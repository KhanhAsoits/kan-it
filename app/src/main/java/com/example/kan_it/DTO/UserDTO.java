package com.example.kan_it.DTO;

import com.example.kan_it.model.Follow;
import com.example.kan_it.model.User;

import java.util.List;

public class UserDTO extends User {
    public int post_count;
    public int follower_count;

    public List<Follow> follow_user;
}
