package no.hiof.olaka;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dongliu.requests.Requests;
import net.dongliu.requests.Session;

import java.util.*;

/***
 * For non-commercial use and education purposes only.
 * @author Ola Kanten
 * @description An API-wrapper for NorgesGruppens API.
 */

// TODO: Rense opp
public class NorgesGruppenAPI {
    private int storeID;
    private static final String BEGIN_IMAGE_URL = "https://res.cloudinary.com/norgesgruppen/image/upload/c_pad,b_transparent,f_auto,h_640,q_50,w_640/";

    /*
        Store IDs:
            Spar - 1210
            Joker - 1220
            Meny - 1300
    */

    public NorgesGruppenAPI(int storeID) {
        this.storeID = storeID;
    }

    /***
     * Get the title of the item.
     * @param ISBN
     * @param obj
     * @return String
     */
    public String getTitle(@Nullable String ISBN, @Nullable JsonObject obj) {
        if (obj == null) {
            obj = getSingleItem(ISBN);
        } else {
            obj = addContentData(obj);
        }
        return obj.get("title").getAsString();
    }

    /***
     * Get the price of the item.
     * I chose to return this as a String. Using float were prone to errors.
     * @param ISBN
     * @param obj
     * @return
     */
    public String getPrice(@Nullable String ISBN, @Nullable JsonObject obj) {
        if (obj == null) {
            obj = getSingleItem(ISBN);
        } else {
            obj = addContentData(obj);
        }
        String originalPrice = obj.get("pricePerUnitOriginal").getAsString();
        return originalPrice;
    }

    /***
     * Returns the image URL of the item
     * @param ISBN
     * @param obj
     * @return String
     */
    public String getImageURL(@Nullable String ISBN, @Nullable JsonObject obj) {
        if (obj == null) {
            obj = getSingleItem(ISBN);
        } else {
            obj = addContentData(obj);
        }
        String imageURL = BEGIN_IMAGE_URL + obj.get("imageName").getAsString();
        return imageURL;
    }

    /***
     * Returns the group name of the item.
     * Similar to getCategoryName, but returns a more accurate answer.
     * e.g fruit, vegetables etc
     * @param ISBN
     * @param obj
     * @return
     */
    public String getShoppingListGroupName(@Nullable String ISBN, @Nullable JsonObject obj) {
        if (obj == null) {
            obj = getSingleItem(ISBN);
        } else {
            obj = addContentData(obj);
        }
        return obj.get("shoppingListGroupName").getAsString();
    }

    /***
     * Gets the category for the item.
     * e.g beverage, 'fruit & vegetables' etc
     * @param ISBN
     * @param obj
     * @return String
     */
    public String getCategoryName(@Nullable String ISBN, @Nullable JsonObject obj) {
        if (obj == null) {
            obj = getSingleItem(ISBN);
        } else {
            obj = addContentData(obj);
        }
        return obj.get("categoryName").getAsString();
    }


    /***
     * Checks if the item is sold as a weight item
     * @param ISBN
     * @param obj
     * @return boolean
     */
    public Boolean getProductByWeightSoldAsItem(@Nullable String ISBN, @Nullable JsonObject obj) {
        if (obj == null) {
            obj = getSingleItem(ISBN);
        } else {
            obj = addContentData(obj);
        }
        return obj.get("productByWeightSoldAsItem").getAsBoolean();
    }

    /***
     * Gets the brand of the item.
     * For some reason NG uses this to differentiate between type of (e.g) Coca-Cola Zero or Regular
     * @param ISBN - barcode / search query (can be null)
     * @param obj - can be used to pass a JsonObject instead of a search query (can be null)
     * @return
     */
    public String getBrand(@Nullable String ISBN, @Nullable JsonObject obj) {
        if (obj == null) {
            obj = getSingleItem(ISBN);
        } else {
            obj = addContentData(obj);
        }
        return obj.get("brand").getAsString();
    }

    /***
     * Checks if the item contains alcohol or not.
     * @param ISBN - barcode / search query (can be null)
     * @param obj - can be used to pass a JsonObject instead of a search query (can be null)
     * @return boolean
     */
    public Boolean getContainsAlcohol(@Nullable String ISBN, @Nullable JsonObject obj) {
        if (obj == null) {
            obj = getSingleItem(ISBN);
        } else {
            obj = addContentData(obj);
        }
        return obj.get("containsAlcohol").getAsBoolean();
    }

    /***
     * Checks if the item is on sale or at a reduced price
     * @param ISBN - barcode / search query (can be null)
     * @param obj - can be used to pass a JsonObject instead of a search query (can be null)
     * @return boolean
     */
    public Boolean getIsOffer(@Nullable String ISBN, @Nullable JsonObject obj) {
        if (obj == null) {
            obj = getSingleItem(ISBN);
        } else {
            obj = addContentData(obj);
        }
        return obj.get("isOffer").getAsBoolean();
    }

    /***
     * Returns the calculated unit type for the item in a shortened version.
     * (eg. kg, stk, etc)
     * @param ISBN - barcode / search query (can be null)
     * @param obj - can be used to pass a JsonObject instead of a search query (can be null)
     * @return a String consisting of the unit type
     */
    public String getCalcUnit(@Nullable String ISBN, @Nullable JsonObject obj) {
        if (obj == null) {
            obj = getSingleItem(ISBN);
        } else {
            obj = addContentData(obj);
        }
        return obj.get("calcUnit").getAsString();
    }


    /***
     * Returns the calculated unit type for the item.
     * (eg. Kilo, Stykk, etc)
     * @param ISBN - barcode / search query (can be null)
     * @param obj - can be used to pass a JsonObject instead of a search query (can be null)
     * @return a String consisting of the unit type
     */
    public String getCalcUnitType(@Nullable String ISBN, @Nullable JsonObject obj) {
        if (obj == null) {
            obj = getSingleItem(ISBN);
        } else {
            obj = addContentData(obj);
        }
        return obj.get("calcUnitType").getAsString();
    }

    /***
     * Returns the barcode of the item.
     * Useful if you want the barcode of a item you searched for by name.
     * @param ISBN
     * @param obj
     * @return
     */
    public String getISBN(@Nullable String ISBN, @Nullable JsonObject obj) {
        if (obj == null) {
            obj = getSingleItem(ISBN);
        }
        return obj.get("contentId").getAsString();
    }

    /***
     * This is useful if you want the query returned as a JsonObject.
     * @param ISBN
     * @return JsonObject
     */
    public JsonObject getJson(String ISBN) {
        JsonObject obj = getSingleItem(ISBN);
        return obj;
    }

    /***
     * This is a method used by the other getters.
     * It will return an json object that is further down in the json hierarchy.
     * @param obj
     * @return a JsonObject for the method calling it
     */
    private JsonObject addContentData(JsonObject obj) {
        return obj.get("contentData").getAsJsonObject()
                .get("_source").getAsJsonObject();
    }

    /***
     * This is a method for getting the full array of a search.
     * The method returns the full array of the search word.
     * Useful for searching for items by name.
     * @param ISBN
     * @return a ArrayList<JsonObject> consisting of the full search query.
     */
    public ArrayList<JsonObject> getFullJson(String ISBN) {

        Session requestsSession = Requests.session();

        requestsSession.get("https://meny.no/").send();

        Map<String, Object> headers = new HashMap<>();
        headers.put("x-csrf-token", requestsSession.currentCookies().get(0).getValue());

        Map<String, Object> params = new HashMap<>();
        params.put("types", "products,articles");
        params.put("search", ISBN);
        params.put("page_size", "10");
        params.put("suggest", "false");
        params.put("full_response", "false");


        String responseFromNG =
                requestsSession.get("https://platform-rest-prod.ngdata.no/api/episearch/" + this.storeID + "/all")
                        .headers(headers)
                        .params(params)
                        .send().readToText();

        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(responseFromNG).getAsJsonObject()
                .get("products").getAsJsonObject()
                .get("hits").getAsJsonObject()
                .get("hits").getAsJsonArray();
        ArrayList<JsonObject> cleanJson = new ArrayList<>();
        for (JsonElement item: jsonArray) {
            cleanJson.add(item.getAsJsonObject());
        }
        return cleanJson;
    }


    /***
     * This is a method for getting one single item from NorgesGruppen.
     * It will return the first query for the search result.
     * This is useful for getting a single item based on a barcode or another unique identifier.
     * @param ISBN
     * @return A single item in the form of a JsonObject
     */
    private JsonObject getSingleItem(@Nullable String ISBN) {
        Session requestsSession = Requests.session();

        requestsSession.get("https://meny.no/").send();

        Map<String, Object> headers = new HashMap<>();
        headers.put("x-csrf-token", requestsSession.currentCookies().get(0).getValue());

        Map<String, Object> params = new HashMap<>();
        params.put("types", "products,articles");
        params.put("search", ISBN);
        params.put("page_size", "10");
        params.put("suggest", "false");
        params.put("full_response", "false");


        String responseFromNG =
                requestsSession.get("https://platform-rest-prod.ngdata.no/api/episearch/" + this.storeID + "/all")
                        .headers(headers)
                        .params(params)
                        .send().readToText();

        JsonParser parser = new JsonParser();
        JsonObject jsonObj = parser.parse(responseFromNG).getAsJsonObject()
                .get("products").getAsJsonObject()
                .get("hits").getAsJsonObject()
                .get("hits").getAsJsonArray()
                .get(0).getAsJsonObject()
                .get("contentData").getAsJsonObject()
                .get("_source").getAsJsonObject();
        return jsonObj;
    }

    /***
     * This method makes a request to NorgesGruppens API.
     * Should only be used exclusively in this class.
     * (Android: Vi bruker ikke denne fordi det virker som API 21 bruker for lang tid på å prosessere requests..)
     * @param ISBN
     * @return the query result in the form of a String
     */
    private String getResponseFromNG(String ISBN) {
        Session requestsSession = Requests.session();


        requestsSession.get("https://meny.no/").send();

        Map<String, Object> headers = new HashMap<>();
        headers.put("x-csrf-token", requestsSession.currentCookies().get(0).getValue());

        Map<String, Object> params = new HashMap<>();
        params.put("types", "products,articles");
        params.put("search", ISBN);
        params.put("page_size", "10");
        params.put("suggest", "false");
        params.put("full_response", "false");


        String responseFromNG =
                requestsSession.get("https://platform-rest-prod.ngdata.no/api/episearch/" + this.storeID + "/all")
                        .headers(headers)
                        .params(params)
                        .send().readToText();
        System.out.println(responseFromNG);
        return responseFromNG;
    }



}


