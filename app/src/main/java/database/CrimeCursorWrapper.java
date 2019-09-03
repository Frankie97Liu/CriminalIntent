package database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.criminalintent.Crime;

import java.util.Date;
import java.util.UUID;

import database.CrimeDbSchema.CrimeTable;

/**
 * CrimeCursorWrapper的作用是遍历cursor对象，取出数据并打印
 */
public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Crime getCrime(){
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));
        String suspectPhone = getString(getColumnIndex(CrimeTable.Cols.PHONE));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setTitleData(new Date(date));
        crime.setSolved(isSolved!=0);
        crime.setSuspect(suspect);
        crime.setSuspectPhone(suspectPhone);

        return crime;
    }
}
