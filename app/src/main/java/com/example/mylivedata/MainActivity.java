package com.example.mylivedata;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

   // GLiveData<String> mutableLiveData;
   LiveDataBus.MyMutableLiveData mutableLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mutableLiveData=LiveDataBus.getInstance().with("Main_act",String.class);
         mutableLiveData.observe(MainActivity.this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.e("MainActivity----------1",s);
            }
        });
      //  mutableLiveData=new MutableLiveData<>();
//        mutableLiveData.observe();
//        mutableLiveData.setValue();
    }

    //观察者和接口回调的区别？ 接口回调是1对1的，观察者是1对多的
    public void getMessage(View view) {
        //注册观察者，来获取mutableLiveData
        LiveDataBus.getInstance().with("Main_act",String.class).postValue("李三");

    }

    /**
     * 跳转
     * @param view
     */
    public void jumpBt(View view) {
        Intent intent=new Intent(MainActivity.this,TwoAct.class);
        startActivity(intent);
    }
}
