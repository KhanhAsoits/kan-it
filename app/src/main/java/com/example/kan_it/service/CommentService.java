package com.example.kan_it.service;

import com.example.kan_it.core.FireStore;
import com.example.kan_it.model.Comment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class CommentService {
    private static CommentService I;

    public static CommentService gI() {
        if (I == null) {
            I = new CommentService();
        }
        return I;
    }

    public void createComment(Comment comment, OnCompleteListener<DocumentReference> onCompleteListener, OnFailureListener onFailureListener) {
        FireStore.gI().collection(FireStore.COMMENT_COLLECTION).add(comment).addOnCompleteListener(onCompleteListener).addOnFailureListener(onFailureListener);
    }
}
