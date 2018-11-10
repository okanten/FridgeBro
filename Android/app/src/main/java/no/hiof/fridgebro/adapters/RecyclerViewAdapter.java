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
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import no.hiof.fridgebro.R;
import no.hiof.fridgebro.activities.AddActivity;
import no.hiof.fridgebro.fragments.RecyclerViewFragment;
import no.hiof.fridgebro.models.Item;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<Item> productList = new ArrayList<>();
    private ArrayList<Item> shoppingListProducts = new ArrayList<>();
    private Context mContext;
    private RecyclerViewFragment rcFrag;

    public RecyclerViewAdapter(ArrayList<Item> productList, Context mContext) {
        this.productList = productList;
        this.shoppingListProducts = shoppingListProducts;
        this.mContext = mContext;
    }

    public RecyclerViewAdapter(ArrayList<Item> productList, Context mContext, RecyclerViewFragment recyclerViewFragment) {
        this.productList = productList;
        this.shoppingListProducts = shoppingListProducts;
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
                .load(productList.get(i).getImageUrl())
                .into(viewHolder.image);

        viewHolder.text.setText(productList.get(i).getItemName());
        viewHolder.expdate.setText(productList.get(i).getExpDate());
        // henter ut string fra strings.xml med placeholders
        viewHolder.price.setText(mContext.getResources().getString(R.string.currency, productList.get(i).getItemPrice()));
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
                bundle.putParcelableArrayList("productList", productList);
                bundle.putInt("position", viewHolder.getAdapterPosition());
                intent.putExtras(bundle);
                ((Activity) mContext).startActivityForResult(intent, 100);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView text;
        TextView price;
        TextView expdate;
        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.recyclerImageView);
            text = itemView.findViewById(R.id.recylerTextView);
            price = itemView.findViewById(R.id.txtPriceAdd);
            expdate = itemView.findViewById(R.id.txtExpDate);

            parentLayout = itemView.findViewById(R.id.parentLayoutList);
        }
    }
}
