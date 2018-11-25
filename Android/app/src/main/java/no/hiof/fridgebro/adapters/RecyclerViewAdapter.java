package no.hiof.fridgebro.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import no.hiof.fridgebro.R;
import no.hiof.fridgebro.activities.AddActivity;
import no.hiof.fridgebro.fragments.RecyclerViewFragment;
import no.hiof.fridgebro.models.Item;

import static no.hiof.fridgebro.activities.MainActivity.REQUEST_CODE_EDIT_ITEM;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<Item> productList = new ArrayList<>();
    private ArrayList<Item> currentSelectedItems = new ArrayList<>();
    private Context mContext;
    private RecyclerViewFragment rcFrag;
    private boolean isOnShoppingList;
    private ImageButton deleteButton;
    private View view;
    private int layoutID;
    private CheckBox checkBox;




    public RecyclerViewAdapter(ArrayList<Item> productList, Context mContext) {
        this.productList = productList;
        this.mContext = mContext;
        setHasStableIds(true);
    }

    public RecyclerViewAdapter(ArrayList<Item> productList, Context mContext, RecyclerViewFragment recyclerViewFragment, boolean isOnShoppingList) {
        this.productList = productList;
        this.mContext = mContext;
        this.rcFrag = recyclerViewFragment;
        this.isOnShoppingList = isOnShoppingList;
        setHasStableIds(true);
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
        setHasStableIds(true);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (isOnShoppingList) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_slistitem, viewGroup, false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem, viewGroup, false);
        }
        //view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutID, viewGroup,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }




    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Glide.with(mContext)
                .asBitmap()
                .apply(new RequestOptions().placeholder(R.drawable.ic_placeholder_image))
                .load(productList.get(i).getImageUrl())
                .into(viewHolder.image);

        checkBox = viewHolder.checkBox;

        if (!isOnShoppingList) {
            if(productList.get(i).getExpDate().equals("99/99/9999")) {
                viewHolder.expdate.setText("");
            } else {
                viewHolder.expdate.setText(productList.get(i).getExpDate());
            }
            if(!productList.get(i).getItemPrice().equals(""))
                viewHolder.price.setText(mContext.getResources().getString(R.string.currency, productList.get(i).getItemPrice()));
        }
        viewHolder.text.setText(productList.get(i).getItemName());
        // henter ut string fra strings.xml med placeholders


        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int pos = viewHolder.getAdapterPosition();
                Log.d("position", "Position: " + pos);
                Item item = rcFrag.getProductList().get(pos);
                //productList.remove(pos);
                rcFrag.deleteItem(item);
            }
        });


        if (isOnShoppingList) {
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int pos = viewHolder.getAdapterPosition();
                    if (isChecked && !currentSelectedItems.contains(productList.get(pos))){
                        currentSelectedItems.add(productList.get(pos));
                        System.out.println(currentSelectedItems.size());
                    }
                    else if(!isChecked && currentSelectedItems.contains(productList.get(pos))){
                        currentSelectedItems.remove(productList.get(pos));
                    }
                    Log.i("yolo", currentSelectedItems.toString());

                }
            });
        }
        

        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onClick listener for å gå videre til detaljvisningen til et produkt
                Intent intent = new Intent(mContext, AddActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("productList", productList);
                bundle.putInt("position", viewHolder.getAdapterPosition());
                intent.putExtras(bundle);
                ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE_EDIT_ITEM);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public ArrayList<Item> getCurrentSelectedItems() {
        return currentSelectedItems;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView text;
        TextView price;
        TextView expdate;
        ConstraintLayout parentLayout;
        CheckBox checkBox;


        ViewHolder(@NonNull View itemView) {
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

