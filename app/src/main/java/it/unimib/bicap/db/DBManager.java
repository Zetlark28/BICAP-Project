package it.unimib.bicap.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class DBManager {

    private DBHelper dbhelper;
    public DBManager(Context ctx) {
        dbhelper=new DBHelper(ctx);
    }
    public void saveCompletati(Integer idUtente, Integer idProgetto)
    {
        SQLiteDatabase db=dbhelper.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(DBConstants.FIELD_ID_UTENTE, idUtente);
        cv.put(DBConstants.FIELD_ID_PROGETTO, idProgetto);
        try {
            db.insert(DBConstants.TBL_NAME_COMPLETATI, null,cv);
        }
        catch (SQLiteException sqle) {

        }
    }
    public void saveDaCompletare(Integer idUtente, Integer idProgetto)
    {
        SQLiteDatabase db=dbhelper.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(DBConstants.FIELD_ID_UTENTE, idUtente);
        cv.put(DBConstants.FIELD_ID_PROGETTO, idProgetto);
        try
        {
            db.insert(DBConstants.TBL_NAME_DA_COMPLETARE, null,cv);
        }
        catch (SQLiteException sqle) {
        }
    }

    public Cursor selectCompletati(Integer idUtente) {
        Cursor crs=null;
        try
        {
            SQLiteDatabase db=dbhelper.getReadableDatabase();
            crs=db.query(DBConstants.TBL_NAME_COMPLETATI, new String[]{DBConstants.FIELD_ID_PROGETTO},DBConstants.FIELD_ID_UTENTE + "=?",
                    new String[]{Integer.toString(idUtente)}, null, null, null, null);
        }
        catch(SQLiteException sqle)
        {
            return null;
        }
        return crs;
    }

    public static boolean isCompletato(Cursor progettiCompletati, int idProgetto){
        if(progettiCompletati == null)
            return false;
        int indexColumn = progettiCompletati.getColumnIndex(DBConstants.FIELD_ID_PROGETTO);
        progettiCompletati.moveToFirst();
        if(progettiCompletati.getInt(indexColumn) == idProgetto)
            return true;
        else
            while(progettiCompletati.moveToNext())
                if(progettiCompletati.getInt(indexColumn) == idProgetto)
                    return true;
        return false;
    }
    public static boolean isDaCompletare(Cursor progettiDaCompletare, int idProgetto){
        if(progettiDaCompletare == null)
            return true;
        int indexColumn = progettiDaCompletare.getColumnIndex(DBConstants.FIELD_ID_PROGETTO);
        progettiDaCompletare.moveToFirst();
        if(progettiDaCompletare.getInt(indexColumn) == idProgetto)
            return true;
        else
            while(progettiDaCompletare.moveToNext())
                if(progettiDaCompletare.getInt(indexColumn) == idProgetto)
                    return true;
        return false;
    }

    public Cursor selectDaCompletare(Integer idUtente) {
        Cursor crs=null;
        try
        {
            SQLiteDatabase db=dbhelper.getReadableDatabase();
            crs=db.query(DBConstants.TBL_NAME_COMPLETATI, new String[]{DBConstants.FIELD_ID_PROGETTO},DBConstants.FIELD_ID_UTENTE + "=?",
                    new String[]{Integer.toString(idUtente)}, null, null, null, null);
        }
        catch(SQLiteException sqle)
        {
            return null;
        }
        return crs;
    }
    public boolean deleteCompletati(Integer idUtente, Integer idProgetto)
    {
        SQLiteDatabase db=dbhelper.getWritableDatabase();
        try
        {
            if (db.delete(DBConstants.TBL_NAME_COMPLETATI, DBConstants.FIELD_ID_UTENTE+"=? AND " + DBConstants.FIELD_ID_PROGETTO ,
                    new String[]{Integer.toString(idUtente), Integer.toString(idProgetto)})==1)
                return true;
            return false;
        }
        catch (SQLiteException sqle) {
            return false;
        }
    }
    public boolean deleteDaCompletare(Integer idUtente, Integer idProgetto)
    {
        SQLiteDatabase db=dbhelper.getWritableDatabase();
        try
        {
            if (db.delete(DBConstants.TBL_NAME_DA_COMPLETARE, DBConstants.FIELD_ID_UTENTE+"=? AND " + DBConstants.FIELD_ID_PROGETTO ,
                    new String[]{Integer.toString(idUtente), Integer.toString(idProgetto)})==1)
                return true;
            return false;
        }
        catch (SQLiteException sqle) {
            return false;
        }
    }
}
