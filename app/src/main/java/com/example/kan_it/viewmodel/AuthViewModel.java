package com.example.kan_it.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.kan_it.core.ShareDataManager;
import com.example.kan_it.model.RegAccount;
import com.example.kan_it.model.User;
import com.example.kan_it.ui.MainActivity;

public class AuthViewModel extends AndroidViewModel {
    public User mCurrentUser;
    public RegAccount regAccount;

    public boolean wrapperAuth() {
        return mCurrentUser == null;
    }

    public boolean autoLoginWrapper() {
        String email = MainActivity.mShareDataManager.getStringRef(ShareDataManager.AUTH_SHARED_KEY, Context.MODE_PRIVATE, "auth_email", "");
        String password = MainActivity.mShareDataManager.getStringRef(ShareDataManager.AUTH_SHARED_KEY, Context.MODE_PRIVATE, "auth_password", "");
        return !email.equals("") && !password.equals("");
    }

    public AuthViewModel(@NonNull Application application) {
        super(application);
    }
}
