package com.openclassrooms.netapp.Utils;


import com.openclassrooms.netapp.Models.GithubUserInfo;
import com.openclassrooms.netapp.Models.GithubUser;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Philippe on 20/12/2017.
 */

public interface GithubService {
    //we are going to modife the folowing request:
    //
//    @GET("users/{username}/following")
//    Call<List<GithubUser>> getFollowing(@Path("username") String username);
//
//    public static final Retrofit retrofit = new Retrofit.Builder()
//            .baseUrl("https://api.github.com/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build();
    //in:

    @GET("users/{username}/following")
    Observable<List<GithubUser>> getFollowing(@Path("username") String username);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            //the adapter transform data in observable
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
    //can delete githubcall now
    //and replace it with githubstream ;)

    @GET("/users/{username}")
    Observable<GithubUserInfo> getUserInfos(@Path("username") String username);
}
