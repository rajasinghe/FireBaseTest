package com.example.testproject76312;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.testproject76312.databinding.ActivityNotesBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class NotesActivity extends AppCompatActivity implements NotesAdapter.onItemClickListener{
    FirebaseDatabase database;
    ActivityNotesBinding binding;
    String TAG="NotesActivity";
    NotesAdapter notesListAdapter;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    DatabaseReference userNoteReference;
    ArrayList<Notes> notesArrayList;
    String UID;
    Notes currentNote;
    int currentSelectedNoteIndex;
    boolean isNewNote=false;

    DatabaseReference currentSelectedNoteReference;



    @Override
    public void onItemClick(int position) {
       // Toast.makeText(this, position+"", Toast.LENGTH_SHORT).show();
        currentNote=notesArrayList.get(position);
        currentSelectedNoteIndex=position;
        setNoteToLayout(currentNote);
        isNewNote=false;
        //the current selected note reference is  created to delete data it can also be used to upadte the specific note
        currentSelectedNoteReference=userNoteReference.child(currentNote.getId());
    }
    void setCurrentSelectedNoteReferemce(){
        currentSelectedNoteReference=userNoteReference.child(currentNote.getId());
    }

    public void setNoteToLayout(Notes note){
        binding.noteTitle.setText(note.getTitle());
        binding.noteBody.setText(note.getNote());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityNotesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeNewNote();
            }
        });
        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

        binding.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote();
            }
        });

    }
    void initializeNewNote(){
        currentNote=new Notes();
        binding.noteBody.setText("");
        binding.noteTitle.setText("");
        isNewNote=true;
    }

    void saveNote(){
        Date currentDate=new Date();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        String dateStamp=simpleDateFormat.format(currentDate);
        currentNote.setTitle(binding.noteTitle.getText().toString());
        currentNote.setNote(binding.noteBody.getText().toString());
        currentNote.setDate(dateStamp);
        //checking if its a new note or selected note
        if(isNewNote){
            createNewNote();
        }else{
            updateNote();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //user initialization
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        //database initialization
        database=FirebaseDatabase.getInstance();
        //adapter initialization with empty data
        notesArrayList=new ArrayList<Notes>();
        NotesAdapter adapter=new NotesAdapter(notesArrayList,this);
        binding.notes.setLayoutManager(new LinearLayoutManager(this));
        binding.notes.setAdapter(adapter);
        //



        //checking if the user exists and getting the userid
       if(user!=null){
           UID=user.getUid();
           //get the current user user Note Reference
           userNoteReference=database.getReference("users").child(UID).child("notes");
           Log.d(TAG, "onStart: "+UID);
           //geting the users current notes to the recycler
           getNotesFromDbAndSetToRecycler(adapter);
           //initialize a new note for user to insert note at initially
           initializeNewNote();
       }else{
           Log.d(TAG, "onStart: "+"account not exists");
       };

    }
    public void getNotesFromDbAndSetToRecycler(NotesAdapter adapter){
        userNoteReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userNoteReference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Log.d(TAG, "onChildAdded: "+snapshot.getValue().toString()+" key ="+snapshot.getKey());
                        Notes note=new Notes(snapshot.child("date").getValue(String.class),snapshot.child("title").getValue(String.class),snapshot.child("note").getValue(String.class),snapshot.getKey());
                        notesArrayList.add(note);
                        currentNote=note;
                        currentSelectedNoteReference=userNoteReference.child(currentNote.getId());
                        Log.d(TAG, "onChildAdded: "+ notesArrayList.size());
                        currentSelectedNoteIndex=notesArrayList.size()-1;
                        adapter.notifyItemInserted(notesArrayList.size()-1);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Log.d(TAG, "onChildChanged: "+snapshot.getValue().toString());
                        Notes updatedNote=new Notes(snapshot.child("date").getValue(String.class),snapshot.child("title").getValue(String.class),snapshot.child("note").getValue(String.class),snapshot.getKey());
                        //validate if required eg- map the snapshot id is equal to updated Current notes Id
                        notesArrayList.set(currentSelectedNoteIndex,updatedNote);
                        //notify the Notes object changed in the araylist at specified postition AKA currentSelectedNoteIndex
                        Log.d(TAG, "onChildChanged: "+notesArrayList.get(currentSelectedNoteIndex));
                        adapter.notifyItemChanged(currentSelectedNoteIndex);
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "onChildRemoved: "+snapshot.getValue().toString());
                        notesArrayList.remove(currentSelectedNoteIndex);
                        adapter.notifyItemRemoved(currentSelectedNoteIndex);
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                ////
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error);
            }
        });
    }

    public void updateNote(){
        String currentId=currentNote.getId();
        DatabaseReference notesReference=userNoteReference.child(currentNote.getId());
        currentNote=new Notes(currentNote.getDate(),binding.noteTitle.getText().toString(),binding.noteBody.getText().toString());
        notesReference.setValue(currentNote).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "onSuccess: update sucessfull");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: failded to update database");
            }
        });
        //here i ve set this because to keep the structure of the notes object straight as possible without creating a local variable for the notes ID
        currentNote.setId(currentId);
    }

    void createNewNote(){
        userNoteReference.push().setValue(currentNote).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: success");
                    //after setting the id less Object to the firebase

                }
            }
        });
    }

    void deleteNote(){
        currentSelectedNoteReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "onSuccess: deleted sucessfully ");
                currentSelectedNoteReference=null;
                currentSelectedNoteIndex=-1;
                currentNote=null;
                initializeNewNote();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: delete failed");
            }
        });
    }

}