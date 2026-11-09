package com.example.lembraai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Toast;

import android.view.Menu;
import android.view.MenuItem;

import android.app.AlarmManager;
import android.app.PendingIntent;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.lembraai.data.AppDatabase;
import com.example.lembraai.model.Tarefa;
import com.example.lembraai.util.SessaoUtil;
import android.widget.AdapterView;

import java.util.List;

public class ListaActivity extends AppCompatActivity {

    private Button btnNovaTarefa;
    private ListView listaTarefas;
    private AppDatabase banco;
    private TarefaAdapter adapter;
    private static final String CANAL_LEMBRETES = "canal_lembretes";
    private ActivityResultLauncher<String> solicitarPermissaoNotificacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        // Verifica se existe um usuário conectado
        int usuarioId = SessaoUtil.getUsuarioLogadoId(this);

        if (usuarioId == -1) {
            Intent intent = new Intent(ListaActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        criarCanalNotificacao();

        solicitarPermissaoNotificacao =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        concedida -> {
                            if (concedida) {
                                Toast.makeText(this,
                                        "Permissão para notificações concedida!",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this,
                                        "As notificações não foram autorizadas.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                );

        if (Build.VERSION.SDK_INT >= 33 &&
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {

            solicitarPermissaoNotificacao.launch(
                    Manifest.permission.POST_NOTIFICATIONS
            );
        }

        btnNovaTarefa = findViewById(R.id.btnNovaTarefa);
        listaTarefas = findViewById(R.id.listaTarefas);

        // Permite abrir um menu ao pressionar uma tarefa
        registerForContextMenu(listaTarefas);

        // Abre o formulário para cadastrar uma nova tarefa
        btnNovaTarefa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListaActivity.this, FormTarefaActivity.class);
                startActivity(intent);
            }
        });

        // Abre a tarefa selecionada para edição
        listaTarefas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // Obtém a tarefa selecionada
                Tarefa tarefa = (Tarefa) adapter.getItem(position);

                // Abre o formulário enviando o ID da tarefa
                Intent intent = new Intent(ListaActivity.this, FormTarefaActivity.class);
                intent.putExtra("tarefaId", tarefa.id);
                startActivity(intent);
            }
        });

        banco = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "lembraai.db"
        ).allowMainThreadQueries().build();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Obtém o identificador do usuário conectado
        int usuarioId = SessaoUtil.getUsuarioLogadoId(this);

        if (usuarioId != -1) {
            // Busca somente as tarefas desse usuário
            List<Tarefa> tarefas = banco.tarefaDao().listarPorUsuario(usuarioId);

            // Conecta os dados ao ListView
            adapter = new TarefaAdapter(this, tarefas);
            listaTarefas.setAdapter(adapter);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("Opções da tarefa");

        menu.add(0, 1, 0, "Editar");
        menu.add(0, 2, 1, "Excluir");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Tarefa tarefa = (Tarefa) adapter.getItem(info.position);

        // Verifica se o usuário escolheu Editar
        if (item.getItemId() == 1) {

            Intent intent = new Intent(ListaActivity.this,
                    FormTarefaActivity.class);

            intent.putExtra("tarefaId", tarefa.id);
            startActivity(intent);

            return true;
        }

        // Verifica se o usuário escolheu Excluir
        if (item.getItemId() == 2) {

            Intent intent = new Intent(this, LembreteReceiver.class);

            PendingIntent alarmeIntent = PendingIntent.getBroadcast(
                    this,
                    tarefa.id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager gerenciadorAlarme =
                    (AlarmManager) getSystemService(ALARM_SERVICE);

            gerenciadorAlarme.cancel(alarmeIntent);

            banco.tarefaDao().excluir(tarefa);

            Toast.makeText(this, "Tarefa excluída com sucesso!",
                    Toast.LENGTH_SHORT).show();

            // Atualiza a tela para remover a tarefa da lista
            recreate();

            return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Verifica se o usuário escolheu Sair
        if (item.getItemId() == R.id.action_sair) {

            // Encerra a sessão do usuário
            SessaoUtil.logout(this);

            // Retorna para a tela de login
            Intent intent = new Intent(ListaActivity.this,
                    LoginActivity.class);

            // Impede que o usuário volte para a lista pelo botão Voltar
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void criarCanalNotificacao() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel canal = new NotificationChannel(
                    CANAL_LEMBRETES,
                    "Lembretes de tarefas",
                    NotificationManager.IMPORTANCE_HIGH
            );

            canal.setDescription("Notificações dos lembretes das tarefas");

            NotificationManager gerenciador =
                    getSystemService(NotificationManager.class);

            gerenciador.createNotificationChannel(canal);
        }
    }

}