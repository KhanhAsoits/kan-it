package com.example.kan_it.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.kan_it.R;
import com.example.kan_it.core.FireStore;
import com.example.kan_it.core.Logger;
import com.example.kan_it.core.ShareDataManager;
import com.example.kan_it.databinding.ActivityMainBinding;
import com.example.kan_it.viewmodel.AuthViewModel;
import com.example.kan_it.viewmodel.PostDetailViewModel;
import com.example.kan_it.viewmodel.PostViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public static AuthViewModel mAuthModel;
    public static ActivityMainBinding mBinder;
    public static PostViewModel mPostViewModel;

    public static PostDetailViewModel mPostDetailViewModel;
    public static NavController mMenuController;
    public static ShareDataManager mShareDataManager;
    public static int IMAGE_SELECT_CODE = 1312;
    boolean isExitApp = false;

    public interface IOpenFileChooser {
        public void choseImage();
    }

    public static IOpenFileChooser mIOpenFileChooser;

    public void loadStatic() {
        mAuthModel = new ViewModelProvider(this).get(AuthViewModel.class);
        mPostViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        mPostDetailViewModel = new ViewModelProvider(this).get(PostDetailViewModel.class);
    }

    public void loadNavController() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        if (navHostFragment != null) {
            mMenuController = navHostFragment.getNavController();
        } else {
            this.onDestroy();
        }
    }

    public static void loader() {
        mBinder.loaderContainer.setVisibility(View.VISIBLE);
        mBinder.loaderContainer.bringToFront();
    }

    public static void unLoader() {
        mBinder.loaderContainer.setVisibility(View.INVISIBLE);
    }

    public void loadElement() {
        loadNavController();
    }

    public void initSharedReference() {
        mShareDataManager = new ShareDataManager();
        mShareDataManager.init(getApplicationContext());
    }

    public void loadPermission() {
        boolean per = false;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            per = true;
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 99);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Ứng dụng không đủ quyền có lẽ sẽ khiến một vài chức năng không hoạt động.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinder = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinder.getRoot());
        loadPermission();
        loadStatic();
        loadElement();
        initSharedReference();
        loadFileChooser();
        if (mAuthModel.autoLoginWrapper() && mAuthModel.wrapperAuth()) {
            mMenuController.navigate(R.id.auoLoginFragment);
        }
    }

    public void loadFileChooser() {
        mIOpenFileChooser = new IOpenFileChooser() {
            @Override
            public void choseImage() {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_SELECT_CODE);
            }
        };
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (mMenuController.getCurrentDestination().getId() != R.id.homeFragment) {
            super.onBackPressed();
        } else {
            if (isExitApp) {
                finish();
                System.exit(0);
            } else {
                isExitApp = true;
                showToast("Ấn lần nữa để thoát ứng dụng");
                new Handler().postDelayed(() -> {
                    isExitApp = false;
                }, 2000);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_SELECT_CODE && resultCode == RESULT_OK) {
            Uri result = data.getData();
            // upload to firebase
            PostCreatorFragment.mIOnPickImageResult.onResult(result);
        }
    }

}