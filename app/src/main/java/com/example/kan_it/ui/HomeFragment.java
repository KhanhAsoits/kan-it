package com.example.kan_it.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.kan_it.DTO.CommentDTO;
import com.example.kan_it.DTO.PostDTO;
import com.example.kan_it.R;
import com.example.kan_it.adapter.PostAdapter;
import com.example.kan_it.core.FireStore;
import com.example.kan_it.core.Logger;
import com.example.kan_it.databinding.FragmentHomeBinding;
import com.example.kan_it.model.User;
import com.example.kan_it.service.PostService;
import com.example.kan_it.service.UserService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    FragmentHomeBinding mBinder;

    public static PostAdapter postAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mBinder = FragmentHomeBinding.inflate(inflater);
        return mBinder.getRoot();
    }

    public void regListener() {
        PostAdapter.OnEvent onEvent = new PostAdapter.OnEvent() {
            @Override
            public void onClick(View view, String id) {
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                MainActivity.mMenuController.navigate(R.id.postDetailFragment, bundle);
            }
        };
        postAdapter = new PostAdapter(requireActivity().getApplicationContext(), onEvent);
        MainActivity.mPostViewModel.data.observe(requireActivity(), new Observer<List<PostDTO>>() {
            @Override
            public void onChanged(List<PostDTO> postDTOS) {
                Log.d(Logger.TAG_LOG, postDTOS.size() + "");
                postAdapter.setData(postDTOS);
            }
        });
    }

    public void showToast(String msg) {
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void loadPost() {

        if (MainActivity.mPostViewModel.data.getValue() == null) {
            PostService.gI().getListPost(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        MainActivity.loader();
                        List<PostDTO> postDTOS = new ArrayList<>();
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            PostDTO postDTO = snapshot.toObject(PostDTO.class);
                            postDTO.setID(snapshot.getId());
                            UserService.gI().getUserById(postDTO.getUserID(), new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        User user = task.getResult().toObject(User.class);
                                        if (user != null) {
                                            postDTO.user_name = user.getUsername();
                                            postDTO.user_photo = user.getPhoto();
                                            postDTO.star = user.getStar();
                                            // load comment
                                            FireStore.gI().collection(FireStore.COMMENT_COLLECTION).whereEqualTo("postID", postDTO.getID()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    List<CommentDTO> commentDTOS = new ArrayList<>();
                                                    if (task.isSuccessful()) {
                                                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                                            CommentDTO commentDTO = documentSnapshot.toObject(CommentDTO.class);
                                                            commentDTO.setID(documentSnapshot.getId());
                                                            UserService.gI().getUserById(commentDTO.getUserID(), new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        User user = task.getResult().toObject(User.class);
                                                                        commentDTO.user = user;
                                                                        commentDTOS.add(commentDTO);
                                                                        postDTO.post_comment_count = commentDTOS.size();
                                                                        postAdapter.setData(postDTOS);
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            });
                                            postDTOS.add(postDTO);
                                            MainActivity.mPostViewModel.data.postValue(postDTOS);
                                        }
                                    }
                                }
                            });
                        }
                        MainActivity.unLoader();
                    } else {
//                        showToast("Có lỗi xảy ra");
                    }
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                    showToast("Có lỗi xảy ra");
                }
            });
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        regListener();
        mBinder.rcyPosts.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext(), RecyclerView.VERTICAL, false));
        mBinder.rcyPosts.setAdapter(postAdapter);
        loadPost();
    }
}