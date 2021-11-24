package edu.ieu.basedatosarticulos;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import edu.ieu.basedatosarticulos.datos.AdminSQLiteOpenHelper;

public class MainActivity extends AppCompatActivity {
    private EditText etCodigo;
    private EditText etDescripcion;
    private EditText etPrecio;

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

        btnNuevo.setOnClickListener( view -> {
            alta();
        });
    }

    private void alta() {
        AdminSQLiteOpenHelper adminDbHelper =
                new AdminSQLiteOpenHelper(this,"administracion");
        SQLiteDatabase adminDb = adminDbHelper.getWritableDatabase();
        String codigo = etCodigo.getText().toString();
        adminDb.close();
    }
}