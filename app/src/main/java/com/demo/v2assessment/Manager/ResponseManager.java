package com.demo.v2assessment.Manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.demo.v2assessment.models.JsonResponse;
import com.google.gson.Gson;

public class ResponseManager {
    private static final String PREF_NAME = "ResponseDataPrefs";
    private static final String KEY_FORM_JSON = "form_response"; // unified key

    private static ResponseManager instance;
    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    private ResponseManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized ResponseManager getInstance(Context context) {
        if (instance == null) {
            instance = new ResponseManager(context.getApplicationContext());
        }
        return instance;
    }

    public void setToPrefs(JsonResponse jsonResponse) {
        if (jsonResponse == null || jsonResponse.getRecord() == null || jsonResponse.getRecord().isEmpty()) return;

        String json = gson.toJson(jsonResponse);
        sharedPreferences.edit().putString(KEY_FORM_JSON, json).apply();
    }

    public JsonResponse getFromPrefs() {
        String json = sharedPreferences.getString(KEY_FORM_JSON, null);
        if (json != null) {
            return gson.fromJson(json, JsonResponse.class);
        }
        return null;
    }
}

