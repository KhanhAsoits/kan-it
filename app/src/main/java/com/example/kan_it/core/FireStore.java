package com.example.kan_it.core;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FireStore {
    public static final String FOLLOW_COLLECTION = "follows";
    public static String USER_COLLECTION = "users";
    public static String POST_COLLECTION = "posts";
    public static String COMMENT_COLLECTION = "comments";
    public static String IMAGE_STORAGE = "uploads";
    private FirebaseFirestore DB;
    private static FireStore I;

    public static FireStore gI() {
        if (I == null) {
            I = new FireStore();
        }
        return I;
    }

    public CollectionReference collection(String collection) {
        return DB.collection(collection);
    }

    public DocumentReference document(String document) {
        return DB.document(document);
    }

    public FireStore() {
        DB = FirebaseFirestore.getInstance();
    }

    public void get(String collection, OnCompleteListener<QuerySnapshot> onSuccessCallBack) {
        CollectionReference collRef = DB.collection(collection);
        collRef.get().addOnCompleteListener(onSuccessCallBack);
    }

    public void getDocument(String collection, String document, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        DocumentReference docRef = DB.collection(collection).document(document);
        docRef.get().addOnCompleteListener(onCompleteListener);
    }

    public void set(String collection, Object objs, OnSuccessListener<DocumentReference> successCallBack, OnFailureListener failedCallBack) {
        DB.collection(collection).add(objs).addOnSuccessListener(successCallBack).addOnFailureListener(failedCallBack);
    }

    public void set(String collection, String document, Object objs, OnSuccessListener<Void> successCallBack, OnFailureListener failedCallBack) {
        DB.collection(collection).document(document).set(objs).addOnSuccessListener(successCallBack).addOnFailureListener(failedCallBack);
    }
}
