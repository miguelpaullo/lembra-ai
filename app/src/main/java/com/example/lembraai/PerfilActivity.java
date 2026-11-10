package com.example.lembraai;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.room.Room;
import com.example.lembraai.data.AppDatabase;
import com.example.lembraai.model.Usuario;
import com.example.lembraai.util.SegurancaUtil;
import com.example.lembraai.util.SessaoUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

// Responsabilidade de Reinaldo: editar perfil, senha, foto e sair.
public class PerfilActivity extends AppCompatActivity {
    private EditText edtNome, edtEmail, edtSenhaAtual, edtNovaSenha, edtConfirmarSenha;
    private Button btnSalvar, btnAlterarSenha, btnSair, btnTirarFoto, btnGaleria;
    private ImageView imgFotoPerfil;
    private AppDatabase banco;
    private Usuario usuario;
    private File photoFile;
    private ActivityResultLauncher<String> permissaoCamera;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<String> galeriaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Aula 17: telas restritas verificam a sessão antes de abrir.
        int usuarioId = SessaoUtil.getUsuarioLogadoId(this);
        if (usuarioId == -1) {
            voltarLogin();
            return;
        }
        setContentView(R.layout.activity_perfil);
        edtNome = findViewById(R.id.edtPerfilNome);
        edtEmail = findViewById(R.id.edtPerfilEmail);
        edtSenhaAtual = findViewById(R.id.edtSenhaAtual);
        edtNovaSenha = findViewById(R.id.edtNovaSenha);
        edtConfirmarSenha = findViewById(R.id.edtConfirmarNovaSenha);
        btnSalvar = findViewById(R.id.btnSalvarPerfil);
        btnAlterarSenha = findViewById(R.id.btnAlterarSenha);
        btnSair = findViewById(R.id.btnSairPerfil);
        btnTirarFoto = findViewById(R.id.btnTirarFoto);
        btnGaleria = findViewById(R.id.btnGaleria);
        imgFotoPerfil = findViewById(R.id.imgFotoPerfil);

        banco = Room.databaseBuilder(getApplicationContext(), AppDatabase.class,
                "lembraai.db").allowMainThreadQueries().build();
        usuario = banco.usuarioDao().buscarPorId(usuarioId);
        if (usuario == null) {
            SessaoUtil.logout(this);
            voltarLogin();
            return;
        }
        edtNome.setText(usuario.nome);
        edtEmail.setText(usuario.email);
        exibirFoto();

        // Restaura o caminho da foto se a Activity for recriada.
        if (savedInstanceState != null) {
            String caminho = savedInstanceState.getString("foto_pendente");
            if (caminho != null) photoFile = new File(caminho);
        }

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { salvarDados(); }
        });
        btnAlterarSenha.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { alterarSenha(); }
        });
        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                SessaoUtil.logout(PerfilActivity.this);
                voltarLogin();
            }
        });

        // Aula 16: resultado de TakePicture com FileProvider.
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override public void onActivityResult(Boolean sucesso) {
                        if (sucesso && photoFile != null) {
                            salvarFoto(photoFile);
                        } else {
                            Toast.makeText(PerfilActivity.this, "Foto não capturada",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Fora das aulas: GetContent permite selecionar uma foto da galeria.
        galeriaLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override public void onActivityResult(Uri uri) {
                        if (uri != null) copiarFotoGaleria(uri);
                    }
                });

        // Aula 16: solicita permissão da câmera e trata negação.
        permissaoCamera = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                new ActivityResultCallback<Boolean>() {
                    @Override public void onActivityResult(Boolean concedida) {
                        if (concedida) abrirCamera();
                        else Toast.makeText(PerfilActivity.this,
                                "Permissão da câmera negada", Toast.LENGTH_SHORT).show();
                    }
                });
        btnTirarFoto.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { checarPermissao(); }
        });
        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { galeriaLauncher.launch("image/*"); }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle estado) {
        super.onSaveInstanceState(estado);
        if (photoFile != null) estado.putString("foto_pendente", photoFile.getAbsolutePath());
    }

    // Aula 17: atualiza nome e e-mail no Room.
    private void salvarDados() {
        String nome = edtNome.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        if (nome.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Preencha nome e e-mail", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Informe um e-mail válido", Toast.LENGTH_SHORT).show();
            return;
        }
        Usuario existente = banco.usuarioDao().buscarPorEmail(email);
        if (existente != null && existente.id != usuario.id) {
            Toast.makeText(this, "Este e-mail já está cadastrado", Toast.LENGTH_SHORT).show();
            return;
        }
        usuario.nome = nome;
        usuario.email = email;
        banco.usuarioDao().atualizar(usuario);
        Toast.makeText(this, "Perfil atualizado com sucesso", Toast.LENGTH_SHORT).show();
    }

    // Aula 24: BCrypt gera novo hash com salt ao trocar senha.
    private void alterarSenha() {
        String atual = edtSenhaAtual.getText().toString();
        String nova = edtNovaSenha.getText().toString();
        String confirmar = edtConfirmarSenha.getText().toString();
        if (atual.isEmpty() || nova.isEmpty() || confirmar.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos de senha", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!SegurancaUtil.verificarSenha(atual, usuario.senhaHash)) {
            Toast.makeText(this, "Senha atual incorreta", Toast.LENGTH_SHORT).show();
            return;
        }
        if (nova.length() < 6) {
            Toast.makeText(this, "A nova senha deve ter pelo menos 6 caracteres",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (!nova.equals(confirmar)) {
            Toast.makeText(this, "As novas senhas não coincidem", Toast.LENGTH_SHORT).show();
            return;
        }
        usuario.senhaHash = SegurancaUtil.hashPassword(nova);
        banco.usuarioDao().atualizar(usuario);
        edtSenhaAtual.setText("");
        edtNovaSenha.setText("");
        edtConfirmarSenha.setText("");
        Toast.makeText(this, "Senha alterada com sucesso", Toast.LENGTH_SHORT).show();
    }

    private void checarPermissao() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            abrirCamera();
        } else {
            permissaoCamera.launch(Manifest.permission.CAMERA);
        }
    }

    // Aula 16: arquivo dentro de Pictures do aplicativo.
    private File criarArquivoImagem() throws IOException {
        File pasta = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (pasta == null) throw new IOException("Pasta de imagens indisponível");
        return File.createTempFile("perfil_", ".jpg", pasta);
    }

    private void abrirCamera() {
        try {
            photoFile = criarArquivoImagem();
            Uri uri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider", photoFile);
            takePictureLauncher.launch(uri);
        } catch (IOException | RuntimeException e) {
            Toast.makeText(this, "Não foi possível abrir a câmera",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Fora das aulas: copia a imagem escolhida; não depende do URI temporário.
    private void copiarFotoGaleria(Uri uri) {
        File arquivo;
        try {
            arquivo = criarArquivoImagem();
            try (InputStream entrada = getContentResolver().openInputStream(uri);
                 FileOutputStream saida = new FileOutputStream(arquivo)) {
                if (entrada == null) throw new IOException("Imagem indisponível");
                byte[] buffer = new byte[4096];
                int quantidade;
                while ((quantidade = entrada.read(buffer)) != -1) {
                    saida.write(buffer, 0, quantidade);
                }
            }
            salvarFoto(arquivo);
        } catch (IOException | SecurityException e) {
            Toast.makeText(this, "Não foi possível carregar a imagem",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Aula 17 adaptada: salva apenas o caminho em Usuario.fotoPath.
    private void salvarFoto(File arquivo) {
        Bitmap foto = BitmapFactory.decodeFile(arquivo.getAbsolutePath());
        if (foto == null) {
            Toast.makeText(this, "Imagem inválida", Toast.LENGTH_SHORT).show();
            return;
        }
        usuario.fotoPath = arquivo.getAbsolutePath();
        banco.usuarioDao().atualizar(usuario);
        imgFotoPerfil.setImageBitmap(foto);
        Toast.makeText(this, "Foto atualizada com sucesso", Toast.LENGTH_SHORT).show();
    }

    private void exibirFoto() {
        if (usuario.fotoPath != null && !usuario.fotoPath.isEmpty()) {
            Bitmap foto = BitmapFactory.decodeFile(usuario.fotoPath);
            if (foto != null) imgFotoPerfil.setImageBitmap(foto);
        }
    }

    private void voltarLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
