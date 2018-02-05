package com.example.administrator.myfacetest.retrofit.api;

import org.json.JSONObject;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by xyuxiao on 2016/9/23.
 */
public interface BaseApi {



    @POST("FacePhoto.php")
    Observable<JSONObject> uploadPhotoBase(
            @Query("ticketid") String ticketid,
            @Query("type") int type
    );





}

