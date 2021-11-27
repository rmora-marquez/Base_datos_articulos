package edu.ieu.basedatosarticulos;

import androidx.appcompat.app.AppCompatActivity;

import static edu.ieu.basedatosarticulos.datos.AdminSQLiteOpenHelper.BASE_DATOS;
import static edu.ieu.basedatosarticulos.datos.AdminSQLiteOpenHelper.CODIGO;
import static edu.ieu.basedatosarticulos.datos.AdminSQLiteOpenHelper.DESCRIPCION;
import static edu.ieu.basedatosarticulos.datos.AdminSQLiteOpenHelper.PRECIO;
import static edu.ieu.basedatosarticulos.datos.AdminSQLiteOpenHelper.TABLA;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.ieu.basedatosarticulos.datos.AdminSQLiteOpenHelper;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private  AdminSQLiteOpenHelper adminDbHelper = new AdminSQLiteOpenHelper(this, BASE_DATOS );
    private EditText etCodigo;
    private EditText etDescripcion;
    private EditText etPrecio;

    private ListView lstVwArticulos;
    private ArrayAdapter<String> articulosAdapter;
    private List<String> articulos = new ArrayList<>();

    private Button btnNuevo;
    private Button btnBorrar;
    private Button btnActualizar;

    private Button btnBuscarCodigo;
    private Button btnBuscarDescripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etCodigo = findViewById(R.id.et_codigo);
        etDescripcion = findViewById(R.id.et_descripcion);
        etPrecio = findViewById(R.id.et_precio);

        btnActualizar = findViewById(R.id.btn_actualizar);
        btnBorrar = findViewById(R.id.btn_borrar);
        btnNuevo = findViewById(R.id.btn_nuevo);
        btnBuscarCodigo = findViewById(R.id.btn_buscar_codigo);
        btnBuscarDescripcion = findViewById(R.id.btn_buscar_descripcion);
        lstVwArticulos = findViewById(R.id.list_articulos);

        btnActualizar.setOnClickListener( view -> {
            actualizar();
        });
        btnNuevo.setOnClickListener( view -> {
            alta();
        });
        btnBorrar.setOnClickListener(view -> {
            borrar();
        });
        btnBuscarCodigo.setOnClickListener(view -> {
            buscarCodigo();
        });
        articulosAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                leerArticulosBd()
        );
        lstVwArticulos.setAdapter(articulosAdapter);
        lstVwArticulos.setOnItemClickListener((adapterView, view, i, l) -> {
            String articulo = articulos.get(i);
            etCodigo.setText( articulo.substring(7,11) );
        });
    }

    @SuppressLint("Range")
    private List<String> leerArticulosBd() {
        articulos = new ArrayList<>();
        SQLiteDatabase adminDb = adminDbHelper.getReadableDatabase();
        Cursor cursor = adminDb.query(TABLA,
                null,
                null,
                null,
                null,
                null,
                CODIGO );
        while (cursor.moveToNext()){
            String articulo = CODIGO + ":" + cursor.getString(cursor.getColumnIndex(CODIGO));
            articulo += ", " + cursor.getString(cursor.getColumnIndex(DESCRIPCION)) ;
            articulo += ", " + PRECIO + ":" + cursor.getString(cursor.getColumnIndex(PRECIO)) ;
            articulos.add(articulo);
        }
        cursor.close();
        adminDb.close();
        return articulos;
    }

    private void borrar() {
        String strCodigo = etCodigo.getText().toString();
        try{
            Integer.parseInt(strCodigo);
            SQLiteDatabase adminDb = adminDbHelper.getReadableDatabase();
            int registrosAfectados = adminDb.delete(TABLA,
                    CODIGO + " = ?",
                    new String[]{ strCodigo }//args
                    );
            if(registrosAfectados == 1){
                Toast.makeText(this, "Articulo con código " + strCodigo + "  eliminiado",
                        Toast.LENGTH_SHORT).show();
                limpiarEditText();
            }else{
                Toast.makeText(this, "No se borro, causa codigo " + strCodigo + " inexistente",
                        Toast.LENGTH_SHORT).show();
            }
            adminDb.close();
        }catch(NumberFormatException ex) {
            Toast.makeText(this, "Ingrese un código númerico", Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }

    private void actualizar() {
        SQLiteDatabase adminDb = adminDbHelper.getWritableDatabase();
        String codigo = etCodigo.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String precio = etPrecio.getText().toString();
        if(codigo.isEmpty() ){
            Toast.makeText(this, "Codigo no puede ser vacio", Toast.LENGTH_SHORT)
                    .show();
            adminDb.close();
            return;
        }
        ContentValues values = new ContentValues();
        values.put(DESCRIPCION, descripcion);
        values.put(PRECIO, precio);
        int registrosAfectados =
                adminDb.update(TABLA,
                    values,
                    CODIGO + " = ?",
                    new String[]{ codigo });
        adminDb.close();
        if(registrosAfectados == 1){
            Toast.makeText(this, "Articulo con codigo " + codigo
                    + " guardado en la base de datos ", Toast.LENGTH_SHORT).show();
            limpiarEditText();
        }else{
            Toast.makeText(this, "Articulo con codigo " + codigo
                    + " no encontrado en la base de datos ", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("Range")
    private void buscarCodigo() {
        String strCodigo = etCodigo.getText().toString();
        try{
            Integer.parseInt(strCodigo);
            SQLiteDatabase adminDb = adminDbHelper.getReadableDatabase();
            Cursor cursor = adminDb.query(TABLA,
                    null,
                    CODIGO + " = ?",
                    new String[]{ strCodigo }, //args
                    null,
                    null,
                    null);

            /*Cursor cursor = adminDb.rawQuery ("SELECT * FROM articulos",
                    null);
            */
            Log.d(TAG, " query select ejecutado con codigo "+ strCodigo);

            if(cursor.moveToNext()){
                Log.d(TAG, "cursos length count " + cursor.getCount());
                etDescripcion.setText( cursor.getString( cursor.getColumnIndex(DESCRIPCION) ) );
                etPrecio.setText( cursor.getString( cursor.getColumnIndex(PRECIO) ) );
            }else{
                Toast.makeText(this, "Código " + strCodigo + " inexistente",
                        Toast.LENGTH_SHORT).show();
            }
            cursor.close();
            adminDb.close();
        }catch(NumberFormatException ex) {
            Toast.makeText(this, "Ingrese un código númerico", Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }

    }

    private void alta() {
        SQLiteDatabase adminDb = adminDbHelper.getWritableDatabase();
        String codigo = etCodigo.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String precio = etPrecio.getText().toString();
        if(codigo.isEmpty() ){
            Toast.makeText(this, "Codigo no puede ser vacio", Toast.LENGTH_SHORT)
                    .show();
            adminDb.close();
            return;
        }
        ContentValues values = new ContentValues();
        values.put(CODIGO, codigo);
        values.put(DESCRIPCION, descripcion);
        values.put(PRECIO, precio);
        adminDb.insert(TABLA, null, values);
        adminDb.close();
        limpiarEditText();
        Toast.makeText(this, "Articulo con codigo " + codigo
                + " guardado en la base de datos ", Toast.LENGTH_SHORT).show();
    }

    private void limpiarEditText(){
        etCodigo.setText("");
        etDescripcion.setText("");
        etPrecio.setText("");
    }
}