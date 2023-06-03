package com.maciejgogulski.eventschedulingbackend.util;

import com.google.gson.*;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Singleton class for configuration of gson. // TODO Maybe not needed.
 */
@NoArgsConstructor
public class GsonWrapper {
    private static volatile Gson INSTANCE;

    public static Gson getInstance() {
        if (INSTANCE == null) {
            synchronized (Gson.class) {
                if (INSTANCE == null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    INSTANCE = new GsonBuilder()
                            .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, cont) -> {
                                String dateString = json.getAsString();
                                try {
                                    return dateFormat.parse(dateString);
                                } catch (ParseException e) {
                                    return null;
                                }
                            })
                            .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (date, typeOfSrc, cont) ->
                                    new JsonPrimitive(dateFormat.format(date))
                            )
                            .create();
                }
            }
        }
        return INSTANCE;
    }
}
