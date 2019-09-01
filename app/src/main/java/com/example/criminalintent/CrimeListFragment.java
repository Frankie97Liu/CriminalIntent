package com.example.criminalintent;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CrimeListFragment extends Fragment {

    private static final int REQUEST_CRIME = 1;

    //为CrimeListFragment配置视图
    private RecyclerView mCrimeRecyclerView;

    private CrimeAdapter mAdapter;

    private boolean mSubtitleVisible; //记录子标签状态

    private TextView nullCrimeList;

    private Button addCrimeButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //接收选项菜单方法回调
        setHasOptionsMenu(true);

        if (savedInstanceState!=null){
            mSubtitleVisible = savedInstanceState.getBoolean("subtitle");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list,container,false);

        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        /*CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        mAdapter = new CrimeAdapter(crimes);
        mCrimeRecyclerView.setAdapter(mAdapter);
        */
        updateUI();

        nullCrimeList = view.findViewById(R.id.null_crime_list);
        addCrimeButton = view.findViewById(R.id.add_crime);
        addCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewCrimePagerAct();
            }
        });


        return view;
    }

    //创建ViewHolder
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //绑定列表项
        private TextView mTitleTextView;
        private TextView mDataTextView;
        private ImageView mSolvedImageView;

        private Crime mCrime;

        public CrimeHolder(LayoutInflater inflater,ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_crime,parent,false));
            itemView.setOnClickListener(this);

            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDataTextView = itemView.findViewById(R.id.crime_data);
            mSolvedImageView = itemView.findViewById(R.id.crime_solved);

        }

        public void bind(Crime crime){
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDataTextView.setText(formateDate(mCrime.getTitleData()));
            mSolvedImageView.setVisibility(crime.isSolved()? View.VISIBLE:View.GONE);
        }

        @Override
        public void onClick(View v) {
            //启动CrimePagerActivity活动,传递extra
            Intent intent = new Intent(getActivity(),CrimePagerActivity.class);
            intent.putExtra("crime_id",mCrime.getTitleId());
            Log.d("id", "onClick: "+mCrime.getTitleId());
            startActivity(intent);
        }
    }

    //创建Adapter
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{
        private List<Crime>mCrimes;

        public CrimeAdapter(List<Crime> crimes){
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }

    /**
     * 创建菜单
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);

        //更新菜单项
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        }else{
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    /**
     * 响应菜单项选择事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_crime:
                addNewCrimePagerAct();
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                //告诉菜单项要更改
                getActivity().invalidateOptionsMenu();

                updateSubtitle();
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    //显示Crime记录条数，设置子标题
    private void updateSubtitle(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeSize = crimeLab.getCrimes().size();
        //复数字符串资源
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural,crimeSize,crimeSize);

        //实现菜单项标题和子标题联动
        if (!mSubtitleVisible){
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (mAdapter == null){
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }else {
            //重绘当前可见区域
            mAdapter.notifyDataSetChanged();

            //重绘部分区域
            //mAdapter.notifyItemChanged(crimes.indexOf(crimes));
            if (crimes.size() != 0){
                nullCrimeList.setVisibility(View.INVISIBLE);
                addCrimeButton.setVisibility(View.INVISIBLE);
            }else {
                nullCrimeList.setVisibility(View.VISIBLE);
                addCrimeButton.setVisibility(View.VISIBLE);
            }
            //更新子标题
            updateSubtitle();
        }
    }

    /**
     * 保存子标题的状态值
     * @param outState
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("subtitle",mSubtitleVisible);
    }

    //格式化日期
    private String formateDate(Date date){
        SimpleDateFormat df = new SimpleDateFormat("EEEE MM/dd/yyyy", Locale.CHINA);
        return df.format(date);
    }

    /**
     * 创建新的CrimePagerActivity
     */
    private void addNewCrimePagerAct(){
        Crime crime = new Crime();
        CrimeLab.get(getActivity()).addCrime(crime);

        Intent intent = new Intent(getActivity(),CrimePagerActivity.class);
        intent.putExtra("crime_id",crime.getTitleId());
        startActivity(intent);
    }
}
