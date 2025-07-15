package com.demo.v2assessment.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.demo.v2assessment.entity.FormSubmission;

import java.util.List;

@Dao
public interface FormSubmissionDao {
    @Insert
    void insert(FormSubmission submission);

    @Query("SELECT * FROM submitted_forms ORDER BY id DESC")
    List<FormSubmission> getAll();
}
