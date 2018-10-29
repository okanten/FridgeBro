package no.hiof.olaka;

import android.support.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dongliu.requests.Requests;
import net.dongliu.requests.Session;

import java.io.IOException;
import java.util.*;

/***
 * @author Ola Kanten
 * @description An API-wrapper for NorgesGruppens API.
 */

// TODO: Rense opp
public class NorgesGruppenAPI {
    private int storeID;
    private Session session;
    private static final String BEGIN_IMAGE_URL = "https://res.cloudinary.com/norgesgruppen/image/upload/c_pad,b_transparent,f_auto,h_640,q_50,w_640/";
    /*
        Spar - 1210
        Joker - 1220
        Meny - 1300
    */

    public NorgesGruppenAPI(int storeID) {
        this.storeID = storeID;
        this.session = Requests.session();
    }

    public String getTitle(@Nullable String ISBN, @Nullable JsonObject obj) {
        if (obj == null) {
            obj = fixTheUglyJson(ISBN);
        } else {
            obj = addContentData(obj);
        }
        return obj.get("title").getAsString();
    }

    public String getPrice(@Nullable String ISBN, @Nullable JsonObject obj) {
        if (obj == null) {
            obj = fixTheUglyJson(ISBN);
        } else {
            obj = addContentData(obj);
        }
        String originalPrice = obj.get("pricePerUnitOriginal").getAsString().replace(".", ",");
        String pricePerUnit = obj.get("pricePerUnit").getAsString().replace(".", ",");
        if (originalPrice != pricePerUnit) {
            //System.out.println("Tilbud!!");
        }
        return originalPrice;
    }

    public String getImageURL(@Nullable String ISBN, @Nullable JsonObject obj) {
        if (obj == null) {
            obj = fixTheUglyJson(ISBN);
        } else {
            obj = addContentData(obj);
        }
        String imageURL = BEGIN_IMAGE_URL + obj.get("imageName").getAsString();
        return imageURL;
    }

    public String getShoppingListGroupName(@Nullable String ISBN, @Nullable JsonObject obj) {
        if (obj == null) {
            obj = fixTheUglyJson(ISBN);
        } else {
            obj = addContentData(obj);
        }
        return obj.get("shoppingListGroupName").getAsString();
    }

    public String getCategoryName(@Nullable String ISBN, @Nullable JsonObject obj) {
        if (obj == null) {
            obj = fixTheUglyJson(ISBN);
        } else {
            obj = addContentData(obj);
        }
        return obj.get("categoryName").getAsString();
    }



    public Boolean getProductByWeightSoldAsItem(@Nullable String ISBN, @Nullable JsonObject obj) {
        if (obj == null) {
            obj = fixTheUglyJson(ISBN);
        } else {
            obj = addContentData(obj);
        }
        return obj.get("productByWeightSoldAsItem").getAsBoolean();
    }

    public String getBrand(@Nullable String ISBN, @Nullable JsonObject obj) {
        if (obj == null) {
            obj = fixTheUglyJson(ISBN);
        } else {
            obj = addContentData(obj);
        }
        return obj.get("brand").getAsString();
    }

    public Boolean getContainsAlcohol(@Nullable String ISBN, @Nullable JsonObject obj) {
        if (obj == null) {
            obj = fixTheUglyJson(ISBN);
        } else {
            obj = addContentData(obj);
        }
        return obj.get("containsAlcohol").getAsBoolean();
    }

    public Boolean getIsOffer(@Nullable String ISBN, @Nullable JsonObject obj) {
        if (obj == null) {
            obj = fixTheUglyJson(ISBN);
        } else {
            obj = addContentData(obj);
        }
        return obj.get("isOffer").getAsBoolean();
    }

    public float getWeight(@Nullable String ISBN, @Nullable JsonObject obj) {
        if (obj == null) {
            obj = fixTheUglyJson(ISBN);
        } else {
            obj = addContentData(obj);
        }
        return obj.get("weight").getAsFloat();
    }

    public String getCalcUnit(@Nullable String ISBN, @Nullable JsonObject obj) {
        if (obj == null) {
            obj = fixTheUglyJson(ISBN);
        } else {
            obj = addContentData(obj);
        }
        return obj.get("calcUnit").getAsString();
    }

    public String getCalcUnitType(@Nullable String ISBN, @Nullable JsonObject obj) {
        if (obj == null) {
            obj = fixTheUglyJson(ISBN);
        } else {
            obj = addContentData(obj);
        }
        return obj.get("calcUnitType").getAsString();
    }

    public String getISBN(@Nullable String ISBN, @Nullable JsonObject obj) {
        if (obj == null) {
            obj = fixTheUglyJson(ISBN);
        }
        return obj.get("contentId").getAsString();
    }

    public JsonObject getJson(String ISBN) {
        JsonObject obj = fixTheUglyJson(ISBN);
        return obj;
    }

    private JsonObject addContentData(JsonObject obj) {
        return obj.get("contentData").getAsJsonObject().get("_source").getAsJsonObject();
    }

    public ArrayList<JsonObject> getFullJson(String ISBN) {
        Session getProperJson = Requests.session();
        getProperJson.get("https://meny.no/").send();
        Map<String, Object> headers = new HashMap<>();
        headers.put("x-csrf-token", getProperJson.currentCookies().get(0).getValue());

        Map<String, Object> params = new HashMap<>();
        params.put("types", "products,articles");
        params.put("search", ISBN);
        params.put("page_size", "10");
        params.put("suggest", "false");
        params.put("full_response", "false");


        String responseFromNG =
                getProperJson.get("https://platform-rest-prod.ngdata.no/api/episearch/" + this.storeID + "/all")
                        .headers(headers)
                        .params(params)
                        .send().readToText();

        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(responseFromNG).getAsJsonObject();
        String pricePerUnit = obj.get("products").toString();
        obj = parser.parse(pricePerUnit).getAsJsonObject();
        pricePerUnit = obj.get("hits").toString();
        obj = parser.parse(pricePerUnit).getAsJsonObject();
        pricePerUnit = obj.get("hits").toString();
        JsonArray jsonArray = parser.parse(pricePerUnit).getAsJsonArray();
        ArrayList<JsonObject> cleanJson = new ArrayList<>();
        for (JsonElement item: jsonArray) {
            cleanJson.add(item.getAsJsonObject());
        }
        return cleanJson;
    }

    private JsonObject fixTheUglyJson(@Nullable String ISBN) {
        Session getProperJson = Requests.session();
        getProperJson.get("https://meny.no/").send();
        Map<String, Object> headers = new HashMap<>();
        headers.put("x-csrf-token", getProperJson.currentCookies().get(0).getValue());

        Map<String, Object> params = new HashMap<>();
        params.put("types", "products,articles");
        params.put("search", ISBN);
        params.put("page_size", "10");
        params.put("suggest", "false");
        params.put("full_response", "false");


        String responseFromNG =
                getProperJson.get("https://platform-rest-prod.ngdata.no/api/episearch/" + this.storeID + "/all")
                        .headers(headers)
                        .params(params)
                        .send().readToText();

        JsonObject cleanJson = new JsonObject();

        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(responseFromNG).getAsJsonObject();
        String pricePerUnit = obj.get("products").toString();
        obj = parser.parse(pricePerUnit).getAsJsonObject();
        pricePerUnit = obj.get("hits").toString();
        obj = parser.parse(pricePerUnit).getAsJsonObject();
        pricePerUnit = obj.get("hits").toString();
        // Her skal iterator være mulig
        JsonArray jsonArray = parser.parse(pricePerUnit).getAsJsonArray();
        obj = parser.parse(String.valueOf(jsonArray.get(0))).getAsJsonObject();
        pricePerUnit = obj.get("contentData").toString();
        obj = parser.parse(pricePerUnit).getAsJsonObject();
        pricePerUnit = obj.get("_source").toString();
        obj = parser.parse(pricePerUnit).getAsJsonObject();
        return obj;
    }



}


