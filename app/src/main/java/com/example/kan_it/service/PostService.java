package com.example.kan_it.service;

import androidx.annotation.NonNull;

import com.airbnb.lottie.L;
import com.example.kan_it.DTO.PostDTO;
import com.example.kan_it.core.FireStore;
import com.example.kan_it.model.Post;
import com.example.kan_it.ui.MainActivity;
import com.example.kan_it.viewmodel.AuthViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PostService {
    private static PostService I;

    public static PostService gI() {
        if (I == null) {
            I = new PostService();
        }
        return I;
    }

    public void createPost(Post post, OnCompleteListener<DocumentReference> onCompleteListener, OnFailureListener onFailureListener) {
        if (post != null) {
            post.setCreated_at(System.currentTimeMillis());
            post.setUpdated_at(System.currentTimeMillis());
            post.setUserID(MainActivity.mAuthModel.mCurrentUser.getUUID());
            post.setView(0);
            post.setVerify(false);
            post.setVote(0);
            post.setBook_mark_count(0);
            FireStore.gI().collection(FireStore.POST_COLLECTION).add(post).addOnCompleteListener(onCompleteListener).addOnFailureListener(onFailureListener);
        }
    }

    public void getPostById(String id, OnCompleteListener<DocumentSnapshot> onCompleteListener, OnFailureListener onFailureListener) {
        FireStore.gI().collection(FireStore.POST_COLLECTION).document(id).get().addOnCompleteListener(onCompleteListener).addOnFailureListener(onFailureListener);
    }

    public void getListPost(OnCompleteListener<QuerySnapshot> onCompleteListener, OnFailureListener onFailureListener) {
        FireStore.gI().collection(FireStore.POST_COLLECTION).whereEqualTo("verify", true).get().addOnCompleteListener(onCompleteListener).addOnFailureListener(onFailureListener);
    }
}
