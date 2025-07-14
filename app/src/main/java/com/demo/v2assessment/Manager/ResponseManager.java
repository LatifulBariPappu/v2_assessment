package com.demo.v2assessment.Manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.demo.v2assessment.models.JsonResponse;
import com.google.gson.Gson;

public class ResponseManager {
        private static final String PREF_NAME = "ResponseDataPrefs";
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

        // Save full JsonResponse
        public void setToPrefs(JsonResponse jsonResponse) {
            if (jsonResponse == null || jsonResponse.getRecord() == null || jsonResponse.getRecord().isEmpty()) return;

            String key = jsonResponse.getRecord().get(0).getId();
            String json = gson.toJson(jsonResponse);

            sharedPreferences.edit().putString(key, json).apply();
        }

        // Retrieve FormResponse by record ID
        public JsonResponse getFromPrefs(String recordId) {
            String json = sharedPreferences.getString(recordId, null);
            if (json != null) {
                return gson.fromJson(json, JsonResponse.class);
            }
            return null;
        }

}
