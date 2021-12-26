package pl.konarzewski.uboze.adapter;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import pl.konarzewski.uboze.R;
import pl.konarzewski.uboze.entity.Imige;
import pl.konarzewski.uboze.ui.main.Engine;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {

    private Engine e;

    public CardStackAdapter(Engine e) {
        this.e = e;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.word_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(e.getImige(position));
    }

    @Override
    public int getItemCount() {
        return e.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_place_holder);
        }

        void setData(Imige imige) {
            image.setImageBitmap(BitmapFactory.decodeFile(imige.getPath()));
        }
    }
}