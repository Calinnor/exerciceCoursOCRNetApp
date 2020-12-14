package com.openclassrooms.netapp.Utils;

import com.openclassrooms.netapp.Models.GithubUserInfo;
import com.openclassrooms.netapp.Models.GithubUser;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class GitHubStreams {
    public static Observable<List<GithubUser>> streamGettingUserFollowing(String username){
        GithubService githubService = GithubService.retrofit.create(GithubService.class);
        return githubService.getFollowing(username)
                //beware there's a difference between Scheduler and Schedulers !!:!
                //A schedulers.io is a dedied thread: not anymore asynctask or handlerthread
                .subscribeOn(Schedulers.io())
                //all suscribers listen stream (AndroidSchedulers) on mainthread
                .observeOn(AndroidSchedulers.mainThread())
                //beware the timeunit use here is the java.util.conccurent
                .timeout(10, TimeUnit.SECONDS);
    }

    //create a new stream with ionfos
    public static Observable<GithubUserInfo> streamGettingUserInfo(String username){
        GithubService githubService = GithubService.retrofit.create(GithubService.class);
        return githubService.getUserInfos(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    //create a stream fectching stream one and two
    public static Observable<GithubUserInfo>
    streamGettingTwoStream(String username) {
        return streamGettingUserFollowing(username)
                .map(new Function<List<GithubUser>, GithubUser>() {
                    @Override
                    public GithubUser apply(@NonNull List<GithubUser> githubUsers) throws Exception {
                        return githubUsers.get(0);
                    }
                })
                //beware here , android studio create this line
                // .map(new Function<List<GithubUser>, GitHubUserInfo>() {
                //instead:
                // .map(new Function<List<GithubUser>, GithubUser>()

                .flatMap(new Function<GithubUser, Observable<GithubUserInfo>>() {
                    @Override
                    public Observable<GithubUserInfo> apply(@NonNull GithubUser githubUser) throws Exception {
                        return streamGettingUserInfo(githubUser.getLogin());
                    }
                });
    }

}
