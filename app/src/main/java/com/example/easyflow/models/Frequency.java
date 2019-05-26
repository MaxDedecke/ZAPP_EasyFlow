package com.example.easyflow.models;

public enum Frequency {
    Einmalig(0),Täglich(1),Wöchentlich(2),Monatlich(3),Jährlich(4);

    private int mValue;

    Frequency(int value) { this.mValue = value;} // Constructor
    public int id(){return mValue;}

    public static Frequency fromId(int value) {
        for(Frequency frequency : values()) {
            if (frequency.mValue == value) {
                return frequency;
            }
        }
        return Einmalig;
    }

    //todo create spinner from enum?
}