package com.example.kan_it.core;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.example.kan_it.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import dev.shreyaspatil.MaterialDialog.AbstractDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

public class AlertCore {
    public static MaterialDialog initEmailMotVerifyDiaLog(FragmentActivity activity) {
        return build(activity);
    }

    public static void showToast(FragmentActivity activity, String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    public static MaterialDialog initYesNoDiaLog(FragmentActivity activity, String msg, AbstractDialog.OnClickListener yesClick, AbstractDialog.OnClickListener noClick) {
        return new MaterialDialog.Builder(activity).setTitle("Thông báo").setMessage(msg).setCancelable(false).setPositiveButton("Đồng ý", R.drawable.baseline_verified_user_24, yesClick).setNegativeButton("Đóng", R.drawable.baseline_close_24, noClick
        ).build();
    }

    public static MaterialDialog build(FragmentActivity activity) {
        return new MaterialDialog.Builder(activity).setTitle("Lỗi xác thực").setMessage("Tài khoản của bạn đã tồn tại trên hệ thống nhưng chưa xác thực.Vui lòng kiếm tra email chúng tôi đã gửi cho bạn.").setCancelable(false).setPositiveButton("Gửi lại email kích hoạt", R.drawable.baseline_verified_user_24, new AbstractDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                // resend email;
                FireAuth.gI().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        showToast(activity, "Đã gửi lại email kích hoạt hãy kiểm tra lại.");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(activity, "Có lỗi xảy ra hãy thử lại sau.");
                    }
                });
                dialogInterface.dismiss();
            }
        }).setNegativeButton("Đóng", R.drawable.baseline_close_24, new AbstractDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        }).build();
    }
}
