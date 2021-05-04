package com.example.map_pa;

import android.content.Context;
import android.media.Image;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class postAdapter extends RecyclerView.Adapter<postAdapter.postViewHolder>{

    private Context mContext;
    private List<post> mPost;
    public postAdapter(Context context, List<post> posts){
        this.mContext = context;
        this.mPost = posts;
    }

    @NonNull
    @Override
    public postViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item,parent,false);
        postViewHolder holder = new postViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull postViewHolder holder, int position) {
        post currentPost = mPost.get(position);
        holder.username.setText(currentPost.getUsername());
        holder.content.setText(currentPost.getContent());
        holder.tag.setText(currentPost.getTag());
        Glide.with(mContext).load(currentPost.getProfile()).fitCenter().centerCrop().into(holder.profileView);
        Glide.with(mContext).load(currentPost.getImage()).fitCenter().centerCrop().into(holder.imageView);
        if(TextUtils.isEmpty(currentPost.getProfile())){
            holder.profileView.setVisibility(View.GONE);
        }
        if(TextUtils.isEmpty(currentPost.getImage())){
            holder.imageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }


    public class postViewHolder extends RecyclerView.ViewHolder{
        public ImageView profileView;
        public TextView username;
        public TextView content;
        public TextView tag;
        public ImageView imageView;

        public postViewHolder(@NonNull View itemView) {
            super(itemView);

            profileView = itemView.findViewById(R.id.profile_post_item);
            username = itemView.findViewById(R.id.name_post_item);
            content = itemView.findViewById(R.id.content_post_item);
            tag = itemView.findViewById(R.id.tag_post_item);
            imageView = itemView.findViewById(R.id.image_view);


        }

    }
}
