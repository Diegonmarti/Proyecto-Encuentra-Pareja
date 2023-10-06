package com.example.juegodecartas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    EditText intro_pwd, intro_pwd_conf, intro_nombre, intro_mail, intro_telefono;
    Button boton_registro;
    TextView link_volver;
    ProgressBar progressBar;
    FirebaseAuth myAuth;
    FirebaseFirestore myStore;
    String id_usuario;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        intro_nombre = findViewById(R.id.nombre);
        intro_mail = findViewById(R.id.email);
        intro_telefono = findViewById(R.id.telefono);
        intro_pwd = findViewById(R.id.contraseña);
        intro_pwd_conf = findViewById(R.id.contraseñaconf);
        boton_registro = findViewById(R.id.registrar);
        link_volver = findViewById(R.id.volver);
        progressBar = findViewById(R.id.progressBar2);
        myAuth = FirebaseAuth.getInstance();
        myStore = FirebaseFirestore.getInstance();
        if (myAuth.getCurrentUser() != null) {
            Toast.makeText(Register.this, "usuario actualmente identificado, redirigiendo...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), Inicio.class);
            startActivity(intent);
            finish();
        }

    }

    public void registrar(View view) {
        String email = intro_mail.getText().toString();
        String nombre = intro_nombre.getText().toString();
        String password = intro_pwd.getText().toString();
        String confpasswd = intro_pwd_conf.getText().toString();
        String telefono = intro_telefono.getText().toString();
        if(email.length()==0){
            intro_mail.setError("Debes introducir un email");
            return;
        }
        if ((telefono.length() < 9 )||(telefono.length()>9)) {
            intro_telefono.setError("El teléfono debe tener 9 dígitos");
            return;
        }

            if (intro_pwd.length() < 6) {
                intro_pwd.setError("La contraseña debe contener como mínimo 6 dígitos");
                return;
            }
            if (intro_pwd_conf.length() < 6) {
                intro_pwd.setError("La contraseña debe contener como mínimo 6 dígitos");
                return;
            }
        if(!(password.equals(confpasswd))){
            intro_pwd_conf.setError("La contraseña no coincide");
            return;
        }
        myAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.VISIBLE);
                //Si no ha habido problemas (la tarea de registrar el usuario ha sido exitosa: Feedback y redireccion a la MainActivity
                if (task.isSuccessful()) {
                    Toast.makeText(Register.this, "Usuario registrado correctamente.", Toast.LENGTH_SHORT).show();
                    //despues de asegurar que no hay problemas en la conexion vamos a almacenar los datos personales del usuario (esribimos dentro del document, no collection)
                    id_usuario = myAuth.getCurrentUser().getUid();
                    DocumentReference document = myStore.collection("Usuarios Registrados").document(id_usuario);
                    HashMap<String, String> informacion = new HashMap<>();
                    informacion.put("Nombre", nombre);
                    informacion.put("Email", email);
                    informacion.put("Telefono", telefono);
                    informacion.put("Contraseña", password);
                    document.set(informacion);
                    Intent intent = new Intent(getApplicationContext(), Inicio.class);
                    startActivity(intent);
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    //Toast.makeText(Register.this,"Se ha producido une error en el proceso de registro.", Toast.LENGTH_SHORT ).show();
                    Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void MandarLogin(View view) {
        System.exit(0);
    }

    public void volver(View view) {
        finish();

    }

}
