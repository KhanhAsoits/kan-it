package com.example.kan_it.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kan_it.R;
import com.example.kan_it.core.AlertCore;
import com.example.kan_it.core.FireAuth;
import com.example.kan_it.core.FireStore;
import com.example.kan_it.core.Logger;
import com.example.kan_it.core.ShareDataManager;
import com.example.kan_it.core.StringCore;
import com.example.kan_it.databinding.ActivityLoginBinding;
import com.example.kan_it.model.User;
import com.example.kan_it.viewmodel.AuthViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;

import dev.shreyaspatil.MaterialDialog.AbstractDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

public class LoginActivity extends Fragment {

    ActivityLoginBinding mBinder;
    EditText mEdtEmail;
    EditText mEdtPassword;
    TextView mTxtForgotPassword;
    CheckBox mCbRemember;
    Button mBtnLogin;
    boolean mIsLogin;
    TextView mTxtSignUp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinder = ActivityLoginBinding.inflate(inflater);
        return mBinder.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (MainActivity.mAuthModel.wrapperAuth()) {
            loadElement();
            registerEvent();
            boundView();
            loadRegAccount();
        } else {
            showToast("Bạn đã đăng nhập rồi");
            onDestroy();
        }
    }

    public void boundView() {
        MainActivity.mBinder.mainNav.setVisibility(View.INVISIBLE);
    }

    public void unBoundView() {
        MainActivity.mBinder.mainNav.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unBoundView();
        MainActivity.mMenuController.navigate(R.id.homeFragment);
    }

    public void loadElement() {
        mEdtEmail = mBinder.edtEmail;
        mEdtPassword = mBinder.edtPassword;
        mTxtForgotPassword = mBinder.txtForgotPassword;
        mBtnLogin = mBinder.btnLogin;
        mCbRemember = mBinder.cbbRemember;
        mTxtSignUp = mBinder.txtReg;
    }

    public void loadRegAccount() {
        if (MainActivity.mAuthModel.regAccount != null) {
            mEdtPassword.setText(MainActivity.mAuthModel.regAccount.getPassword());
            mEdtEmail.setText(MainActivity.mAuthModel.regAccount.getEmail());
        }
    }

    public void registerEvent() {
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        mTxtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsLogin) {
                    MainActivity.mMenuController.navigate(R.id.registerFragment);
                } else {
                    showToast("Đang đăng nhập không thể thực hiện");
                }
            }
        });
        mTxtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsLogin) {
                    MainActivity.mMenuController.navigate(R.id.forgotPasswordFragment);
                }
            }
        });
    }


    public void showToast(String msg) {
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public boolean valid() {
        StringBuilder stringBuilder = new StringBuilder();
        if (String.valueOf(mEdtEmail.getText()).equals("")) {
            stringBuilder.append("Email không được trống");
            showToast(stringBuilder.toString());
            return false;
        }

        if (String.valueOf(mEdtPassword.getText()).equals("")) {
            stringBuilder.append("Password không được để trống");
            showToast(stringBuilder.toString());
            return false;
        }

        if (stringBuilder.toString().equals("")) {
            /// check email
            if (!String.valueOf(mEdtEmail.getText()).matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                stringBuilder.append("Định dạng email không đúng");
                showToast(stringBuilder.toString());
                return false;
            }
            if (String.valueOf(mEdtPassword.getText()).length() < 8) {
                stringBuilder.append("Mật khẩu ít nhất phải có 8 ký tự");
                showToast(stringBuilder.toString());
                return false;
            }
        }
        return true;
    }

    public void login() {
        if (valid() && !mIsLogin) {
            mIsLogin = true;
            MainActivity.loader();
            String email = String.valueOf(mEdtEmail.getText()).trim();
            String password = String.valueOf(mEdtPassword.getText()).trim();
            FireAuth.gI().login(email, password, new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    mIsLogin = false;
                    // fetch user
                    if (!FireAuth.gI().getCurrentUser().isEmailVerified()) {
                        MaterialDialog dialog = AlertCore.initEmailMotVerifyDiaLog(requireActivity());
                        dialog.show();
                    } else {
                        FireStore.gI().collection(FireStore.USER_COLLECTION).document(authResult.getUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    User user = documentSnapshot.toObject(User.class);
                                    MainActivity.mAuthModel.mCurrentUser = user;
                                    if (mBinder.cbbRemember.isChecked()) {
                                        MainActivity.mShareDataManager.setStringRef(ShareDataManager.AUTH_SHARED_KEY, Context.MODE_PRIVATE, "auth_email", StringCore.strToBase64(user.getEmail()));
                                        MainActivity.mShareDataManager.setStringRef(ShareDataManager.AUTH_SHARED_KEY, Context.MODE_PRIVATE, "auth_password", StringCore.strToBase64(String.valueOf(mEdtPassword.getText()).trim()));
                                    }
                                    showToast("Đăng nhập thành công.");
                                    mIsLogin = false;
                                    onDestroy();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(Logger.TAG_LOG, e.getMessage());
                            }
                        });
                    }
                    MainActivity.unLoader();
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showToast("Tài khoản hoặc mật khẩu không đúng.");
                    mIsLogin = false;
                    MainActivity.unLoader();
                }
            });
        }
    }
}