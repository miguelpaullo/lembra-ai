package com.example.lembraai;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import com.example.lembraai.util.SessaoUtil;

// Splash oficial (fora das aulas, exigida), sem atraso artificial.
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        Intent destino;
        if (SessaoUtil.getUsuarioLogadoId(this) != -1) {
            destino = new Intent(this, ListaActivity.class);
        } else {
            destino = new Intent(this, LoginActivity.class);
        }
        startActivity(destino);
        finish();
    }
}
