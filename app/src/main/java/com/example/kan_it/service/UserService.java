package com.example.kan_it.service;

import androidx.annotation.NonNull;

import com.example.kan_it.core.FireStore;
import com.example.kan_it.model.Follow;
import com.example.kan_it.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UserService {


    private static UserService I;

    public static UserService gI() {
        if (I == null) {
            I = new UserService();
        }
        return I;
    }

    public void getPostsById(String id, OnCompleteListener<QuerySnapshot> onCompleteListener, OnFailureListener onFailureListener) {
        FireStore.gI().collection(FireStore.POST_COLLECTION).whereEqualTo("userID", id).whereEqualTo("verify", true).get().addOnCompleteListener(onCompleteListener).addOnFailureListener(onFailureListener);
    }

    public void getFollowerById(String id, OnCompleteListener<QuerySnapshot> onCompleteListener, OnFailureListener onFailureListener) {
        FireStore.gI().collection(FireStore.FOLLOW_COLLECTION).whereEqualTo("followerID", id).get().addOnFailureListener(onFailureListener).addOnCompleteListener(onCompleteListener);
    }

    public void getUserById(String userId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        FireStore.gI().collection(FireStore.USER_COLLECTION).document(userId).get().addOnCompleteListener(onCompleteListener);
    }

    public void followUser(String uuid, String userID, OnCompleteListener<DocumentReference> onCompleteListener, OnFailureListener onFailureListener) {
        Follow follow = new Follow();
        follow.setFollowerID(userID);
        follow.setUserID(uuid);
        FireStore.gI().collection(FireStore.FOLLOW_COLLECTION).add(follow).addOnCompleteListener(onCompleteListener).addOnFailureListener(onFailureListener);
    }

    public void giveStar(User sender, User rev, OnCompleteListener<Void> onCompleteListener) {
        FireStore.gI().collection(FireStore.USER_COLLECTION).document(sender.getUUID()).update("star", sender.getStar() - 1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FireStore.gI().collection(FireStore.USER_COLLECTION).document(rev.getUUID()).update("star", rev.getStar() + 1).addOnCompleteListener(onCompleteListener);
                }
            }
        });
    }

    public void upStar(String uuid, int star, OnCompleteListener<Void> onCompleteListener) {
        FireStore.gI().collection(FireStore.USER_COLLECTION).document(uuid).update("star", star + 1).addOnCompleteListener(onCompleteListener);
    }
}
