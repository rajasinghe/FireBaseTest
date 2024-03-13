package com.example.testproject76312;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder>{
    private ArrayList<Notes> notes;
    public NotesAdapter(ArrayList<Notes> notes){
            this.notes=notes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.getDate().setText((CharSequence) notes.get(position).getDate());
            holder.getTitle().setText(notes.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
       return notes.size();
    }

    public static class ViewHolder  extends RecyclerView.ViewHolder{
        private final TextView date;
        private final TextView title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.notes_item_date);
            title=itemView.findViewById(R.id.notes_item_title);
        }

        public TextView getDate() {
            return date;
        }

        public TextView getTitle() {
            return title;
        }
    }
}
