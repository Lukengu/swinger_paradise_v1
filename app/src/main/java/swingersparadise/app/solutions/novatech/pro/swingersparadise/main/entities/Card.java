package swingersparadise.app.solutions.novatech.pro.swingersparadise.main.entities;

import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;

public class Card implements Serializable {

    private String display_name;
    private String uuid;
    private String gender;
    private int age;
    private String drinking;
    private String body_part;
    private String build;
    private String country;
    private String about_me;
    private String hair_color;
    private String marital_status;
    private String sexual_prefs;
    private String ethnicity;
    private String smoking;


    public Card() {

    }
    public Card(JSONObject data) throws IllegalAccessException {
        for(Field f : Card.class.getDeclaredFields()){
            if(f.getType() == String.class) {
                try {
                    f.set(this, data.optString(f.getName()));
                }catch(NullPointerException e){

                }
            }
            if(f.getType() == Integer.class) {
                f.set(this, data.optInt(f.getName()));
            }
        }

    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }


    public String getDrinking() {
        return drinking;
    }

    public void setDrinking(String drinking) {
        this.drinking = drinking;
    }

    public String getBody_part() {
        return body_part;
    }

    public void setBody_part(String body_part) {
        this.body_part = body_part;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAbout_me() {
        return about_me;
    }

    public void setAbout_me(String about_me) {
        this.about_me = about_me;
    }

    public String getHair_color() {
        return hair_color;
    }

    public void setHair_color(String hair_color) {
        this.hair_color = hair_color;
    }

    public String getMarital_status() {
        return marital_status;
    }

    public void setMarital_status(String marital_status) {
        this.marital_status = marital_status;
    }

    public String getSexual_prefs() {
        return sexual_prefs;
    }

    public void setSexual_prefs(String sexual_prefs) {
        this.sexual_prefs = sexual_prefs;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getSmoking() {
        return smoking;
    }

    public void setSmoking(String smoking) {
        this.smoking = smoking;
    }


}
