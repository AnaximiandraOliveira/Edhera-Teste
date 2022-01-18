package com.hera.edhera.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hera.edhera.R;
import com.hera.edhera.model.Comentario;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.List;

public class AdapterComentario extends RecyclerView.Adapter<AdapterComentario.MyViewHolder> {

    private List<Comentario> listaComentarios;
    private Context context;

    public AdapterComentario(List<Comentario> listaComentarios, Context context) {
        this.listaComentarios = listaComentarios;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_comentario, parent, false);

        return new AdapterComentario.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Comentario comentario = listaComentarios.get(position);

        holder.comentario.setText(comentario.getComentario());

    }

    @Override
    public int getItemCount() {

        return listaComentarios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView comentario;

        public MyViewHolder(View itemView){

            super(itemView);

            comentario = itemView.findViewById(R.id.textComentario);
        }

    }

}
