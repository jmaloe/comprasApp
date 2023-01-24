package com.malo.comprasapp;

import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;

public class MainController {
	MySQLiteHelper msqlh;
	TableLayout tabla;
	Context contexto;
	MyProductList mpl;
	MainActivity main;
	View current_row_view;
	String input_text;
	
	public MainController(MySQLiteHelper msqlhelper, TableLayout t, Context context){
		msqlh = msqlhelper;
		tabla = t;
		contexto = context;
		mpl = new MyProductList(msqlh);
	}
	
	public void setMainActivity(MainActivity m){
		main = m;
	}
	
	public void construirTabla(){
		if(tabla.getChildCount()>1)
			tabla.removeViews(1, tabla.getChildCount()-1); //eliminamos todos los elementos de la lista menos las cabeceras
		List<Producto> milista = mpl.getAllProductosFromMyList();
		Iterator<Producto> iterador = milista.iterator();
		while(iterador.hasNext()){
			Producto p = iterador.next();
			showInTable(p);
		}
		calcularGranTotal();
	}
	
	public void showInTable(Producto p){
		TableRow row = getNewTableRow();
		row.setFocusable(false);
		row.addView(getTextView(p.getNombre()));
	    row.addView(getEditText(String.valueOf(p.getPrecio()),"PRECIO:"+String.valueOf(p.getId())));
	    row.addView(getEditText(String.valueOf(p.getCantidad()),"CANTIDAD:"+String.valueOf(p.getId())));
	    row.addView(getTextView(String.valueOf(p.getPrecio()*p.getCantidad())));
	    row.setTag(p.getId()+":NONE");/*asignamos el ID del producto y Color de fila en el tag del row*/
	    if(p.isChecked())
	    {
	    	row.setBackgroundColor(Color.parseColor("#A9F5A9"));
	    	row.setTag(p.getId()+":#A9F5A9");
	    }
	    /*Al hacer click en un elemento de la fila cambiamos el color de fondo*/
	    row.setOnClickListener(new View.OnClickListener(){
	    	
            @Override
            public void onClick(View v) {
            	current_row_view = v;            	
            	if(getCurrentRowColor().equals("#A9F5A9"))
            	{
            		v.setBackgroundColor(Color.TRANSPARENT);
            		v.setTag(getCurrentRowID()+":NONE");
            		Producto p = new Producto();
            		p.setId(getCurrentRowID());
            		p.setCantidad(-1);
            		p.setChecked(false);
            		mpl.updateProductoFromMyList(p);
            		//Log.i("No pressed","aqui1");
            	}
            	else
            	{
            		v.setBackgroundColor(Color.parseColor("#A9F5A9"));            		
            		v.setTag(getCurrentRowID()+":#A9F5A9");
            		Producto p = new Producto();
            		p.setId(getCurrentRowID());
            		p.setCantidad(-1);
            		p.setChecked(true);
            		mpl.updateProductoFromMyList(p);
            		//Log.i("Pressed","aqui2");
            	}                             
            }
         });
	    
	    /*Identificamos la fila sobre la cual se ejecutarán las operaciones definidas en onContextItemSelected de MainActivity*/
	    row.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v){
				current_row_view = v;				
				return false;
			}
		});
	    
	    main.registerForContextMenu(row);
	    
	    tabla.addView(row);
	    final ScrollView scroll = (ScrollView)main.findViewById(R.id.scroll_resultado);
	    scroll.fullScroll(ScrollView.FOCUS_DOWN);	       
	    //scroll.fullScroll(ScrollView.FOCUS_DOWN); //nos posicionamos en el ultimo elemento agregado
	}
	
	public TableRow getNewTableRow(){
		TableRow row = new TableRow(contexto);
		row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	  return row; 
	}
	
	public TextView getTextView(String descripcion){
		TextView tv = new TextView(contexto);			    
	    tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
	    //tv.setBackgroundResource(R.drawable.cell_shape);
	    tv.setGravity(Gravity.CENTER);
	    tv.setTextSize(18);
	    tv.setPadding(0, 5, 0, 5);

	    tv.setText(descripcion);
	    //main.registerForContextMenu(tv);

	    return tv;
	}
	
	public EditText getEditText(String texto, String ID_PROD){
		final EditText et = new EditText(contexto);
		et.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);
		et.setGravity(Gravity.CENTER);
		et.setText(texto);
		et.setTag(ID_PROD); /*guardamos el id del producto a modificar en el tag*/
		et.setSelectAllOnFocus(true);		
		
		/*et.setOnClickListener(new View.OnClickListener(){
	    	
            @Override
            public void onClick(View v) {
            	v.setFocusable(true);
            	v.requestFocus();
            }
         });*/

		et.addTextChangedListener(new TextWatcher(){
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) { }

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }

			@Override
			public void afterTextChanged(Editable s){            	
    				String partes[] = et.getTag().toString().split(":"); //obtenemos el el campo y el id del producto
                	ProductoController pc = new ProductoController(msqlh);
                	Producto p = new Producto();
                	//Log.i("DATO:",partes[0]);
                	String dato = et.getText().toString();
                	if(dato.equals(""))
                	{
                		return;
                	}
                	if(dato.equals(".")){                		
                		return;
                	}
                	if(partes[0].equals("PRECIO")){
                		p.setId(Integer.parseInt(partes[1]));
                		p.setPrecio(Float.parseFloat(dato));
                			pc.updateProducto(p);
                	}
                	else if(partes[0].equals("CANTIDAD")){
                		p.setId(Integer.parseInt(partes[1]));
                		p.setCantidad(Float.parseFloat(dato));	            		
                			mpl.updateProductoFromMyList(p);
                	}
                	TableRow tr = (TableRow)et.getParent(); /*el parent de este EditText es el TableRow*/
                	modificarArticulo(tr);
                   //Log.i("cambió",obj.getTag()+" - valor:"+obj.getText().toString());            		
			}
	    });
		
		et.setOnFocusChangeListener(new OnFocusChangeListener() {			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
					EditText et = (EditText)v;
					if(et.getText().toString().equals("")){
						et.setText("0");						
						modificarArticulo((TableRow)v.getParent());
					}
				}				
			}
		});
		
		return et;
	}
	
	public void modificarArticulo(TableRow tr){
		EditText tv_precio = (EditText)tr.getChildAt(1);
    	EditText tv_cantidad = (EditText)tr.getChildAt(2);
		TextView tv_total = (TextView)tr.getChildAt(3); /*obtenemos el textview de la fila en pos 3*/
		float total = Float.parseFloat(tv_precio.getText().toString()) * Float.parseFloat(tv_cantidad.getText().toString());
		tv_total.setText(""+total);
		calcularGranTotal();
	}
	
	/*eliminacion del producto de la tabla mi_lista*/
	public void deteleProductFromMyList(){
		Producto p = new Producto();
		p.setId(getCurrentRowID());
		mpl.deleteProductoFromMyList(p); /*eliminacion del producto de mi_lista*/
		deleteCurrentRow();
		//Log.e("Eliminar producto ID:",""+p.getId());
	}
	
	/*eliminacion del producto de la tabla productos*/
	public void deleteProductFromStore(){
		Producto p = new Producto();
		p.setId(getCurrentRowID());
		ProductoController p_controller = new ProductoController(msqlh);
		p_controller.deleteProducto(p); /*eliminacion definitiva del producto*/
		deleteCurrentRow();
		//Log.e("Borrar producto ID:",""+p.getId());
	}
	
	public void deleteCurrentRow(){
		tabla.removeView(current_row_view);
	}
	
	public void renombrar(){
		TableRow tr = (TableRow)current_row_view; /*hacemos casting a la vista en tipo TableRow*/
		final TextView descripcion = (TextView)tr.getChildAt(0); /*obtenemos el textview de la fila en pos 0*/
		AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
		builder.setTitle("Modificar: "+descripcion.getText());
		// Set up the input
		final EditText input = new EditText(contexto);
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {		    	
		        input_text = input.getText().toString();
		        if(!input_text.equals(""))
		        {
			        Log.i("modificando"+descripcion.getText(),input_text);
			        descripcion.setText(input_text);
			        ProductoController p_controller = new ProductoController(msqlh);
			        Producto prod = new Producto();
			        prod.setId(getCurrentRowID()); //el tag de la fila tiene el ID
			        prod.setPrecio(-1); //para no guardar precio
			        prod.setNombre(input_text);
					p_controller.updateProducto(prod); /*actualizar el producto*/
					main.loadAutocomplete(); /*cargamos nuevamente el autocomplete*/
		        }
		    }
		});
		builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});

		builder.show();
	}
	
	public String[] getCurrentRowTag(){
		return current_row_view.getTag().toString().split(":");		
	}
	
	public int getCurrentRowID(){
		return Integer.parseInt(getCurrentRowTag()[0]);
	}
	
	public String getCurrentRowColor(){
		return getCurrentRowTag()[1];
	}
	
	public void calcularGranTotal(){
		try{
			TextView footer = (TextView) main.findViewById(R.id.cantidad_total);
			int total_filas = tabla.getChildCount();
			float total=0;
			/*inicializamos en 1 el contador para evitar el header de la tabla*/
			for(int i=1; i<total_filas; i++ )
			{
				View v = tabla.getChildAt(i);
				if(v instanceof TableRow)
				{
					TableRow fila = (TableRow)v;
					TextView tv_total = (TextView)fila.getChildAt(3);					
					total += Float.parseFloat(tv_total.getText().toString());				
				}
			}
			footer.setText((total_filas-1)+" artículos, $"+total);
			Log.i("TOTAL",total_filas+" - "+total);
		}catch(Exception e){
			Log.e("ERROR:",e.toString());
		}
	}	
}
