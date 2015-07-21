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
    public static boolean firstRun=false;
    private String topic;
    private static String data_storage;
    private static ArrayList<Topic> topicList=null;
    private ArrayList<Questions> questionList=null;

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

    public boolean removeTopic(){
        return !topicList.contains(this)||topicList.remove(this);
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
        FileOutputStream fout=null;
        ObjectOutputStream objOut=null;
        try {
            fout= new FileOutputStream(data_storage);
             objOut= new ObjectOutputStream(fout);
            objOut.writeObject(topicList);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(objOut!=null)
                try{
                    objOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if(fout!=null){
                try{
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void restoreResult(){
        FileInputStream fin=null;
        ObjectInputStream objIn=null;
        if(topicList !=null)
            return;
        if(new File(data_storage).isFile()) {
            try {
                fin = new FileInputStream(data_storage);
                objIn = new ObjectInputStream(fin);
                topicList = (ArrayList<Topic>) objIn.readObject();
                firstRun=false;
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
                firstRun=true;
                topicList=new ArrayList<>();
            } finally {
                if(objIn!=null)
                    try {
                        objIn.close();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                if(fin!=null)
                    try{
                        fin.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
        else{
            topicList=new ArrayList<>();
            firstRun=true;
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

    public static void removeTopic(int id){
        topicList.remove(id);
    }

    public static void removeTopic(String topic){
        topicList.remove(topicList.indexOf(new Topic(topic)));
    }
}
