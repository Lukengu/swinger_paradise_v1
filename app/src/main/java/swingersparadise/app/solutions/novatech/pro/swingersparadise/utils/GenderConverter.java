package swingersparadise.app.solutions.novatech.pro.swingersparadise.utils;

import java.io.IOException;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.entities.Card;


public class GenderConverter {

    public static int  convert(Card card) {

        if(card.getMarital_status().contains("Married"))
            return R.drawable.couple_icon;
        if(card.getMarital_status().equals("Single") && card.getGender().equals("Male"))
            return R.drawable.male;
        if(card.getMarital_status().equals("Single") && card.getGender().equals("Female"))
            return R.drawable.female;
        return 0;
    }
}
