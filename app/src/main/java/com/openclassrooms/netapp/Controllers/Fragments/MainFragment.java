package com.openclassrooms.netapp.Controllers.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.openclassrooms.netapp.Models.GithubUserInfo;
import com.openclassrooms.netapp.Models.GithubUser;
import com.openclassrooms.netapp.R;
import com.openclassrooms.netapp.Utils.GitHubStreams;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    // FOR DESIGN
    @BindView(R.id.fragment_main_textview) TextView textView;

    //4 disposable var creation
    private Disposable disposable;

    public MainFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.observableDesinscriptionWhenDestroy();
    }

    // -----------------
    // ACTIONS
    // -----------------

    @OnClick(R.id.fragment_main_button_retrofit)
    public void submitWithRetrofit(View view) {
        this.executeHttpRequestWithRetrofit();
    }


    // -----------------------------------------
    //  HTTP REQUEST (Retrofit Way) using RxJava
    // -----------------------------------------

    private void executeHttpRequestWithRetrofit(){
        this.updateUIWhenStartingHTTPRequest();
        //Nous appelons depuis la classe GithubStreams notre Observable (streamobtainuserfollowing avec un param username)
        // qui va émettre les données JSON récupérées depuis l'API Github (suscribeWith) grâce à Retrofit.
        // Nous y souscrivons en créant un Subscriber (DisposableObserver)
        // et en plaçant la souscription générée
        // dans la variable de classe  this.disposable  pour éviter tout risque de Memory Leaks.
        //beware Android studio create : new DisposableObserver<List<GithubUserInfo>> and not new DisposableObserver<GithubUserInfo
        this.disposable = GitHubStreams.streamGettingTwoStream("JakeWharton").subscribeWith(new DisposableObserver<GithubUserInfo>() {
            @Override
            public void onNext(@NonNull GithubUserInfo githubUserInfos) {
                updateWithUserInfo(githubUserInfos);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    //dispose form sucribing to the stream
    //5 desinscription creation
    private void observableDesinscriptionWhenDestroy(){
        if(this.disposable != null && this.disposable.isDisposed()) this.disposable.dispose();
    }

    // ------------------
    //  UPDATE UI
    // ------------------

    private void updateUIWhenStartingHTTPRequest(){
        this.textView.setText("Downloading...");
    }

    private void updateUIWhenStoppingHTTPRequest(String response){
        this.textView.setText(response);
    }

    private void updateUIWithListOfUsers(List<GithubUser> users){
        StringBuilder stringBuilder = new StringBuilder();
        for (GithubUser user : users){
            stringBuilder.append("-"+user.getLogin()+"\n");
        }
        updateUIWhenStoppingHTTPRequest(stringBuilder.toString());
    }

    private void updateWithUserInfo(GithubUserInfo userInfo){
        updateUIWhenStoppingHTTPRequest("Le premier qui suit est"
                +userInfo.getName()+"avec"+userInfo.getFollowers()+"suivants."+userInfo.getPublicRepos()+"nb");
    }

}
