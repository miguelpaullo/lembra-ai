package com.example.lembraai.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.lembraai.dao.TarefaDao;
import com.example.lembraai.dao.UsuarioDao;
import com.example.lembraai.model.Tarefa;
import com.example.lembraai.model.Usuario;

// Define o banco de dados do aplicativo
@Database(
        entities = {Usuario.class, Tarefa.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    // Disponibiliza as operações dos usuários
    public abstract UsuarioDao usuarioDao();

    // Disponibiliza as operações das tarefas
    public abstract TarefaDao tarefaDao();
}