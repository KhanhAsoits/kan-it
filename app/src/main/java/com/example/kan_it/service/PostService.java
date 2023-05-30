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

    public void getPostByUser(String userId, OnCompleteListener<QuerySnapshot> onCompleteListener, OnFailureListener onFailureListener) {
        FireStore.gI().collection(FireStore.POST_COLLECTION).whereEqualTo("userID", userId).whereEqualTo("verify", true).limit(5).get().addOnCompleteListener(onCompleteListener).addOnFailureListener(onFailureListener);
    }

    public void increaseReader(String postId) {
        FireStore.gI().collection(FireStore.POST_COLLECTION).document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Post post = task.getResult().toObject(Post.class);
                    FireStore.gI().collection(FireStore.POST_COLLECTION).document(postId).update("view", post.getView() + 1);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void giveVote(String uuid, List<String> currentIds, String postID, OnCompleteListener<Void> onCompleteListener) {
        currentIds.add(uuid);
        FireStore.gI().collection(FireStore.POST_COLLECTION).document(postID).update("voteIds", currentIds).addOnCompleteListener(onCompleteListener);
    }
}
