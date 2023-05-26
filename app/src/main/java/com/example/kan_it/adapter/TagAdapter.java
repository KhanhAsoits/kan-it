package com.example.kan_it.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kan_it.R;
import com.example.kan_it.model.Tag;

import java.util.ArrayList;
import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {

    private List<Tag> mData;


    public TagAdapter() {
        mData = new ArrayList<>();
    }

    public void setData(List<Tag> tags) {
        mData = tags;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_item, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        holder.onBind(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class TagViewHolder extends RecyclerView.ViewHolder {


        TextView mTxtTagName;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            mTxtTagName = itemView.findViewById(R.id.txt_tag_name);
        }

        public void onBind(Tag tag) {
            mTxtTagName.setText(tag.getTagName());
        }
    }
}
