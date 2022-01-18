package com.hera.edhera.model;

import com.google.firebase.database.DatabaseReference;
import com.hera.edhera.helper.ConfiguracaoFirebase;

public class Comentario {

    public  String idPostagem;
    public  String idComentario;
    public  String idUsuario;
    public  String comentario;
    private Anuncio anuncio;


    public Comentario() {
    }

    public boolean salvar()
    {
        DatabaseReference comentariosRef = ConfiguracaoFirebase.getFirebase()
                .child("comentarios");


        String chaveComentario = comentariosRef.push().getKey();
        setIdComentario(chaveComentario);

        comentariosRef.child(getIdComentario()).setValue(this);


        return true;
    }


    public String getIdPostagem() {
        return idPostagem;
    }

    public void setIdPostagem(String idPostagem) {
        this.idPostagem = idPostagem;
    }


    public String getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(String idComentario) {
        this.idComentario = idComentario;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
