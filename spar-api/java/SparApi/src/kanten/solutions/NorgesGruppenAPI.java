package kanten.solutions;

import com.google.gson.JsonArray;
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

    public String getTitle(String ISBN) {
        JsonObject obj = fixTheUglyJson(ISBN);
        return obj.get("title").getAsString();
    }

    public float getPrice(String ISBN) throws IOException {
        JsonObject obj = fixTheUglyJson(ISBN);
        float originalPrice = obj.get("pricePerUnitOriginal").getAsFloat();
        float pricePerUnit = obj.get("pricePerUnit").getAsFloat();
        if (originalPrice != pricePerUnit) {
            System.out.println("Tilbud!!");
        }
        return originalPrice;
    }

    public String getImageURL(String ISBN) {
        JsonObject obj = fixTheUglyJson(ISBN);
        String imageURL = BEGIN_IMAGE_URL + obj.get("imageName").getAsString();
        return imageURL;
    }

    public String getShoppingListGroupName(String ISBN) {
        JsonObject obj = fixTheUglyJson(ISBN);
        return obj.get("shoppingListGroupName").getAsString();
    }

    public String getCategoryName(String ISBN) {
        JsonObject obj = fixTheUglyJson(ISBN);
        return obj.get("categoryName").getAsString();
    }

    public Boolean getProductByWeightSoldAsItem(String ISBN) {
        JsonObject obj = fixTheUglyJson(ISBN);
        return obj.get("productByWeightSoldAsItem").getAsBoolean();
    }

    public String getBrand(String ISBN) {
        JsonObject obj = fixTheUglyJson(ISBN);
        return obj.get("brand").getAsString();
    }

    public Boolean getContainsAlcohol(String ISBN) {
        JsonObject obj = fixTheUglyJson(ISBN);
        return obj.get("containsAlcohol").getAsBoolean();
    }

    public Boolean getIsOffer(String ISBN) {
        JsonObject obj = fixTheUglyJson(ISBN);
        return obj.get("isOffer").getAsBoolean();
    }

    public float getWeight(String ISBN) {
        JsonObject obj = fixTheUglyJson(ISBN);
        return obj.get("weight").getAsFloat();
    }

    public String getCalcUnit(String ISBN) {
        JsonObject obj = fixTheUglyJson(ISBN);
        return obj.get("calcUnit").getAsString();
    }

    public String getCalcUnitType(String ISBN) {
        JsonObject obj = fixTheUglyJson(ISBN);
        return obj.get("calcUnitType").getAsString();
    }

    private JsonObject fixTheUglyJson(String ISBN) {
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
        /*
            Tungvint måte for å se hvordan json-et er satt opp. Bra jobba NG ;-)
         */
        System.out.println(responseFromNG);
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


