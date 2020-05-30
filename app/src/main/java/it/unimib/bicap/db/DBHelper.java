package it.unimib.bicap.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBNAME="BICAP";
    public DBHelper(Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String progettiCompletatiTB="CREATE TABLE "+DBConstants.TBL_NAME_COMPLETATI+
                " ( _id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                DBConstants.FIELD_ID_PROGETTO+" INTEGER)";
        String progettiDaCompletareTB="CREATE TABLE "+DBConstants.TBL_NAME_COMPLETATI+
                " ( _id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                DBConstants.FIELD_ID_PROGETTO+" INTEGER)";

        String passoProgetto = "CREATE TABLE "+DBConstants.TBL_NAME_PASSO_PROGETTO +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                DBConstants.FIELD_ID_PROGETTO+ " INTEGER, " +
                DBConstants.FIELD_N_PASSO+" INTEGER)";
        db.execSQL(progettiCompletatiTB);
        db.execSQL(progettiDaCompletareTB);
        db.execSQL(passoProgetto);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
