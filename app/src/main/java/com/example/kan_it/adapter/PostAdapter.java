package com.example.kan_it.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kan_it.DTO.PostDTO;
import com.example.kan_it.R;
import com.example.kan_it.core.Logger;
import com.example.kan_it.model.Tag;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<PostDTO> mData;
    public Context mContext;

    public interface OnEvent {
        public void onClick(View view, String id);
    }

    private OnEvent onEvent;

    public PostAdapter(Context context, OnEvent onEvent) {
        mData = new ArrayList<>();
        this.mContext = context;
        this.onEvent = onEvent;
    }


    public void setData(List<PostDTO> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.onBind(mData.get(position), mContext, onEvent);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView mImvPhoto;
        TextView mTxtName;
        TextView mTxtTimeUp;
        TextView mTxtDuration;
        TextView mTxtTitle;
        RecyclerView mRcvTag;
        TextView mTxtRead;
        TextView mTxtBookMark;
        TextView mTxtVote;
        TextView mTxtComment;
        TextView mTxtStar;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            mTxtStar = itemView.findViewById(R.id.txt_start_count);
            mImvPhoto = itemView.findViewById(R.id.imv_photo);
            mTxtName = itemView.findViewById(R.id.txt_poser_name);
            mTxtTimeUp = itemView.findViewById(R.id.txt_time_up);
            mTxtDuration = itemView.findViewById(R.id.txt_time_read);
            mTxtTitle = itemView.findViewById(R.id.txt_title);
            mRcvTag = itemView.findViewById(R.id.ryc_tags);
            mTxtRead = itemView.findViewById(R.id.txt_read_count);
            mTxtBookMark = itemView.findViewById(R.id.txt_bookmark_count);
            mTxtVote = itemView.findViewById(R.id.txt_vote_count);
            mTxtComment = itemView.findViewById(R.id.txt_comment_count);

        }

        public void onBind(PostDTO post, Context context, OnEvent onEvent) {
            // bind avatar
            Glide.with(context).load(post.user_photo).error(R.drawable.logo).centerCrop().into(mImvPhoto);
            mTxtName.setText(post.user_name);
            mTxtDuration.setText(post.getDurationTimeRead(post.getDesc().length()));
            mTxtComment.setText(String.valueOf(post.post_comment_count));
            mTxtVote.setText(String.valueOf(post.voteIds.size()));
            mTxtRead.setText(String.valueOf(post.getView()));
            mTxtBookMark.setText(String.valueOf(post.getBook_mark_count()));
            mTxtTitle.setText(post.getTitle());
            mTxtTimeUp.setText(post.getTimeUp(post.getUpdated_at()));
            mTxtStar.setText(String.valueOf(post.star));
            // bind tag
            TagAdapter tagAdapter = new TagAdapter();
            mRcvTag.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            mRcvTag.setAdapter(tagAdapter);
            List<Tag> tags = new ArrayList<>();
            for (String s : post.getTags()) {
                Tag tag = new Tag();
                tag.setTagName(s);
                tag.setID(UUID.randomUUID().toString());
                tags.add(tag);
            }
            tagAdapter.setData(tags);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEvent.onClick(itemView, post.getID());
                }
            });
        }
    }

}
