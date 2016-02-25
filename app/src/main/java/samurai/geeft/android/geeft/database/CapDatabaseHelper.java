package samurai.geeft.android.geeft.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by oldboy on 25/02/16.
 */
public class CapDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "CapDB.db";
    public static final String CAP_TABLE_NAME = "Cap";
    public static final String CAP_COLUMN_CAP = "Cap";
    public static final String CAP_COLUMN_COMUNE = "Comune";
    public static final String CAP_COLUMN_PROVINCIA = "Provincia";

    public CapDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table Cap " +
                        "(Cap text, Comune text, Provincia text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS Cap");
        onCreate(db);
    }

    public boolean insertContact  (String cap, String comune, String provincia)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Cap", cap);
        contentValues.put("Comune", comune);
        contentValues.put("Provincia", provincia);
        db.insert("Cap", null, contentValues);
        return true;
    }

    public Cursor getData(String cap){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Cap where Cap="+cap+"", null );
        return res;
    }

    //Return the name of "Comune" where is contained the given cap
    public Cursor getComune(String cap){
        SQLiteDatabase db = this.getReadableDatabase();
        //Roma contains all number with prefix "001xx" so if the inserted string contains 001--
        // we replace with 001xx for the query
        String capPrefix = cap.substring(0,2);
        if (capPrefix.equals("001")) {
            cap = "001xx";
        }

        Cursor res =  db.rawQuery("select Comune from Cap where Cap =" + cap + "", null);
        //return the cursor with all "Comuni" founded
        return res;

    }

    //Return the name of "Provincia" where is contained the given cap
    public String getProvincia(String cap){
        SQLiteDatabase db = this.getReadableDatabase();
        //Roma contains all number with prefix "001xx" so if the inserted string contains 001--
        // we replace with 001xx for the query
        String capPrefix = cap.substring(0,2);
        if (capPrefix.equals("001")) {
            cap = "001xx";
        }

        Cursor res =  db.rawQuery("select Provincia from Cap where Cap =" + cap + "", null);
        //return the first name of the column (the province are the same in the selected column)
        res.moveToFirst();
        return res.getString(1);
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CAP_TABLE_NAME);
        return numRows;
    }

    /*public boolean updateContact (String cap, String comune, String provincia)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Cap", cap);
        contentValues.put("Comune", comune);
        contentValues.put("Provincia", provincia);
        db.update("Cap", contentValues, "Cap = ? ", new String[] { Cap } );
        return true;
    }*/
/*
    public Integer deleteCap (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("Cap",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }*/

    public ArrayList<String> getAllCaps()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Cap", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(CAP_COLUMN_CAP)));
            res.moveToNext();
        }
        return array_list;
    }

}
