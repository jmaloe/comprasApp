package com.malo.comprasapp;

public class Producto {
	private long id=0;
    private String nombre;
    private float precio=0;
    private float cantidad=0;
    private boolean checked=false;
 
    public Producto(){}
 
    public Producto(String nombre, float precio){
        super();
        this.nombre = nombre;
        this.precio = precio;
    }
    
    public void setId(long id){
    	this.id = id;
    }
    
    public void setNombre(String nom){
    	nombre = nom;
    }
    
    public void setPrecio(float pre){
    	precio = pre;
    }
    
    public void setCantidad(float cant){
    	cantidad = cant;
    }
    
    public void setChecked(boolean tof){
    	checked = tof;
    }
        
    //getters
    public long getId(){
    	return id;
    }
    
    public String getNombre(){
    	return nombre;
    }
    
    public float getPrecio(){
    	return precio;
    }
    
    public float getCantidad(){
    	return cantidad;
    }
    
    public boolean isChecked(){
    	return checked;
    }
 
    @Override
    public String toString(){
        return "Producto [id=" + id + ", nombre=" + nombre + ", precio=" + precio + ", cantidad="+cantidad+", checked="+checked+"]";
    }
}
