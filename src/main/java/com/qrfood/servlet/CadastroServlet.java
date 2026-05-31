package com.qrfood.servlet;

import com.google.gson.Gson;
import com.qrfood.dao.UsuarioDAO;
import com.qrfood.model.Usuario;

/**
 * Cria conta de restaurante com email, senha e nome.
 * Gera hash/salt, salva usuário e cria restaurante associado.
 */
import com.qrfood.util.ConexaoBD;
import com.qrfood.util.SenhaUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/cadastro")
public class CadastroServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            StringBuilder json = new StringBuilder();
            try (BufferedReader br = req.getReader()) {
                String linha;
                while ((linha = br.readLine()) != null) {
                    json.append(linha);
                }
            }

            CadastroRequest cadastro = gson.fromJson(json.toString(), CadastroRequest.class);

            if (cadastro.nome == null || cadastro.email == null || cadastro.senha == null ||
                cadastro.nome.isBlank() || cadastro.email.isBlank() || cadastro.senha.isBlank()) {
                resp.setStatus(400);
                resp.getWriter().write("{\"erro\":\"Todos os campos são obrigatórios\"}");
                return;
            }

            if (cadastro.senha.length() < 6) {
                resp.setStatus(400);
                resp.getWriter().write("{\"erro\":\"Senha deve ter no mínimo 6 caracteres\"}");
                return;
            }

            // Verificar se email já existe
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            if (usuarioDAO.buscarPorEmail(cadastro.email) != null) {
                resp.setStatus(409);
                resp.getWriter().write("{\"erro\":\"Email já cadastrado\"}");
                return;
            }

            // Gerar salt e hash da senha
            String salt = SenhaUtil.gerarSaltHex();
            String senhaHash = SenhaUtil.hashSenha(cadastro.senha, salt);

            // Inserir usuário
            String sqlUsuario = "INSERT INTO Usuario (nome, email, senha_hash, salt) VALUES (?, ?, ?, ?)";
            
            try (Connection c = ConexaoBD.getConnection();
                 PreparedStatement ps = c.prepareStatement(sqlUsuario, PreparedStatement.RETURN_GENERATED_KEYS)) {
                
                ps.setString(1, cadastro.nome);
                ps.setString(2, cadastro.email);
                ps.setString(3, senhaHash);
                ps.setString(4, salt);
                ps.executeUpdate();

                long usuarioId;
                try (var rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        usuarioId = rs.getLong(1);
                    } else {
                        throw new RuntimeException("Erro ao criar usuário");
                    }
                }

                // Criar restaurante automaticamente
                String sqlRestaurante = "INSERT INTO Restaurante (user_id, nome, descricao) VALUES (?, ?, ?)";
                try (PreparedStatement ps2 = c.prepareStatement(sqlRestaurante)) {
                    ps2.setLong(1, usuarioId);
                    ps2.setString(2, cadastro.nome);
                    ps2.setString(3, "Restaurante " + cadastro.nome);
                    ps2.executeUpdate();
                }
            }

            resp.setContentType("application/json");
            resp.getWriter().write("{\"sucesso\":true,\"mensagem\":\"Conta criada com sucesso!\"}");

        } catch (Exception e) {
            try {
                resp.setStatus(500);
                resp.getWriter().write("{\"erro\":\"" + e.getMessage() + "\"}");
            } catch (Exception ignored) {}
        }
    }

    private static class CadastroRequest {
        String nome;
        String email;
        String senha;
    }
}