package org.eclipse.smarthome.binding.opensensenetwork.internal;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class OHItem {
    private final String link;
    private final String state;
    private final JSONObject stateDescription;
    private final Boolean editable;
    private final String type;
    private final String name;
    private final String label;
    private final String category;
    private final ArrayList<String> tags;
    private final ArrayList<String> groupNames;

    public OHItem(String link, String state, JSONObject stateDecription, Boolean editable, String type, String name,
            String label, String category, ArrayList<String> tags, ArrayList<String> groupNames) {

        this.link = link;
        this.state = state;
        this.stateDescription = stateDecription;
        this.editable = editable;
        this.type = type;
        this.name = name;
        this.label = label;
        this.category = category;
        this.tags = tags;
        this.groupNames = groupNames;
    }

    public String getLink() {
        return link;
    }

    public String getState() {
        return state;
    }

    public JSONObject getStateSecription() {
        return stateDescription;
    }

    public Boolean getEditable() {
        return editable;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public String getCategory() {
        return category;
    }

    public ArrayList<String> tags() {
        return tags;
    }

    public ArrayList<String> getGroupNames() {
        return groupNames;
    }

    public static ArrayList<String> getMeasurandsFromOpenHab() {
        try {
            ArrayList<OHItem> items = getItemsFromOH();
            ArrayList<String> measurands = new ArrayList<String>();
            for (OHItem ohItem : items) {
                measurands.add(ohItem.getLabel());
            }
            return measurands;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public static OHItem getOHItemFromJson(JSONObject json) {
        String link;
        String state;
        JSONObject stateDescription;
        Boolean editable;
        String type;
        String name;
        String label;
        String category;
        ArrayList<String> tags = new ArrayList<>();
        ArrayList<String> groupNames = new ArrayList<>();

        link = json.getString("link");
        state = json.getString("state");
        stateDescription = json.has("stateDescription") ? (JSONObject) json.get("stateDescription") : null;
        editable = json.getBoolean("editable");
        type = json.getString("type");
        name = json.getString("name");
        label = json.getString("label");
        category = json.has("category") ? json.getString("category") : null;
        JSONArray tagsArray = json.getJSONArray("tags");
        JSONArray groupNamesArray = json.getJSONArray("groupNames");
        for (Object jsonElement : tagsArray) {
            tags.add(jsonElement.toString());
        }
        for (Object jsonElement : groupNamesArray) {
            groupNames.add(jsonElement.toString());
        }

        return new OHItem(link, state, stateDescription, editable, type, name, label, category, tags, groupNames);
    }

    public static ArrayList<OHItem> getItemsFromOH() {
        try {
            HttpResponse<String> response = Unirest.get("https://home.myopenhab.org/rest/items")
                    .header("Content-Type", "application/json").header("Accept", "application/json")
                    .header("Authorization", "Basic bWF0ZW85NkBvMi5wbDpTbWFydEhvbWU=").asString();
            System.out.println(response.getStatusText());
            if (response.getStatus() == 200) {
                JsonNode body = new JsonNode(response.getBody());
                JSONArray arr = body.getArray();
                ArrayList<OHItem> itemsList = new ArrayList<>();
                for (Object object : arr) {
                    JSONObject o = (JSONObject) object;
                    itemsList.add(getOHItemFromJson(o));
                }
                return itemsList;
            }
            return null;
        } catch (UnirestException | JSONException je) {
            System.out.println(je.getMessage());
            return null;
        }
    }

    public static String getLinkForMeasurand(String measurand) {
        ArrayList<OHItem> items = getItemsFromOH();
        for (OHItem ohItem : items) {
            if (ohItem.label.toLowerCase().equals(measurand.toLowerCase())) {
                return ohItem.link;
            }
        }
        return "";
    }
}