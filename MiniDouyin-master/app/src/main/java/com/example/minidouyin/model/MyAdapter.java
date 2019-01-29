package com.example.minidouyin.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.minidouyin.R;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    List<Feed> feedList;
    private Context context;
    private List<Integer> mHeights;

    private final ListItemClickListener mOnClickListener;

    public MyAdapter(Context context,List<Feed> messages, ListItemClickListener listener) {
        this.context = context;
        feedList = messages;
        mOnClickListener = listener;
        getRandomHeight(this.feedList);
    }

    public void getRandomHeight(List<Feed> mList){
        mHeights = new ArrayList<>();
        for(int i=0; i < mList.size();i++){
            //随机的获取一个范围为200-600直接的高度
            mHeights.add((int)(300+Math.random()*400));
        }
    }
    public void addRandomHeight(List<Feed> mList){
        for(int i=mHeights.size(); i < mList.size();i++){
            //随机的获取一个范围为200-600直接的高度
            mHeights.add((int)(400+Math.random()*400));
        }
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        MyViewHolder holder = new MyViewHolder(
                LayoutInflater.from(context).inflate(R.layout.activity_item, viewGroup, false));
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.activity_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        ViewGroup.LayoutParams layoutParams = myViewHolder.itemView.getLayoutParams();
        layoutParams.height = mHeights.get(i);
        myViewHolder.itemView.setLayoutParams(layoutParams);

        Feed feed = feedList.get(i);

        myViewHolder.updateUI(feed);
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private final ImageView imageView;
        private final TextView author;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            author = itemView.findViewById(R.id.author);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //通过代理
                    mOnClickListener.onListItemClick(getAdapterPosition());
                }
            });
        }
        public void updateUI(Feed feed){

            author.setText(feed.getUser_name());
            String url = feed.getCover_image();
            Glide.with(imageView.getContext()).load(url).into(imageView);
        }

    }
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }


}
