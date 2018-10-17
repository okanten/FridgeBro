package no.hiof.fridgebro;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> productName = new ArrayList<>();
    private ArrayList<String> productImage = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<String> productName, ArrayList<String> productImage, Context mContext) {
        this.productName = productName;
        this.productImage = productImage;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem,viewGroup,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Glide.with(mContext)
                .asBitmap()
                .load(productImage.get(i))
                .into(viewHolder.image);

        viewHolder.text.setText(productName.get(i));

        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onClick listener for å gå videre til detaljvisningen til et produkt
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
