package com.example.pm1examengrupo2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pm1examengrupo2.Models.RestApiMethods;
import com.example.pm1examengrupo2.Models.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityListarContactos extends AppCompatActivity {
    private List<Usuario> mLista = new ArrayList<>();
    ListView listUsuario;
    List<Usuario> usuarioList;
    ArrayList<String> arrayUsuario;
    Button btnActualizar, btnAtras, btnEliminar;
    Usuario usuario;
    EditText buscar;
    ArrayAdapter adp;


    int previousPosition = 1;
    int count=1;
    long previousMil=0;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_contactos);

        listUsuario = (ListView) findViewById(R.id.listaContacto);
        usuarioList = new ArrayList<>();
        arrayUsuario = new ArrayList<String>();

        btnActualizar = (Button) findViewById(R.id.alcbtnActualizar);
        btnAtras = (Button) findViewById(R.id.alcbtnAtras);
        btnEliminar = (Button) findViewById(R.id.alcbtnEliminar);

        buscar = (EditText) findViewById(R.id.alctxtbuscar);

        listarUsuarios();

        //------------------------------EVENTOS BOTONES Y BUSCAR------------------------------------
        buscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {



            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    BuscarUsuario(buscar.getText().toString());
                    if (buscar.getText().toString().equals("")){
                        listarUsuarios();
                    }
                } catch (Exception ex){
                    Toast.makeText(getApplicationContext(),"Valor invalido",Toast.LENGTH_SHORT).show();
                }

            }
        });


        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ActivityUsuario.class);
                startActivity(intent);
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set title

                alertDialogBuilder.setTitle("Eliminar Contacto");


                // set dialog message
                alertDialogBuilder
                        .setMessage("¿Está seguro de eliminar el contacto?")
                        .setCancelable(false)
                        .setPositiveButton("SI",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // si el usuario da click en si procede a llamar el metodo de eliminar
                                //eliminarContacto();
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });

        //-- lista evento click
        listUsuario.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(previousPosition==i)
                {
                    count++;
                    if(count==2 && System.currentTimeMillis()-previousMil<=1000)
                    {
                        //Toast.makeText(getApplicationContext(), "Doble Click ",Toast.LENGTH_LONG).show();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setTitle("Acción");
                        alertDialogBuilder
                                .setMessage("¿Desea ir a la Ubicacion de "+"?")
                                .setCancelable(false)
                                .setPositiveButton("SI",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // si el usuario da click en si procede a llamar el metodo de eliminar
                                        try{
                                            //permisoLlamada();
                                        }catch (Exception ex){
                                            ex.toString();
                                        }

                                        Toast.makeText(getApplicationContext(),"Realizando llamada",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        count=1;
                    }
                }
                else
                {
                    previousPosition=i;
                    count=1;
                    previousMil=System.currentTimeMillis();
                    //un clic
//                    contacto = listaContactos.get(i);//lleno la lista de contacto
//                    setContactoSeleccionado();
                }
            }


        });



    }
    //-----------------------------------METODOS---------------------------------------



    private void listarUsuarios() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, RestApiMethods.EndPointGetContact,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray contactoArray = jsonObject.getJSONArray( "contacto");

                            arrayUsuario.clear();//limpiar la lista de usuario antes de comenzar a listar
                            for (int i=0; i<contactoArray.length(); i++)
                            {
                                JSONObject RowUsuario = contactoArray.getJSONObject(i);
                                Usuario usuario = new Usuario(  RowUsuario.getInt("id"),
                                        RowUsuario.getString("nombre"),
                                        RowUsuario.getInt("telefono"),
                                        RowUsuario.getString("latitud"),
                                        RowUsuario.getString("longitud")
                                );
                                usuarioList.add(usuario);
                                arrayUsuario.add(usuario.getNombre()+' '+usuario.getTelefono());
                            }

                            adp = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_checked, arrayUsuario);
                            listUsuario.setAdapter(adp);

                        }catch (JSONException ex){
                            Toast.makeText(getApplicationContext(), "mensaje "+ex, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(getApplicationContext(), "mensaje "+error, Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    private void BuscarUsuario(String dato) {
        RequestQueue queue = Volley.newRequestQueue(this);
        //String url = "http://transportweb2.online/APIexam/listasinglecontacto.php?nombre=";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, RestApiMethods.EndPointGetBuscarContact+dato,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray contactoArray = jsonObject.getJSONArray( "contacto");

                            arrayUsuario.clear();//limpiar la lista de usuario antes de comenzar a buscar

//                            if ()){
//                                Toast.makeText(getApplicationContext(), "No se encontro el valor", Toast.LENGTH_SHORT).show();
//                            }

                            for (int i=0; i<contactoArray.length(); i++)
                            {
                                JSONObject RowUsuario = contactoArray.getJSONObject(i);
                                Usuario usuario = new Usuario(  RowUsuario.getInt("id"),
                                        RowUsuario.getString("nombre"),
                                        RowUsuario.getInt("telefono"),
                                        RowUsuario.getString("latitud"),
                                        RowUsuario.getString("longitud")
                                );
                                usuarioList.add(usuario);
                                arrayUsuario.add(usuario.getNombre()+' '+usuario.getTelefono());
                            }

                            adp = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_checked, arrayUsuario);
                            listUsuario.setAdapter(adp);

                        }catch (JSONException ex){
                            //Toast.makeText(getApplicationContext(), "mensaje "+ex, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                //Toast.makeText(getApplicationContext(), "mensaje "+error, Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

}