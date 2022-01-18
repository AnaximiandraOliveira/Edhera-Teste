package com.hera.edhera.activicty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.hera.edhera.R;
import com.hera.edhera.helper.ConfiguracaoFirebase;

public class CadastroActivity extends AppCompatActivity {

    private Button botaoAcessar;
    private EditText campoEmail, campoSenha;
    private Switch tipoAcesso;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        inicializarComponentes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        botaoAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if (!email.isEmpty()) {

                    if (!senha.isEmpty()) {

                        //VERIFICAR O ESTADO DO SWITCH

                        if (tipoAcesso.isChecked()) { //CADASTRO

                            autenticacao.createUserWithEmailAndPassword(email, senha)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                            if (task.isSuccessful()) {

                                                Toast.makeText(CadastroActivity.this, "Cadastro Realizado com Sucesso",
                                                        Toast.LENGTH_LONG).show();

                                                //Direcionar para a tela principal do App

                                            } else {

                                                String erroExcecao = "";

                                                try {

                                                    throw task.getException();
                                                } catch (FirebaseAuthWeakPasswordException e) {
                                                    erroExcecao = "Digite uma senha mais forte!";
                                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                                    erroExcecao = "Por Favor digite um e-mail válido";
                                                } catch (
                                                        FirebaseAuthUserCollisionException e) {
                                                    erroExcecao = "Este e-mail já foi cadastrado!";
                                                } catch (Exception e) {
                                                    erroExcecao = "Ao cadastrar usuário: " + e.getMessage();
                                                    e.printStackTrace();
                                                }
                                                Toast.makeText(CadastroActivity.this,
                                                        "Erro: " + erroExcecao, Toast.LENGTH_SHORT).show();

                                            }

                                        }
                                    });

                        } else { //LOGIN

                            autenticacao.signInWithEmailAndPassword(email, senha)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {

                                                Toast.makeText(CadastroActivity.this,
                                                        "Logado com Sucesso", Toast.LENGTH_SHORT).show();

                                                startActivity(new Intent(getApplicationContext(), AnunciosActivity.class));

                                            } else {

                                                Toast.makeText(CadastroActivity.this,
                                                        "Erro ao fazer login: " + task.getException(), Toast.LENGTH_SHORT).show();


                                            }
                                        }
                                    });


                        }

                    } else {
                        Toast.makeText(CadastroActivity.this, "Preecha a senha!", Toast.LENGTH_SHORT).show();

                    }

                } else {

                    Toast.makeText(CadastroActivity.this, "Preecha o E-mail!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
        private void inicializarComponentes()
            {

                campoEmail = findViewById(R.id.editCadastroEmail);
                campoSenha = findViewById(R.id.editCadastroSenha);
                tipoAcesso = findViewById(R.id.switchAcesso);
                botaoAcessar = findViewById(R.id.buttonAcesso);
            };




    }
