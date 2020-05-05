package com.example.mylivedata;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

public class TwoAct extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.two_activity);

        LiveDataBus.getInstance().with("Main_act",String.class).observe(TwoAct.this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.e("TwoAct----------2",s);
            }
        });
    }

    public void sedMessage(View view) {


    }
}
