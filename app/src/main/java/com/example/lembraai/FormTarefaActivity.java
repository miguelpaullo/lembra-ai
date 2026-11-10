package com.example.lembraai;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.SystemClock;

import com.example.lembraai.data.AppDatabase;
import com.example.lembraai.model.Tarefa;
import com.example.lembraai.util.SessaoUtil;

public class FormTarefaActivity extends AppCompatActivity {

    private EditText edtTituloTarefa;
    private EditText edtDescricaoTarefa;
    private Button btnSalvarTarefa;
    private AppDatabase banco;
    private int tarefaId = -1;
    private EditText edtLembreteMinutos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_tarefa);

        // Verifica se existe um usuário conectado
        int usuarioId = SessaoUtil.getUsuarioLogadoId(this);

        if (usuarioId == -1) {
            Intent intent = new Intent(FormTarefaActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Localiza os componentes da tela
        edtTituloTarefa = findViewById(R.id.edtTituloTarefa);
        edtDescricaoTarefa = findViewById(R.id.edtDescricaoTarefa);
        btnSalvarTarefa = findViewById(R.id.btnSalvarTarefa);
        edtLembreteMinutos = findViewById(R.id.edtLembreteMinutos);
        Button btnVoltar = findViewById(R.id.btnVoltar);

        btnVoltar.setOnClickListener(v -> {
            finish();
        });

        // Abre o banco de dados
        banco = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "lembraai.db"
        ).allowMainThreadQueries().build();

        // Verifica se a tela recebeu o ID de uma tarefa
        tarefaId = getIntent().getIntExtra("tarefaId", -1);

        // Se recebeu um ID, busca a tarefa para edição
        if (tarefaId != -1) {

            Tarefa tarefa = banco.tarefaDao().buscarPorId(tarefaId);

            if (tarefa != null && tarefa.usuarioId == usuarioId) {

                edtTituloTarefa.setText(tarefa.titulo);
                edtDescricaoTarefa.setText(tarefa.descricao);

                btnSalvarTarefa.setText("Atualizar tarefa");

            } else {

                Toast.makeText(this, "Tarefa não encontrada.",
                        Toast.LENGTH_SHORT).show();

                finish();
                return;
            }
        }

        // Configura o botão de salvar
        btnSalvarTarefa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvarTarefa();
            }
        });
    }

    private void salvarTarefa() {

        // Obtém os dados digitados
        String titulo = edtTituloTarefa.getText().toString().trim();
        String descricao = edtDescricaoTarefa.getText().toString().trim();

        String minutosTexto = edtLembreteMinutos.getText().toString().trim();

        int minutos = 0;

        if (!minutosTexto.isEmpty()) {
            try {
                minutos = Integer.parseInt(minutosTexto);

                if (minutos <= 0) {
                    Toast.makeText(this, "O lembrete deve ser maior que zero",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Informe um número válido de minutos",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Verifica se o título foi preenchido
        if (titulo.isEmpty()) {
            Toast.makeText(this, "Informe o título da tarefa",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtém o ID do usuário conectado
        int usuarioId = SessaoUtil.getUsuarioLogadoId(this);

        if (usuarioId == -1) {
            Toast.makeText(this, "Faça login novamente",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Verifica se estamos cadastrando ou editando
        Tarefa tarefa;

        if (tarefaId == -1) {
            tarefa = new Tarefa();
            tarefa.usuarioId = usuarioId;
        } else {
            tarefa = banco.tarefaDao().buscarPorId(tarefaId);

            if (tarefa == null || tarefa.usuarioId != usuarioId) {
                Toast.makeText(this, "Tarefa não encontrada",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Atualiza os dados da tarefa
        tarefa.titulo = titulo;
        tarefa.descricao = descricao;

        // Salva ou atualiza no banco
        if (tarefaId == -1) {
            long novoId = banco.tarefaDao().inserir(tarefa);
            tarefa.id = (int) novoId;
        } else {
            banco.tarefaDao().atualizar(tarefa);
        }

        if (tarefaId != -1) {
            cancelarLembrete(tarefaId);
        }

        if (minutos > 0) {
            agendarLembrete(tarefa, minutos);
        }

        String mensagem = tarefaId == -1
                ? "Tarefa cadastrada com sucesso!"
                : "Tarefa atualizada com sucesso!";

        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();

        // Fecha o formulário e retorna para a lista
        finish();
    }

    private void agendarLembrete(Tarefa tarefa, int minutos) {

        Intent intent = new Intent(this, LembreteReceiver.class);

        intent.putExtra("titulo", tarefa.titulo);
        intent.putExtra("descricao", tarefa.descricao);

        PendingIntent alarmeIntent = PendingIntent.getBroadcast(
                this,
                tarefa.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager gerenciadorAlarme =
                (AlarmManager) getSystemService(ALARM_SERVICE);

        long tempoAlarme = SystemClock.elapsedRealtime()
                + (minutos * 60 * 1000L);

        gerenciadorAlarme.set(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                tempoAlarme,
                alarmeIntent
        );
    }

    private void cancelarLembrete(int idTarefa) {

        Intent intent = new Intent(this, LembreteReceiver.class);

        PendingIntent alarmeIntent = PendingIntent.getBroadcast(
                this,
                idTarefa,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager gerenciadorAlarme =
                (AlarmManager) getSystemService(ALARM_SERVICE);

        gerenciadorAlarme.cancel(alarmeIntent);
    }
}