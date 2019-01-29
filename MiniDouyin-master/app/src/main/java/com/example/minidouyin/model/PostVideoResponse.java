package com.example.minidouyin.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Xavier.S
 * @date 2019.01.18 17:53
 */
public class PostVideoResponse {

    @SerializedName("success")
     private  String success ;
     @SerializedName("item")
    private Feed item ;

    public void setItem(Feed item) {
        this.item = item;
    }

    public Feed getItem() {
        return this.item;
    }



    public void setSuccess(String success) {
        this.success = success;
    }

    public String getSuccess() {
        return success;
    }


}
