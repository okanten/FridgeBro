package no.hiof.fridgebro.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Item implements Parcelable, Comparable<Item> {
    private String itemName;
    private String itemPrice;
    private String barcode;
    private String imageUrl;
    private String itemBrand;
    private String expDate;
    private String itemUid;


    public Item() {

    }

    public Item(String itemName, String itemPrice, String barcode, String imageUrl, String itemBrand) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.barcode = barcode;
        this.imageUrl = imageUrl;
        this.itemBrand = itemBrand;
    }



    public Item(String itemName, String itemPrice, String barcode, @Nullable String imageUrl, @Nullable String itemBrand, String expDate) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.barcode = barcode;
        this.imageUrl = imageUrl;
        this.itemBrand = itemBrand;
        this.expDate = expDate;

    }

    public Item(String itemName, String itemPrice, String barcode, @Nullable String imageUrl, @Nullable String itemBrand, String expDate, @Nullable String itemUid) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.barcode = barcode;
        this.imageUrl = imageUrl;
        this.itemBrand = itemBrand;
        this.expDate = expDate;
        this.itemUid = itemUid;
    }

    /*
    public Item(String categoryName, String itemName, String itemPrice, String barcode, String imageUrl) {
        //super(categoryName);
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.barcode = barcode;
        this.imageUrl = imageUrl;
    }

    public Item(String categoryName, String itemName, String itemPrice, String barcode, String imageUrl, String expDate) {
        //super(categoryName);
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.barcode = barcode;
        this.imageUrl = imageUrl;
        this.expDate = expDate;
    }*/

    protected Item(Parcel in) {
        itemName = in.readString();
        itemPrice = in.readString();
        barcode = in.readString();
        expDate = in.readString();
        imageUrl = in.readString();
        itemBrand = in.readString();
        itemUid = in.readString();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public Date getExpDateAsDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
        try {
            return sdf.parse(getExpDate());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getItemBrand() {
        return itemBrand;
    }

    public void setItemBrand(String itemBrand) {
        this.itemBrand = itemBrand;
    }

    public String getItemUid() {
        return itemUid;
    }

    public void setItemUid(String itemUid) {
        this.itemUid = itemUid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemName);
        dest.writeString(itemPrice);
        dest.writeString(barcode);
        dest.writeString(expDate);
        dest.writeString(imageUrl);
        dest.writeString(itemBrand);
        dest.writeString(itemUid);
    }

    @Override
    public int compareTo(@NonNull Item item) {
        // TODO: Sorterer ikke på desimal - må fikses. Egen klasse?
//        int compareTo = (int) Double.parseDouble(item.getItemPrice());
        if (item.getExpDateAsDate().after(this.getExpDateAsDate())) {
            return -1;
        } else if (item.getExpDateAsDate().before(this.getExpDateAsDate())){
            return 1;
        } else {
            return 0;
        }
    }

}
