package com.vma.goapp.ui.logbook;

import java.io.Serializable;
import java.util.Comparator;

public class FishHolder implements Serializable {
    private int id = 0;
    private String name = "";
    private String type = "";
    private String imageName = "";
    private String latinName = "";

    public static class SortByName implements Comparator<FishHolder> {
        @Override
        public int compare(FishHolder a, FishHolder b) {
            return a.name.compareTo(b.name);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getLatinName() {
        return latinName;
    }

    public void setLatinName(String latinName) {
        this.latinName = latinName;
    }
}
