package com.example.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import database.CrimeBaseHelper;
import database.CrimeCursorWrapper;

import database.CrimeDbSchema.CrimeTable;

public class CrimeLab {
    private static CrimeLab sCrimeLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context){
        if (sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }
    //内部私有类
    private CrimeLab(Context context){

        //创建数据库
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();

    }

    //返回数组列表
    public  List<Crime> getCrimes(){
        List<Crime> crimes = new ArrayList<>();

        //返回crimes列表
        CrimeCursorWrapper cursor = queryCrimes(null,null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                //遍历数据库中的crime数据
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return crimes;
    }

    //返回带有指定ID的crime对象
    public Crime getCrime(UUID id){

        CrimeCursorWrapper cursor = queryCrimes(CrimeTable.Cols.UUID + "= ?",new String[]{id.toString()});

        try {
            if (cursor.getCount() == 0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        }finally {
            cursor.close();
        }
    }
    //使用ContentValues向数据库添加数据
    private static ContentValues getContentValues(Crime crime){
        ContentValues values = new ContentValues();

        //添加数据
        values.put(CrimeTable.Cols.UUID,crime.getTitleId().toString());
        values.put(CrimeTable.Cols.TITLE,crime.getTitle());
        values.put(CrimeTable.Cols.DATE,crime.getTitleData().getTime());
        values.put(CrimeTable.Cols.SOLVED,crime.isSolved() ? 1:0);

        return values;
    }

    //添加新的crime
    public void addCrime(Crime c){
        //通过getContentValues插入数据
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeTable.NAME,null,values);
    }

    //删除crime
    public void deleteCrime(Crime c){
        String uuidString = c.getTitleId().toString();
        mDatabase.delete(CrimeTable.NAME,CrimeTable.Cols.UUID +"= ?",new String[]{uuidString});
    }

    //更新crime
    public void updateCrime(Crime c){
        String uuidString = c.getTitleId().toString();
        ContentValues values = getContentValues(c);
        mDatabase.update(CrimeTable.NAME,values,
                CrimeTable.Cols.UUID + "= ?",new String[]{uuidString});
    }



    //查询Crimes记录
    public CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new CrimeCursorWrapper(cursor);
    }
}
