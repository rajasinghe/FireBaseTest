package com.example.testproject76312;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder>{
    private ArrayList<Notes> notes;
    private onItemClickListener clickListener;

    public NotesAdapter(ArrayList<Notes> notes, onItemClickListener clickListener) {
        this.notes = notes;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.getDate().setText(notes.get(position).getDate());
            holder.getTitle().setText(notes.get(position).getTitle());
            holder.getCardView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NotesAdapter.this.clickListener.onItemClick(position);
                }
            });

    }

    @Override
    public int getItemCount() {
       return notes.size();
    }

    public static class ViewHolder  extends RecyclerView.ViewHolder{
        private final TextView date;
        private final TextView title;
        private final MaterialCardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date=(TextView) itemView.findViewById(R.id.notes_item_date);
            title=(TextView) itemView.findViewById(R.id.notes_item_title);
            cardView=(MaterialCardView) itemView.findViewById(R.id.item_card);
        }

        public TextView getDate() {
            return date;
        }

        public TextView getTitle() {
            return title;
        }

        public MaterialCardView getCardView() {
            return cardView;
        }
    }

    public interface onItemClickListener{
        void onItemClick(int position);
    }
}
