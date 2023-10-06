package com.example.juegodecartas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class CartasPoker extends AppCompatActivity {
    FirebaseFirestore myStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poker);
        chronometer= findViewById(R.id.cronometro);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.stop();
        myStore = FirebaseFirestore.getInstance();
        iniciar();
    }

    private void guardarPuntuacionFirebase(){
        Map<String, Object> puntos = new HashMap<>();
        puntos.put("puntuaciondificil", puntuacion);
        puntos.put("tiempo", (SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000);
        myStore.collection("puntuaciondificil")
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
        myStore.collection("puntuaciondificil")
                .orderBy("puntuaciondificil", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                long puntuacionMaxima = document.getLong("puntuaciondificil");
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
    ImageButton carta0,carta1, carta2, carta3, carta4, carta5, carta6, carta7, carta8, carta9, carta10, carta11, carta12, carta13, carta14, carta15;
    ImageButton[] cartas = new ImageButton[16];

    private void cargartablero() {
        carta0 = findViewById(R.id.cartaPoker0);
        carta1 = findViewById(R.id.cartaPoker1);
        carta2 = findViewById(R.id.cartaPoker2);
        carta3 = findViewById(R.id.cartaPoker3);
        carta4 = findViewById(R.id.cartaPoker4);
        carta5 = findViewById(R.id.cartaPoker5);
        carta6 = findViewById(R.id.cartaPoker6);
        carta7 = findViewById(R.id.cartaPoker7);
        carta8 = findViewById(R.id.cartaPoker8);
        carta9 = findViewById(R.id.cartaPoker9);
        carta10 = findViewById(R.id.cartaPoker10);
        carta11 = findViewById(R.id.cartaPoker11);
        carta12 = findViewById(R.id.cartaPoker12);
        carta13 = findViewById(R.id.cartaPoker13);
        carta14 = findViewById(R.id.cartaPoker14);
        carta15 = findViewById(R.id.cartaPoker15);

        cartas[0] = carta0;
        cartas[1] = carta1;
        cartas[2] = carta2;
        cartas[3] = carta3;
        cartas[4] = carta4;
        cartas[5] = carta5;
        cartas[6] = carta6;
        cartas[7] = carta7;
        cartas[8] = carta8;
        cartas[9] = carta9;
        cartas[10] = carta10;
        cartas[11] = carta11;
        cartas[12] = carta12;
        cartas[13] = carta13;
        cartas[14] = carta14;
        cartas[15] = carta15;

    }


    int[] imagenes;
    int fondo;
    private void cargarimagenes() {
        imagenes = new int[]{
                R.drawable.po1,
                R.drawable.po2,
                R.drawable.po3,
                R.drawable.po4,
                R.drawable.po5,
                R.drawable.po6,
                R.drawable.po7,
                R.drawable.po8
        };

        fondo = R.drawable.fondo;
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
            startTimer3();
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
                puntuacion=puntuacion+3;
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
                        puntuacion=puntuacion-3;
                        puntuaciontxt.setText(getString(R.string.punto)+puntuacion);
                    }
                }, 1000);
            }
        }
    }

    private void mostrarcartasprincipio(){
        handler.postDelayed(new Runnable() { //enviar y procesar obj
            @Override
            public void run() {
                for (int i = 0; i < cartas.length; i++) {
                    cartas[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
                    cartas[i].setImageResource(fondo);
                }
            }
        }, 2000);

        for (int i=0; i<cartas.length; i++){
            final int j=i;
            cartas[i].setEnabled(true);
            cartas[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!bloqueo) {
                        comprobar(j, cartas[j]);
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
        for(int i=0; i<cartas.length; i++){
            cartas[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
            cartas[i].setImageResource(imagenes[arraydesordenado.get(i)]); //asociar cada boton con imagen aleatoria
        }
        mostrarcartasprincipio();
    }
    private void girar(){
        handler.postDelayed(new Runnable() { //enviar y procesar obj
            @Override
            public void run() {
                for (int i = 0; i < cartas.length; i++) {
                    if(cartas[i].getTag() != "girada" && !bloqueada) {
                        cartas[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
                        cartas[i].setImageResource(fondo);

                        RotateAnimation rotate = new RotateAnimation(0, 360,
                                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                        rotate.setDuration(3000);
                        rotate.setFillAfter(true);
                        cartas[i].startAnimation(rotate);
                    }
                }
            }
        }, 1000);

        for (int i=0; i<cartas.length; i++){
            final int j=i;
            cartas[i].setEnabled(true);
            cartas[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!bloqueo && !bloqueada) {
                        comprobar(j, cartas[j]);
                    }
                }
            });
        }
    }

    public void tiempo() {
        cargartablero();
        cargarbotones();
        cargarimagenes();
        for(int i=0; i<cartas.length; i++){
            cartas[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
            cartas[i].setImageResource(imagenes[arraydesordenado.get(i)]); //asociar cada boton con imagen aleatoria
        }
        girar();
    }

    private CountDownTimer countDownTimer;
    public void startTimer3() {
        countDownTimer = new CountDownTimer(45000, 1000) { // 60000 es el tiempo en milisegundos, 1000 es el intervalo en milisegundos
            public void onTick(long millisUntilFinished) {
                // Aquí puedes poner código para actualizar la interfaz de usuario con el tiempo restante
            }

            public void onFinish() {
                // Este código se ejecutará cuando el cronómetro llegue a 0
                tiempo();
                startTimer3();
                puntuacion=0;
                aciertos=0;
            }
        }.start();
    }
}