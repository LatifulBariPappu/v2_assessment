package com.demo.v2assessment.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonResponse {
    @SerializedName("record")
    private List<Record> record;

    public List<Record> getRecord() {
        return record;
    }
    public  class Record {
        @SerializedName("id")
        private String id;

        @SerializedName("skip")
        private Skip skip;

        @SerializedName("type")
        private String type;

        @SerializedName("options")
        private List<Option> options;

        @SerializedName("referTo")
        private ReferTo referTo;

        @SerializedName("question")
        private Question question;

        @SerializedName("validations")
        private Validation validations;

        public String getId() { return id; }
        public Skip getSkip() { return skip; }
        public String getType() { return type; }
        public List<Option> getOptions() { return options; }
        public ReferTo getReferTo() { return referTo; }
        public Question getQuestion() { return question; }
        public Validation getValidations() { return validations; }
    }
    public class Skip {
        @SerializedName("id")
        private String id;
        public String getId() { return id; }
    }

    public class ReferTo {
        @SerializedName("id")
        private String id;
        public String getId() { return id; }
    }

    public class Question {
        @SerializedName("slug")
        private String slug;
        public String getSlug() { return slug; }
    }

    public class Option {
        @SerializedName("value")
        private String value;

        @SerializedName("referTo")
        private ReferTo referTo; // Nullable

        public String getValue() { return value; }
        public ReferTo getReferTo() { return referTo; }


    }

    public class Validation {
        @SerializedName("regex")
        private String regex;
        public String getRegex() { return regex; }
    }
}



