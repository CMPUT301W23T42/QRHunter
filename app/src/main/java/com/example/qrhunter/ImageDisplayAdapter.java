package com.example.qrhunter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ImageDisplayAdapter extends RecyclerView.Adapter<ImageDisplayAdapter.ImageHolder> {

    ArrayList<byte[]> images;


    public ImageDisplayAdapter(ArrayList<byte[]> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_display_card,null,false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(images.get(position),0,images.get(position).length);
        holder.photo.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    class  ImageHolder extends RecyclerView.ViewHolder{

        ImageView photo;

        public ImageHolder(@NonNull View itemView) {
            super(itemView);

            photo = itemView.findViewById(R.id.image_taken);
        }
    }
}
