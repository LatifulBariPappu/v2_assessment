package com.demo.v2assessment.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.demo.v2assessment.Dao.FormSubmissionDao;
import com.demo.v2assessment.entity.FormSubmission;

@Database(entities = {FormSubmission.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FormSubmissionDao submissionDao();
}
