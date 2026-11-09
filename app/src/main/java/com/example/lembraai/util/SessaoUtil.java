package com.example.lembraai.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessaoUtil {

    private static final String NOME_ARQUIVO = "sessao";
    private static final String CHAVE_USUARIO = "usuario_id";

    // Salva o ID do usuário que fez login
    public static void salvarLogin(Context context, int usuarioId) {
        SharedPreferences preferencias = context.getSharedPreferences(
                NOME_ARQUIVO,
                Context.MODE_PRIVATE
        );

        preferencias.edit()
                .putInt(CHAVE_USUARIO, usuarioId)
                .apply();
    }

    // Retorna o ID do usuário conectado
    public static int getUsuarioLogadoId(Context context) {
        SharedPreferences preferencias = context.getSharedPreferences(
                NOME_ARQUIVO,
                Context.MODE_PRIVATE
        );

        return preferencias.getInt(CHAVE_USUARIO, -1);
    }

    // Encerra a sessão do usuário
    public static void logout(Context context) {
        SharedPreferences preferencias = context.getSharedPreferences(
                NOME_ARQUIVO,
                Context.MODE_PRIVATE
        );

        preferencias.edit()
                .remove(CHAVE_USUARIO)
                .apply();
    }
}