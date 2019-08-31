package com.example.criminalintent;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";

    private static final int REQUEST_DATE = 0;

    //模型向控制器传递参数
    private Crime mCrime;

    private EditText mTitleField;
    private Button mDataButton;
    private CheckBox mSolvedCheckBox;

    //编写newInstance(UUID)方法，从CrimePagerActivity获取crimeId
    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID,crimeId);

        CrimeFragment fragment= new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mCrime = new Crime();

        //直接调用Activity中的extra
        //UUID crimeId = (UUID)getActivity().getIntent().getSerializableExtra(CrimeActivity.EXTRA_CRIME_ID);

        //从Argument获取crimeId
        UUID crimeId = (UUID)getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime,container,false);

        //实例化组件
        mTitleField = v.findViewById(R.id.crime_title);
        //使用Crime数据更新视图
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }

            @Override
            /**
             * CharSequence代表用户输入
             */
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mDataButton = v.findViewById(R.id.crime_data);
        updateDate();
        mDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                //DatePickerFragment dialog = new DatePickerFragment();
                /**
                 * 添加newIntance
                 */
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getTitleData());
                //向DatePickerFragment传递
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_DATE);
                dialog.show(fm,"dialog_date");
            }
        });

        mSolvedCheckBox = v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        //设置监听器
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        return  v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                if (resultCode == Activity.RESULT_OK){
                    Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                    mCrime.setTitleData(date);
                    updateDate();
                }
        }
    }

    private void updateDate() {
        mDataButton.setText(formateDate(mCrime.getTitleData()));
    }

    //格式化日期
    private String formateDate(Date date){
        SimpleDateFormat df = new SimpleDateFormat("EE MM/dd/yyyy", Locale.CHINA);
        return df.format(date);
    }
}
