package com.example.kan_it.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kan_it.DTO.CommentDTO;
import com.example.kan_it.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<CommentDTO> mData;

    private Context mContext;

    public interface OnCommentEvent {
        public void onClickUpVoteButton(View view, String commentID);

        public void onClickDownVoteButton(View view, String commentID);
    }

    private OnCommentEvent onCommentEvent;

    public CommentAdapter(Context context, OnCommentEvent onCommentEvent) {
        this.onCommentEvent = onCommentEvent;
        mData = new ArrayList<>();
        mContext = context;
    }

    public void setData(List<CommentDTO> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.onBind(mData.get(position), onCommentEvent, mContext);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView imvPhoto;
        TextView txtName;
        TextView txtEmail;
        TextView txtTime;
        TextView txtContent;
        TextView btnUpVote;
        TextView btnDownVote;
        TextView txtVote;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            imvPhoto = itemView.findViewById(R.id.imv_comment_user_photo);
            txtName = itemView.findViewById(R.id.txt_comment_username);
            txtEmail = itemView.findViewById(R.id.txt_comment_email);
            txtTime = itemView.findViewById(R.id.txt_user_up_time);
            txtContent = itemView.findViewById(R.id.txt_comment_content);
        }

        public void onBind(CommentDTO commentDTO, OnCommentEvent onCommentEvent, Context context) {
            try {
                Glide.with(itemView).load(commentDTO.user.getPhoto()).error(R.drawable.logo).into(imvPhoto);
                txtName.setText(commentDTO.user.getUsername());
                txtEmail.setText(commentDTO.user.getEmail());
                txtTime.setText(new Date(commentDTO.getCreate_at()).toLocaleString());
                txtContent.setText(commentDTO.getContent());
                txtVote.setText(String.valueOf(commentDTO.getVote()));
                btnDownVote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCommentEvent.onClickDownVoteButton(v, commentDTO.getID());
                    }
                });
                btnUpVote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCommentEvent.onClickUpVoteButton(v, commentDTO.getID());
                    }
                });
            } catch (Exception e) {

            }
        }
    }
}
