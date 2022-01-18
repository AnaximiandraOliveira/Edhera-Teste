package com.hera.edhera.activicty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.hera.edhera.R;
import com.hera.edhera.adapter.AdapterComentario;
import com.hera.edhera.helper.ConfiguracaoFirebase;
import com.hera.edhera.model.Anuncio;
import com.hera.edhera.model.Comentario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ComentariosActivity extends AppCompatActivity {


    private Anuncio anuncio;
    private Comentario comentario;
    private String idPostagem;
    private AdapterComentario adapterComentario;


    private EditText editComentario;
    private RecyclerView recyclerComentarios;
    public List<Comentario> listaComentarios = new ArrayList<>();

    private DatabaseReference firebaserRef;
    private DatabaseReference comentariosRef;
    private ValueEventListener valueEventListenerComentarios;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        //Inicializar Componentes
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        editComentario = findViewById(R.id.editTextTComentario);
        recyclerComentarios = findViewById(R.id.recyclerComentarios);
        firebaserRef = ConfiguracaoFirebase.getFirebase();

        //Configurando o recyclerview
        adapterComentario = new AdapterComentario(listaComentarios, getApplicationContext() );
        recyclerComentarios.setHasFixedSize(true);
        recyclerComentarios.setLayoutManager(new LinearLayoutManager(this));
        recyclerComentarios.setAdapter(adapterComentario);


        inicializarComponentes();

       /* //Recuperar o ID DO ANUNCIO
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        {
            idPostagem = bundle.getString("idAnuncio");
        }*/

    }

    private void recuperarComentarios(){

        comentariosRef = firebaserRef.child("comentarios");
                //.child(idPostagem);

        valueEventListenerComentarios = comentariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaComentarios.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    listaComentarios.add( ds.getValue (Comentario.class) );
                }

                Collections.reverse(listaComentarios);
                adapterComentario.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarComentarios();
    }

    @Override
    protected void onStop() {
        super.onStop();
        comentariosRef.removeEventListener( valueEventListenerComentarios );
    }

    public void salvarComentario(View view)
    {

        String textoComentario = editComentario.getText().toString();
        if(autenticacao.getCurrentUser() != null){
        if(textoComentario!=null && !textoComentario.equals("")){

            Comentario comentario = new Comentario();
            comentario.setComentario(textoComentario);
            if(comentario.salvar())
            {
                Toast.makeText(this, "Comentário Enviado", Toast.LENGTH_SHORT).show();


            }


        }else {
            Toast.makeText(this, "Insira um Comentário!", Toast.LENGTH_SHORT).show();

        } }else {

                Toast.makeText(this, "Inicie Sessão para poder comentar", Toast.LENGTH_LONG).show();
            }



        //Limpar Comentario Digitado
        editComentario.setText("");

    }

    public void inicializarComponentes(){

        recyclerComentarios = findViewById(R.id.recyclerComentarios);

    }




}