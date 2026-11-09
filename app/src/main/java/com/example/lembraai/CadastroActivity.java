
package com.example.lembraai;

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

public class CadastroActivity extends AppCompatActivity {

    private EditText edtNome;
    private EditText edtEmail;
    private EditText edtSenha;
    private EditText edtConfirmarSenha;
    private Button btnCadastrar;

    private AppDatabase banco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        // Conecta os componentes do XML ao Java
        edtNome = findViewById(R.id.edtNome);
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        edtConfirmarSenha = findViewById(R.id.edtConfirmarSenha);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        // Abre o banco de dados Room
        banco = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "lembraai.db"
        ).allowMainThreadQueries().build();

        // Configura o clique do botão
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastrarUsuario();
            }
        });
    }

    private void cadastrarUsuario() {

        // Obtém os valores digitados
        String nome = edtNome.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString();
        String confirmarSenha = edtConfirmarSenha.getText().toString();

        // Verifica campos vazios
        if (nome.isEmpty() || email.isEmpty()
                || senha.isEmpty() || confirmarSenha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Verifica o formato básico do e-mail
        if (!email.contains("@")) {
            Toast.makeText(this, "Informe um e-mail válido",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Verifica o tamanho mínimo da senha
        if (senha.length() < 6) {
            Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Verifica se as senhas são iguais
        if (!senha.equals(confirmarSenha)) {
            Toast.makeText(this, "As senhas não coincidem",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Verifica se o e-mail já está cadastrado
        Usuario usuarioExistente = banco.usuarioDao().buscarPorEmail(email);

        if (usuarioExistente != null) {
            Toast.makeText(this, "Este e-mail já está cadastrado",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Cria o usuário e protege a senha com BCrypt
        Usuario usuario = new Usuario();
        usuario.nome = nome;
        usuario.email = email;
        usuario.senhaHash = SegurancaUtil.hashPassword(senha);
        usuario.fotoPath = null;

        // Salva o usuário no banco
        banco.usuarioDao().inserir(usuario);

        Toast.makeText(this, "Cadastro realizado com sucesso",
                Toast.LENGTH_SHORT).show();

        // Fecha a tela e retorna à tela anterior
        finish();
    }
}
