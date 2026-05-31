package com.qrfood.servlet;

import com.qrfood.util.QRCodeUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * Gera imagem PNG do QR Code para uma mesa.
 *
 * GET /qrcode?mesa=1&restauranteId=1
 *   → retorna image/png com o QR Code da URL do cardápio
 */
@WebServlet("/qrcode")
public class QRCodeServlet extends HttpServlet {

    private static final int SIZE = 300; // px

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String mesa          = req.getParameter("mesa");
        String restauranteId = req.getParameter("restauranteId");

        if (mesa == null || restauranteId == null
                || mesa.isBlank() || restauranteId.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Parâmetros mesa e restauranteId são obrigatórios");
            return;
        }

        // Monta URL que o QR Code vai conter
        String base = req.getScheme() + "://" + req.getServerName()
                      + ":" + req.getServerPort()
                      + req.getContextPath();
        String url  = base + "/cardapio/index.jsp?mesa=" + mesa
                      + "&restauranteId=" + restauranteId;

        try {
            byte[] png = QRCodeUtil.gerarQRCodePng(url, SIZE, SIZE);
            resp.setContentType("image/png");
            resp.setContentLength(png.length);
            // Cache de 1 hora – o QR Code para a mesma mesa não muda
            resp.setHeader("Cache-Control", "public, max-age=3600");
            resp.getOutputStream().write(png);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Erro ao gerar QR Code: " + e.getMessage());
        }
    }
}