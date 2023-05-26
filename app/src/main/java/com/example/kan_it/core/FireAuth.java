package com.example.kan_it.core;

import android.content.Context;

import com.example.kan_it.ui.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FireAuth {
    private static FireAuth I;
    private final FirebaseAuth mAuth;

    public static FireAuth gI() {
        if (I == null) {
            I = new FireAuth();
        }
        return I;
    }

    public void forgotPassword(String email, OnCompleteListener<Void> onCompleteListener, OnFailureListener onFailureListener) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(onCompleteListener).addOnFailureListener(onFailureListener);
    }

    public void logout() {
        mAuth.signOut();
    }

    public void resetShare() {
        MainActivity.mShareDataManager.setStringRef(ShareDataManager.AUTH_SHARED_KEY, Context.MODE_PRIVATE, "auth_email", "");
        MainActivity.mShareDataManager.setStringRef(ShareDataManager.AUTH_SHARED_KEY, Context.MODE_PRIVATE, "auth_password", "");
    }

    public FireAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public void login(String email, String password, OnSuccessListener<AuthResult> authResultOnSuccessListener, OnFailureListener onFailureListener) {
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResultOnSuccessListener).addOnFailureListener(onFailureListener);
    }

    public void register(String email, String password, OnSuccessListener<AuthResult> onSuccessListener, OnFailureListener onFailureListener) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }
}
