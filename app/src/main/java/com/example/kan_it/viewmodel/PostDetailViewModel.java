package com.example.kan_it.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.kan_it.DTO.CommentDTO;
import com.example.kan_it.DTO.PostDTO;
import com.example.kan_it.model.Comment;
import com.example.kan_it.ui.MainActivity;

import java.util.List;

public class PostDetailViewModel extends AndroidViewModel {
    public MutableLiveData<PostDTO> postDTO;
    public MutableLiveData<List<PostDTO>> postOfUser;
    public MutableLiveData<List<PostDTO>> postOfTag;
    public MutableLiveData<List<CommentDTO>> commentOfPost;

    public PostDetailViewModel(@NonNull Application application) {
        super(application);
        postDTO = new MutableLiveData<>();
        postOfUser = new MutableLiveData<>();
        postOfTag = new MutableLiveData<>();
        commentOfPost = new MutableLiveData<>();
    }

}
