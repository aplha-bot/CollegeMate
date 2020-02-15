package com.example.collegemate;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Application;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.Dimension;
import com.google.zxing.WriterException;

import java.nio.file.Files;
import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getExternalStorageDirectory;
import static android.os.Environment.getExternalStorageState;

public class Books extends AppCompatActivity {

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

        @NonNull
        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v =getLayoutInflater().inflate(R.layout.books_recycler,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, final int position) {
            final File data = Global.documentData.savedFile.get(position);
            holder.name.setText(data.name);
            holder.uploaded.setText(data.uploaded);

            holder.download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(data.path  != null){
                        java.io.File file = new java.io.File(data.path);


                        // Get URI and MIME type of file
                        Intent myIntent = new Intent(Intent.ACTION_VIEW);
                        myIntent.setData(Uri.fromFile(file));
                        Intent j = Intent.createChooser(myIntent, "Choose an application to open with:");
                        startActivity(j);
                    }else{
                        StorageReference ref = mStorageRef.child(Global.documentData.userInfo.uid+"/Books/"+data.id+".pdf");
                        getFileUrl(ref);
                    }

                }
            });


            QRGEncoder qrgEncoder = new QRGEncoder(Global.documentData.userInfo.uid+"/Books/"+data.id, null, QRGContents.Type.TEXT, 380);
            qrgEncoder.setColorBlack(Color.BLACK);
            qrgEncoder.setColorWhite(Color.WHITE);

            final Bitmap bitmap = qrgEncoder.getBitmap();
            // Setting Bitmap to ImageView

            holder.qrimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Books.this);
                    ImageView imageView = new ImageView(Books.this);
                    ViewGroup.LayoutParams params  = new ViewGroup.LayoutParams(400,400);
                    imageView.setLayoutParams(params);
                    imageView.setImageBitmap(bitmap);
                    builder.setView(imageView);
                    builder.show();
                }
            });

        }

        @Override
        public int getItemCount() {
            return Global.documentData.savedFile.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            ImageView preview;
            ImageButton qrimage;
            Button download;
            TextView name, uploaded;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                preview = itemView.findViewById(R.id.books_recycler_image_preview);
                qrimage = itemView.findViewById(R.id.books_recycler_imagebutton);
                download = itemView.findViewById(R.id.books_recycler_download);
                name = itemView.findViewById(R.id.books_recycler_name);
                uploaded = itemView.findViewById(R.id.books_recycler_uploaded);
            }
        }
    }


    StorageReference mStorageRef;
    DatabaseReference databaseReference;
    RecyclerView rv;

    static RecyclerViewAdapter adapter;

    Integer STORAGE_WRITE_REQUEST_CODE = 100;
    Integer STORAGE_READ_REQUET_CODE = 100;
    FloatingActionButton upload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
        Toolbar toolbar = findViewById(R.id.books_toolbar);
        setSupportActionBar(toolbar);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(Books.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_WRITE_REQUEST_CODE);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(Books.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_READ_REQUET_CODE);
        }

        mStorageRef = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(Global.documentData.userInfo.uid);
        //Toast.makeText(this, Global.documentData.userinfo.uid, Toast.LENGTH_SHORT).show();

        //Toolbar


        //Refrencing objects
        rv = findViewById(R.id.books_rv);

        upload = findViewById(R.id.books_add);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogAddBook dialog = new DialogAddBook(Books.this);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                dialog.show(ft,"hello");
            }
        });

        if(Global.documentData.savedFile == null){
            Global.documentData.savedFile = new ArrayList<>();
        }

        rv.setLayoutManager(new LinearLayoutManager(Books.this));
        adapter = new RecyclerViewAdapter();
        rv.setAdapter(adapter);

        //Refrencing the object
        if(Global.documentData.savedFile == null){
            Global.documentData.savedFile = new ArrayList<>();
        }

    }

    /*public void getPDFFile() {
        Intent intent=new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select PDF FILE"),1);

    } */



    public void uploadPDFFile(Uri data) {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        String location_file=data.getPath();


         final File upload_file=new File(Global.documentData.userInfo.name,DialogAddBook.nameString,System.currentTimeMillis(),data.getPath());
        //String filext=location_file.substring(location_file.lastIndexOf("."));
        //Toast.makeText(this, ""+data, Toast.LENGTH_LONG).show();
        //System.out.println(data);
        StorageReference reference=mStorageRef.child(Global.documentData.userInfo.uid+"/Books/"+upload_file.id+".pdf");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uri=taskSnapshot.getStorage().getDownloadUrl();
                        while(!uri.isComplete());
                        Uri url=uri.getResult();
                        uploadPDF uploadPDF=new uploadPDF(url.toString());
                        databaseReference.child(databaseReference.push().getKey()).setValue(uploadPDF);
                        Toast.makeText(Books.this, "File Uploaded", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                        Global.documentData.savedFile.add(upload_file);
                        Global.userRef.update("savedFile",Global.documentData.savedFile);
                        adapter.notifyDataSetChanged();


                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                double progress=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                progressDialog.setMessage("Uploaded: "+(int)progress+"%");
            }
        });

    }
    private void viewAllFiles(Long id){
        StorageReference listRef = mStorageRef.child(Global.documentData.userInfo.uid+"/Books/");

        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            int flag=0;
                            //System.out.println(File.arr_file.toString());
                            for(int i=0;i< File.arr_file.size();i++){
                                if(item.getName().substring(0,item.getName().lastIndexOf('.')-1).equals(((Long)File.arr_file.get(i).id).toString())){
                                    flag++;
                                }
                            }
                            if(flag==0){
                                getFileUrl(item);
                                File down_file=new File();
                                down_file.id=Long.valueOf(item.getName().substring(0,item.getName().lastIndexOf('.')-1));
                                System.out.println(down_file.id);
                                down_file.path=DIRECTORY_DOWNLOADS;
                                down_file.uploaded = Global.documentData.userInfo.name;
                                down_file.name = "om";
                                Global.documentData.savedFile.add(down_file);
                                Global.userRef.update("savedFile",Global.documentData.savedFile);
                            }

                            //Opening File
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });
    }
    private void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {

        DownloadManager downloadmanager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadmanager.enqueue(request);

    }
    private void getFileUrl(StorageReference ref){
        final String name=ref.getName();
        int flag = 0 ;
        for(int i=0;i< Global.documentData.savedFile.size();i++){
            if(ref.getName().substring(0,ref.getName().lastIndexOf('.')).equals(((Long)Global.documentData.savedFile.get(i).id).toString())){
                flag++;
            }
        }
        if(flag==0){
            //getFileUrl(ref);
            File down_file=new File();
            down_file.id=Long.valueOf(ref.getName().substring(0,ref.getName().lastIndexOf('.')));
            System.out.println(down_file.id);
            down_file.path=DIRECTORY_DOWNLOADS;
            down_file.uploaded = Global.documentData.userInfo.name;
            down_file.name = "om";
            Global.documentData.savedFile.add(down_file);
            Global.userRef.update("savedFile",Global.documentData.savedFile);
        }else{
            Toast.makeText(this, "File Already Exists", Toast.LENGTH_SHORT).show();


            return;
        }
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url =uri.toString();
                downloadFile(Books.this,name.substring(0,name.lastIndexOf('.')),name.substring(name.lastIndexOf('.')+1),DIRECTORY_DOWNLOADS,url);
            }
        });
    }

    private View.OnClickListener downloadEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
