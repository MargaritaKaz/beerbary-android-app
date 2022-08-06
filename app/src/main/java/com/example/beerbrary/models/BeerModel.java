package com.example.beerbrary.models;

import java.text.DecimalFormat;

public class BeerModel implements Comparable<BeerModel> {

    private int id;
    private float rating;
    private int brewery_id;
    private String name;
    private float voltage;
    private String line;
    private String flavour;
    private int type;

    public BeerModel() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setRating(float rating) {
        rating = (float) (Math.floor(rating * 100) / 100);
        this.rating = rating;
    }

    public float getRating() {
        return this.rating;
    }

    public void setBrewery(int id) {
        this.brewery_id = id;
    }

    public int getBrewery() {
        return this.brewery_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setFlavour(String flavour) {
        this.flavour = flavour;
    }

    public String getFlavour() {
        return this.flavour;
    }

    public void setVoltage(float voltage) {
        this.voltage = voltage;
    }

    public float getVoltage() {
        return this.voltage;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getLine() {
        return this.line;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    @Override
    public int compareTo(BeerModel beerModel) {
        return Float.compare(this.rating, beerModel.rating);
    }
}
