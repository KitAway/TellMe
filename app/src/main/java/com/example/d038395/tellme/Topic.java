package com.example.d038395.tellme;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by d038395 on 2015-07-20.
 */

public class Topic implements Serializable{
    private String topic;
    private static String data_storage;
    private static ArrayList<Topic> topicList;
    private ArrayList<Questions> questionList;

    public Topic(String topic) {
        this.topic = topic;
        questionList=new ArrayList<>();
    }

    public String getTopic() {
        return topic;
    }

    public static ArrayList<Topic> getTopicList() {
        return topicList;
    }

    public ArrayList<Questions> getQuestionList() {
        return questionList;
    }

    public boolean addQuestion(Questions questions){
        return questionList.contains(questions)||questionList.add(questions);
    }

    public boolean addTopic(){
        return topicList.contains(this) || topicList.add(this);
    }

    public ArrayList<String> getQuestions(){
        ArrayList<String> arrayList = new ArrayList<>();
        for (Questions questions:questionList){
            arrayList.add(questions.getQuestion());
        }

        return arrayList;
    }

    public static void initialize(String dir){
        data_storage=dir+"topicList";
        restoreResult();
    }
    public static void storeResult(){
        try {
            FileOutputStream fout = new FileOutputStream(data_storage);
            ObjectOutputStream objOut = new ObjectOutputStream(fout);
            objOut.writeObject(topicList);

            objOut.close();
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void restoreResult(){
        if(new File(data_storage).isFile()) {
            try {
                FileInputStream fin = new FileInputStream(data_storage);
                ObjectInputStream objIn = new ObjectInputStream(fin);
                topicList = (ArrayList<Topic>) objIn.readObject();

                objIn.close();
                fin.close();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
        else if(topicList==null){
            topicList=new ArrayList<>();
        }
    }

    @Override
    public boolean equals(Object o) {
        return topic.equals(o.toString());
    }

    @Override
    public String toString() {
        return topic;
    }

    public static void addTopic(String topic){
        Topic topic1 = new Topic(topic);
        if (!topicList.contains(topic1))
            topicList.add(topic1);
    }
}
