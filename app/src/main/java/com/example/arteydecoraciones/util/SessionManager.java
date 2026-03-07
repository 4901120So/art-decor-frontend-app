package com.example.arteydecoraciones.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.arteydecoraciones.model.Cliente;
import com.google.gson.Gson;

public class SessionManager {
    private static final String PREF_NAME = "ArteyDecoSession";
    private static final String KEY_CLIENT = "cliente";
    private static final String KEY_LOGGED_IN = "logged_in";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;
    private final Gson gson;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
        gson = new Gson();
    }

    public void saveCliente(Cliente cliente) {
        editor.putString(KEY_CLIENT, gson.toJson(cliente));
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.apply();
    }

    public Cliente getCliente() {
        String json = prefs.getString(KEY_CLIENT, null);
        if (json == null) return null;
        return gson.fromJson(json, Cliente.class);
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
