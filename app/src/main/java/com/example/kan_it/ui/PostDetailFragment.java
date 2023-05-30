package com.example.kan_it.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.TextUtils;
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
import com.example.kan_it.DTO.CommentDTO;
import com.example.kan_it.DTO.PostDTO;
import com.example.kan_it.DTO.UserDTO;
import com.example.kan_it.R;
import com.example.kan_it.adapter.CommentAdapter;
import com.example.kan_it.adapter.PostAdapter;
import com.example.kan_it.core.AlertCore;
import com.example.kan_it.core.FireStore;
import com.example.kan_it.core.IBoundLayout;
import com.example.kan_it.core.Logger;
import com.example.kan_it.databinding.FragmentPostDetailBinding;
import com.example.kan_it.model.Comment;
import com.example.kan_it.model.Follow;
import com.example.kan_it.model.User;
import com.example.kan_it.service.CommentService;
import com.example.kan_it.service.PostService;
import com.example.kan_it.service.UserService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import dev.shreyaspatil.MaterialDialog.AbstractDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

public class PostDetailFragment extends Fragment implements IBoundLayout {
    FragmentPostDetailBinding mBinder;
    String postId;
    PostAdapter ownerPostAdapter;
    PostAdapter tagsPostAdapter;
    CommentAdapter commentAdapter;

    boolean isFollow = false;
    boolean isReader;
    boolean isPushComment = false;


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
        updatePost();
    }

    public void updatePost() {
        try {
            new Handler().postDelayed(() -> {
                if (!isReader) {
                    PostService.gI().increaseReader(postId);
                }
            }, 60 * 1000);
        } catch (Exception e) {

        }
    }


    public void regEvent() {
        MainActivity.mPostDetailViewModel.postDTO.observe(requireActivity(), new Observer<PostDTO>() {
            @Override
            public void onChanged(PostDTO postDTO) {
                renderView(postDTO);
            }
        });
        MainActivity.mPostDetailViewModel.postOfUser.observe(requireActivity(), new Observer<List<PostDTO>>() {
            @Override
            public void onChanged(List<PostDTO> postDTOS) {
                renderPostOfUser(postDTOS);
            }
        });
        MainActivity.mPostDetailViewModel.postOfTag.observe(requireActivity(), new Observer<List<PostDTO>>() {
            @Override
            public void onChanged(List<PostDTO> postDTOS) {
                renderPostOfTag(postDTOS);
            }
        });
        MainActivity.mPostDetailViewModel.commentOfPost.observe(requireActivity(), new Observer<List<CommentDTO>>() {
            @Override
            public void onChanged(List<CommentDTO> CommentDTOs) {
                renderComment(CommentDTOs);
            }
        });
        mBinder.btnGiveStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.mAuthModel.wrapperAuth()) {
                    showToast("Bạn cần đăng nhập để sử dụng chức năng này.");
                    return;
                }
                if (MainActivity.mAuthModel.mCurrentUser.getStar() - 1 < 0) {
                    showToast("Bạn không đủ sao.");
                    return;
                }
                if (MainActivity.mAuthModel.mCurrentUser.getUUID().equals(MainActivity.mPostDetailViewModel.postDTO.getValue().user.getUUID())) {
                    showToast("Bạn không thể tặng sao cho chính mình");
                    return;
                }
                UserService.gI().giveStar(MainActivity.mAuthModel.mCurrentUser, MainActivity.mPostDetailViewModel.postDTO.getValue().user, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mBinder.txtStar.setText(String.valueOf(MainActivity.mPostDetailViewModel.postDTO.getValue().user.getStar() + 1));
                            MainActivity.mAuthModel.mCurrentUser.setStar(MainActivity.mAuthModel.mCurrentUser.getStar() - 1);
                            showToast("Tặng sao thành công.");
                        }
                    }
                });
            }
        });
        mBinder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.mAuthModel.wrapperAuth()) {
                    showToast("Chức năng này yêu cầu bạn phải đăng nhập");
                    return;
                }
                if (isFollow) {
                    showToast("Bạn đã theo dõi người này rồi");
                    return;
                }
                UserService.gI().followUser(MainActivity.mAuthModel.mCurrentUser.getUUID(), MainActivity.mPostDetailViewModel.postDTO.getValue().getUserID(), new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        mBinder.btnFollow.setText("Đã theo dõi");
                    }
                }, new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Có lỗi xảy ra");
                    }
                });
            }
        });

        mBinder.btnVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MainActivity.mAuthModel.wrapperAuth()) {
                    if (MainActivity.mPostDetailViewModel.postDTO.getValue().voteIds.contains(MainActivity.mAuthModel.mCurrentUser.getUUID())) {
                        showToast("Bạn đã vote rồi.");
                        return;
                    }
                    AlertCore.initYesNoDiaLog(requireActivity(), "Bạn có muốn vote cho bài viết này?", new AbstractDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            PostService.gI().giveVote(MainActivity.mAuthModel.mCurrentUser.getUUID(), MainActivity.mPostDetailViewModel.postDTO.getValue().voteIds, MainActivity.mPostDetailViewModel.postDTO.getValue().getID(), new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // update vote here
                                    if (task.isSuccessful()) {
                                        showToast("vote thành công.");
                                        HomeFragment.loadPost();
                                        dialogInterface.dismiss();
                                    }
                                }
                            });
                        }
                    }, new AbstractDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                } else {
                    showToast("Chức năng này yêu cầu bạn phải đăng nhập");
                }
            }
        });
    }

    public void renderComment(List<CommentDTO> commentDTOS) {
        if (commentAdapter == null) {
            CommentAdapter.OnCommentEvent onCommentEvent = new CommentAdapter.OnCommentEvent() {
                @Override
                public void onClickUpVoteButton(View view, String commentID) {

                }

                @Override
                public void onClickDownVoteButton(View view, String commentID) {

                }
            };
            commentAdapter = new CommentAdapter(requireActivity(), onCommentEvent);
        }
        mBinder.rcyCommentOfPost.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mBinder.rcyCommentOfPost.setAdapter(commentAdapter);
        commentAdapter.setData(commentDTOS);
        mBinder.txtTitleComment.setText(String.format("Bình luận(%d)", commentDTOS.size()));
        mBinder.txtDetailCommentCount.setText(String.valueOf(commentDTOS.size()));
    }

    public void renderPostOfTag(List<PostDTO> postDTOS) {
        PostAdapter.OnEvent onEvent = new PostAdapter.OnEvent() {
            @Override
            public void onClick(View view, String id) {
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                MainActivity.mMenuController.navigate(R.id.postDetailFragment, bundle);
            }
        };
        mBinder.rcyPostOfTag.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        if (tagsPostAdapter == null) {
            tagsPostAdapter = new PostAdapter(requireActivity().getApplicationContext(), onEvent);
        }
        mBinder.rcyPostOfTag.setAdapter(tagsPostAdapter);
        tagsPostAdapter.setData(postDTOS);
    }

    public void renderPostOfUser(List<PostDTO> postDTOS) {
        PostAdapter.OnEvent onEvent = new PostAdapter.OnEvent() {
            @Override
            public void onClick(View view, String id) {
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                MainActivity.mMenuController.navigate(R.id.postDetailFragment, bundle);
            }
        };
        mBinder.rcyPostOfUser.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        if (ownerPostAdapter == null) {
            ownerPostAdapter = new PostAdapter(requireActivity().getApplicationContext(), onEvent);
        }
        mBinder.rcyPostOfUser.setAdapter(ownerPostAdapter);
        ownerPostAdapter.setData(postDTOS);
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
            mBinder.btnPushComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createComment();
                }
            });
            if (!MainActivity.mAuthModel.wrapperAuth()) {
                for (Follow user : postDTO.user.follow_user) {
                    if (TextUtils.equals(user.getFollowerID(), MainActivity.mAuthModel.mCurrentUser.getUUID())) {
                        isFollow = true;
                        mBinder.btnFollow.setText("Đã theo dõi");
                    }
                }
            }
            loadRelatedPostByOwner();
            loadRelatedPostByTag();
            loadPostComment();
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

    public void loadPostComment() {
        FireStore.gI().collection(FireStore.COMMENT_COLLECTION).whereEqualTo("postID", MainActivity.mPostDetailViewModel.postDTO.getValue().getID()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                                    MainActivity.mPostDetailViewModel.commentOfPost.postValue(commentDTOS);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void createComment() {
        if (MainActivity.mAuthModel.mCurrentUser == null || MainActivity.mAuthModel.mCurrentUser.getUUID().equals("")) {
            showToast("Bạn cần đăng nhập để sử dụng chức năng này");
            return;
        }
        if (!String.valueOf(mBinder.edtCommentContent.getText()).equals("") && !isPushComment) {
            isPushComment = true;
            Comment comment = new Comment();
            comment.setCreate_at(System.currentTimeMillis());
            comment.setUserID(MainActivity.mAuthModel.mCurrentUser.getUUID());
            comment.setContent(String.valueOf(mBinder.edtCommentContent.getText()));
            comment.setVote(0);
            comment.setPostID(MainActivity.mPostDetailViewModel.postDTO.getValue().getID());
            CommentService.gI().createComment(comment, new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()) {

                        DocumentReference documentReference = task.getResult();
                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    List<CommentDTO> commentDTOS = MainActivity.mPostDetailViewModel.commentOfPost.getValue();
                                    if (commentDTOS == null) {
                                        commentDTOS = new ArrayList<>();
                                    }
                                    CommentDTO commentDTO = task.getResult().toObject(CommentDTO.class);
                                    if (commentDTO != null) {
                                        List<CommentDTO> finalCommentDTOS = commentDTOS;
                                        UserService.gI().getUserById(commentDTO.getUserID(), new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    commentDTO.user = task.getResult().toObject(User.class);
                                                    finalCommentDTOS.add(commentDTO);
                                                    MainActivity.mPostDetailViewModel.commentOfPost.postValue(finalCommentDTOS);
                                                    showToast("Đã đăng bình luận");
                                                    isPushComment = false;
                                                    mBinder.edtCommentContent.setText("");
                                                }
                                            }
                                        });
                                    }

                                }
                            }
                        });
                    }
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    isPushComment = false;
                    showToast("Tạm thời không thể đăng bình luận hãy thử lại sau.");
                }
            });
        } else {
            showToast("Nội dung comment không để trống!");
        }
    }

    public void loadRelatedPostByTag() {
        try {
            FireStore.gI().collection(FireStore.POST_COLLECTION).whereArrayContainsAny("tagIds", MainActivity.mPostDetailViewModel.postDTO.getValue().getTags()).whereEqualTo("verify", true).limit(5).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<PostDTO> postDTOS = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            if (documentSnapshot.exists()) {
                                PostDTO postDTO = documentSnapshot.toObject(PostDTO.class);
                                postDTO.setID(documentSnapshot.getId());
                                UserService.gI().getUserById(postDTO.getUserID(), new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            User user = task.getResult().toObject(User.class);
                                            if (user != null) {
                                                postDTO.user_name = user.getUsername();
                                                postDTO.user_photo = user.getPhoto();
                                                postDTO.star = user.getStar();
                                                postDTOS.add(postDTO);
                                                MainActivity.mPostDetailViewModel.postOfTag.postValue(postDTOS);
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {

        }
    }

    public void loadRelatedPostByOwner() {
        try {
            PostService.gI().getPostByUser(MainActivity.mPostDetailViewModel.postDTO.getValue().getUserID(), new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<PostDTO> postDTOS = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            if (documentSnapshot.exists()) {
                                PostDTO postDTO = documentSnapshot.toObject(PostDTO.class);
                                postDTO.setID(documentSnapshot.getId());
                                UserService.gI().getUserById(postDTO.getUserID(), new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            User user = task.getResult().toObject(User.class);
                                            if (user != null) {
                                                postDTO.user_name = user.getUsername();
                                                postDTO.user_photo = user.getPhoto();
                                                postDTO.star = user.getStar();
                                                postDTOS.add(postDTO);
                                                MainActivity.mPostDetailViewModel.postOfUser.postValue(postDTOS);
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showToast("Có lỗi xảy ra");
                }
            });
        } catch (Exception e) {

        }

    }

    public void loadPost() {
        MainActivity.loader();
        PostService.gI().getPostById(postId, task -> {
            if (task.isSuccessful()) {
                PostDTO postDTO = task.getResult().toObject(PostDTO.class);
                postDTO.setID(task.getResult().getId());
                UserService.gI().getUserById(postDTO.getUserID(), task12 -> {
                    if (task12.isSuccessful()) {
                        UserDTO userDTO = task12.getResult().toObject(UserDTO.class);
                        // get post count and get follower
                        UserService.gI().getPostsById(userDTO.getUUID(), task1 -> {
                            if (task1.isSuccessful()) {
                                userDTO.post_count = task1.getResult().getDocuments().size();
                                UserService.gI().getFollowerById(userDTO.getUUID(), task11 -> {
                                    if (task11.isSuccessful()) {
                                        userDTO.follow_user = task11.getResult().toObjects(Follow.class);
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