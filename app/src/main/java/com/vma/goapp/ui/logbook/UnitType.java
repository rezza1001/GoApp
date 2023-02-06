package com.vma.goapp.ui.logbook;

public enum UnitType {
    EKOR ("Ekor","E"),
    KG ("Kilogram","K");

    final String value;
    final String key;
    UnitType(String value, String key){
        this.value = value;
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    public static UnitType find(String key){
        for (UnitType type : values()){
            if (type.key.equals(key)){
                return type;
            }
        }
        return KG;
    }
}
