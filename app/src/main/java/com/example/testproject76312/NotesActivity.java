package com.example.testproject76312;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.testproject76312.databinding.ActivityNotesBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NotesActivity extends AppCompatActivity {
    FirebaseDatabase database;
    ActivityNotesBinding binding;
    String TAG="NotesActivity";
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    String UID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityNotesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database=FirebaseDatabase.getInstance();
        init();
        getNotesFromDb();
        binding.addNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NotesActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onClick: clicked");
                createNote(binding.noteTitle.getText().toString(),binding.noteBody.getText().toString());
            }
        });



    }
    void createNote(String title,String note){
        DatabaseReference notesReference=database.getReference();
        Date currentDate=new Date();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        String dateStamp=simpleDateFormat.format(currentDate);
        Notes noteData=new Notes(dateStamp,title,note);
        notesReference.child("users").child(user.getUid()).child("notes").push().setValue(noteData);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
       if(user!=null){ UID=user.getUid();
           Log.d(TAG, "onStart: "+UID);
       };
    }
    public void getNotesFromDb(){
        database.getReference("users").child(user.getUid()).child("notes").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Log.d(TAG, "onComplete: "+String.valueOf(task.getResult().getValue()));
            }
        });
    }
    void init(){
        DatabaseReference databaseReference=database.getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // String  value=snapshot.getValue(String.class);
                // Log.d(TAG, "onDataChange: "+value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error);
            }
        });
    }


}