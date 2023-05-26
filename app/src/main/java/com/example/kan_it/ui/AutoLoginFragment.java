package com.example.kan_it.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kan_it.R;
import com.example.kan_it.core.FireAuth;
import com.example.kan_it.core.FireStore;
import com.example.kan_it.core.ShareDataManager;
import com.example.kan_it.core.StringCore;
import com.example.kan_it.databinding.FragmentAutoLoginBinding;
import com.example.kan_it.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;

public class AutoLoginFragment extends Fragment {
    FragmentAutoLoginBinding mBinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinder = FragmentAutoLoginBinding.inflate(inflater);
        bound();
        return mBinder.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (MainActivity.mAuthModel.wrapperAuth()) {
            authLogin();
        } else {
            onDestroy();
            MainActivity.mMenuController.navigate(R.id.homeFragment);
        }
        super.onViewCreated(view, savedInstanceState);
    }

    public void showToast(String msg) {
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public void bound() {
        MainActivity.mBinder.mainNav.setVisibility(View.INVISIBLE);
    }

    public void unBound() {
        MainActivity.mBinder.mainNav.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unBound();
    }

    public void authLogin() {
        String email = StringCore.base64ToStr(MainActivity.mShareDataManager.getStringRef(ShareDataManager.AUTH_SHARED_KEY, Context.MODE_PRIVATE, "auth_email", ""));
        String password = StringCore.base64ToStr(MainActivity.mShareDataManager.getStringRef(ShareDataManager.AUTH_SHARED_KEY, Context.MODE_PRIVATE, "auth_password", ""));
        if (!email.equals("") && !password.equals("")) {
            FireAuth.gI().login(email, password, new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    if (!FireAuth.gI().getCurrentUser().isEmailVerified()) {
                        showToast("Email chưa được kích hoạt vui lòng kích hoạt email.");
                        MainActivity.mMenuController.navigate(R.id.loginActivity);
                        onDestroy();
                    } else {
                        // fetch user
                        FireStore.gI().collection(FireStore.USER_COLLECTION).document(FireAuth.gI().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful() && task.getResult().exists()) {
                                    User user = task.getResult().toObject(User.class);
                                    MainActivity.mAuthModel.mCurrentUser = user;
                                    showToast("Đăng nhập thành công.");
                                    MainActivity.mMenuController.navigate(R.id.homeFragment);
                                    onDestroy();
                                }
                            }
                        });
                    }
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showToast("Có lỗi xảy ra hãy thử đăng nhập thủ công.");
                    MainActivity.mMenuController.navigate(R.id.loginActivity);
                    onDestroy();
                }
            });
        }
    }
}
