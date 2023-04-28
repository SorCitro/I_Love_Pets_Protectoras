package com.example.ilpp.models;

import android.os.Bundle;

import com.example.ilpp.classes.model.Data;
import com.example.ilpp.classes.model.Field;
import com.example.ilpp.classes.model.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Data
public class Address extends Model {

    @Field
    private String address;
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Field
    private String city;
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    @Field
    private String postalCode;
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public static Address from(Map<String, Object> data) {
        Address address = new Address();
        if (data == null) return address;
        address.address = data.getOrDefault("address", "").toString();
        address.city = data.getOrDefault("city", "").toString();
        address.postalCode = data.getOrDefault("postalCode", "").toString();
        return address;
    }

    public static Address from(Bundle data) {
        Address address = new Address();
        address.address = data.getString("address");
        address.city = data.getString("city");
        address.postalCode = data.getString("postalCode");
        return address;
    }

    public Bundle toBundle(){
        Bundle bundle = super.toBundle();
        bundle.putString("address", address);
        bundle.putString("city", city);
        bundle.putString("postalCode", postalCode);
        return bundle;
    }

    public Map<String, Object> toDocData(){
        Map<String, Object> map = new HashMap<>();
        map.put("address", address);
        map.put("city", city);
        map.put("postalCode", postalCode);
        return map;
    }

    public String getLongDisplay(){
        ArrayList<String> parts = new ArrayList<>();
        if (address != null && !address.isEmpty()) parts.add(address);
        if (postalCode != null && !postalCode.isEmpty()) parts.add(postalCode);
        if (city != null && !city.isEmpty()) parts.add(city);
        if (parts.isEmpty()) return "";
        return String.join(", ", parts);
    }

    public String getShortDisplay(){
        ArrayList<String> parts = new ArrayList<>();
        if (address != null && !address.isEmpty()) parts.add(address);
        if (city != null && !city.isEmpty()) parts.add(city);
        if (parts.isEmpty()) return "";
        return String.join(", ", parts);
    }

    public String getSearchText(){
        return this.getLongDisplay();
    }

}
