package com.example.yellow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterVideo extends RecyclerView.Adapter<AdapterVideo.HolderVideo> {

    //dentist ID
    String userID, dentistID;

    //context
    private Context context;
    //array list
    private ArrayList<ModelVideo> videoArrayList;

    //constructor
    public AdapterVideo(Context context, ArrayList<ModelVideo> videoArrayList) {
        this.context = context;
        this.videoArrayList = videoArrayList;
    }

    @NonNull
    @Override
    public HolderVideo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();
        userID = userF.getUid();
        dentistID = "ARZWrJlFEgToMUYScCWexldGPmC3";
        if (!(userID.equals(dentistID))) {
            //inflate layout row_video.xml
            View view = LayoutInflater.from(context).inflate(R.layout.row_video_user, parent, false);
            return new HolderVideo(view);
        } else {
            //inflate layout row_video.xml
            View view = LayoutInflater.from(context).inflate(R.layout.row_video, parent, false);
            return new HolderVideo(view);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(@NonNull HolderVideo holder, int position) {
        //Get, format, set data, handle clicks, etc

        //Get data
        ModelVideo modelVideo = videoArrayList.get(position);

        String id = modelVideo.getId();
        String title = modelVideo.getTitle();
        String trimester = modelVideo.getTrimester();
        String timestamp = modelVideo.getTimestamp();
        String videoUrl = modelVideo.getVideoUrl();

        //format timestamp e.g. 07/09/2022 02:08 PM
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String formattedDateTime = DateFormat.format("dd/MM/yyyy K:mm a", calendar).toString();

        //set data
        holder.titleTv.setText(title);
        holder.trimesterTv.setText(trimester);
        holder.timeTv.setText(formattedDateTime);
        setVideoUrl(modelVideo, holder);

        if (userID.equals(dentistID)) {
            //handle click, delete video
            holder.deleteFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //show alert dialog, confirm to delete
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete")
                            .setMessage("Are you sure you want to delete video: " + title)
                            .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //confirmed to delete
                                    deleteVideo(modelVideo);
                                }
                            })
                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //cancelled delete
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
                }
            });
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setVideoUrl(ModelVideo modelVideo, HolderVideo holder) {
        //show progress
        holder.progressBar.setVisibility(View.VISIBLE);

        //get video url
        String videoUrl = modelVideo.getVideoUrl();

        //Media controller for play, pause, seekbar, timer, etc
        MediaController mediaController = new MediaController(context);
        mediaController.setAnchorView(holder.videoView);

        Uri videoUri = Uri.parse(videoUrl);
        holder.videoView.setMediaController(mediaController);
        holder.videoView.setVideoURI(videoUri);

        holder.videoView.requestFocus();
        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                //video is ready to play
                mediaPlayer.start();
            }
        });

        holder.videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                //to check if buffering, rendering, etc
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: {
                        //RENDERING STARTED
                        holder.progressBar.setVisibility(View.VISIBLE);
                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START: {
                        //BUFFERING STARTED
                        holder.progressBar.setVisibility(View.VISIBLE);
                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END: {
                        //BUFFERING ENDED
                        holder.progressBar.setVisibility(View.GONE);
                        return true;
                    }
                }
                return false;
            }
        });

        holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.start();//restart video if completed
            }
        });

    }

    private void deleteVideo(ModelVideo modelVideo) {
        String videoId = modelVideo.getId();
        String videoUrl = modelVideo.getVideoUrl();

        //1| DELETE FROM FIREBASE STORAGE
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(videoUrl);
        reference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //deleted from firebase storgge

                        //2| DELETE FROM REALTIME DATABASE
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Videos");
                        databaseReference.child(videoId)
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //deleted from realtime db
                                        Toast.makeText(context, "Video deleted successfully...", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //failed deleting from realtime db
                                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed deleting from firebase storage, show error message
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        //return size of list
        return videoArrayList.size();
    }

    //Video holder class, holds, inits the UI views
    class HolderVideo extends RecyclerView.ViewHolder {

        //UI view of row_video.xml
        VideoView videoView;
        TextView titleTv, trimesterTv, timeTv;
        ProgressBar progressBar;
        FloatingActionButton deleteFab;

        public HolderVideo(@NonNull View itemView) {
            super(itemView);

            //init UI Views of row_video.xml
            videoView = itemView.findViewById(R.id.videoView);
            titleTv = itemView.findViewById(R.id.titleTv);
            trimesterTv = itemView.findViewById(R.id.trimesterTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            progressBar = itemView.findViewById(R.id.progressBar);

            if (userID.equals(dentistID)) {
                deleteFab = itemView.findViewById(R.id.deleteFab);
            }


        }
    }

}
