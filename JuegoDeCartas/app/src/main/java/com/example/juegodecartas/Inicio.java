package com.example.juegodecartas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Locale;

public class Inicio extends AppCompatActivity {
    Button jugar;
    FirebaseAuth myAuth;
    FirebaseFirestore myStore;
    String idUsuario;
    ImageView imageView;
    StorageReference myStorage;
    final long ONE_MEGABYTE = 1024 * 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        myAuth = FirebaseAuth.getInstance();
        myStore = FirebaseFirestore.getInstance();
        idUsuario = myAuth.getCurrentUser().getUid();
        imageView=findViewById(R.id.imageView);
        DocumentReference docRef = myStore.collection("usuarios").document(idUsuario);
        myStorage = FirebaseStorage.getInstance().getReference();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference photoReference = storageReference.child("usuarios/" + idUsuario + "/imagen_perfil.jpg");
        photoReference.getBytes(100 * 1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bmp);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void salir(View view) {
          myStore.terminate();
          myAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }

      public void facil(View view) {
        jugar = (Button) findViewById(R.id.facil);
        Intent i = new Intent(Inicio.this, CartasAnimales.class); //El intent vincula las dos clases
        startActivity(i);
    }

    public void medio(View view) {
        jugar = (Button) findViewById(R.id.medio);
        Intent i = new Intent(Inicio.this, CartasBanderas.class); //El intent vincula las dos clases
        startActivity(i);
    }


    public void dificil(View view) {
        jugar = (Button) findViewById(R.id.dificil);
        Intent i = new Intent(Inicio.this, CartasPoker.class); //El intent vincula las dos clases
        startActivity(i);
    }


    public void cambiarfoto(View view) {
        //Este intent te abre la galeria
        Intent abrirGaleriaIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(abrirGaleriaIntent, 8888);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 8888) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imagenUri = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imagenUri);
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    int newWidth = imageView.getWidth();
                    int newHeight = (int) (((float) height * newWidth) / width);

                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
                    if (width > height) {
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(resizedBitmap, 0, 0, newWidth, newHeight, matrix, true);
                        imageView.setImageBitmap(rotatedBitmap);
                    } else {
                        imageView.setImageBitmap(resizedBitmap);
                    }
                    subirImagenCloudStorageFirebase(imagenUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void subirImagenCloudStorageFirebase(Uri imagenUri) {
        StorageReference referenciaFichero = myStorage.child("usuarios/" + idUsuario + "/imagen_perfil.jpg");
        referenciaFichero.putFile(imagenUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Inicio.this, "Imagen Cargada.", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Inicio.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    int tiemposeleccionado=0;
    public void showtiempo(View view) {
        Intent intent = new Intent(this, CartasAnimales.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_language);
        final String[] languages = {"2", "5", "8"};
        builder.setItems(languages, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    tiemposeleccionado = 2000;
                } else if (which == 1) {
                    tiemposeleccionado = 5000;
                } else {
                    tiemposeleccionado = 8000;
                }
                intent.putExtra("tiemposeleccionado", tiemposeleccionado);
                startActivity(intent);
                recreate();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}