package com.demo.v2assessment.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.demo.v2assessment.R;
import com.demo.v2assessment.database.AppDatabase;
import com.demo.v2assessment.databinding.ActivitySubmittedDataBinding;
import com.demo.v2assessment.entity.FormSubmission;

import java.util.List;

public class SubmittedDataActivity extends AppCompatActivity {
    ActivitySubmittedDataBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySubmittedDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, "form_db").allowMainThreadQueries().build();
        List<FormSubmission> list = db.submissionDao().getAll();

        StringBuilder builder = new StringBuilder();
        for (FormSubmission item : list) {
            builder.append("Form ID: ").append(item.id).append("\n");
            builder.append("Answers: ").append(item.answers).append("\n\n");
        }

        binding.textViewSubmissions.setText(builder.toString());

    }
}