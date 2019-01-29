package com.example.minidouyin.model;

import com.google.gson.annotations.SerializedName;

public class Feed {
    @SerializedName("student_id")
    private String student_id;

    @SerializedName("user_name")
    private String user_name;

    @SerializedName("image_url")
    private String  cover_image;

    @SerializedName("video_url")
    private String video;

    public Feed(String id, String user_name, String cover_image, String video){
        this.cover_image = cover_image;
        this.student_id = id;
        this.user_name = user_name;
        this.video =video;
    }

    public String getStudent_id(){
        return this.student_id;
    }
    public String getUser_name(){
        return this.user_name;
    }

    public String getCover_image() {
        return cover_image;
    }

    public String getVideo() {
        return video;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
