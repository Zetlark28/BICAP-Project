package it.unimib.bicap.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import it.unimib.bicap.constanti.DBConstants;

public class DBManager {

    private static DBHelper dbhelper;
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

    public boolean isCompletato(int idProgetto){
        Cursor crs=null;
        try
        {
            SQLiteDatabase db=dbhelper.getReadableDatabase();
            crs=db.query(DBConstants.TBL_NAME_COMPLETATI, new String[]{DBConstants.FIELD_ID_PROGETTO},
                    DBConstants.FIELD_ID_PROGETTO + "=?",new String[]{Integer.toString(idProgetto)}, null, null, null, null);
        }
        catch(SQLiteException sqle)
        {
            return false;
        }

        if(crs.getCount()<=0){
            crs.close();
            return false;
        }
        crs.close();
        return true;
    }
    public boolean isDaCompletare(int idProgetto){
        Cursor crs=null;
        try
        {
            SQLiteDatabase db=dbhelper.getReadableDatabase();
            crs=db.query(DBConstants.TBL_NAME_DA_COMPLETARE, new String[]{DBConstants.FIELD_ID_PROGETTO},
                    DBConstants.FIELD_ID_PROGETTO + "=?",new String[]{Integer.toString(idProgetto)}, null, null, null, null);
        }
        catch(SQLiteException sqle)
        {
            return false;
        }

        if(crs.getCount()<=0){
            crs.close();
            return false;
        }
        crs.close();
        return true;

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

    public void updatePasso(Integer idProgetto, Integer passo){
        SQLiteDatabase db=dbhelper.getWritableDatabase();

        ContentValues cv=new ContentValues();
        cv.put(DBConstants.FIELD_ID_PROGETTO, idProgetto);
        cv.put(DBConstants.FIELD_N_PASSO, passo);

        db.update(DBConstants.TBL_NAME_PASSO_PROGETTO, cv, DBConstants.FIELD_ID_PROGETTO + "=?", new String[]{idProgetto.toString()});

    }

    public boolean passoPresente(Integer idProgetto){
        SQLiteDatabase db = dbhelper.getWritableDatabase();

        Cursor crs = db.query(DBConstants.TBL_NAME_PASSO_PROGETTO,new String[]{DBConstants.FIELD_ID_PROGETTO},
                DBConstants.FIELD_ID_PROGETTO + "=?", new String[]{idProgetto.toString()},null, null, null);
        if(crs.getCount()<=0){
            crs.close();
            return false;
        }

        crs.close();
        return true;
    }
}
