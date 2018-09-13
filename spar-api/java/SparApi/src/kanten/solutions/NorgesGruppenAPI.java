package kanten.solutions;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dongliu.requests.Requests;
import net.dongliu.requests.Session;

import java.io.IOException;
import java.util.*;

public class NorgesGruppenAPI {
    private int storeID;
    private Session session;

    public NorgesGruppenAPI(int storeID) {
        this.storeID = storeID;
        this.session = Requests.session();
    }

    public float getPrice(String ISBN) throws IOException {
        Session getActualPrice = Requests.session();

        String response = getActualPrice.get("https://spar.no/").send().readToText();
        Map<String, Object> headers = new HashMap<>();
        headers.put("x-csrf-token", getActualPrice.currentCookies().get(0).getValue());

        Map<String, Object> params = new HashMap<>();
        params.put("types", "products,articles");
        params.put("search", ISBN);
        params.put("page_size", "10");
        params.put("suggest", "false");
        params.put("full_response", "false");


        String responseFromNG =
                getActualPrice.get("https://platform-rest-prod.ngdata.no/api/episearch/1210/all")
                .headers(headers)
                .params(params)
                .send().readToText();
        /*
            Tungvint måte for å se hvordan json-et er satt opp. Bra jobba NG ;-)
         */
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(responseFromNG).getAsJsonObject();
        JsonObject childOfObj = (JsonObject)obj.get("products");
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
        float pricePerUnit2 = obj.get("pricePerUnitOriginal").getAsFloat();
        System.out.println(response + "\n");
        System.out.println(pricePerUnit2 + "eeeee\n");
        return pricePerUnit2;
    }
}


