package com.example.ilpp.models;

import com.example.ilpp.classes.model.Data;
import com.example.ilpp.classes.model.Field;

@Data
public class AnimalServices {
    @Field
    private Boolean walks;
    public Boolean getWalks(){ return this.walks; }
    public void setWalks(Boolean walks){ this.walks = walks; }

    @Field
    private Boolean hosting;
    public Boolean getHosting(){ return this.hosting; }
    public void setHosting(Boolean hosting){ this.hosting = hosting; }

    @Field
    private Boolean adoption;
    public Boolean getAdoption(){ return this.adoption; }
    public void setAdoption(Boolean adoption){ this.adoption = adoption; }
}
