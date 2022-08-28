package com.example.yellow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddVideoActivity extends AppCompatActivity {

    //Drop down menu for trimester input
    String[] items = {"1", "2", "3", "General"};
    AutoCompleteTextView trimesterAuto;
    ArrayAdapter<String> adapterItems;

    //Other xml items
    TextInputLayout titleBox, trimesterBox;
    VideoView videoView;
    Button uploadVideoBtn;
    FloatingActionButton pickVideoFab;

    //For camera and gallery
    private static final int VIDEO_PICK_GALLERY_CODE = 100;
    private static final int VIDEO_PICK_CAMERA_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    //camera permissions
    private String[] cameraPermissions;
    //uri of picked video
    private Uri videoUri = null;

    private ProgressDialog progressDialog;

    private String title, trimester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);

        //Drop down menu for trimester input
        trimesterAuto = findViewById(R.id.trimesterAuto);
        adapterItems = new ArrayAdapter<String>(this, R.layout.trimester_list_item, items);
        trimesterAuto.setAdapter(adapterItems);
        trimesterAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
            }
        });

        titleBox = findViewById(R.id.titleBox);
        trimesterBox = findViewById(R.id.trimesterBox);
        videoView = findViewById(R.id.videoView);
        uploadVideoBtn = findViewById(R.id.uploadVideoBtn);
        pickVideoFab = findViewById(R.id.pickVideoFab);

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Uploading video");
        progressDialog.setCanceledOnTouchOutside(false);

        //camera permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //pick video from camera/gallery
        pickVideoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoPickDialog();
            }
        });

        //upload video
        uploadVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = titleBox.getEditText().getText().toString().trim();
                trimester = trimesterBox.getEditText().getText().toString().trim();
                if (TextUtils.isEmpty(title)) {
                    titleBox.setError("Title is required");
                    titleBox.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(trimester)) {
                    titleBox.setError(null);
                    titleBox.setErrorEnabled(false);
                    trimesterBox.setError("Please select trimester");
                    trimesterBox.requestFocus();
                    return;
                } else if (videoUri == null) {
                    titleBox.setError(null);
                    titleBox.setErrorEnabled(false);
                    trimesterBox.setError(null);
                    trimesterBox.setErrorEnabled(false);
                    Toast.makeText(AddVideoActivity.this, "Pick a video to upload...", Toast.LENGTH_SHORT).show();
                } else {
                    titleBox.setError(null);
                    titleBox.setErrorEnabled(false);
                    trimesterBox.setError(null);
                    trimesterBox.setErrorEnabled(false);
                    //upload video function call
                    uploadVideoFirebase();
                }
            }
        });

    }

    private void uploadVideoFirebase() {
        //show progress
        progressDialog.show();

        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        String fuserID = fuser.getUid();

        //timestamp
        String timestamp = "" + System.currentTimeMillis();

        //file path and name is firebase storage
        String filePathAndName = "Videos/" + "video_" + timestamp;

        //storage reference
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        //upload video
        storageReference.putFile(videoUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Video uploaded, get url of uploaded video
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();
                        if (uriTask.isSuccessful()){
                            //uri of uploaded video is received

                            //add video details to realtime database
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", ""+timestamp);
                            hashMap.put("userID", ""+fuserID);
                            hashMap.put("title", ""+title);
                            hashMap.put("trimester", ""+trimester);
                            hashMap.put("timestamp", ""+timestamp);
                            hashMap.put("videoUrl", ""+downloadUri);

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Videos");
                            reference.child(timestamp)
                                    .setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //Video details added to realtime db
                                            progressDialog.dismiss();
                                            Toast.makeText(AddVideoActivity.this, "Video uploaded...", Toast.LENGTH_SHORT).show();
                                            //refresh activity without the "blink"
                                            finish();
                                            overridePendingTransition(0, 0);
                                            startActivity(getIntent());
                                            overridePendingTransition(0, 0);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //failed to add details to realtime database
                                            progressDialog.dismiss();
                                            Toast.makeText(AddVideoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed to upload to storage
                        progressDialog.dismiss();
                        Toast.makeText(AddVideoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void videoPickDialog() {
        //options to display in dialog
        String[] options = {"Camera", "Gallery"};

        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Video From")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            //camera clicked
                            if (!checkCameraPermission()) {
                                //camera permission not allowed, request it
                                requestCameraPermission();
                            } else {
                                //permission already allowed, take picture
                                videoPickCamera();
                            }
                        } else if (i == 1) {
                            //gallery clicked
                            videoPickGallery();
                        }
                    }
                }).show();
    }

    private void requestCameraPermission() {
        //request camera permission
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED;

        return result1 && result2;
    }

    private void videoPickGallery() {
        //pick video from gallery - intent

        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Videos"), VIDEO_PICK_GALLERY_CODE);
    }

    private void videoPickCamera() {
        //pick video from camera - intent

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, VIDEO_PICK_CAMERA_CODE);
    }

    private void setVideoToVideoView() {
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        //set media controller to video view
        videoView.setMediaController(mediaController);
        //set video uri
        videoView.setVideoURI(videoUri);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.pause();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    //check permission allowed or not
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        // both permission allowed
                        videoPickCamera();
                    } else {
                        //both or one of those denied
                        Toast.makeText(this, "Camera & Storage permission are required", Toast.LENGTH_SHORT).show();
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //call after picking video from camera/gallery
        if (resultCode == RESULT_OK) {
            if (requestCode == VIDEO_PICK_GALLERY_CODE) {
                videoUri = data.getData();
                //show picked video in View Video
                setVideoToVideoView();
            } else if (requestCode == VIDEO_PICK_CAMERA_CODE) {
                videoUri = data.getData();
                //show picked video in View Video
                setVideoToVideoView();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Return to previous page button
    public void goBack(View view) {
        Intent intent = new Intent(AddVideoActivity.this, DentistProfile.class);
        startActivity(intent);
    }
}