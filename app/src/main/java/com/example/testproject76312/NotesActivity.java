package com.example.testproject76312;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.testproject76312.databinding.ActivityNotesBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        Intent intent=getIntent();

        binding.addNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNote("title","note");
            }
        });
        database=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=database.getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String  value=snapshot.getValue(String.class);
                Log.d(TAG, "onDataChange: "+value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error);
            }
        });
    }
    void createNote(String title,String note){
        DatabaseReference notes=database.getReference("users");
        //String noteId=notes.child(UID).child("notes").push().getKey();
       /* Map<String, Object> data=new HashMap<>();
        data.put(UID,data);
        notes.updateChildren(data);*/
        notes.setValue("dadsa");
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
}