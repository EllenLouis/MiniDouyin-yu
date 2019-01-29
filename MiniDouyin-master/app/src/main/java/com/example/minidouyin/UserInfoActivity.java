package com.example.minidouyin;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.net.URI;

import okhttp3.MultipartBody;

public class UserInfoActivity extends AppCompatActivity {
    private TextView user_name;
    private TextView user_id;
    private Button btn_submit;
    private Button btn_add;
    private final String TAG = "UserInfo";
    private Uri mSelectedImage =null;
    private ImageView avatar_image;


    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);


        user_name = findViewById(R.id.user_name);
        user_id = findViewById(R.id.user_id);
        btn_submit = findViewById(R.id.submit);
        btn_add = findViewById(R.id.change_avatar);
        avatar_image = findViewById(R.id.avatar_iamge);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                        PICK_IMAGE);
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user_id.getText().toString().length()==0 || user_name.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"请输入完整信息",Toast.LENGTH_LONG).show();
                }
                else{

                    String id_get = user_id.getText().toString().trim();
                    String name_get = user_name.getText().toString().trim();
                    String image_path ;
                    if(mSelectedImage != null){
                        image_path = mSelectedImage.toString();
                    }
                    else {
                        Resources resources = getResources();
                        image_path = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                                resources.getResourcePackageName(R.drawable.icon) + "/" +
                                resources.getResourceTypeName(R.drawable.icon) + "/" +
                                resources.getResourceEntryName(R.drawable.icon);
                    }
                    SharedPreferences.Editor editor = getSharedPreferences("lock",MODE_PRIVATE).edit();
                    SharedPreferences sharedPreferences = getSharedPreferences("lock",MODE_PRIVATE);
                    editor.putString("user_name",name_get);
                    editor.putString("user_id",id_get);
                    editor.putString("avatar_path",image_path);
                    editor.commit();
                    startActivity(new Intent(UserInfoActivity.this, MainActivity.class));
                    finish();
                    //把返回数据存入Intent

                }
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && null != data) {
            if (requestCode == PICK_IMAGE) {
                mSelectedImage = data.getData();
                Glide.with(avatar_image.getContext())
                        .load( mSelectedImage)
                        .into(avatar_image);
            }
        }
    }
}
