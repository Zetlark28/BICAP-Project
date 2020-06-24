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

    }

    public void saveCompletati( Integer idProgetto)
    {
        SQLiteDatabase db=dbhelper.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(DBConstants.FIELD_ID_PROGETTO, idProgetto);
        db.insert(DBConstants.TBL_NAME_COMPLETATI, null,cv);

    }
    public void saveDaCompletare(Integer idProgetto)
    {
        SQLiteDatabase db=dbhelper.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(DBConstants.FIELD_ID_PROGETTO, idProgetto);
        db.insert(DBConstants.TBL_NAME_DA_COMPLETARE, null,cv);
    }
    public void saveProgettoPasso(Integer idProgetto, Integer passo)
    {
        SQLiteDatabase db=dbhelper.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(DBConstants.FIELD_ID_PROGETTO, idProgetto);
        cv.put(DBConstants.FIELD_N_PASSO, passo);
        db.insert(DBConstants.TBL_NAME_PASSO_PROGETTO, null,cv);

    }

    public boolean isCompletato(int idProgetto){
        Cursor crs;
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
        Cursor crs;
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

    public Cursor selectNPasso(Integer progettoId) {
        Cursor crs;
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

    public void deleteDaCompletare(Integer idProgetto)
    {
        SQLiteDatabase db=dbhelper.getWritableDatabase();
        db.delete(DBConstants.TBL_NAME_DA_COMPLETARE, DBConstants.FIELD_ID_PROGETTO +"=?",
                    new String[]{ Integer.toString(idProgetto)});
    }

    public void updatePasso(Integer idProgetto, Integer passo){
        SQLiteDatabase db=dbhelper.getWritableDatabase();

        ContentValues cv=new ContentValues();
        cv.put(DBConstants.FIELD_ID_PROGETTO, idProgetto);
        cv.put(DBConstants.FIELD_N_PASSO, passo);

        db.update(DBConstants.TBL_NAME_PASSO_PROGETTO, cv, DBConstants.FIELD_ID_PROGETTO + "=?", new String[]{idProgetto.toString()});

    }

}
