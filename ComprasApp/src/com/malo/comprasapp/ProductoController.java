package com.malo.comprasapp;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ProductoController {
	
	SQLiteOpenHelper sqloh = null;
	
	private static final String TABLE_PRODUCTOS = "productos";
    private static final String KEY_ID = "id";
    private static final String KEY_NOMBRE = "nombre";
    private static final String KEY_PRECIO = "precio";
    private static final String[] COLUMNS = {KEY_ID,KEY_NOMBRE,KEY_PRECIO};
    
	
	public ProductoController(SQLiteOpenHelper db){
		this.sqloh = db;
	}
	
	public void addProducto(Producto prod){
		//for logging
        Log.d("addProducto", prod.toString()); 
 
        // 1. get reference to writable DB
        SQLiteDatabase db = sqloh.getWritableDatabase();
 
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NOMBRE, prod.getNombre()); // get nombre 
        values.put(KEY_PRECIO, prod.getPrecio()); // get precio
 
        // 3. insert
        long id = db.insert(TABLE_PRODUCTOS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values
 
        // 4. close
        prod.setId(id);
        db.close();        
	}
	
	public Producto getProductoById(int id){
		 
        // 1. get reference to readable DB
        SQLiteDatabase db = sqloh.getReadableDatabase();
 
        // 2. build query
        Cursor cursor = 
                db.query(TABLE_PRODUCTOS, // a. table
                COLUMNS, // b. column names
                " id = ?", // c. selections 
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        Producto prod = new Producto();
        // 3. if we got results get the first one
        if (cursor != null)
        {
            if(cursor.moveToFirst())
            { 
		        // 4. build producto object        
		        prod.setId(cursor.getInt(0));
		        prod.setNombre(cursor.getString(1));
		        prod.setPrecio(cursor.getFloat(2));
	        }
        }
        Log.d("getProducto("+id+")", prod.toString());
        db.close();
        // 5. return producto
        return prod;
    }
	
	public Producto getProductoByName(String nombre){
		 
        // 1. get reference to readable DB
        SQLiteDatabase db = sqloh.getReadableDatabase();
 
        // 2. build query
        Cursor cursor = 
                db.query(TABLE_PRODUCTOS, // a. table
                COLUMNS, // b. column names
                " nombre = ?", // c. selections 
                new String[] { String.valueOf(nombre) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
 
        Producto prod = new Producto();
        // 3. if we got results get the first one
        if (cursor != null)
        {
	        if(cursor.moveToFirst())
	        {
		        // 4. build producto object
		        prod.setId(cursor.getInt(0));
		        prod.setNombre(cursor.getString(1));
		        prod.setPrecio(cursor.getFloat(2));
	        }
        }
        Log.d("getProducto("+nombre+"):", prod.toString());
        db.close();
        // 5. return producto
        return prod;
    }
	
	
	// Get All Productos
    public List<Producto> getAllProductos(){
        List<Producto> productos = new LinkedList<Producto>();
 
        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_PRODUCTOS;
 
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
 
                // Add Producto to Productos
                productos.add(prod);
            } while (cursor.moveToNext());
        }
 
        //Log.d("getAllProductos()", productos.toString());
        db.close();
        // return productos
        return productos;
    }
    
 // Updating single producto
    public int updateProducto(Producto prod){
 
        // 1. get reference to writable DB
        SQLiteDatabase db = sqloh.getWritableDatabase();
 
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        if(prod.getNombre()!=null)
        	values.put("nombre", prod.getNombre()); // get nombre
        if(prod.getPrecio()>=0)
        	values.put("precio", prod.getPrecio()); // get precio
 
        // 3. updating row
        int i = db.update(TABLE_PRODUCTOS, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(prod.getId()) }); //selection args
        Log.d("updateProducto()", prod.toString());
        // 4. close
        db.close();
        return i;
    }
    
 // Deleting single book
    public void deleteProducto(Producto prod) {
 
        // 1. get reference to writable DB
        SQLiteDatabase db = sqloh.getWritableDatabase();
 
        // 2. delete
        db.delete(TABLE_PRODUCTOS,
                KEY_ID+" = ?",
                new String[] { String.valueOf(prod.getId()) });
        
        Log.d("deleteProducto("+prod.getId()+")","");
        // 3. close
        db.close();
    }
	
}
