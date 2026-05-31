package com.qrfood.util;

import com.google.gson.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Fábrica de Gson com suporte a LocalDateTime.
 * Use em todos os Servlets e no WebSocket para garantir serialização consistente.
 */
public class GsonFactory {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static Gson create() {
        return new GsonBuilder()
            .registerTypeAdapter(
                LocalDateTime.class,
                (JsonSerializer<LocalDateTime>)
                    (src, t, c) -> new JsonPrimitive(src.format(FMT))
            )
            .registerTypeAdapter(
                LocalDateTime.class,
                (JsonDeserializer<LocalDateTime>)
                    (json, t, c) -> LocalDateTime.parse(json.getAsString(), FMT)
            )
            .create();
    }
}