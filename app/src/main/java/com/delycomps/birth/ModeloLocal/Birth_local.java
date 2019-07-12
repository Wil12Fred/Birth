package com.delycomps.birth.ModeloLocal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.delycomps.birth.Entidades.Contacto;

import java.util.ArrayList;
import java.util.List;

public class Birth_local  extends SQLiteOpenHelper {
    private static String COLUMNA_NAME = "name";
    private static String COLUMNA_VALUE = "value";
    private static final String DATABASE_NAME = "birth.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLA_DATOS = "datos";
    private static final String TABLA_CONTACTOS= "contactos";

    private static final String IDUSER = "idUser";
    private static final String NAMES = "names";
    private static final String SURNAMES = "surnames";
    private static final String BIRTHDAY = "birthday";
    private static final String PHONENUMBER = "phonenumber";
    private static final String NAME = "name";
    private static final String HIDEYEAR = "hideYear";
    private static final String TOKENFB = "tokenFB";


    private static final String SQL_CREAR_DATOS  = "create table datos"
            + "(_id integer primary key autoincrement,"
            + COLUMNA_NAME + " text unique,"
            + COLUMNA_VALUE + " text"
            + ");" ;
    private static final String SQL_CREAR_CONTACTOS = "create table contactos"
            + "(_id integer primary key autoincrement,"
            + IDUSER + " integer,"
            + HIDEYEAR + " integer,"
            + NAME + " text,"
            + BIRTHDAY + " text,"
            + PHONENUMBER + " text,"
            + SURNAMES + " text,"
            + NAMES + " text,"
            + TOKENFB + " text"
            + ");" ;

    public Birth_local(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREAR_DATOS);
        db.execSQL(SQL_CREAR_CONTACTOS);

        db.execSQL("INSERT INTO datos (name, value) VALUES ('names','')");
        db.execSQL("INSERT INTO datos (name, value) VALUES ('surnames','')");
        db.execSQL("INSERT INTO datos (name, value) VALUES ('birthday','')");
        db.execSQL("INSERT INTO datos (name, value) VALUES ('hideYear','')");
        db.execSQL("INSERT INTO datos (name, value) VALUES ('phonenumber','')");
        db.execSQL("INSERT INTO datos (name, value) VALUES ('horoscopo','')");
        db.execSQL("INSERT INTO datos (name, value) VALUES ('register','false')");
        db.execSQL("INSERT INTO datos (name, value) VALUES ('codUpdate','false')");
    }
    public void updateUserActive(Contacto contacto){
        updateDato("names", contacto.getNames());
        updateDato("surnames", contacto.getSurnames());
        updateDato("birthday", contacto.getBirthday());
        updateDato("hideYear", contacto.getHideYear());
        updateDato("phonenumber", contacto.getPhonenumber());
        updateDato("codUpdate", contacto.getCodUpdate());
        updateDato("register", "true");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void updateDato(String dato, String result){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMNA_VALUE, result);

        db.update(TABLA_DATOS,
                values,
                " name = ?",
                new String[] { String.valueOf( dato ) });
        db.close();
    }
    private void updateDato(String dato, int result){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMNA_VALUE, result);

        db.update(TABLA_DATOS,
                values,
                " name = ?",
                new String[] { String.valueOf( dato ) });
        db.close();
    }



    public String getDato(String dato){
        String result = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMNA_VALUE};
        Cursor cursor =
                db.query(TABLA_DATOS,
                        projection,
                        "name = ?",
                        new String[] { String.valueOf(dato) },
                        null,
                        null,
                        null,
                        null);
        if (cursor != null){
            cursor.moveToFirst();
            db.close();
            result = cursor.getString(0);
        }
        return result;
    }
    public List<Contacto> getContactos() {
        SQLiteDatabase db = getReadableDatabase();
        List<Contacto> contactos = new ArrayList<>();
        String[] valores_recuperar = {IDUSER, HIDEYEAR, NAME, PHONENUMBER, BIRTHDAY , SURNAMES, NAMES, TOKENFB};
        Cursor c = db.query(TABLA_CONTACTOS, valores_recuperar,
                null, null, null, null, null, null);
        c.moveToFirst();
        do {
            try {
                Contacto contacto = new Contacto(Integer.parseInt(c.getString(0)), Integer.parseInt(c.getString(1)), c.getString(2), c.getString(3),
                                                c.getString(4), c.getString(5), c.getString(6),c.getString(7));
                contactos.add(contacto);
            }catch (Exception e){
                Log.d("respuesta", e.toString());
                return null;
            }
        } while (c.moveToNext());
        db.close();
        c.close();
        return contactos;
    }
    public boolean deleteAllContactos() {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.delete(TABLA_CONTACTOS, null, null);
            db.close();
            return true;
        }catch(Exception ex){
            return false;
        }
    }
    public void setContacto(Contacto contacto, boolean contactoFalso){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IDUSER, contacto.getIdUser());
        values.put(NAMES, contacto.getNames());
        values.put(BIRTHDAY, contacto.getBirthday());
        values.put(PHONENUMBER, contacto.getPhonenumber());
        values.put(HIDEYEAR, contacto.getHideYear());
        values.put(NAME, contacto.getName());
        values.put(TOKENFB, contacto.getTokenFB());
        db.insert(TABLA_CONTACTOS, null,values);
        db.close();
    }
}
