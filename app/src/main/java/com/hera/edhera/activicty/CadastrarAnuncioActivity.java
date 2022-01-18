package com.hera.edhera.activicty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hera.edhera.R;
import com.hera.edhera.helper.ConfiguracaoFirebase;
import com.hera.edhera.helper.Permissoes;
import com.hera.edhera.model.Anuncio;
import com.santalu.maskara.widget.MaskEditText;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class CadastrarAnuncioActivity extends AppCompatActivity
             implements View.OnClickListener{

    private EditText campoNome, campoServico, campoDescricao;
    private Spinner campoEstado, campoCategoria;
    private ImageView imagem1, imagem2, imagem3;
    private MaskEditText campoTelefone;


    private Anuncio anuncio;
    private StorageReference storage;

    private android.app.AlertDialog dialog;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaUrlFotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        //CONFIGURAÇÕES INICIAIS
        storage = ConfiguracaoFirebase.getFirebaseStorage();



        //VALIDAR PERMISSÕES
        Permissoes.validarPermissoes(permissoes, this, 1);

        inicializarComponenetes();
        carregarDadosSpinner();

    }


    public void salvarAnuncio(){

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Salvando Anúncio")
                .setCancelable(false)
                .build();
        dialog.show();

        //Salvar Imagens no Storage
        for( int i=0; i < listaFotosRecuperadas.size() ; i++ ) {
            String urlImagem = listaFotosRecuperadas.get(i);
            int tamanhoLista = listaFotosRecuperadas.size();
            salvarFotoStorage(urlImagem,tamanhoLista, i );


        }


    }


    private void salvarFotoStorage(String urlString,final int totalFotos, int contador){

        /*StorageReference imagemAnuncio = storage.child("imagens")
                .child("anuncios")
                .child(anuncio.getIdAnuncio())
                .child("imagem" + contador);*/



        //CRIAR NÓ NO STORAGE
        StorageReference imagens = storage.child("imagens")
                .child("anuncios")
                .child(anuncio.getIdAnuncio())
                .child("imagem" + contador);

       //Upload do arquivo

        //Fazer upload do arquivo
        UploadTask uploadTask = imagens.putFile( Uri.parse(urlString) );
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

               Task <Uri> firebaseUrl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                   @Override
                   public void onComplete(@NonNull Task<Uri> task) {
                       String urlConvertida = task.getResult().toString();

                       listaUrlFotos.add( urlConvertida );

                       if( totalFotos == listaUrlFotos.size() ){
                           anuncio.setFotos( listaUrlFotos );
                           anuncio.salvar();

                           dialog.dismiss();
                           finish();

                       }
                   }
               });
                /*String urlConvertida = firebaseUrl.toString();

                listaUrlFotos.add( urlConvertida );

                if( totalFotos == listaUrlFotos.size() ){
                    anuncio.setFotos( listaUrlFotos );
                    anuncio.salvar();

                    dialog.dismiss();
                    finish();

                }*/

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                exibirMensagemErro("Falha ao fazer upload");
                Log.i("INFO", "Falha ao fazer upload: " + e.getMessage());
            }
        });
    }

    private Anuncio configurarAnuncio(){
        String estado = campoEstado.getSelectedItem().toString();
        String categoria = campoCategoria.getSelectedItem().toString();
        String nome = campoNome.getText().toString();
        String servico = campoServico.getText().toString();
        String telefone = campoTelefone.getMasked();
        String descricao = campoDescricao.getText().toString();

        Anuncio anuncio = new Anuncio();
        anuncio.setEstado(estado);
        anuncio.setCategoria(categoria);
        anuncio.setNome(nome);
        anuncio.setServico(servico);
        anuncio.setTelefone(telefone);
        anuncio.setDescricao(descricao);

        return anuncio;


    }

    public void validarDadosAnuncio (View view)
    {
        anuncio = configurarAnuncio();

        if(listaFotosRecuperadas.size() !=0)
        {
            if(!anuncio.getEstado().isEmpty())

            { if(!anuncio.getCategoria().isEmpty()){

                if(!anuncio.getNome().isEmpty()){

                    if(!anuncio.getServico().isEmpty()){

                        if(!anuncio.getTelefone().isEmpty()){

                            if (!anuncio.getDescricao().isEmpty()){

                             salvarAnuncio();

                            }else{

                                exibirMensagemErro("Preencha o campo descrição!");
                            }


                        }  else{

                            exibirMensagemErro("Preencha o Telefone!");
                        }


                    } else{

                        exibirMensagemErro("Preencha o campo serviço!");

                    }



                }else{

                    exibirMensagemErro("Preencha o Nome!");
                }


            }else{

                exibirMensagemErro("Preencha o campo categoria!");

            }

            }else{
                exibirMensagemErro("Preencha o campo estado!");

            }


        } else {

            exibirMensagemErro("Selecione ao menos uma foto!");

        }

    }

    public void exibirMensagemErro (String mensagem)
    {
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.imageCadastro1:
                escolherImagem(1);
                break;

            case R.id.imageCadastro2:
                escolherImagem(2);
                break;


            case R.id.imageCadastro3:
                escolherImagem(3);
                break;


        }
    }

    public void escolherImagem(int requestCode)
    {

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){

            //RECUPERAR IMAGEM
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            //CONFIGURAR IMAGEM NO IMAGEVIEW
            if(requestCode == 1)
            {
                imagem1.setImageURI(imagemSelecionada);


            } else if(requestCode == 2)
            {

                imagem2.setImageURI(imagemSelecionada);

            } else if(requestCode == 3)
            {

            imagem3.setImageURI(imagemSelecionada);

            }

            listaFotosRecuperadas.add(caminhoImagem);

        }
    }


    private void carregarDadosSpinner() {

        /*String [] estados = new String[]{

        };*/

        //CONFIGURANDO OS SPINNERS
        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter <String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, estados);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoEstado.setAdapter(adapter);

        String[] categoria = getResources().getStringArray(R.array.categoria);
        ArrayAdapter <String> adapterCategoria = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoria);
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoCategoria.setAdapter(adapterCategoria);


    }

    private void inicializarComponenetes(){

        campoNome = findViewById(R.id.editNome);
        campoServico = findViewById(R.id.editServico);
        campoDescricao = findViewById(R.id.editDescricao);
        campoTelefone= findViewById(R.id.editTelefone);
        campoEstado = findViewById(R.id.spinnerEstado);
        campoCategoria = findViewById(R.id.spinnerCategoria);
        imagem1 = findViewById(R.id.imageCadastro1);
        imagem2 = findViewById(R.id.imageCadastro2);
        imagem3 = findViewById(R.id.imageCadastro3);
        imagem1.setOnClickListener(this);
        imagem2.setOnClickListener(this);
        imagem3.setOnClickListener(this);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado: grantResults){
            if(permissaoResultado == PackageManager.PERMISSION_DENIED)
            {

                alertaValidacaoPermissao();

            }

        }
    }

    private void alertaValidacaoPermissao()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para Utilizar o APP é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog Dialog = builder.create();
        Dialog.show();
    }



}