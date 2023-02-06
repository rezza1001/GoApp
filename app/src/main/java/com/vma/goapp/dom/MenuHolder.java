package com.vma.goapp.dom;

import android.graphics.drawable.Drawable;

/**
 * Created by Mochamad Rezza Gumilang on 15/02/2022
 */
public class MenuHolder {
    public enum MENU  {MESSAGE, SETTING, SOS, LOGBOOK, FISH_MAP, WEATHER, DPI, CAMERA, LOCATION}

    public String name;
    public int icon = 0;
    public Drawable background ;
    public MENU menu;

    public MenuHolder(MENU menu, String name, int icon, Drawable background){
        this.name = name;
        this.icon = icon;
        this.background = background;
        this.menu = menu;
    }
}
