package com.example.kan_it.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.bumptech.glide.Glide;
import com.example.kan_it.DTO.PostDTO;
import com.example.kan_it.DTO.UserDTO;
import com.example.kan_it.R;
import com.example.kan_it.core.FireStore;
import com.example.kan_it.core.IBoundLayout;
import com.example.kan_it.core.Logger;
import com.example.kan_it.databinding.FragmentPostDetailBinding;
import com.example.kan_it.model.User;
import com.example.kan_it.service.PostService;
import com.example.kan_it.service.UserService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.nio.charset.StandardCharsets;

public class PostDetailFragment extends Fragment implements IBoundLayout {

    FragmentPostDetailBinding mBinder;
    String postId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinder = FragmentPostDetailBinding.inflate(inflater);
        return mBinder.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getString("id");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        bound();
        regEvent();
        loadPost();
        postReader();
    }

    public void showToast(String msg) {
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public void postReader() {
        new Handler().postDelayed(this::updatePost, 60 * 1000);
    }

    public void updatePost() {

    }

    public void regEvent() {
        MainActivity.mPostDetailViewModel.postDTO.observe(requireActivity(), new Observer<PostDTO>() {
            @Override
            public void onChanged(PostDTO postDTO) {
                renderView(postDTO);
            }
        });
    }

    public void renderView(PostDTO postDTO) {
        try {
            Glide.with(getContext()).load(postDTO.user.getPhoto()).error(R.drawable.logo).centerCrop().into(mBinder.imvAvatar);
            mBinder.txtUsername.setText(postDTO.user.getUsername());
            mBinder.txtUserEmail.setText(postDTO.user.getEmail());
            mBinder.txtStar.setText(String.valueOf(postDTO.user.getStar()));
            mBinder.txtFollower.setText(String.valueOf(postDTO.user.follower_count));
            mBinder.txtPostCount.setText(String.valueOf(postDTO.user.post_count));
            mBinder.txtDetailTimeUp.setText(postDTO.getTimeUp(postDTO.getUpdated_at()));
            mBinder.txtDetailTimeRead.setText(" - " + postDTO.getDurationTimeRead(postDTO.getDesc().length()));
            mBinder.txtDetailReadCount.setText(String.valueOf(postDTO.getView()));
            mBinder.txtDetailCommentCount.setText(String.valueOf(postDTO.post_comment_count));
            mBinder.txtDetailBookmarkCount.setText(String.valueOf(postDTO.getBook_mark_count()));
            mBinder.txtTitle.setText(postDTO.getTitle());
            mBinder.wvContent.getSettings().setJavaScriptEnabled(true);
            mBinder.wvContent.loadData(postDTO.getDesc(), "text/html", "UTF-8");
            MainActivity.unLoader();
        } catch (Exception e) {
            Log.d(Logger.TAG_LOG, e.getMessage());
        }
    }

    @Override
    public void bound() {
        MainActivity.mBinder.mainNav.setVisibility(View.INVISIBLE);
        MainActivity.mBinder.mainNav.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
    }

    @Override
    public void unBound() {
        MainActivity.mBinder.mainNav.setVisibility(View.VISIBLE);
        MainActivity.mBinder.mainNav.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unBound();
        MainActivity.mPostDetailViewModel.postDTO.postValue(null);
    }

    public void loadPost() {
        MainActivity.loader();
        PostService.gI().getPostById(postId, task -> {
            if (task.isSuccessful()) {
                PostDTO postDTO = task.getResult().toObject(PostDTO.class);
                UserService.gI().getUserById(postDTO.getUserID(), task12 -> {
                    if (task12.isSuccessful()) {
                        UserDTO userDTO = task12.getResult().toObject(UserDTO.class);
                        // get post count and get follower
                        UserService.gI().getPostsById(userDTO.getUUID(), task1 -> {
                            if (task1.isSuccessful()) {
                                userDTO.post_count = task1.getResult().getDocuments().size();
                                UserService.gI().getFollowerById(userDTO.getUUID(), task11 -> {
                                    if (task11.isSuccessful()) {
                                        userDTO.follower_count = task11.getResult().getDocuments().size();
                                        postDTO.user = userDTO;
                                        MainActivity.mPostDetailViewModel.postDTO.postValue(postDTO);
                                    }
                                }, e -> Log.d(Logger.TAG_LOG, e.getMessage()));
                            }
                        }, e -> Log.d(e.getMessage(), ""));
                    }
                });
            }
        }, e -> showToast("Có lỗi xảy ra vui lòng thử lại sau."));
    }
}