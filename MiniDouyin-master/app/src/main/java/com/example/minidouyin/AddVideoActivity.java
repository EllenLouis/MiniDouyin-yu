package com.example.minidouyin;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.minidouyin.model.PostVideoResponse;
import com.example.minidouyin.network.IMiniDouyinService;
import com.example.minidouyin.utils.ResourceUtils;

import java.io.File;

import javax.xml.datatype.Duration;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddVideoActivity extends AppCompatActivity {

    private VideoView videoView;
    private ImageView cover_image;
    private Button btn_post;

    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int PICK_IMAGE = 1;
    private static final String TAG = "AddVideo";

    public Uri mSelectedImage;
    private Uri mSelectedVideo;
    private MultipartBody.Part image;
    private MultipartBody.Part video;
    private String student_id;
    private String student_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Intent intent = getIntent();
        String value = intent.getStringExtra("video_uri");
        if(value != null)
            mSelectedVideo = Uri.fromFile(new File(value));
        videoView = findViewById(R.id.videoView);
        videoView.setVideoURI(mSelectedVideo);
        videoView.start();

        cover_image = findViewById(R.id.cover_image);

        btn_post = findViewById(R.id.btn_post);

        getUserInfo();


        setCover_image();

        findViewById(R.id.choose_cover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }

        });
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.start();
            }
        });
        findViewById(R.id.btn_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                video = getMultipartFromUri("video", mSelectedVideo);
                postVideo();

            }

        });



    }
    public void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && null != data) {
            if (requestCode == PICK_IMAGE) {
                mSelectedImage = data.getData();
                setCover_image();
                videoView.setVideoURI(mSelectedVideo);
                videoView.start();
                MultipartBody.Part coverImage = getMultipartFromUri("cover_image", mSelectedImage);
                image = coverImage;
                cover_image.setImageURI(mSelectedImage);
            }
        }
        else{
            Log.d("errorrr", "selectedImage = " );
        }

    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        // if NullPointerException thrown, try to allow storage permission in system settings
        File f = new File(ResourceUtils.getRealPath(AddVideoActivity.this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        Log.d("Test", "f.getName="+f.getName() + ", name="+name + ",request="+requestFile);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    private void postVideo() {
        btn_post.setText("POSTING...");
        btn_post.setEnabled(false);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.108.10.39:8080/") // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();
        retrofit.create(IMiniDouyinService.class).createVideo(student_id,student_name,image,video).
                enqueue(new Callback<PostVideoResponse>() {
                    @Override public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {
                        //response.body().
                        //   Log.d(TAG, response.body().getItem().getCover_image());
                        Toast.makeText(AddVideoActivity.this,"上传成功",  Toast.LENGTH_LONG).show();
                        // TODO: 2019/1/27 上传后
                    }

                    @Override public void onFailure(Call<PostVideoResponse> call, Throwable t) {
                        Log.d(TAG, "onFailure: "+t.getMessage());
                    }
                });
        AddVideoActivity.this.finish();

    }

    private void setCover_image(){
        Glide.with(cover_image.getContext())
                .load( mSelectedVideo)
                .into(cover_image);

    }
    private void getUserInfo(){
        SharedPreferences lock = getSharedPreferences("lock",MODE_PRIVATE);
        student_name = lock.getString("user_name","");
        student_id  = lock.getString("user_id","");
    }
}
