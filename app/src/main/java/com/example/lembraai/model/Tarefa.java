package com.example.lembraai.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Representa a tabela de tarefas no banco de dados
@Entity(tableName = "tarefas")
public class Tarefa {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int usuarioId;
    public String titulo;
    public String descricao;
}