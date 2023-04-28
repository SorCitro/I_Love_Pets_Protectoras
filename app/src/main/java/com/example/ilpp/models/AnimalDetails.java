package com.example.ilpp.models;

import com.example.ilpp.classes.model.Data;
import com.example.ilpp.classes.model.Field;

@Data
public class AnimalDetails {
    @Field
    private String gender;
    public String getGender(){ return this.gender; }
    public void setGender(String gender){ this.gender = gender; }

    @Field
    private String breed;
    public String getBreed(){ return this.breed; }
    public void setBreed(String breed){ this.breed = breed; }

    @Field
    private String type;
    public String getType(){ return this.type; }
    public void setType(String type){ this.type = type; }

    @Field
    private String weight;
    public String getWeight(){ return this.weight; }
    public void setWeight(String weight){ this.weight = weight; }

    public String getSearchText(){
        return getGender() + ", " + getBreed() + ", " + getType() + ", " + getWeight();
    }

}
