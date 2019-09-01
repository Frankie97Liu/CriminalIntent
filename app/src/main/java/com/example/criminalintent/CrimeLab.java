package com.example.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;

    //创建空列表用来保存Crime对象
    private List<Crime> mCrimes;

    public static CrimeLab get(Context context){
        if (sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }
    //内部私有类
    private CrimeLab(Context context){
        mCrimes = new ArrayList<>();

        /*
        //生成100个crime数据
        for (int i=0;i<100;i++){
            Crime crime = new Crime();
            crime.setTitle("Crime #"+i);
            crime.setSolved(i % 2 ==0);
            mCrimes.add(crime);
        }
         */

    }

    //返回数组列表
    public  List<Crime> getCrimes(){
        return mCrimes;
    }

    //返回带有指定ID的crime对象
    public Crime getCrime(UUID id){
        for (Crime crime:mCrimes){
            if (crime.getTitleId().equals(id)){
                return crime;
            }
        }
        return null;
    }

    //添加新的crime
    public void addCrime(Crime c){
        mCrimes.add(c);
    }

    //删除crime
    public void deleteCrime(Crime c){mCrimes.remove(c);}
}
