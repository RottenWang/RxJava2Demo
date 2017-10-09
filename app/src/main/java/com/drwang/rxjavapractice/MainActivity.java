package com.drwang.rxjavapractice;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.ResourceSubscriber;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        compositeDisposableUseExample();
        //Zip操作符
        zip();
    }
    private void compositeDisposableUseExample() {
        mCompositeDisposable.add(Flowable.create((FlowableOnSubscribe<String>) e -> {
            Log.i(TAG, "compositeDisposableUseExample: " + Thread.currentThread().getName() + "size = " + mCompositeDisposable.size());
            e.onNext("123");
            e.onComplete();
        }, BackpressureStrategy.BUFFER)
                .compose(upstream -> upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()))
                .subscribeWith(new ResourceSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        Log.i("compositeDisposableUse", "onNext: " + s + Thread.currentThread().getName());
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.i("compositeDisposableUse", "onError: " + Thread.currentThread().getName());
                    }

                    @Override
                    public void onComplete() {
                        mCompositeDisposable.delete(this);
                        Log.i("compositeDisposableUse", "onComplete: " + Thread.currentThread().getName() + "size = " + mCompositeDisposable.size());

                    }
                }));
    }

    private void zip() {
        Observable.zip(getStringObservable(), getStringObservable2(), (s1, s2) -> {
            Log.i("zip", "Finish" + "s1 = " + s1 + ",s2 " + s2);
            return "success";
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(@NonNull String result) {
                        Log.i("zip", "finish: result = " + result);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.i("zip", "finish: result = ");
                    }

                    @Override
                    public void onComplete() {
                        Log.i("zip", "finish: result = ");
                    }
                });
    }

    private Observable getStringObservable() {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                SystemClock.sleep(1000);
                Log.i("zip", "finish: S1 FINISH");
                e.onNext("1000");
                e.onComplete();
            }
        });
    }

    private Observable getStringObservable2() {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                SystemClock.sleep(3000);
                Log.i("zip", "finish: S2 FINISH");
                e.onNext("3000");
                e.onComplete();
            }
        });
    }

}
