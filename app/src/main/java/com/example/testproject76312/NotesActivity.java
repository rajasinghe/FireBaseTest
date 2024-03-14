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

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, position+"", Toast.LENGTH_SHORT).show();
        //need to implement the notes data in the creating notes section
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityNotesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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
        String key=notesReference.child("users").child(user.getUid()).child("notes").push().getKey();
        Log.d(TAG, "createNote: "+key);
        Map<String,Object> childUpdates=new HashMap<>();
        childUpdates.put("/users/"+user.getUid()+"/notes/"+key,noteData);
        notesReference.updateChildren(childUpdates);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
       if(user!=null){ UID=user.getUid();
           Log.d(TAG, "onStart: "+UID);
           getNotesFromDbAndSetToRecycler();
       }else{
           Log.d(TAG, "onStart: "+"account not exists");
       };

    }
    public void getNotesFromDbAndSetToRecycler(){
        database=FirebaseDatabase.getInstance();
        //adapter initialization with empty data
        notesArrayList=new ArrayList<Notes>();
        NotesAdapter adapter=new NotesAdapter(notesArrayList,this);
        binding.notes.setLayoutManager(new LinearLayoutManager(this));
        binding.notes.setAdapter(adapter);
        //
        userNoteReference=database.getReference("users").child(UID).child("notes");
        userNoteReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               /* notesArrayList.clear();
                for (DataSnapshot notesnapShot:snapshot.getChildren()){
                    Log.d(TAG, "onDataChange: "+notesnapShot.getKey());
                    Notes note=new Notes(notesnapShot.child("date").getValue(String.class),notesnapShot.child("title").getValue(String.class),notesnapShot.child("note").getValue(String.class),notesnapShot.getKey());
                    notesArrayList.add(note);
                }
                setNotesToRecycler(notesArrayList);*/

                ///adding the change listener///
                userNoteReference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Log.d(TAG, "onChildAdded: "+snapshot.getValue().toString()+" key ="+snapshot.getKey());
                        Notes note=new Notes(snapshot.child("date").getValue(String.class),snapshot.child("title").getValue(String.class),snapshot.child("note").getValue(String.class),snapshot.getKey());
                        notesArrayList.add(note);
                        Log.d(TAG, "onChildAdded: "+ notesArrayList.size());
                        adapter.notifyItemInserted(notesArrayList.size()-1);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

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


}