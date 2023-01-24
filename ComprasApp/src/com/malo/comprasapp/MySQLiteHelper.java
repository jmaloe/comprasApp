package com.malo.comprasapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {
	
	// Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "ComprasAppDB";
    
	public MySQLiteHelper(Context context, String name, CursorFactory factory, int version){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db){
		// SQL statement to create tables
        String CREATE_PRODUCTOS_TABLE = "CREATE TABLE productos(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
                "nombre TEXT, "+
                "precio REAL )";
        String CREATE_MILISTA_TABLE = "CREATE TABLE mi_lista(" +
                "id INTEGER PRIMARY KEY, " + 
                "cantidad REAL, "+
                "checked BOOLEAN )";
 
        // create tables
        db.execSQL(CREATE_PRODUCTOS_TABLE);
        db.execSQL(CREATE_MILISTA_TABLE);
        /*Nota: si la base de datos no existe, entonces se invoca onCreate*/
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS productos");
        db.execSQL("DROP TABLE IF EXISTS mi_lista");
 
        // create fresh tables
        this.onCreate(db);
		
	}

}
