package com.example.collegemate;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

public class SignUpInfoDialog extends DialogFragment {

    TextInputEditText name;
    Button submit;
    ListView year,batch,branch;
    RadioGroup gender;

    int yearid = -1;
    int branchid = -1;
    int batchid = -1;
    int genderid = -1;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.dialog_signup_info,container,false);
        //Setting up toolbar

        Toolbar toolbar = rootview.findViewById(R.id.signup_info_dialog_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                FirebaseAuth.getInstance().signOut();
            }
        });

        //Refrencing
        name= rootview.findViewById(R.id.signup_info_dialog_name);
        submit = rootview.findViewById(R.id.signup_info_dialog_submit);
        year = rootview.findViewById(R.id.signup_info_dialog_year);
        batch = rootview.findViewById(R.id.signup_info_dialog_batch);
        branch = rootview.findViewById(R.id.signup_info_dialog_branches);
        gender = rootview.findViewById(R.id.signup_info_dialog_gender);

        //Loading Data

        ArrayAdapter<String> branch_adapter = new ArrayAdapter<String>(getActivity(),R.layout.support_simple_spinner_dropdown_item,Global.branches);
        ArrayAdapter<String> batch_adapter = new ArrayAdapter<String>(getActivity(),R.layout.support_simple_spinner_dropdown_item,Global.batches);
        branch.setAdapter(branch_adapter);
        batch.setAdapter(batch_adapter);


        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.signup_info_dialog_male: genderid = 1; break;
                    case R.id.signup_info_dialog_female: genderid = 2; break;
                }
            }
        });

        year.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                yearid = position;
                parent.getChildAt(position).setBackgroundColor(Color.GRAY);
            }
        });
        branch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                branchid = position;
                parent.getChildAt(position).setBackgroundColor(Color.GRAY);
            }
        });
        batch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                batchid = position;
                parent.getChildAt(position).setBackgroundColor(Color.GRAY);
            }
        });






        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(getArguments()!=null){
                    getDialog().dismiss();
                }else{
                    FirebaseAuth mAuth =FirebaseAuth.getInstance();
                    FirebaseUser user = mAuth.getCurrentUser();
                    FirebaseFirestore db= FirebaseFirestore.getInstance();

                    Global.ModalClasses.UserInfoModal data = new Global.ModalClasses.UserInfoModal("",user.getUid(),"",name.getText().toString(),genderid,branchid,batchid,yearid);
                    Map<String,Global.ModalClasses.UserInfoModal>payLoad = new HashMap<>();
                    payLoad.put("userInfo",data);
                    db.collection("users").document(user.getUid()).set(payLoad).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(new Intent(getActivity(),LoadScreen.class));
                        }
                    });
                }


            }
        });

        //Checking Edit profile

        if(getArguments() !=null){
            setupValues();
        }


        return rootview;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }


    private boolean isEmtpy(){

        if(yearid == -1){
            return true;
        }else if(branchid == -1){
            return true;
        }else if(batchid == -1){
            return true;
        }else if(genderid == -1){
            return true;
        }

        return false;

    }

    private void setupValues(){
        Global.ModalClasses.UserInfoModal data = Global.documentData.userInfo;
        name.setText(data.name);
    }
}
