package com.example.lembraai.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.lembraai.model.Usuario;

// Define as operações de acesso aos dados dos usuários
@Dao
public interface UsuarioDao {

    // Cadastra um novo usuário
    @Insert
    void inserir(Usuario usuario);

    // Atualiza os dados de um usuário
    @Update
    void atualizar(Usuario usuario);

    // Busca um usuário pelo e-mail
    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    Usuario buscarPorEmail(String email);

    // Busca um usuário pelo ID
    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    Usuario buscarPorId(int id);
}