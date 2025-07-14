package com.demo.v2assessment;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.demo.v2assessment.Manager.ResponseManager;
import com.demo.v2assessment.controller.ApiController;
import com.demo.v2assessment.databinding.ActivityMainBinding;
import com.demo.v2assessment.models.JsonResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private Context context;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this;
        getJsonResponse();

    }
    private void getJsonResponse() {
        Call<JsonResponse> call = ApiController.getInstance().getApi().getJsonResponse();
        call.enqueue(new Callback<JsonResponse>() {

            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                if (response.isSuccessful()) {

                    JsonResponse jsonResponse = response.body();

                    ResponseManager.getInstance(context).setToPrefs(jsonResponse);

                    String id = jsonResponse.getRecord().get(0).getId();

                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
    }
    private void loadView(JsonResponse.Record record) {
        String type = record.getType();

        switch (type) {
            case "multipleChoice":

                break;

        }

        // Show skip if applicable
        if (!record.getSkip().getId().equals("-1")) {

        }
    }

}