package com.example.abc.myapplication26;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmMigration;
import io.realm.RealmResults;
import io.realm.rx.CollectionChange;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "text";

    Realm realm;
    @BindView(R.id.tv_showMessage)
    TextView tvShowMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();


    }


    public void add() {
        realm.beginTransaction();

        Person person1 = new Person();
        person1.setAge(10);
        person1.setId("1");
        person1.setName("alibaba");
        realm.copyToRealmOrUpdate(person1);//把指定RealmObject类插入数据库，如已存在主键相同的记录则更新原记录。
        //推荐使用该方法


        Person person2 = realm.createObject(Person.class, "2");
        person2.setName("wahaha");
        person2.setAge(56);//从RealmObject类创建一条数据库记录，后面直接使用该类的设置方法即可写入字段值。
        //如果类里存在主键，则不推荐使用这种方法

        Person person3 = new Person();
        person3.setAge(34);
        person3.setName("json");
        person3.setId("3");
        realm.copyToRealm(person3);//把指定RealmObject类插入数据库，如已存在主键相同的记录则扔出异常。

        realm.commitTransaction();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Person person4 = new Person();
                person4.setId("4");
                person4.setName("doublekk");
                person4.setAge(3);
                realm.copyToRealmOrUpdate(person4);
            }
        });//相比realm.beginTransaction()与realm.commitTranslation()的配套使用，还可以这样开启一个事务
        //注意在ui线程插入大量数据可能会ANR

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Person person5 = new Person();
                person5.setId("5");
                person5.setName("caonima");
                person5.setAge(54);
                realm.copyToRealmOrUpdate(person5);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {

            }
        });//异步开启一个事务，并且有回调

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Cat cat=new Cat();
                cat.setAge(1);
                cat.setName("fac");
                realm.copyToRealmOrUpdate(cat);
            }
        });

    }

    public void delete() {
        RealmResults<Person> realmResults = realm.where(Person.class).equalTo("id", "2")
                .findAll();
        realm.beginTransaction();
        if (realmResults.isLoaded() && realmResults.size() != 0) {
            realmResults.get(0).deleteFromRealm();

        }
        realm.commitTransaction();

        //删除操作先要查询出数据，然后再删
    }

    public void query() {


        RealmResults<Person> realmResults=realm.where(Person.class).equalTo("id","3").findAll();


        RealmResults<Person> realmResults1=realm.where(Person.class).findAllAsync();
        realmResults.addChangeListener(new RealmChangeListener<RealmResults<Person>>() {
            @Override
            public void onChange(RealmResults<Person> people) {
                String s="";
                for(Person p:people)
                {
                    s+=p.getId()+p.getName()+p.getAge();
                    tvShowMessage.setText(s);
                }

                Log.d(TAG, Thread.currentThread().getName());
            }
        });

    }

    public void update() {
        RealmResults<Person> realmResults = realm.where(Person.class).equalTo("id", "1")
                .findAll();
        realm.beginTransaction();
        if (realmResults.isLoaded() && realmResults.size() != 0) {


            realmResults.get(0).setAge(38);

        }
        realm.commitTransaction();
    }

    public void realmAndRxjava()
    {
        realm.where(Person.class).findAll().asChangesetObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CollectionChange<RealmResults<Person>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CollectionChange<RealmResults<Person>> value) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @OnClick({R.id.btn_add, R.id.btn_delete, R.id.btn_update, R.id.btn_query})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                add();
                break;
            case R.id.btn_delete:
                delete();
                break;
            case R.id.btn_update:
                update();
                break;
            case R.id.btn_query:
                query();
                break;
        }
    }
}
