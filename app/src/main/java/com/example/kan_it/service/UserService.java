package com.example.kan_it.service;

import com.example.kan_it.core.FireStore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
}
