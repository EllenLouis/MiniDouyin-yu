package com.example.minidouyin;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.minidouyin.model.Constant;
import com.example.minidouyin.model.Feed;
import com.example.minidouyin.model.FeedResponse;
import com.example.minidouyin.model.MyAdapter;
import com.example.minidouyin.model.SlideMenu;
import com.example.minidouyin.network.IMiniDouyinService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements MyAdapter.ListItemClickListener{

    private MyAdapter mAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private Button add_botton;
    private de.hdodenhof.circleimageview.CircleImageView user_detail;
    private SlideMenu slideMenu;
    private ImageView menu_image;

    List<Feed> feeds = new ArrayList<>();
    private static final int REQUEST_VIDEO_CAPTURE = 1;
    // TODO: 2019/1/27 增加历史提交记录
    private final String TAG = "main";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: ");

        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.video_list);
        add_botton = findViewById(R.id.add_botton);
        user_detail = findViewById(R.id.user_detail);
        slideMenu = (SlideMenu)findViewById(R.id.slideMenu);


        getAvatar();

        mRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.layout_swipe_refresh);
            // TODO: 2019/1/26 获取feedlist
        updateFeed();

        mAdapter = new MyAdapter(this,feeds,this);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager
                (2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        add_botton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                                || ContextCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                                ||ContextCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
                                            Manifest.permission.READ_EXTERNAL_STORAGE},
                                    REQUEST_VIDEO_CAPTURE);
                        } else {
                            takeVideo();
                        }
                      //
                    }
                }
            );
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            // 最后一个完全可见项的位置
            private int lastCompletelyVisibleItemPosition;
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (visibleItemCount > 0 && lastCompletelyVisibleItemPosition >= totalItemCount - 1) {
                        Toast.makeText(MainActivity.this, "已滑动到底部!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    lastCompletelyVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
                }
                //Log.d(TAG, "onScrolled: lastVisiblePosition=" + lastCompletelyVisibleItemPosition);
            }
        });
        user_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideMenu.switchMenu();;
            }
        });

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            public void onRefresh() {
                updateFeed();
                //数据重新加载完成后，提示数据发生改变，并且设置现在不在刷新
                mAdapter.notifyDataSetChanged();
                mRefreshLayout.setRefreshing(false);
            }
        });
    }


    @Override
    public void onListItemClick(int clickedItemIndex) {
        //Log.d("getin","ok");
        Intent intent = new Intent(this,DetailPlayerActivity.class);
        intent.putExtra("student_name",feeds.get(clickedItemIndex).getUser_name().toString());
        intent.putExtra("video_url",feeds.get(clickedItemIndex).getVideo().toString());
        intent.putExtra("student_id",feeds.get(clickedItemIndex).getStudent_id().toString());
        Constant.feeds = feeds;
        startActivity(intent);
    }
    private void loadPics(Feed[] newfeeds) {
        if(feeds.size() > 0){
            feeds.clear();
        }
        for(int i = 0; i < newfeeds.length; i++){
            feeds.add(newfeeds[i]);
        }
        mAdapter.addRandomHeight(feeds);
        mAdapter.notifyDataSetChanged();
    }
    private void updateFeed(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.108.10.39:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.d("message", "fetchFeed: ok");
        retrofit.create(IMiniDouyinService.class).randomFeeds().
                enqueue(new Callback<FeedResponse>() {
                    @Override public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {
                        Toast.makeText(MainActivity.this,"刷新成功", Toast.LENGTH_LONG).show();

                        loadPics(response.body().getFeeds());
                    }

                    @Override public void onFailure(Call<FeedResponse> call, Throwable t) {
                        Toast.makeText(MainActivity.this ,"刷新失败", Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void takeVideo() {
        Intent intent = new Intent(MainActivity.this,CustomCameraActivity.class);
        startActivity(intent);
    }
    private void getAvatar(){
        SharedPreferences lock = getSharedPreferences("lock",MODE_PRIVATE);
        String avatar_path = lock.getString("avatar_path","");
        user_detail.setImageURI(Uri.parse(avatar_path));
        Uri uri = Uri.fromFile(new File(avatar_path));
        menu_image = findViewById(R.id.menu_image);
        menu_image.setImageURI(uri);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = intent.getData();
            Intent mIntent = new Intent(MainActivity.this, AddVideoActivity.class);
            mIntent.putExtra("video_uri",String.valueOf(videoUri));
            startActivity(mIntent);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_VIDEO_CAPTURE: {
                //todo 判断权限是否已经授予

                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                ) {
                    takeVideo();
                    break;
                }


            }
        }
    }

}
