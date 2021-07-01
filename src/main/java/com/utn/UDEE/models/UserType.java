package com.utn.UDEE.models;

public enum UserType {

    CLIENT("Cliente"), //0
    EMPLOYEE("Empleado"); //1

    private String description;

    UserType(String description){
        this.description = description;
    }

    public static UserType find (String value){
        for ( UserType u : values()){
            if(u.toString().equalsIgnoreCase(value)){
                return u;
            }
        }
        throw  new IllegalArgumentException(String.format("Invalid user type %s", value));
    }

    public String getDescription(){
        return description;
    }

}

