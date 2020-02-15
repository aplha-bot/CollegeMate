package com.example.collegemate;

import android.net.Uri;

import java.util.ArrayList;

public class File {
    public static ArrayList<File> arr_file=new ArrayList<>();
    public String path;
    public Long id;
    public String uploaded;
    public String name;
    public File(){}

    public File(String uploaded,String name,Long id ,String path){
        this.id=id ;
        this.path=path;
        this.name = name;
        this.uploaded = uploaded;
    }

    public void add_file(){
        File.arr_file.add(this);
        Global.documentData.savedFile.add(this);
    }
}
