package com.demo.v2assessment.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "submitted_forms")
public class FormSubmission {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "answers")
    public String answers;
}
