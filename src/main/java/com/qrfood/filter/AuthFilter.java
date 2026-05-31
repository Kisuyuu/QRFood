package com.qrfood.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * Bloqueia acesso sem sessão autenticada nos endpoints sensíveis.
 * O filtro deixa o LoginServlet de fora propositalmente.
 */
@WebFilter(urlPatterns = {"/admin/*", "/pedidos", "/pedido/status"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest)  request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        boolean autenticado = session != null
                && session.getAttribute("restauranteId") != null;

        if (!autenticado) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"error\":\"Sessão expirada. Faça login novamente.\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}