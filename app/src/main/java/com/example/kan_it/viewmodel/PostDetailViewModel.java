package com.example.kan_it.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.kan_it.DTO.PostDTO;

public class PostDetailViewModel extends AndroidViewModel {
    public MutableLiveData<PostDTO> postDTO;

    public PostDetailViewModel(@NonNull Application application) {
        super(application);
        postDTO = new MutableLiveData<>();
    }

}
