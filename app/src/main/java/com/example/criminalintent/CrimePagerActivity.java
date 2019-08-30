package com.example.criminalintent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String EXTRA_CRIME_ID = "crime_id";

    private ViewPager mViewPager;
    private List<Crime> mCrimes;

    private Button mStartButton;
    private Button mLastButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        //创建fragment argument
        //获取CrimeFragment传来的extra
        final UUID crimeId = (UUID)getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        Log.d("crimeId", "onCreate: "+crimeId.toString());


        mViewPager = findViewById(R.id.activity_crime_pager_view_pager);

        //从Crimelab中获取数据集
        mCrimes = CrimeLab.get(this).getCrimes();

        mStartButton = findViewById(R.id.start);
        mStartButton.setOnClickListener(this);
        mLastButton = findViewById(R.id.end);
        mLastButton.setOnClickListener(this);

        //设置pageAdapter
        FragmentManager fragmentManager = getSupportFragmentManager();

        //FragmentStateAdapter是负责管理与ViewPager的对话并协同工作
        /**
         * 代理工作：将返回的fragment托管给activity，并帮助viewpager找到fragment视图并一一对应
         */
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                //默认只显示第一个列表项,创建一个CrimeFragment视图
                return CrimeFragment.newInstance(crime.getTitleId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        //设置初始分页显示项
        for (int i =0; i<mCrimes.size();i++){
            //如果Crime实例的mID和intent extra的crimeId相匹配
            if (mCrimes.get(i).getTitleId().equals(crimeId)){
                //显示指定位置列表项
                mViewPager.setCurrentItem(i);
                break;
            }
        }

        //设置viewpager响应监听器
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position==0 || position==mCrimes.size()-1){
                    mStartButton.setEnabled(false);
                    mLastButton.setEnabled(false);
                }else {
                    mStartButton.setEnabled(true);
                    mLastButton.setEnabled(true);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.end:
                mViewPager.setCurrentItem(mCrimes.size()-1);
        }
    }

}
