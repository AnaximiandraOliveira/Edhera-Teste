package com.hera.edhera.activicty;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hera.edhera.R;
import com.hera.edhera.model.Anuncio;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

public class DetalhesProdutoActivity extends AppCompatActivity {

    private CarouselView carouselView;
    private TextView nome;
    private TextView servico;
    private TextView estado;
    private TextView descricao;
    private Anuncio anuncioSelecionado;
    private List<Anuncio> anuncios = new ArrayList<>();
    private Anuncio anuncio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_produto);

        //CONFIGURAR TOOLBAR
        getSupportActionBar().setTitle("Detalhes da Publicação");


        //INICIALIZAR COMPONENTES
        inicializarComponentes();

        //RECUPERAR ANUNCIO PARA IXIBIÇÃO
        anuncioSelecionado = (Anuncio)getIntent().getSerializableExtra("anuncioSelecionado");

        if(anuncioSelecionado!=null)
        {

            nome.setText(anuncioSelecionado.getNome());
            servico.setText(anuncioSelecionado.getServico());
            estado.setText(anuncioSelecionado.getEstado());
            descricao.setText(anuncioSelecionado.getDescricao());

            ImageListener imageListener = new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    String urlString = anuncioSelecionado.getFotos().get(position);
                    Picasso.with(DetalhesProdutoActivity.this).load(urlString).into(imageView);

                }
            };

            carouselView.setPageCount(anuncioSelecionado.getFotos().size());
            carouselView.setImageListener(imageListener);

        }

    }

    public void visualizarTelefone(View view)
    {

        Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                anuncioSelecionado.getTelefone(), null));
        startActivity(i);


    }

    public void seccaoPerguntas(View view)
    {
        Intent intent = new Intent(this, ComentariosActivity.class);
        //intent.putExtra("idAnuncio",anuncio.getIdAnuncio());
        startActivity(intent);


    }

    public void inicializarComponentes(){
        carouselView = findViewById(R.id.carouselView);
        nome = findViewById(R.id.textNomeDetalhe);
        servico = findViewById(R.id.textServicoDetalhe);
        estado = findViewById(R.id.textEstadoDetalhe);
        descricao = findViewById(R.id.textDescricaoDetalhe);


    }
}