package ru.thegalaxymap.app;

import com.sherdle.webtoapp.App;
import com.sherdle.webtoapp.Config;

public class MyApp extends App {

    @Override
    public void onCreate() {
        super.onCreate();
        Config.loadConfig(this);
    }
}
