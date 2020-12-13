package com.openclassrooms.netapp.Controllers.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.openclassrooms.netapp.Models.GithubUser;
import com.openclassrooms.netapp.R;
import com.openclassrooms.netapp.Utils.GithubCalls;
import com.openclassrooms.netapp.Utils.NetworkAsyncTask;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements NetworkAsyncTask.Listeners, GithubCalls.Callbacks {

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

    @OnClick(R.id.fragment_main_button_perso_httpRequest)
    public void submitWithPersoRequest(View view) {this.executeHttpRequest();}

    @OnClick(R.id.fragment_main_button_display_RxJava_string)
    public void displayRxJavaBase(){this.streamToShowStringEmitInObservable_GetSimpleStringObserved();}

    @OnClick(R.id.fragment_main_button_display_RxJava_string_with_map)
    public void displayRxJavaWithMap(){this.streamToShowStringEmitInObservable_GetSimpleStringObserved_withMapModificateur();}

    @OnClick(R.id.fragment_main_button_display_RxJava_string_with_flatmap)
    public void displayRxJavaWithFlatMap(){this.streamWithFlatMap();}



    // ------------------------------
    //  HTTP REQUEST (Retrofit Way)
    // ------------------------------

    private void executeHttpRequestWithRetrofit(){
        this.updateUIWhenStartingHTTPRequest();
        GithubCalls.fetchUserFollowing(this, "JakeWharton");
    }

    @Override
    public void onResponse(@Nullable List<GithubUser> users) {
        if (users != null) this.updateUIWithListOfUsers(users);
    }

    @Override
    public void onFailure() {
        this.updateUIWhenStoppingHTTPRequest("An error happened !");
    }

    // ------------------
    //  HTTP REQUEST
    // ------------------

    private void executeHttpRequest(){
        new NetworkAsyncTask(this).execute("https://api.github.com/users/JakeWharton/following");
    }

    @Override
    public void onPreExecute() {
        this.updateUIWhenStartingHTTPRequest();
    }

    @Override
    public void doInBackground() { }

    @Override
    public void onPostExecute(String json) {
        this.updateUIWhenStoppingHTTPRequest(json);
    }

    // ------------------
    //  UPDATE UI
    // ------------------

    //http request & retrofit
    private void updateUIWhenStartingHTTPRequest(){
        this.textView.setText("Downloading...");
    }

    private void updateUIWhenStoppingHTTPRequest(String response){
        this.textView.setText(response);
    }

    //only retrofit
    private void updateUIWithListOfUsers(List<GithubUser> users){
        StringBuilder stringBuilder = new StringBuilder();
        for (GithubUser user : users){
            stringBuilder.append("-"+user.getLogin()+"\n");
        }
        updateUIWhenStoppingHTTPRequest(stringBuilder.toString());
    }

    //-----------------------
    //ReactiveX
    //-----------------------

    //1 observable creation: create a simple string to be observe
    //beware...Observable is a reactive object, not java.util, not database
   private Observable<String> getSimpleStringObserved(){
        return Observable.just("Cool");
   }

   //2 suscriber creation: a lambda suscriber which use a string s
    private DisposableObserver<String> getSuscriber(){
        return new DisposableObserver<String>(){
            @Override
            public void onNext(@NonNull String s){
                textView.setText("Observable emits: "+s);
            }

            @Override
            public void onError(@NonNull Throwable e){
                Log.e("Tag", "On error" +Log.getStackTraceString(e));
            }

            @Override
            public void onComplete(){
                Log.e("Tag", "Complete");
            }
        };
    }

    //3 stream creation wich bound an observable to a suscriber
    //this execute the stream too
    private void streamToShowStringEmitInObservable_GetSimpleStringObserved(){
        //disposable dosn't exist. It may be create as a class var from a Disposable interface
        this.disposable = this.getSimpleStringObserved()
                .subscribeWith(getSuscriber());
    }

    //5 desinscription creation
    private void observableDesinscriptionWhenDestroy(){
        if(this.disposable != null && this.disposable.isDisposed()) this.disposable.dispose();
    }

    private void streamToShowStringEmitInObservable_GetSimpleStringObserved_withMapModificateur(){
        this.disposable = this.getSimpleStringObserved()
                //.map doit avoir un parametre (une fonction a appliquer au mapping)
                .map(getFunctionInUppercase())
                .map(getFunctionAddingString())
                .subscribeWith(getSuscriber());
    }

    private Function<String, String> getFunctionInUppercase() {
        return new Function<String, String>() {
            //override automatiquement implementé lors de l'ecriture du return new
            @Override
            public String apply(@NonNull String s) throws Exception {
                return s.toUpperCase();
            }
        };
    }

    private Function<String,String> getFunctionAddingString(){
        return new Function<String, String>() {
            @Override
            public String apply(@NonNull String s) throws Exception {
                //on retourne une string modifiée
                return s+" Deux fois plus cool avec un second .map";
            }
        };
    }

    private void streamWithFlatMap(){
        this.disposable = this.getSimpleStringObserved()
                .map(getFunctionAddingString())
                .map(getFunctionInUppercase())
                .flatMap(getNewObservable())
                .subscribeWith(getSuscriber());
    }

    //todo ask to valdese what is the difference betwen map and flatmap...???

    private Function<String, Observable<String>> getNewObservable() {
        return new Function<String, Observable<String>>() {
            //override avec un observable
            @Override
            public Observable<String> apply(@NonNull String s) throws Exception {
                //on return un nouvel observable
                return Observable.just(s+" Et on ajoute un nouvel observable");
            }
        };
    }

}
