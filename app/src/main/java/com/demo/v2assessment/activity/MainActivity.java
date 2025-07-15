package com.demo.v2assessment.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.demo.v2assessment.Manager.ResponseManager;
import com.demo.v2assessment.controller.ApiController;
import com.demo.v2assessment.database.AppDatabase;
import com.demo.v2assessment.databinding.ActivityMainBinding;
import com.demo.v2assessment.entity.FormSubmission;
import com.demo.v2assessment.models.JsonResponse;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    private Context context;
    private ActivityMainBinding binding;
    private List<JsonResponse.Record> recordList;
    private Map<String, String> answersMap = new HashMap<>();
    private int currentIndex = 0;
    private JsonResponse jsonResponse;
    private List<JsonResponse.Option> currentDropdownOptions = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this;
        initViews();
        fetchApiAndInitialize();
    }

    private void initViews() {
        binding.skipBtn.setOnClickListener(v -> {
            JsonResponse.Record current = recordList.get(currentIndex);
            if (current.getSkip() != null && !"-1".equals(current.getSkip().getId())) {
                loadViewById(current.getSkip().getId());
            }
        });

        binding.nextBtn.setOnClickListener(v -> {
            JsonResponse.Record current = recordList.get(currentIndex);
            if (!saveCurrentAnswer(current)) {
                Toast.makeText(context, "Please answer before continuing", Toast.LENGTH_SHORT).show();
                return;
            }

            String nextId = null;

            if ("multipleChoice".equals(current.getType())) {
                int checkedId = binding.multipleChoiceGroup.getCheckedRadioButtonId();
                if (checkedId != -1) {
                    RadioButton selected = binding.multipleChoiceGroup.findViewById(checkedId);
                    JsonResponse.ReferTo tag = (JsonResponse.ReferTo) selected.getTag();
                    nextId = tag.getId();
                }
            } else if ("dropdown".equals(current.getType())) {
                int selectedPosition = binding.spinnerOption.getSelectedItemPosition();
                if (selectedPosition > 0 && currentDropdownOptions.size() > selectedPosition) {
                    JsonResponse.Option selectedOpt = currentDropdownOptions.get(selectedPosition);
                    if (selectedOpt.getReferTo() != null) {
                        nextId = selectedOpt.getReferTo().getId();
                    }
                }
                if (nextId == null && current.getReferTo() != null) {
                    nextId = current.getReferTo().getId(); // fallback
                }
            } else {
                if (current.getReferTo() != null) {
                    nextId = current.getReferTo().getId();
                }
            }

            if ("submit".equalsIgnoreCase(nextId)) {
                showSubmit();
            } else if (nextId != null) {
                loadViewById(nextId);
            }
        });

        binding.submitBtn.setOnClickListener(v -> {
            JsonResponse.Record current = recordList.get(currentIndex);
            saveCurrentAnswer(current);

            String answersJson = new Gson().toJson(answersMap);
            Log.d("SubmitAnswers", answersJson);

            AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "form_db").build();

            FormSubmission submission = new FormSubmission();
            submission.answers = answersJson;

            Executors.newSingleThreadExecutor().execute(() -> {
                db.submissionDao().insert(submission);

                runOnUiThread(() -> {
                    Toast.makeText(context, "Submitted!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, SubmittedDataActivity.class);
                    startActivity(intent);
                    finish();
                });
            });
        });


    }

    private boolean saveCurrentAnswer(JsonResponse.Record current) {
        String id = current.getId();
        String type = current.getType();

        switch (type) {
            case "multipleChoice":
                int checkedId = binding.multipleChoiceGroup.getCheckedRadioButtonId();
                if (checkedId == -1) return false;
                RadioButton selected = binding.multipleChoiceGroup.findViewById(checkedId);
                answersMap.put(id, selected.getText().toString());
                return true;

            case "textInput":
            case "numberInput":
                String text = binding.numberInput.getText() != null ? binding.numberInput.getText().toString().trim() : "";
                if (text.isEmpty()) return false;
                answersMap.put(id, text);
                return true;

            case "checkbox":
                List<String> selectedOptions = new ArrayList<>();
                if (binding.checkbox1.isChecked()) selectedOptions.add(binding.checkbox1.getText().toString());
                if (binding.checkbox2.isChecked()) selectedOptions.add(binding.checkbox2.getText().toString());
                if (binding.checkbox3.isChecked()) selectedOptions.add(binding.checkbox3.getText().toString());
                if (binding.checkbox4.isChecked()) selectedOptions.add(binding.checkbox4.getText().toString());
                if (selectedOptions.isEmpty()) return false;
                answersMap.put(id, String.join(",", selectedOptions));
                return true;


            case "dropdown":
                if (binding.spinnerOption.getAdapter() == null || binding.spinnerOption.getSelectedItem() == null) {
                    return false;
                }

                String choose = binding.spinnerOption.getSelectedItem().toString();
                if ("Choose an option".equals(choose) || choose.isEmpty()) return false;

                answersMap.put(id, choose);
                return true;

            case "camera":
                answersMap.put(id, "camera_clicked");
                return true;

            default:
                return false;
        }
    }

    private void fetchApiAndInitialize() {
        Call<JsonResponse> call = ApiController.getInstance()
                .getApi()
                .getJsonResponse();

        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    jsonResponse = response.body();
                    ResponseManager.getInstance(context).setToPrefs(jsonResponse);
                    jsonResponse = ResponseManager.getInstance(context).getFromPrefs();

                    if (jsonResponse != null) {
                        recordList = jsonResponse.getRecord();
                        if (recordList != null && !recordList.isEmpty()) {
                            currentIndex = 0;
                            loadViewByRecord(recordList.get(0));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                Toast.makeText(context, "API call failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadViewById(String id) {
        for (int i = 0; i < recordList.size(); i++) {
            if (recordList.get(i).getId().equals(id)) {
                currentIndex = i;
                loadViewByRecord(recordList.get(i));
                return;
            }
        }
        Toast.makeText(context, "Question not found!", Toast.LENGTH_SHORT).show();
    }

    private void loadViewByRecord(JsonResponse.Record record) {
        hideAllLayouts();
        if (record == null || record.getType() == null) return;

        binding.questionText.setText(record.getQuestion().getSlug());

        // Skip logic
        if (record.getSkip() != null && !"-1".equals(record.getSkip().getId())) {
            binding.skipBtn.setVisibility(View.VISIBLE);
        }

        String type = record.getType();

        switch (type) {
            case "multipleChoice":
                binding.multipleChoiceLayout.setVisibility(View.VISIBLE);
                binding.nextBtn.setVisibility(View.VISIBLE);
                binding.multipleChoiceGroup.removeAllViews();

                if (record.getOptions() != null) {
                    for (JsonResponse.Option opt : record.getOptions()) {
                        RadioButton rb = new RadioButton(context);
                        rb.setText(opt.getValue());
                        rb.setTag(opt.getReferTo()); // Store ReferTo object
                        binding.multipleChoiceGroup.addView(rb);
                    }
                }
                break;

            case "textInput":
                binding.numberInputLayout.setVisibility(View.VISIBLE);
                binding.numberInput.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.numberInput.setText("");
                binding.nextBtn.setVisibility(View.VISIBLE);
                break;

            case "numberInput":
                binding.numberInputLayout.setVisibility(View.VISIBLE);
                binding.numberInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                binding.numberInput.setText("");
                binding.nextBtn.setVisibility(View.VISIBLE);
                break;

            case "checkbox":
                binding.checkboxLayout.setVisibility(View.VISIBLE);
                clearCheckboxes();
                binding.nextBtn.setVisibility(View.VISIBLE);
                break;

            case "dropdown":
                binding.dropDownLayout.setVisibility(View.VISIBLE);
                binding.nextBtn.setVisibility(View.VISIBLE);

                currentDropdownOptions.clear(); // Clear previous

                List<JsonResponse.Option> dropOptions = record.getOptions();
                if (dropOptions != null && !dropOptions.isEmpty()) {
                    List<String> items = new ArrayList<>();
                    items.add("Choose an option");  // Hint option

                    currentDropdownOptions.add(null); // For index 0

                    for (JsonResponse.Option option : dropOptions) {
                        items.add(option.getValue());
                        currentDropdownOptions.add(option); // Keep track of options with referTo
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, items);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerOption.setAdapter(adapter);
                } else {
                    binding.spinnerOption.setAdapter(null);  // No options
                }
                break;



            case "camera":
                binding.cameraLayout.setVisibility(View.VISIBLE);
                binding.nextBtn.setVisibility(View.VISIBLE);
                break;

            default:
                Toast.makeText(context, "Unknown view type: " + type, Toast.LENGTH_SHORT).show();
        }

        // Show submit if `referTo` is "submit"
        if (record.getReferTo() != null && "submit".equalsIgnoreCase(record.getReferTo().getId())) {
            showSubmit();
        }
    }

    private void hideAllLayouts() {
        binding.multipleChoiceLayout.setVisibility(View.GONE);
        binding.numberInputLayout.setVisibility(View.GONE);
        binding.checkboxLayout.setVisibility(View.GONE);
        binding.cameraLayout.setVisibility(View.GONE);
        binding.dropDownLayout.setVisibility(View.GONE);

        binding.nextBtn.setVisibility(View.GONE);
        binding.skipBtn.setVisibility(View.GONE);
        binding.submitBtn.setVisibility(View.GONE);
    }

    private void clearCheckboxes() {
        binding.checkbox1.setChecked(false);
        binding.checkbox2.setChecked(false);
        binding.checkbox3.setChecked(false);
        binding.checkbox4.setChecked(false);
    }

    private void showSubmit() {
        binding.nextBtn.setVisibility(View.GONE);
        binding.skipBtn.setVisibility(View.GONE);
        binding.submitBtn.setVisibility(View.VISIBLE);
    }
}

