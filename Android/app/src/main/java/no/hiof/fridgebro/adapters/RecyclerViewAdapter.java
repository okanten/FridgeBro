package no.hiof.fridgebro.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import no.hiof.fridgebro.R;
import no.hiof.fridgebro.activities.AddActivity;
import no.hiof.fridgebro.fragments.RecyclerViewFragment;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> productName = new ArrayList<>();
    private ArrayList<String> productImage = new ArrayList<>();
    private Context mContext;
    private RecyclerViewFragment rcFrag;

    public RecyclerViewAdapter(ArrayList<String> productName, ArrayList<String> productImage, Context mContext) {
        this.productName = productName;
        this.productImage = productImage;
        this.mContext = mContext;
    }

    public RecyclerViewAdapter(ArrayList<String> productName, ArrayList<String> productImage, Context mContext, RecyclerViewFragment recyclerViewFragment) {
        this.productName = productName;
        this.productImage = productImage;
        this.mContext = mContext;
        this.rcFrag = recyclerViewFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem,viewGroup,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        Glide.with(mContext)
                .asBitmap()
                .load(productImage.get(i))
                .into(viewHolder.image);

        viewHolder.text.setText(productName.get(i));

        ImageButton deleteButton = (ImageButton) viewHolder.parentLayout.getViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int pos = viewHolder.getAdapterPosition();
                rcFrag.deleteItem(pos);
            }
        });


        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onClick listener for å gå videre til detaljvisningen til et produkt
                Intent intent = new Intent(mContext, AddActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("productName", productName);
                bundle.putSerializable("productImage", productImage);
                bundle.putInt("position", viewHolder.getAdapterPosition());
                intent.putExtras(bundle);
                ((Activity) mContext).startActivityForResult(intent, 100);
            }
        });
    }



    @Override
    public int getItemCount() {
        return productName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView text;
        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.recyclerImageView);
            text = itemView.findViewById(R.id.recylerTextView);
            parentLayout = itemView.findViewById(R.id.parentLayoutList);
        }
    }
}
