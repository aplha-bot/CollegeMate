package com.example.collegemate;

import android.graphics.Bitmap;
import android.util.Pair;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Global {
    static String webClientId = "709110177436-gv2u5bvti6a6v27ffdffkvmu15jlu42d.apps.googleusercontent.com";
    static UserData documentData;
    static Bitmap profileimage;


    static class ModalClasses{

        static class AssignmentModal{
            public String details;
            public Long timestamp;
            public String subject;

            public AssignmentModal(){

            }

            public AssignmentModal(String details, Long timestamp, String subject) {
                this.details = details;
                this.timestamp = timestamp;
                this.subject = subject;
            }
        }

        static class UserInfoModal{
            public String phone;
            public String uid;
            public  String password;
            public String name;
            public Integer gender;
            public Integer branch;
            public Integer batch;
            public Integer year;

            public UserInfoModal(String phone, String uid, String password, String name, Integer gender, Integer branch, Integer batch, Integer year) {
                this.phone = phone;
                this.uid = uid;
                this.password = password;
                this.name = name;
                this.gender = gender;
                this.branch = branch;
                this.batch = batch;
                this.year = year;
            }

            public UserInfoModal(){
                //No Argument Constructor
            }

        }

        static class TimeTableModal{
            public TimeTableModal(String subname, String venue, int day, int time) {
                this.subname =subname;
                this.venue = venue;
                this.day = day;
            }

            public TimeTableModal(){

            }

            public String subname;
            public String venue;
            public int day;
            public int time;
        }

        static class AttendanceModal{
            public Integer attended;
            public Integer missed;

            public AttendanceModal(){

            }

            public AttendanceModal(Integer attended, Integer missed) {
                this.attended = attended;
                this.missed = missed;
            }
        }
    }

    //Class to store all the data from UserDocument from Firebase
    static class UserData{
        public Map<String,List<ModalClasses.TimeTableModal>> timetable;
        public ModalClasses.UserInfoModal userInfo;
        public List<String> subjects;
        public Map<String,ModalClasses.AttendanceModal> attendance;
        public List<ModalClasses.AssignmentModal> assignment;
        public ArrayList<File> savedFile;

        public UserData(){

        }

        public UserData(Map<String, List<ModalClasses.TimeTableModal>> timetable, ModalClasses.UserInfoModal userinfo,List<String> subjects, ArrayList<File> file) {
            this.timetable = timetable;
            this.userInfo = userinfo;
            this.subjects = subjects;
            this.savedFile = file;
        }

    }
    static DocumentReference userRef;
    static DocumentReference userFieldRef;
    static int NUMBER_OF_CLASSES = 56;

    static List<String> branches;
    static List<String> batches;



}
