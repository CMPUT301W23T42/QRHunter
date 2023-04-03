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

/**
 * An adapter to display the image user taken on adding the QR code.
 */
public class ImageDisplayAdapter extends RecyclerView.Adapter<ImageDisplayAdapter.ImageHolder> {

    ArrayList<byte[]> images;


    public ImageDisplayAdapter(ArrayList<byte[]> images) {
        this.images = images;
    }

    /**
     * Create view
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return
     */
    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_display_card,null,false);
        return new ImageHolder(view);
    }

    /**
     * Bind image to the holder
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(images.get(position),0,images.get(position).length);
        holder.photo.setImageBitmap(bitmap);
    }

    /**
     * Get the size of item.
     * @return
     */
    @Override
    public int getItemCount() {
        return images.size();
    }

    /**
     * The class that holds the image
     */
    class  ImageHolder extends RecyclerView.ViewHolder{

        ImageView photo;

        public ImageHolder(@NonNull View itemView) {
            super(itemView);

            photo = itemView.findViewById(R.id.image_taken);
        }
    }
}
