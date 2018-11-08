package no.hiof.fridgebro.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import no.hiof.fridgebro.R;
import no.hiof.fridgebro.fragments.ContextMenuFragment;
import no.hiof.fridgebro.models.Item;

public class ContextMenuAdapter extends RecyclerView.Adapter<ContextMenuAdapter.ViewHolder> {

    private ArrayList<Item> queryResult = new ArrayList<>();
    private Context mContext;
    private ContextMenuFragment cmFrag;
    private int pos;

    public int getPos() {
        return pos;
    }

    public ContextMenuAdapter(ArrayList<Item> queryResult, Context mContext) {
        this.queryResult = queryResult;
        this.mContext = mContext;
    }

    public ContextMenuAdapter(ArrayList<Item> queryResult, Context mContext, ContextMenuFragment cmFrag) {
        this.queryResult = queryResult;
        this.mContext = mContext;
        this.cmFrag = cmFrag;
    }

    @NonNull
    @Override
    public ContextMenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_context_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ContextMenuAdapter.ViewHolder viewHolder, int i) {
        Glide.with(mContext)
                .asBitmap()
                .load(queryResult.get(i).getImageUrl())
                .into(viewHolder.image);

        viewHolder.text.setText(queryResult.get(i).getItemName());
        viewHolder.txtPrice.setText(queryResult.get(i).getItemPrice() + " kr");
        viewHolder.txtBrand.setText(queryResult.get(i).getItemBrand());

        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pos = viewHolder.getAdapterPosition();
                cmFrag.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return queryResult.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView text;
        TextView txtPrice;
        TextView txtBrand;
        ConstraintLayout parentLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.recyclerImageView);
            text = itemView.findViewById(R.id.recylerTextView);
            txtPrice = itemView.findViewById(R.id.txtPriceAdd);
            txtBrand = itemView.findViewById(R.id.txtBrand);

            parentLayout = itemView.findViewById(R.id.parentLayoutList);

        }
    }
}
