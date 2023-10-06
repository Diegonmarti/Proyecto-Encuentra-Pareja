package com.example.juegodecartas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CartasAnimales extends AppCompatActivity {
    FirebaseFirestore myStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartasanimales);
        chronometer = findViewById(R.id.cronometro);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.stop();
        myStore = FirebaseFirestore.getInstance();
        iniciar();
    }
    private void guardarPuntuacionFirebase(){
        Map<String, Object> puntos = new HashMap<>();
        puntos.put("puntuacionfacil", puntuacion);
        puntos.put("tiempo", (SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000);
        myStore.collection("puntuacionfacil")
                .add(puntos)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("FIREBASE", "Puntuacion guardada con ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("FIREBASE", "Error al guardar la puntuacion", e);
                    }
                });
    }
    public void mejorpuntuacion(View view) {
        myStore.collection("puntuacionfacil")
                .orderBy("puntuacionfacil", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                long puntuacionMaxima = document.getLong("puntuacionfacil");
                                // Aquí puedes mostrar la puntuación máxima en la UI
                                // Por ejemplo, asignándolo a un TextView
                                TextView textView = findViewById(R.id.mejorpuntuacion);
                                textView.setText(String.valueOf(puntuacionMaxima));
                            }
                        } else {
                            Log.d("FIREBASE", "Error al obtener la puntuación máxima", task.getException());
                        }
                    }
                });
    }
    private Chronometer chronometer;
    TextView puntuaciontxt;
    int puntuacion;
    int aciertos;
    private void cargartexto(){
        puntuaciontxt=findViewById(R.id.textopuntuacion);
        puntuacion=0;
        aciertos=0;
        puntuaciontxt.setText(getString(R.string.puntuacion)+puntuacion);
    }

    Button reiniciar, salir;
    private void cargarbotones() {
        reiniciar = findViewById(R.id.reinicio);
        salir = findViewById(R.id.salir);
        reiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chronometer.setBase(SystemClock.elapsedRealtime());
                iniciar();
            }
        });
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.stop();
                finish();
            }
        });
    }

    //Variales para el tablero
    ImageButton animal0,animal1, animal2, animal3, animal4, animal5, animal6, animal7, animal8, animal9, animal10, animal11, animal12, animal13, animal14, animal15;
    ImageButton[] animales = new ImageButton[16];

    private void cargartablero() {
        animal0 = findViewById(R.id.cartaA0);
        animal1 = findViewById(R.id.cartaA1);
        animal2 = findViewById(R.id.cartaA2);
        animal3 = findViewById(R.id.cartaA3);
        animal4 = findViewById(R.id.cartaA4);
        animal5 = findViewById(R.id.cartaA5);
        animal6 = findViewById(R.id.cartaA6);
        animal7 = findViewById(R.id.cartaA7);
        animal8 = findViewById(R.id.cartaA8);
        animal9 = findViewById(R.id.cartaA9);
        animal10 = findViewById(R.id.cartaA10);
        animal11 = findViewById(R.id.cartaA11);
        animal12 = findViewById(R.id.cartaA12);
        animal13 = findViewById(R.id.cartaA13);
        animal14 = findViewById(R.id.cartaA14);
        animal15 = findViewById(R.id.cartaA15);

        animales[0] = animal0;
        animales[1] = animal1;
        animales[2] = animal2;
        animales[3] = animal3;
        animales[4] = animal4;
        animales[5] = animal5;
        animales[6] = animal6;
        animales[7] = animal7;
        animales[8] = animal8;
        animales[9] = animal9;
        animales[10] = animal10;
        animales[11] = animal11;
        animales[12] = animal12;
        animales[13] = animal13;
        animales[14] = animal14;
        animales[15] = animal15;

    }


    int[] imagenes;
    int fondo;
    private void cargarimagenes() {
        imagenes = new int[]{
                R.drawable.cerdo,
                R.drawable.dinosaurio,
                R.drawable.erizo,
                R.drawable.koala,
                R.drawable.pollito,
                R.drawable.nutria,
                R.drawable.pinguino,
                R.drawable.tortuga
        };

        fondo = R.drawable.circulo;
    }

    private ArrayList<Integer> barajar2(int longitud){
        ArrayList <Integer> result = new ArrayList<Integer>();
        for (int i=0; i<longitud*2; i++){
            result.add(i%longitud);
        }
        Collections.shuffle(result);
        return result;
    }

    ArrayList<Integer> arraydesordenado;
    ImageButton primero;
    int numeroPrimero, numeroSegundo;
    boolean bloqueo = false;
    final Handler handler = new Handler();
    boolean primerMovimineto=true;
    public void comprobar(int i, final ImageButton imagen) {
        if(primerMovimineto){
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            startTimer();
        }
        if (primero == null) {

            primerMovimineto=false;
            primero = imagen;
            primero.setScaleType(ImageView.ScaleType.CENTER_CROP);
            primero.setImageResource(imagenes[arraydesordenado.get(i)]);
            primero.setEnabled(false); //para bloquear el boton
            numeroPrimero = arraydesordenado.get(i); //almacenamos el valor del array desordenado

        } else {
            bloqueo = true;  //bloqueamos los demás
            imagen.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imagen.setImageResource(imagenes[arraydesordenado.get(i)]);
            imagen.setEnabled(false);
            numeroSegundo = arraydesordenado.get(i);
            if (numeroPrimero == numeroSegundo) { //si coincide el valor
                //reiniciamos
                primero = null;
                bloqueo = false;
                aciertos++;
                puntuacion++;
                puntuaciontxt.setText(getString(R.string.punto)+puntuacion);
                if (aciertos == imagenes.length) {
                    guardarPuntuacionFirebase();
                    chronometer.stop();
                    long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                    long elapsedSeconds = elapsedMillis / 1000;
                    if(puntuacion!=0){
                        if(puntuacion>0) {
                            Toast toast = Toast.makeText(getApplicationContext(), "¡¡¡Has ganado!!! en " + elapsedSeconds + " segundos y con "+ puntuacion+" puntos.",Toast.LENGTH_LONG);
                            toast.show();
                        }else{
                            Toast toast = Toast.makeText(getApplicationContext(), "Has perdido, vuelve a intentarlo.", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(), "¡¡¡Por que poco!!! EMPATE "+ puntuacion+" puntos.",Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            } else {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        primero.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        primero.setImageResource(fondo);
                        imagen.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imagen.setImageResource(fondo);
                        imagen.setEnabled(true);
                        primero.setEnabled(true); //para bloquear el boton
                        primero = null;
                        bloqueo=false;
                        puntuacion--;
                        puntuaciontxt.setText(getString(R.string.punto)+puntuacion);
                    }
                }, 1000);
            }
        }
    }


    private void mostrarcartasprincipio(){
        Intent intent = getIntent();
        int valor = intent.getIntExtra("tiemposeleccionado", 0);
        handler.postDelayed(new Runnable() { //enviar y procesar obj
            @Override
            public void run() {
                for (int i = 0; i < animales.length; i++) {
                    animales[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
                    animales[i].setImageResource(fondo);
                }
            }
        }, valor);

        for (int i=0; i<animales.length; i++){
            final int j=i;
            animales[i].setEnabled(true);
            animales[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!bloqueo) {
                        comprobar(j, animales[j]);
                    }
                }
            });
        }
    }
    private boolean bloqueada;

    private void iniciar() {
        cargartablero();
        cargarbotones();
        cargartexto();
        cargarimagenes();
        arraydesordenado = barajar2(imagenes.length);
        for(int i=0; i<animales.length; i++){
            animales[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
            animales[i].setImageResource(imagenes[arraydesordenado.get(i)]); //asociar cada boton con imagen aleatoria
        }
        mostrarcartasprincipio();
    }

    private void girar(){
        handler.postDelayed(new Runnable() { //enviar y procesar obj
            @Override
            public void run() {
                for (int i = 0; i < animales.length; i++) {
                    if(animales[i].getTag() != "girada" && !bloqueada) {
                        animales[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
                        animales[i].setImageResource(fondo);

                        RotateAnimation rotate = new RotateAnimation(0, 360,
                                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                        rotate.setDuration(4000);
                        rotate.setFillAfter(true);
                        animales[i].startAnimation(rotate);
                    }
                }
            }
        }, 1000);

        for (int i=0; i<animales.length; i++){
            final int j=i;
            animales[i].setEnabled(true);
            animales[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!bloqueo && !bloqueada) {
                        comprobar(j, animales[j]);
                    }
                }
            });
        }
    }

    public void tiempo() {
        cargartablero();
        cargarbotones();
        cargarimagenes();
        for(int i=0; i<animales.length; i++){
            animales[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
            animales[i].setImageResource(imagenes[arraydesordenado.get(i)]); //asociar cada boton con imagen aleatoria
        }
        girar();
    }
    private CountDownTimer countDownTimer;
    public void startTimer() {
        CountDownTimer countDownTimer = new CountDownTimer(35000, 1000) {
            public void onTick(long millisUntilFinished) {
                // No hacer nada
            }

            public void onFinish() {
                tiempo();
                startTimer();
                aciertos = 0;
                puntuacion = 0;
                puntuaciontxt.setText(getString(R.string.puntuacion) + puntuacion);
                start();
            }
        };
        countDownTimer.start();
    }

}