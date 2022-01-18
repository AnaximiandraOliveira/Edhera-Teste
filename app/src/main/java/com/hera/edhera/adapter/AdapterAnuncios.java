package com.hera.edhera.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.hera.edhera.R;
import com.hera.edhera.activicty.AnunciosActivity;
import com.hera.edhera.model.Anuncio;
import com.squareup.picasso.Picasso;



import java.util.List;

public class AdapterAnuncios extends RecyclerView.Adapter<AdapterAnuncios.MyViewHolder> {

    private List<Anuncio> anuncios;
    private Context context;

    public AdapterAnuncios(List<Anuncio> anuncios, Context context) {
        this.anuncios = anuncios;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item= LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_anuncio, parent, false);

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Anuncio anuncio = anuncios.get(position);
        holder.nome.setText(anuncio.getNome());
        holder.servico.setText(anuncio.getServico());

        //Pegar a primeira imagem da lista
        List<String>urlFotos = anuncio.getFotos();
        String urlCapa = urlFotos.get(0);
        Picasso.with(context).load(urlCapa).into(holder.foto);


        /*Glide.with(context)
                .load(urlCapa)
                .into(holder.foto);*/



    }

    @Override
    public int getItemCount() {

        return anuncios.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder{

    TextView nome;
    TextView servico;
    ImageView foto;



    public  MyViewHolder(View itemView)
    {
        super(itemView);
        nome = itemView.findViewById(R.id.textNome);
        servico = itemView.findViewById(R.id.textServico);
        foto = itemView.findViewById(R.id.imageAnuncio);
    }

}


}
