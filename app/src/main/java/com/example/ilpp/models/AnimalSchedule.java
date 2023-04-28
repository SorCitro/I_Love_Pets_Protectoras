package com.example.ilpp.models;

import com.example.ilpp.classes.model.Data;
import com.example.ilpp.classes.model.Field;

@Data
public class AnimalSchedule {
    @Field
    private String startTime;
    public String getStartTime(){ return this.startTime; }
    public void setStartTime(String startTime){ this.startTime = startTime; }

    @Field private String endTime;
    public String getEndTime(){ return this.endTime; }
    public void setEndTime(String endTime){ this.endTime = endTime; }


    @Field private Boolean canMon;
    public Boolean getCanMon(){ return this.canMon; }
    public void setCanMon(Boolean canMon){ this.canMon = canMon; }

    @Field private Boolean canTue;
    public Boolean getCanTue(){ return this.canTue; }
    public void setCanTue(Boolean canTue){ this.canTue = canTue; }

    @Field private Boolean canWed;
    public Boolean getCanWed(){ return this.canWed; }
    public void setCanWed(Boolean canWed){ this.canWed = canWed; }

    @Field private Boolean canThu;
    public Boolean getCanThu(){ return this.canThu; }
    public void setCanThu(Boolean canThu){ this.canThu = canThu; }

    @Field private Boolean canFri;
    public Boolean getCanFri(){ return this.canFri; }
    public void setCanFri(Boolean canFri){ this.canFri = canFri; }

    @Field private Boolean canSat;
    public Boolean getCanSat(){ return this.canSat; }
    public void setCanSat(Boolean canSat){ this.canSat = canSat; }

    @Field private Boolean canSun;
    public Boolean getCanSun(){ return this.canSun; }
    public void setCanSun(Boolean canSun){ this.canSun = canSun; }

    public void setDay(int day, Boolean value){
        switch (day){
            case 1:
                setCanMon(value);
                break;
            case 2:
                setCanTue(value);
                break;
            case 3:
                setCanWed(value);
                break;
            case 4:
                setCanThu(value);
                break;
            case 5:
                setCanFri(value);
                break;
            case 6:
                setCanSat(value);
                break;
            case 0:
                setCanSun(value);
                break;
        }
    }

    public String getDisplayTime(){
        String start = getStartTime();
        String end = getEndTime();
        if (start.isEmpty()) start = null;
        if (end.isEmpty()) end = null;
        if (start == null && end == null) return "No especificado";
        if (start.equals(end)) return start;
        if (end == null) return "Desde las " + start;
        if (start == null) return "Hasta las " + end;
        return getStartTime() + " a " + getEndTime();
    }

}
