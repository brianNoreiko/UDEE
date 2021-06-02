package com.utn.UDEE.models;

public enum UserType {

    CLIENT("Cliente"),
    EMPLOYEE("Empleado");

    private String description;

    UserType(String description){
        this.description = description;
    }

    public static UserType find (String value){
        for ( UserType u : values()){
            if(u.toString().equals(value)){
                return u;
            }
        }
        throw  new IllegalArgumentException(String.format("Invalid user type %s", value));
    }

    public String getDescription(){
        return description;
    }

}

