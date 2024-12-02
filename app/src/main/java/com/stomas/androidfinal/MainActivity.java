package com.stomas.androidfinal;

import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText txtRut,txtNombre,txtRol,txtDireccion;
    private ListView lista;
    private Spinner spDepartamento;
    private FirebaseFirestore db;
    String[] TiposRol = {"IT", "Recursos humanos", "Diseño"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        db = FirebaseFirestore.getInstance();

        txtRut = findViewById(R.id.txtRut);
        txtNombre = findViewById(R.id.txtNombre);
        txtRol= findViewById(R.id.txtRol);
        txtDireccion = findViewById(R.id.txtDireccion);
        spDepartamento = findViewById(R.id.spDepartamento);
        lista = findViewById(R.id.lista);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,TiposRol);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDepartamento.setAdapter(adapter);

        CargarListaFireStore();


    }

    public void enviarDatosFirestore(View view){

        String rut = txtRut.getText().toString();
        String nombre = txtNombre.getText().toString();
        String rol = txtRol.getText().toString();
        String direccion = txtDireccion.getText().toString();
        String departamento = spDepartamento.getSelectedItem().toString();


        Map<String,Object> trabajador = new HashMap<>();
        trabajador.put("rut",rut);
        trabajador.put("nombre",nombre);
        trabajador.put("rol",rol);
        trabajador.put("direccion", direccion);
        trabajador.put("departamento",departamento);


        db.collection("trabajadores").document(rut).set(trabajador).addOnSuccessListener(aVoid ->{
            Toast.makeText(this,"Datos enviados correctamente",Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this,"Error al enviar los datos",Toast.LENGTH_SHORT).show();
        });
    }
    public void CargarLista(View view){

        CargarListaFireStore();
    }

    public void CargarListaFireStore(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("trabajadores").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<String> listaDepartamentos = new ArrayList<>();
                    for (QueryDocumentSnapshot document: task.getResult()){
                        String linea = "||" + document.getString("rut") + "||" + document.getString("nombre") + "||" + document.getString("direccion") + "||" + document.getString("rol");
                        listaDepartamentos.add(linea);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            MainActivity.this, android.R.layout.simple_list_item_1,listaDepartamentos);
                    lista.setAdapter(adapter);
                } else {
                    Log.e("TAG","Error al obtener los datos", task.getException());
                }
            }
        });
    }
    public void accederMqtt(View view){
        Intent intent = new Intent(this,Mqtt.class);
        startActivity(intent);
    }
}