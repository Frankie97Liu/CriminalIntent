package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import database.CrimeDbSchema.CrimeTable;

public class CrimeBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;

    private static final String DATABASE_NAME = "crimeBase.db";

    //创建crime数据表
    public static final String CREATE_CRIMES = "create table "+ CrimeTable.NAME + "("
            + " _id integer primary key autoincrement, " +CrimeTable.Cols.UUID + ", "
            + CrimeTable.Cols.TITLE+", "
            + CrimeTable.Cols.DATE+", "
            + CrimeTable.Cols.SOLVED+")";

    public CrimeBaseHelper(Context context){
        super(context, DATABASE_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //初次创建SQL
        db.execSQL("create table "+ CrimeTable.NAME + "("
                + " _id integer primary key autoincrement, " +CrimeTable.Cols.UUID + ", "
                + CrimeTable.Cols.TITLE+", "
                + CrimeTable.Cols.DATE+", "
                + CrimeTable.Cols.SOLVED+", "
                + CrimeTable.Cols.SUSPECT+", "
                + CrimeTable.Cols.PHONE+
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
