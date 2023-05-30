package com.example.kan_it.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.kan_it.DTO.CommentDTO;
import com.example.kan_it.DTO.PostDTO;
import com.example.kan_it.core.FireStore;
import com.example.kan_it.model.User;
import com.example.kan_it.service.PostService;
import com.example.kan_it.service.UserService;
import com.example.kan_it.ui.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PostViewModel extends AndroidViewModel {
    public MutableLiveData<List<PostDTO>> data;

    public PostViewModel(@NonNull Application application) {
        super(application);
        data = new MutableLiveData<>();
    }
}
