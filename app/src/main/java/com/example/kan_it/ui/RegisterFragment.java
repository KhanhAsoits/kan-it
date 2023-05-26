package com.example.kan_it.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.kan_it.R;
import com.example.kan_it.core.FireAuth;
import com.example.kan_it.core.FireStore;
import com.example.kan_it.databinding.FragmentRegisterBinding;
import com.example.kan_it.model.RegAccount;
import com.example.kan_it.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentReference;

public class RegisterFragment extends Fragment {

    FragmentRegisterBinding mBinder;
    boolean isReg = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinder = FragmentRegisterBinding.inflate(inflater);
        return mBinder.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (MainActivity.mAuthModel.wrapperAuth()) {
            registerEvent();
        } else {
            showToast("Bạn đã đăng nhập rồi.");
            this.onDestroy();
        }
    }

    public void registerEvent() {
        mBinder.btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reg();
            }
        });
        mBinder.txtReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isReg) {
                    onDestroy();
                } else {
                    showToast("Đang đăng ký không thể trở lại.");
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getFragmentManager().beginTransaction().remove((Fragment) this).commitAllowingStateLoss();
        MainActivity.mMenuController.navigate(R.id.loginActivity);
    }

    public void showToast(String msg) {
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public boolean valid() {
        if (String.valueOf(mBinder.edtEmail.getText()).trim().equals("")) {
            showToast("Email không được để trống.");
            return false;
        }
        if (!String.valueOf(mBinder.edtEmail.getText()).trim().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showToast("Email không đúng định dạng.");
            return false;
        }
        if (String.valueOf(mBinder.edtUsername.getText()).trim().equals("")) {
            showToast("Tên người dùng không được trống.");
            return false;
        }
        if (String.valueOf(mBinder.edtPassword.getText()).trim().length() < 8 || String.valueOf(mBinder.edtPasswordRe.getText()).trim().length() < 8) {
            showToast("Mật khẩu ít nhất phải có 8 ký tự.");
            return false;
        }
        if (!String.valueOf(mBinder.edtPassword.getText()).trim().equals(String.valueOf(mBinder.edtPasswordRe.getText()).trim())) {
            showToast("Mật khẩu xác nhận không khớp.");
            return false;
        }
        return true;
    }

    public void reg() {
        if (valid() && !isReg) {
            isReg = true;
            MainActivity.loader();
            String email = String.valueOf(mBinder.edtEmail.getText()).trim();
            String password = String.valueOf(mBinder.edtPassword.getText()).trim();
            FireAuth.gI().register(email, password, new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    // add user to db
                    User user = new User();
                    user.setUsername(String.valueOf(mBinder.edtUsername.getText()).trim());
                    user.setEmail(email);
                    user.setUUID(authResult.getUser().getUid());
                    user.setCreated_at(System.currentTimeMillis());
                    user.setUpdated_at(System.currentTimeMillis());
                    user.setPhoto(User.DEFAULT_USER_PHOTO);
                    user.setStar(0);
                    FireStore.gI().set(FireStore.USER_COLLECTION, user.getUUID(), user, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            FireAuth.gI().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        isReg = false;
                                        MainActivity.unLoader();
                                        showToast("Đăng ký thành công.Hãy kiểm tra email kích hoạt.");
                                        MainActivity.mAuthModel.regAccount = new RegAccount(email, password);
                                        onDestroy();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    isReg = false;
                                    showToast(e.getMessage());
                                    MainActivity.unLoader();
                                }
                            });
                        }
                    }, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            isReg = false;
                            MainActivity.unLoader();
                        }
                    });
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    isReg = false;
                    showToast("Đăng ký không thành công do email đã được sử dụng.");
                }
            });
        }
    }
}