package com.qrfood.servlet;

import com.google.gson.Gson;
import com.qrfood.dao.RestauranteDAO;
import com.qrfood.dao.UsuarioDAO;

/**
 * Recebe email e senha em JSON e tenta autenticar o restaurante.
 * Se o login der certo, cria sessão HTTP e devolve o restauranteId.
 */
import com.qrfood.model.Restaurante;
import com.qrfood.model.Usuario;
import com.qrfood.util.SenhaUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

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

            LoginRequest login =
                    gson.fromJson(json.toString(), LoginRequest.class);

            UsuarioDAO usuarioDAO = new UsuarioDAO();

            Usuario usuario =
                    usuarioDAO.buscarPorEmail(login.email);

            if (usuario == null) {

                resp.setStatus(401);

                resp.getWriter().write("""
                    {"erro":"Usuário não encontrado"}
                """);

                return;
            }

            String hash =
                    SenhaUtil.hashSenha(
                            login.senha,
                            usuario.getSalt()
                    );

            if (!hash.equals(usuario.getSenhaHash())) {

                resp.setStatus(401);

                resp.getWriter().write("""
                    {"erro":"Senha inválida"}
                """);

                return;
            }

            RestauranteDAO restauranteDAO =
                    new RestauranteDAO();

            Restaurante restaurante =
                    restauranteDAO.buscarPorUsuario(
                            usuario.getId()
                    );

            HttpSession session =
                    req.getSession(true);

            session.setAttribute(
                    "usuarioId",
                    usuario.getId()
            );

            session.setAttribute(
                    "restauranteId",
                    restaurante.getId()
            );

            resp.setContentType("application/json");

            resp.getWriter().write(
                    """
                    {
                        "sucesso":true,
                        "restauranteId":%d
                    }
                    """.formatted(restaurante.getId())
            );

        } catch (Exception e) {

            try {

                resp.setStatus(500);

                resp.getWriter().write(
                        "{\"erro\":\"" +
                                e.getMessage() +
                                "\"}"
                );

            } catch (Exception ignored) {}
        }
    }

    private static class LoginRequest {
        String email;
        String senha;
    }
}