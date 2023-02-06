package com.vma.goapp.ui.logbook;

import java.io.Serializable;
import java.util.Date;

public class LogbookHolder implements Serializable {

    private int id;
    private int fishId;
    private String fishName;
    private String fishImage;
    private UnitType type ;
    private int qty;
    private Date time;
    private double longitude ;
    private double latitude ;

    public int getFishId() {
        return fishId;
    }

    public void setFishId(int fishId) {
        this.fishId = fishId;
    }

    public String getFishName() {
        return fishName;
    }

    public void setFishName(String fishName) {
        this.fishName = fishName;
    }

    public UnitType getType() {
        return type;
    }

    public void setType(UnitType type) {
        this.type = type;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setFishImage(String fishImage) {
        this.fishImage = fishImage;
    }

    public String getFishImage() {
        return fishImage;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
