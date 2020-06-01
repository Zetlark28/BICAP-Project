package it.unimib.bicap.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class DBManager {

    private DBHelper dbhelper;
    public DBManager(Context ctx) {
        if(dbhelper == null)
        dbhelper=new DBHelper(ctx);
        else
            getDbhelper();

    }

    public DBHelper getDbhelper() {
        return dbhelper;
    }

    public void saveCompletati( Integer idProgetto)
    {
        SQLiteDatabase db=dbhelper.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(DBConstants.FIELD_ID_PROGETTO, idProgetto);
        try {
            db.insert(DBConstants.TBL_NAME_COMPLETATI, null,cv);
        }
        catch (SQLiteException sqle) {

        }
    }
    public void saveDaCompletare(Integer idProgetto)
    {
        SQLiteDatabase db=dbhelper.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(DBConstants.FIELD_ID_PROGETTO, idProgetto);
        try
        {
            db.insert(DBConstants.TBL_NAME_DA_COMPLETARE, null,cv);
        }
        catch (SQLiteException sqle) {
        }
    }
    public void saveProgettoPasso(Integer idProgetto, Integer passo)
    {
        SQLiteDatabase db=dbhelper.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(DBConstants.FIELD_ID_PROGETTO, idProgetto);
        cv.put(DBConstants.FIELD_N_PASSO, passo);
        try
        {
            db.insert(DBConstants.TBL_NAME_PASSO_PROGETTO, null,cv);
        }
        catch (SQLiteException sqle) {
        }
    }

    public Cursor selectCompletati() {
        Cursor crs=null;
        try
        {
            SQLiteDatabase db=dbhelper.getReadableDatabase();
            crs=db.query(DBConstants.TBL_NAME_COMPLETATI, new String[]{DBConstants.FIELD_ID_PROGETTO},null,null, null, null, null, null);
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
            return false;
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

    public Cursor selectDaCompletare() {
        Cursor crs=null;
        try
        {
            SQLiteDatabase db=dbhelper.getReadableDatabase();
            crs=db.query(DBConstants.TBL_NAME_COMPLETATI, new String[]{DBConstants.FIELD_ID_PROGETTO},null, null, null, null, null, null);
        }
        catch(SQLiteException sqle)
        {
            return null;
        }
        return crs;
    }

    public Cursor selectNPasso(Integer progettoId) {
        Cursor crs=null;
        try
        {
            SQLiteDatabase db=dbhelper.getReadableDatabase();
            crs=db.query(DBConstants.TBL_NAME_PASSO_PROGETTO, new String[]{DBConstants.FIELD_N_PASSO},DBConstants.FIELD_ID_PROGETTO + "=?", new String []{progettoId.toString()}, null, null, null, null);
        }
        catch(SQLiteException sqle)
        {
            return null;
        }
        return crs;
    }

    public boolean deleteCompletati(Integer idProgetto)
    {
        SQLiteDatabase db=dbhelper.getWritableDatabase();
        try
        {
            if (db.delete(DBConstants.TBL_NAME_COMPLETATI, DBConstants.FIELD_ID_UTENTE+"=? AND " + DBConstants.FIELD_ID_PROGETTO ,
                    new String[]{ Integer.toString(idProgetto)})==1)
                return true;
            return false;
        }
        catch (SQLiteException sqle) {
            return false;
        }
    }
    public boolean deleteDaCompletare(Integer idProgetto)
    {
        SQLiteDatabase db=dbhelper.getWritableDatabase();
        try
        {
            if (db.delete(DBConstants.TBL_NAME_DA_COMPLETARE, DBConstants.FIELD_ID_PROGETTO +"=?",
                    new String[]{ Integer.toString(idProgetto)})==1)
                return true;
            return false;
        }
        catch (SQLiteException sqle) {
            return false;
        }
    }

    public boolean deleteProgettoPasso(Integer idProgetto)
    {
        SQLiteDatabase db=dbhelper.getWritableDatabase();
        try
        {
            if (db.delete(DBConstants.TBL_NAME_PASSO_PROGETTO, DBConstants.FIELD_ID_PROGETTO +"=?",
                    new String[]{ Integer.toString(idProgetto)})==1)
                return true;
            return false;
        }
        catch (SQLiteException sqle) {
            return false;
        }
    }
}
