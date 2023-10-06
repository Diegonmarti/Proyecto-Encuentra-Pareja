package com.example.juegodecartas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
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

import java.util.Locale;

public class Login extends AppCompatActivity {
    EditText intro_mail, intro_pwd;
    Button boton_acceso;
    TextView link_registro;
    ProgressBar progressBar2;
    FirebaseAuth myAuth;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        intro_mail = findViewById(R.id.emaillog);
        intro_pwd = findViewById(R.id.contraseñalog);
        boton_acceso = findViewById(R.id.iniciarsesion);
        link_registro = findViewById(R.id.crearcuenta);
        progressBar2 = findViewById(R.id.progressBar);
        myAuth = FirebaseAuth.getInstance();

        //Música:
        mediaPlayer = MediaPlayer.create(this, R.raw.musicafondo);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    public void mutear(View view) {

        if (mediaPlayer.isPlaying()) {
            // Pausar música
            mediaPlayer.pause();
        } else {
            // Reproducir música
            mediaPlayer.start();
        }
    }

    public void login(View view){
        String email = intro_mail.getText().toString();
        String passwd = intro_pwd.getText().toString();
        if(email.isEmpty()){
            intro_mail.setError("Es necesario que introduzcas un Email. ");
            return;
        }
        if(passwd.length()<6){
            intro_pwd.setError("La contraseña debe contener como mínimo 6 dígitos");
            return;
        }
        myAuth.signInWithEmailAndPassword(email, passwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar2.setVisibility(View.VISIBLE);
                if(task.isSuccessful()){
                    Toast.makeText(Login.this, "Usuario identificado correctamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), Inicio.class);
                    startActivity(intent);
                    progressBar2.setVisibility(View.INVISIBLE);
                }else{
                    Toast.makeText(Login.this, task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    progressBar2.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void crearcuenta(View view) {
        Intent intent = new Intent(getApplicationContext(), Register.class);
        startActivity(intent);
    }
    //IDIOMA:
    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }
    public void showLanguageDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_language);
        final String[] languages = {"English", "Español"};
        builder.setItems(languages, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    setLocale("en");
                } else if (which == 1) {
                    setLocale("es");
                }
                recreate();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}