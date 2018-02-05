package com.example.administrator.myfacetest.retrofit.api;

import org.json.JSONObject;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by xyuxiao on 2016/9/23.
 */
public interface BaseApi {



    //@POST("FacePhoto.php")
    @POST("api.php")
    Observable<JSONObject> uploadPhotoBase(
            @Query("ticketId") String ticketid,
            @Query("type") int type
    );





}

