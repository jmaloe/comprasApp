package com.malo.comprasapp;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyProductList {
	
	SQLiteOpenHelper sqloh = null;
	
	private static final String TABLE_MILISTA = "mi_lista";
	private static final String TABLE_PRODUCTOS = "productos";
    private static final String KEY_ID = "id";
    private static final String KEY_CANTIDAD = "cantidad";
    private static final String KEY_CHECKED = "checked";
    //private static final String[] COLUMNS = {KEY_ID,KEY_CANTIDAD,KEY_CHECKED};
    
	
	public MyProductList(SQLiteOpenHelper db){
		this.sqloh = db;
	}
	
	public void addProductoToMyList(Producto prod){
		//for logging
        Log.d("addProductoToMyList", prod.toString()); 
 
        // 1. get reference to writable DB
        SQLiteDatabase db = sqloh.getWritableDatabase();
 
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ID, prod.getId());
        values.put(KEY_CANTIDAD, prod.getCantidad()); // get nombre 
        values.put(KEY_CHECKED, prod.isChecked()); // get precio
 
        // 3. insert
        db.insert(TABLE_MILISTA, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values
 
        // 4. close
        db.close();
	}
	
	public Producto getProductoFromMyList(long id){
		 
        // 1. get reference to readable DB
        SQLiteDatabase db = sqloh.getReadableDatabase();
        
        // 2. build query
        // 1. build the query
        String query = "SELECT  tp.id,tp.nombre,tp.precio,tml.cantidad,tml.checked FROM " + TABLE_MILISTA +" tml,"+TABLE_PRODUCTOS+" tp WHERE tml.id=tp.id AND tml.id=?";
        String values[] = {String.valueOf(id)};         
        Cursor cursor = db.rawQuery(query, values);
        
        // 3. go over each row, build Producto and add it to list
        Producto prod = new Producto();
        if (cursor.moveToFirst()) {
        	Log.i("encontrado...","moveToFirst");
            do {
                prod.setId(cursor.getInt(0));
                prod.setNombre(cursor.getString(1));
                prod.setPrecio(cursor.getFloat(2));
                prod.setCantidad(cursor.getFloat(3));
                prod.setChecked(Boolean.valueOf(cursor.getString(4)));                
            } while (cursor.moveToNext());
        }        
 
        Log.d("getProductoFromMyList("+id+")", prod.toString());
        db.close();
        // 5. return producto
        return prod;
    }
	
	// Get All Productos
    public List<Producto> getAllProductosFromMyList(){
        List<Producto> productos = new LinkedList<Producto>();
 
        // 1. build the query
        String query = "SELECT  tp.id,tp.nombre,tp.precio,tml.cantidad,tml.checked FROM " + TABLE_MILISTA +" tml,"+TABLE_PRODUCTOS+" tp WHERE tml.id=tp.id";
 
        // 2. get reference to writable DB
        SQLiteDatabase db = sqloh.getWritableDatabase();
        
        Cursor cursor = db.rawQuery(query, null);
 
        // 3. go over each row, build Producto and add it to list
        Producto prod = null;
        if (cursor.moveToFirst()) {
            do {
                prod = new Producto();
                prod.setId(Integer.parseInt(cursor.getString(0)));
                prod.setNombre(cursor.getString(1));
                prod.setPrecio(Float.parseFloat(cursor.getString(2)));
                prod.setCantidad(Float.parseFloat(cursor.getString(3)));
                if(cursor.getString(4).equals("1"))
                	prod.setChecked(true);                
                // Add Producto to Productos
                productos.add(prod);
            } while (cursor.moveToNext());
        }
 
        //Log.d("getAllProductosFromMyList()", productos.toString());
        db.close();
        // return productos
        return productos;
    }
    
 // Updating single producto
    public int updateProductoFromMyList(Producto prod){
 
        // 1. get reference to writable DB
        SQLiteDatabase db = sqloh.getWritableDatabase();
 
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        if(prod.getCantidad()>=0)
        	values.put(KEY_CANTIDAD, prod.getCantidad()); // get cantidad        
        values.put(KEY_CHECKED, prod.isChecked()); // get checked
 
        // 3. updating row
        int i = db.update(TABLE_MILISTA, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(prod.getId()) }); //selection args
        Log.d("updateProductoFromMyList()", prod.toString());
        // 4. close
        db.close();
        return i;
    }
    
 // Deleting single book
    public void deleteProductoFromMyList(Producto prod) {
        // 1. get reference to writable DB
        SQLiteDatabase db = sqloh.getWritableDatabase();
 
        // 2. delete
        db.delete(TABLE_MILISTA,
                KEY_ID+" = ?",
                new String[] { String.valueOf(prod.getId()) });
        Log.d("deleteProductoFromMyList("+prod.getId()+")","");
        // 3. close
        db.close();
    }

	public int deleteAllFromMyList(){
		// 1. get reference to writable DB
        SQLiteDatabase db = sqloh.getWritableDatabase();
 
        // 2. delete
        int eliminados = db.delete(TABLE_MILISTA,
                		null,
                		null);
        Log.d("deleteAllMarkedProductFromMyList()","");
        // 3. close
        db.close();
       return eliminados;
	}

	public int deleteAllMarkedFromMyList(){
		// 1. get reference to writable DB
        SQLiteDatabase db = sqloh.getWritableDatabase();
 
        // 2. delete
        int eliminados = db.delete(TABLE_MILISTA,
                		KEY_CHECKED+" = ?",
                		new String[] { "1" });
        Log.d("deleteAllMarkedProductFromMyList()","");
        // 3. close
        db.close();
       return eliminados;
	}
	
}
