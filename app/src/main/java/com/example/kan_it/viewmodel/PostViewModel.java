package com.example.kan_it.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.kan_it.DTO.PostDTO;

import java.util.List;

public class PostViewModel extends AndroidViewModel {
    public MutableLiveData<List<PostDTO>> data;

    public PostViewModel(@NonNull Application application) {
        super(application);
        data = new MutableLiveData<>();
    }
}
