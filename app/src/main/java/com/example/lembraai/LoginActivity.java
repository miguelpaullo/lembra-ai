
package com.example.lembraai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.lembraai.data.AppDatabase;
import com.example.lembraai.model.Usuario;
import com.example.lembraai.util.SegurancaUtil;
import com.example.lembraai.util.SessaoUtil;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmailLogin, edtSenhaLogin;
    private Button btnEntrar, btnIrCadastro;
    private AppDatabase banco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Localiza os componentes da tela
        edtEmailLogin = findViewById(R.id.edtEmailLogin);
        edtSenhaLogin = findViewById(R.id.edtSenhaLogin);
        btnEntrar = findViewById(R.id.btnEntrar);
        btnIrCadastro = findViewById(R.id.btnIrCadastro);

        // Abre o banco de dados
        banco = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "lembraai.db"
        ).allowMainThreadQueries().build();

        // Configura o botão Entrar
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realizarLogin();
            }
        });

        // Abre a tela de cadastro
        btnIrCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
                startActivity(intent);
            }
        });
    }

    // Realiza a autenticação do usuário
    private void realizarLogin() {

        String email = edtEmailLogin.getText().toString().trim();
        String senha = edtSenhaLogin.getText().toString();

        // Verifica se os campos foram preenchidos
        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Procura o usuário pelo e-mail
        Usuario usuario = banco.usuarioDao().buscarPorEmail(email);

        // Verifica se o usuário existe e se a senha está correta
        if (usuario != null && SegurancaUtil.verificarSenha(senha, usuario.senhaHash)) {

            // Salva o ID do usuário conectado
            SessaoUtil.salvarLogin(this, usuario.id);

            Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();

            // Abre a tela de tarefas
            Intent intent = new Intent(LoginActivity.this, ListaActivity.class);
            startActivity(intent);

            // Finaliza a tela de login
            finish();

        } else {
            Toast.makeText(this, "E-mail ou senha incorretos", Toast.LENGTH_SHORT).show();
        }
    }
}
