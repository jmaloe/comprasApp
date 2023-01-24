package com.malo.comprasapp;

import java.util.Iterator;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	TableLayout tabla;
	MySQLiteHelper msqlh;
	MainController main_controller;
	ProductoController producto_controller;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);				
		start();
		loadAutocomplete();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.exit_activity) {
			finish();			
		}
		else if(id == R.id.about){
			mostrarAlerta("Acerca de ¿?",
					"ComprasApp v2015.10\n"
					+ "Por Jesús Malo Escobar\n"
					+ "dic.malo@gmail.com\n"
					+ "Chiapas, México.");
		}
		else if(id == R.id.limpiar_lista){
			MyProductList mpl = new MyProductList(msqlh);
			int eliminados = mpl.deleteAllFromMyList();
			if(eliminados>0){
				mostrarAlerta("¡Éxito!", "Se eliminaron "+eliminados+" artículos de tu lista.");
				main_controller.construirTabla();
			}
			main_controller.calcularGranTotal();
		}
		else if(id == R.id.quitar_marcados){
			MyProductList mpl = new MyProductList(msqlh);
			int eliminados = mpl.deleteAllMarkedFromMyList();
			if(eliminados>0){
				mostrarAlerta("¡Éxito!", "Se eliminaron "+eliminados+" artículos marcados de tu lista.");
				main_controller.construirTabla();
			}
			main_controller.calcularGranTotal();
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void start(){
		msqlh = new MySQLiteHelper(this, null, null, 0);				
		tabla = (TableLayout)findViewById(R.id.vista_tabla);
		producto_controller = new ProductoController(msqlh);
		main_controller = new MainController(msqlh,tabla,this);
		main_controller.setMainActivity(this);		
		
		AutoCompleteTextView tv_producto_autocomplete = (AutoCompleteTextView) findViewById(R.id.autocomplete_productos);		
		
		final Button btn_agregar = (Button) findViewById(R.id.button_agregar);
					
		btn_agregar.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				agregarProducto();
			}});
			
		tv_producto_autocomplete.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				agregarProducto();
			}			
		});
				
		main_controller.construirTabla();
		main_controller.calcularGranTotal();
	}
	
	public void agregarProducto(){
		TextView tv_producto = (TextView)findViewById(R.id.autocomplete_productos);
		String descripcion_producto = tv_producto.getText().toString();
		tv_producto.setText("");
		
		if(!descripcion_producto.equals(""))
		{
			final MyProductList mpl = new MyProductList(msqlh);
			Producto producto = producto_controller.getProductoByName(descripcion_producto);					
			//Log.i("autocomplete_productos",tv_producto.getText().toString());
			boolean nuevo=false;
			if(producto.getId()==0)
			{
				producto.setNombre(descripcion_producto);
				producto_controller.addProducto(producto);
				nuevo = true;
			}
			if(!nuevo){
				Producto mpl_producto = mpl.getProductoFromMyList(producto.getId());
				if(mpl_producto.getId()>0){
					mostrarAlerta("¡Atención!",producto.getNombre()+" ya está en tu lista");							
					return;
				}
			}					
			mpl.addProductoToMyList(producto);
			main_controller.showInTable(producto);
			main_controller.calcularGranTotal();
			hideSoftKeyboard();//ocultamos el teclado virtual
		}
	}
	
	public void loadAutocomplete(){
		// Get a reference to the AutoCompleteTextView in the layout
		AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autocomplete_productos);
		// Get the string array		
		List<Producto> elementos = producto_controller.getAllProductos();
		String[] productos = new String[elementos.size()];
		Iterator<Producto> iterador = elementos.iterator();
		int cont=0;
		while(iterador.hasNext()){
			Producto p = iterador.next();
			productos[cont] = p.getNombre();
			cont++;
		}
		// Create the adapter and set it to the AutoCompleteTextView 
		ArrayAdapter<String> adapter = 
		        new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, productos);		
		textView.setAdapter(adapter);
	}
	
	/*para invocar este método asigna a un elemento de tipo View en cualquier Activity this.registerForContextMenu(row);*/
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
	    super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.row_options, menu);	    
        menu.setHeaderTitle("Opciones");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item){
	    //ContextMenuInfo info = (ContextMenuInfo) item.getMenuInfo();
		    switch (item.getItemId()){
		        case R.id.op_eliminar:		        	
		        	main_controller.deteleProductFromMyList();
		        	main_controller.calcularGranTotal();
		        	return true;
		    	    
		        case R.id.op_borrado_completo:		        	
		        	main_controller.deleteProductFromStore();
		        	main_controller.calcularGranTotal();
		        	return true;
		        	
		        case R.id.op_renombrar:
		        	main_controller.renombrar();
		        	return true;
		        default:
		            return super.onContextItemSelected(item);
		    }		
	}
	
	public void mostrarAlerta(String titulo, String msj){
		AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
		alertDialog.setTitle(titulo);
		alertDialog.setMessage(msj);
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Aceptar",
		    new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		            dialog.dismiss();
		        }
		    });
		alertDialog.show();
	}
	
	/**
	 * Hides the soft keyboard
	 */
	public void hideSoftKeyboard() {
	    if(getCurrentFocus()!=null) {
	        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
	        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	    }
	}

	/**
	 * Shows the soft keyboard
	 */
	public void showSoftKeyboard(View view) {
	    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
	    view.requestFocus();
	    inputMethodManager.showSoftInput(view, 0);
	}
}
