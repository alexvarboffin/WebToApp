package com.sherdle.webtoapp;


import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class Config {

    public static int DRAWER_ICON = R.mipmap.ic_launcher;

    public static String ANALYTICS_ID = "";
    public static boolean COLLAPSING_ACTIONBAR = false;
    public static boolean HIDE_ACTIONBAR = true;
    public static boolean HIDE_DRAWER_HEADER = false;
    public static boolean HIDE_MENU_HOME = false;
    public static boolean HIDE_MENU_NAVIGATION = false;
    public static boolean HIDE_MENU_SHARE = false;
    public static boolean HIDE_TABS = true;
    public static int INTERSTITIAL_INTERVAL = 2;
    public static boolean INTERSTITIAL_PAGE_LOAD = true;
    public static boolean LIGHT_TOOLBAR_THEME = false;
    public static boolean LOAD_AS_PULL = true;
    public static boolean MULTI_WINDOWS = false;
    public static String NO_CONNECTION_PAGE = "";
    public static boolean PULL_TO_REFRESH = false;
    public static boolean SHOW_NOTIFICATION_SETTINGS = false;
    public static boolean SPLASH = true;
    public static int SPLASH_SCREEN_DELAY = 800;
    public static boolean STATIC_TOOLBAR_TITLE = false;
    public static int TOOLBAR_ICON = 0;
    public static boolean USE_DRAWER = false;
    public static String[] TITLES = {""};
    public static String[] URLS = {"https://google.com"};
    public static int[] ICONS = new int[0];
    public static String[] OPEN_OUTSIDE_WEBVIEW = new String[0];
    public static String[] OPEN_ALL_OUTSIDE_EXCEPT = new String[0];
    public static String[] PERMISSIONS_REQUIRED = new String[0];

    public static void loadConfig(Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream is = assetManager.open("config.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, "UTF-8");
            JSONObject jsonObject = new JSONObject(json);

            ANALYTICS_ID = jsonObject.getString("ANALYTICS_ID");
            COLLAPSING_ACTIONBAR = jsonObject.getBoolean("COLLAPSING_ACTIONBAR");
            HIDE_ACTIONBAR = jsonObject.getBoolean("HIDE_ACTIONBAR");
            HIDE_DRAWER_HEADER = jsonObject.getBoolean("HIDE_DRAWER_HEADER");
            HIDE_MENU_HOME = jsonObject.getBoolean("HIDE_MENU_HOME");
            HIDE_MENU_NAVIGATION = jsonObject.getBoolean("HIDE_MENU_NAVIGATION");
            HIDE_MENU_SHARE = jsonObject.getBoolean("HIDE_MENU_SHARE");
            HIDE_TABS = jsonObject.getBoolean("HIDE_TABS");
            INTERSTITIAL_INTERVAL = jsonObject.getInt("INTERSTITIAL_INTERVAL");
            INTERSTITIAL_PAGE_LOAD = jsonObject.getBoolean("INTERSTITIAL_PAGE_LOAD");
            LIGHT_TOOLBAR_THEME = jsonObject.getBoolean("LIGHT_TOOLBAR_THEME");
            LOAD_AS_PULL = jsonObject.getBoolean("LOAD_AS_PULL");
            MULTI_WINDOWS = jsonObject.getBoolean("MULTI_WINDOWS");
            NO_CONNECTION_PAGE = jsonObject.getString("NO_CONNECTION_PAGE");
            PULL_TO_REFRESH = jsonObject.getBoolean("PULL_TO_REFRESH");
            SHOW_NOTIFICATION_SETTINGS = jsonObject.getBoolean("SHOW_NOTIFICATION_SETTINGS");
            SPLASH = jsonObject.getBoolean("SPLASH");
            SPLASH_SCREEN_DELAY = jsonObject.getInt("SPLASH_SCREEN_DELAY");
            STATIC_TOOLBAR_TITLE = jsonObject.getBoolean("STATIC_TOOLBAR_TITLE");
            TOOLBAR_ICON = jsonObject.getInt("TOOLBAR_ICON");
            USE_DRAWER = jsonObject.getBoolean("USE_DRAWER");

            TITLES = jsonArrayToStringArray(jsonObject.getJSONArray("TITLES"));
            URLS = jsonArrayToStringArray(jsonObject.getJSONArray("URLS"));
            ICONS = jsonArrayToIntArray(jsonObject.getJSONArray("ICONS"));
            OPEN_OUTSIDE_WEBVIEW = jsonArrayToStringArray(jsonObject.getJSONArray("OPEN_OUTSIDE_WEBVIEW"));
            OPEN_ALL_OUTSIDE_EXCEPT = jsonArrayToStringArray(jsonObject.getJSONArray("OPEN_ALL_OUTSIDE_EXCEPT"));
            PERMISSIONS_REQUIRED = jsonArrayToStringArray(jsonObject.getJSONArray("PERMISSIONS_REQUIRED"));

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private static String[] jsonArrayToStringArray(JSONArray jsonArray) throws JSONException {
        String[] array = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            array[i] = jsonArray.getString(i);
        }
        return array;
    }

    private static int[] jsonArrayToIntArray(JSONArray jsonArray) throws JSONException {
        int[] array = new int[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            array[i] = jsonArray.getInt(i);
        }
        return array;
    }
}
