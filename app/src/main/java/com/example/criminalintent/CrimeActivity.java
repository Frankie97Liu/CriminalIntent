package com.example.criminalintent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class CrimeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime);

        //获取FragmentManager
        FragmentManager fm = getSupportFragmentManager();
        //在FragmentManager中添加事务,重建时会首先获取保存的队列
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null){
            fragment = new CrimeFragment();
            //创建一个新的fragment事务，执行一个fragm添加操作，然后提交该事务
            fm.beginTransaction().add(R.id.fragment_container,fragment).commit();
        }
    }
}
