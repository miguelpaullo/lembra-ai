package com.example.lembraai.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Representa a tabela de usuários no banco de dados
@Entity(tableName = "usuarios")
public class Usuario {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nome;
    public String email;
    public String senhaHash;
    public String fotoPath;
}