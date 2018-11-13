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
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import no.hiof.fridgebro.R;
import no.hiof.fridgebro.activities.AddActivity;
import no.hiof.fridgebro.fragments.RecyclerViewFragment;
import no.hiof.fridgebro.models.Item;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<Item> productList = new ArrayList<>();
    private Context mContext;
    private RecyclerViewFragment rcFrag;
    private boolean isOnShoppingList;
    private ImageButton deleteButton;
    private View view;
    private int layoutID;

    public RecyclerViewAdapter(ArrayList<Item> productList, Context mContext) {
        this.productList = productList;
        this.mContext = mContext;
    }

    public RecyclerViewAdapter(ArrayList<Item> productList, Context mContext, RecyclerViewFragment recyclerViewFragment, boolean isOnShoppingList) {
        this.productList = productList;
        this.mContext = mContext;
        this.rcFrag = recyclerViewFragment;
        this.isOnShoppingList = isOnShoppingList;
    }


    // Ubrukt constructor. Kan være nyttig senere.
    public RecyclerViewAdapter(ArrayList<Item> productList, Context mContext, RecyclerViewFragment recyclerViewFragment, int itemLayout) {
        this.layoutID = itemLayout;
        if (layoutID == R.layout.layout_listitem) {
            isOnShoppingList = false;
        } else {
            isOnShoppingList = true;
        }
        this.productList = productList;
        this.mContext = mContext;
        this.rcFrag = recyclerViewFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (isOnShoppingList) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_slistitem, viewGroup,false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem, viewGroup,false);
        }
        //view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutID, viewGroup,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        Glide.with(mContext)
                .asBitmap()
                .apply(new RequestOptions().placeholder(R.drawable.ic_placeholder_image))
                .load(productList.get(i).getImageUrl())
                .into(viewHolder.image);

        if (!isOnShoppingList) {
            viewHolder.expdate.setText(productList.get(i).getExpDate());
            viewHolder.price.setText(mContext.getResources().getString(R.string.currency, productList.get(i).getItemPrice()));
        }
        viewHolder.text.setText(productList.get(i).getItemName());
        // henter ut string fra strings.xml med placeholders


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
        CheckBox checkBox;
        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            if (isOnShoppingList) {
                image = itemView.findViewById(R.id.shoppingListImageView);
                text = itemView.findViewById(R.id.shoppingListTextView);
                checkBox = itemView.findViewById(R.id.shoppingListCheckBox);
                deleteButton = itemView.findViewById(R.id.shoppingListDeleteButton);
                parentLayout = itemView.findViewById(R.id.shoppingListParentLayout);
            } else {
                image = itemView.findViewById(R.id.recyclerImageView);
                text = itemView.findViewById(R.id.recylerTextView);
                price = itemView.findViewById(R.id.txtPriceAdd);
                expdate = itemView.findViewById(R.id.txtExpDate);
                deleteButton = itemView.findViewById(R.id.deleteButton);
                parentLayout = itemView.findViewById(R.id.parentLayoutList);
            }
        }
    }
}
