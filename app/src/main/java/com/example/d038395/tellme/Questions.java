package com.example.d038395.tellme;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.Serializable;

/**
 * Created by d038395 on 2015-07-20.
 */
public class Questions implements Serializable{
    private String question;
    private String answer;
    private String filename;

    public File getPath(Context context){
        if(filename!=null)
            return new File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC),filename);
        else
            return null;
    }

    public Questions(String question) {
        this.question = question;
    }

    public Questions(String question, String filename) {
        this.question = question;
        this.filename = filename;
    }

    public Questions(String question, String answer, String filename) {
        this.question = question;
        this.answer = answer;
        this.filename = filename;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        return question;
    }

    @Override
    public boolean equals(Object o) {
        return question.equals(o.toString());
    }
}
