
package com.example.lembraai;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.example.lembraai.util.SessaoUtil;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Exibe a Splash antes de criar a Activity.
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        // Verifica se existe uma sessão ativa.
        int usuarioId = SessaoUtil.getUsuarioLogadoId(this);

        Intent intent;

        if (usuarioId != -1) {
            // Usuário logado: abre a lista de tarefas.
            intent = new Intent(this, ListaActivity.class);
        } else {
            // Usuário não logado: abre o Login.
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
