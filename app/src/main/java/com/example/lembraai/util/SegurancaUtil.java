package com.example.lembraai.util;

import org.mindrot.jbcrypt.BCrypt;

public class SegurancaUtil {

    // Criptografa a senha antes de salvar no banco
    public static String hashPassword(String senha) {
        return BCrypt.hashpw(senha, BCrypt.gensalt());
    }

    // Confere se a senha digitada corresponde ao hash salvo
    public static boolean verificarSenha(String senha, String senhaHash) {
        return BCrypt.checkpw(senha, senhaHash);
    }
}