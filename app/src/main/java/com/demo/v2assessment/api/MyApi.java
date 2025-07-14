package com.demo.v2assessment.api;

import com.demo.v2assessment.models.JsonResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MyApi {
    @GET("v3/b/687374506063391d31aca23a")
    Call<JsonResponse> getJsonResponse();
}
