package com.example.abc.myapplication26;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration configuration=new RealmConfiguration.Builder().
                name("text.realm")//数据库名字
                .schemaVersion(0)//数据库版本
                .build();
        Realm.setDefaultConfiguration(configuration);
    }
}
