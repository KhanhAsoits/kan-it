package com.example.kan_it.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.kan_it.R;
import com.example.kan_it.core.FireAuth;
import com.example.kan_it.databinding.FragmentForgotPasswordBinding;
import com.example.kan_it.model.RegAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

public class ForgotPasswordFragment extends Fragment {

    FragmentForgotPasswordBinding mBinder;
    boolean isLoading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinder = FragmentForgotPasswordBinding.inflate(inflater);
        return mBinder.getRoot();
    }

    public void showToast(String msg) {
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinder.btnGetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (String.valueOf(mBinder.edtEmailFg.getText()).trim().equals("")) {
                    showToast("Hãy điền email vào nào.");
                    return;
                }
                if (!String.valueOf(mBinder.edtEmailFg.getText()).trim().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                    showToast("Email chưa đúng định dạng.");
                    return;
                }
                if (!isLoading) {
                    isLoading = true;
                    MainActivity.loader();
                    FireAuth.gI().forgotPassword(String.valueOf(mBinder.edtEmailFg.getText()).trim(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                isLoading = false;
                                MainActivity.unLoader();
                                MainActivity.mAuthModel.regAccount = new RegAccount(String.valueOf(mBinder.edtEmailFg.getText()).trim(), "");
                                showToast("Chúng tôi đã gửi một email lấy lại mật khẩu cho bạn.");
                                MainActivity.mMenuController.navigate(R.id.loginActivity);
                            }
                        }
                    }, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            isLoading = false;
                            MainActivity.unLoader();
                            showToast("Không tìm thấy tài khoản nào liên kết với email : " + String.valueOf(mBinder.edtEmailFg.getText()));
                        }
                    });
                }

            }
        });
    }
}