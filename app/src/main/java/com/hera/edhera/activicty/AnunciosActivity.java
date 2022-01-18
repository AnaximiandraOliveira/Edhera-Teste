package com.hera.edhera.activicty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.hera.edhera.R;
import com.hera.edhera.adapter.AdapterAnuncios;
import com.hera.edhera.helper.ConfiguracaoFirebase;
import com.hera.edhera.helper.RecyclerItemClickListener;
import com.hera.edhera.model.Anuncio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class AnunciosActivity extends AppCompatActivity {
    private FirebaseAuth autenticacao;

    private RecyclerView recyclerAnunciosPublicos;
    private Button buttonRegiao, buttonCategoria;
    private AdapterAnuncios adapterAnuncios;
    private List<Anuncio> listaAnuncios = new ArrayList<>();
    private DatabaseReference anunciosPublicosRef;
    private AlertDialog dialog;
    private  String filtroEstado ="";
    private  String filtroCategoria ="";
    private  boolean filtrandoPorEstado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        inicializarComponentes();

        //CONFIGURAÇÕES INICIAIS

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebase().child("anuncios");

        //Configurar RecyclerView
        recyclerAnunciosPublicos.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnunciosPublicos.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(listaAnuncios, this);
        recyclerAnunciosPublicos.setAdapter( adapterAnuncios );

        recuperarAnunciosPublicos();

        recyclerAnunciosPublicos.addOnItemTouchListener(new RecyclerItemClickListener(
                this, recyclerAnunciosPublicos, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Anuncio anuncioSelecionado= listaAnuncios.get(position);
                Intent i = new Intent(AnunciosActivity.this, DetalhesProdutoActivity.class);
                i.putExtra("anuncioSelecionado",anuncioSelecionado);
                startActivity(i);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }
        ));

    }

    public void filtarPorEstado(View view){

        AlertDialog.Builder dialogEstado = new AlertDialog.Builder(this);
        dialogEstado.setTitle("Selecione a Província Desejada");

        View viewSpinner= getLayoutInflater().inflate(R.layout.dialog_spinner, null);

        //Confiurar Spinner
        Spinner spinnerEstado = viewSpinner.findViewById(R.id.spinnerFiltro);

        //CONFIGURANDO OS SPINNERS
        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, estados);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapter);
        dialogEstado.setView(viewSpinner);

        dialogEstado.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filtroEstado = spinnerEstado.getSelectedItem().toString();
                recuperarAnunciosPorEstado();
                filtrandoPorEstado = true;
            }
        });

        dialogEstado.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = dialogEstado.create();
        dialog.show();

    }

    public void filtarPorCategoria(View view){

        if(filtrandoPorEstado==true)
        {
            AlertDialog.Builder dialogEstado = new AlertDialog.Builder(this);
            dialogEstado.setTitle("Selecione a Categoria Desejada");

            View viewSpinner= getLayoutInflater().inflate(R.layout.dialog_spinner, null);

            //Confiurar Spinner
            Spinner spinnerCategoria = viewSpinner.findViewById(R.id.spinnerFiltro);

            //CONFIGURANDO OS SPINNERS
            String[] estados = getResources().getStringArray(R.array.categoria);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, estados);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategoria.setAdapter(adapter);
            dialogEstado.setView(viewSpinner);

            dialogEstado.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    filtroCategoria = spinnerCategoria.getSelectedItem().toString();
                    recuperarAnunciosPorCategoria();
                }
            });

            dialogEstado.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = dialogEstado.create();
            dialog.show();

        } else{

            Toast.makeText(this, "Escolha primeiro uma Região", Toast.LENGTH_SHORT).show();
        }



    }

    public void recuperarAnunciosPorCategoria()
    {

        //Configurar nó por Estado
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebase()
                .child("anuncios")
                .child(filtroEstado)
                .child(filtroCategoria);

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                listaAnuncios.clear();
                for (DataSnapshot anuncios:snapshot.getChildren())
                {
                    Anuncio anuncio = anuncios.getValue(Anuncio.class);
                    listaAnuncios.add(anuncio);

                }
                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });

    }


    public void recuperarAnunciosPorEstado()
    {

        //Configurar nó por Estado
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebase()
                .child("anuncios")
                .child(filtroEstado);

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                listaAnuncios.clear();
                for (DataSnapshot categoria : snapshot.getChildren()) {
                    for (DataSnapshot anuncios:categoria.getChildren())
                    {
                        Anuncio anuncio = anuncios.getValue(Anuncio.class);
                        listaAnuncios.add(anuncio);

                    }
                }

                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });

    }

    public void recuperarAnunciosPublicos()
    {
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando Anúncios")
                .setCancelable(false)
                .build();
        dialog.show();

        listaAnuncios.clear();
        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange (DataSnapshot snapshot) {

                for(DataSnapshot estado: snapshot.getChildren()) {
                    for (DataSnapshot categoria : estado.getChildren()) {
                        for (DataSnapshot anuncios:categoria.getChildren())
                        {
                            Anuncio anuncio = anuncios.getValue(Anuncio.class);
                            listaAnuncios.add( anuncio );


                        }
                    }
                }

                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();



            }


            @Override
            public void onCancelled( DatabaseError error) {

            }
        });


    }


        @Override  //CRIAR OS TENS DE MENU
     public boolean onCreateOptionsMenu(Menu menu){
            getMenuInflater().inflate(R.menu.menu_main, menu);
         return super.onCreateOptionsMenu(menu);
        }

    @Override  //PARA FAZER TESTES DE QUAIS ITENS DE MENU SERÃO EXIBIDOS PARA O USUÁRIO
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(autenticacao.getCurrentUser() == null) { //USUARIO DESLOGADO

            menu.setGroupVisible(R.id.group_deslogado, true);

        } else {   //USUARIO LOGADO

            menu.setGroupVisible(R.id.group_logado, true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override  //PARA INVALIDAR O MENU E RECARREGA-LO NOVAMENTE
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //PARA INVALIDAR O MENU E RECARREGA-LO NOVAMENTE
        switch(item.getItemId())
        {
            case R.id.menu_cadastrar :
            startActivity( new Intent(getApplicationContext(), CadastroActivity.class));
            break;

            case R.id.menu_sair :
                autenticacao.signOut();
                invalidateOptionsMenu();
                break;

            case R.id.menu_anuncios :
                startActivity( new Intent(getApplicationContext(), MeusAnunciosActivity.class));
                break;

        }

        return super.onOptionsItemSelected(item);
    }



    public void inicializarComponentes(){

        recyclerAnunciosPublicos = findViewById(R.id.recyclerAnunciosPublicos);


    }

}