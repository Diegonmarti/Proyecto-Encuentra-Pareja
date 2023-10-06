package com.example.juegodecartas;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class CartasBanderas extends AppCompatActivity {

    FirebaseFirestore myStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartasbanderas);
        chronometer= findViewById(R.id.cronometro);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.stop();
        myStore = FirebaseFirestore.getInstance();
        iniciar();
    }

    private void guardarPuntuacionFirebase(){
        Map<String, Object> puntos = new HashMap<>();
        puntos.put("puntuacionmedio", puntuacion);
        puntos.put("tiempo", (SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000);
        myStore.collection("puntuacionmedio")
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
        myStore.collection("puntuacionmedio")
                .orderBy("puntuacionmedio", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                long puntuacionMaxima = document.getLong("puntuacionmedio");
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
    ImageButton pais0, pais1, pais2, pais3, pais4, pais5, pais6, pais7, pais8, pais9, pais10, pais11, pais12, pais13, pais14, pais15;
    ImageButton[] paises = new ImageButton[16];

    private void cargartablero() {
        pais0 = findViewById(R.id.carta0);
        pais1 = findViewById(R.id.ajuste);
        pais2 = findViewById(R.id.carta2);
        pais3 = findViewById(R.id.carta3);
        pais4 = findViewById(R.id.carta4);
        pais5 = findViewById(R.id.carta5);
        pais6 = findViewById(R.id.carta6);
        pais7 = findViewById(R.id.carta7);
        pais8 = findViewById(R.id.carta8);
        pais9 = findViewById(R.id.carta9);
        pais10 = findViewById(R.id.carta10);
        pais11 = findViewById(R.id.carta11);
        pais12 = findViewById(R.id.carta12);
        pais13 = findViewById(R.id.carta13);
        pais14 = findViewById(R.id.carta14);
        pais15 = findViewById(R.id.carta15);

        paises[0] = pais0;
        paises[1] = pais1;
        paises[2] = pais2;
        paises[3] = pais3;
        paises[4] = pais4;
        paises[5] = pais5;
        paises[6] = pais6;
        paises[7] = pais7;
        paises[8] = pais8;
        paises[9] = pais9;
        paises[10] = pais10;
        paises[11] = pais11;
        paises[12] = pais12;
        paises[13] = pais13;
        paises[14] = pais14;
        paises[15] = pais15;

    }


    int[] imagenes;
    int fondo;
    private void cargarimagenes() {
        imagenes = new int[]{
                R.drawable.ad,
                R.drawable.ec,
                R.drawable.cu,
                R.drawable.dm,
                R.drawable.es,
                R.drawable.fk,
                R.drawable.er,
                R.drawable.bt
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
            startTimer2();
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

        handler.postDelayed(new Runnable() { //enviar y procesar obj
            @Override
            public void run() {
                for (int i = 0; i < paises.length; i++) {
                    paises[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
                    paises[i].setImageResource(fondo);
                }
            }
        }, 1500);


        for (int i=0; i<paises.length; i++){
            final int j=i;
            paises[i].setEnabled(true);
            paises[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!bloqueo) {
                        comprobar(j, paises[j]);
                    }
                }
            });
        }
    }

    private void iniciar() {
        cargartablero();
        cargarbotones();
        cargartexto();
        cargarimagenes();
        arraydesordenado = barajar2(imagenes.length);
        for(int i=0; i<paises.length; i++){
            paises[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
            paises[i].setImageResource(imagenes[arraydesordenado.get(i)]); //asociar cada boton con imagen aleatoria
        }
        mostrarcartasprincipio();
        }


    private boolean bloqueada;
    private void girar(){
        handler.postDelayed(new Runnable() { //enviar y procesar obj
            @Override
            public void run() {
                for (int i = 0; i < paises.length; i++) {
                    if(paises[i].getTag() != "girada" && !bloqueada) {
                        paises[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
                        paises[i].setImageResource(fondo);

                        RotateAnimation rotate = new RotateAnimation(0, 180,
                                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                        rotate.setDuration(3000);
                        rotate.setFillAfter(true);
                        paises[i].startAnimation(rotate);
                    }
                }
            }
        }, 1000);

        for (int i=0; i<paises.length; i++){
            final int j=i;
            paises[i].setEnabled(true);
            paises[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!bloqueo && !bloqueada) {
                        comprobar(j, paises[j]);
                    }
                }
            });
        }
    }
    public void tiempo() {
        cargartablero();
        cargarbotones();
        cargarimagenes();
        for(int i=0; i<paises.length; i++){
            paises[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
            paises[i].setImageResource(imagenes[arraydesordenado.get(i)]); //asociar cada boton con imagen aleatoria
        }
        girar();
    }
    private CountDownTimer countDownTimer;
    public void startTimer2() {
        countDownTimer = new CountDownTimer(40000, 1000) { // 60000 es el tiempo en milisegundos, 1000 es el intervalo en milisegundos
            public void onTick(long millisUntilFinished) {
                // Aquí puedes poner código para actualizar la interfaz de usuario con el tiempo restante
            }

            public void onFinish() {
                // Este código se ejecutará cuando el cronómetro llegue a 0
                tiempo();
                startTimer2();
                puntuacion=0;
                aciertos=0;
            }
        }.start();
    }

}

