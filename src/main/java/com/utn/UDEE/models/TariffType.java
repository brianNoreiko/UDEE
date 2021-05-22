package com.utn.UDEE.models;

public enum TariffType {

    CLASSA("Tarifa clase A", 0.29f),
    CLASSB("Tarifa clase B", 0.54f),
    CLASSC("Tarifa clase C", 0.72f),
    CLASSD("Tarifa clase D", 0.93f);


    private String description;
    private Float value;

    TariffType(String description, float value){
        this.description = description;
        this.value = value;
    }

    public static TariffType find (String value){
        for ( TariffType tariff : values()){
            if(tariff.toString().equals(value)){
                return tariff;
            }
        }
        throw  new IllegalArgumentException(String.format("Invalid tariff type %s", value));
    }

    public  String getDescription(){
        return description;
    }

    public Float getValue(){
        return value;
    }
}
