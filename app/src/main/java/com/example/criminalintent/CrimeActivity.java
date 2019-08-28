package com.example.criminalintent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class CrimeActivity extends SingleFragmentActivity {

    //重写抽象类方法，构造CrimeListFragment实例
    @Override
    protected Fragment createFragment() {
        return new CrimeFragment();
    }
}
