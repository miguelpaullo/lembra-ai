package com.example.lembraai.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.lembraai.model.Tarefa;

import java.util.List;

// Define as operações de acesso aos dados das tarefas
@Dao
public interface TarefaDao {

    // Cadastra uma nova tarefa e retorna seu ID
    @Insert
    long inserir(Tarefa tarefa);

    // Atualiza uma tarefa existente
    @Update
    void atualizar(Tarefa tarefa);

    // Exclui uma tarefa
    @Delete
    void excluir(Tarefa tarefa);

    // Busca todas as tarefas de um usuário
    @Query("SELECT * FROM tarefas WHERE usuarioId = :usuarioId")
    List<Tarefa> listarPorUsuario(int usuarioId);

    @Query("SELECT * FROM tarefas WHERE id = :id LIMIT 1")
    Tarefa buscarPorId(int id);
}