package swingersparadise.app.solutions.novatech.pro.swingersparadise.utils;

import android.util.Log;

import java.io.IOException;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.entities.Card;


public class GenderConverter {

    public static int  convert(Card card) {

        String profile_type = card.getProfile_type();

        if(profile_type.equals("Single Male"))
        {
            return R.drawable.male;
        } else if(profile_type.equals("Single Female")){
            return R.drawable.female;
        } else if(profile_type.equals("Couple")){
            return R.drawable.couple_icon;
        }  else if(profile_type.equals("Male Couple")){
            return R.drawable.male_coupe;
        } else if(profile_type.equals("Female Couple")){
            return R.drawable.female_coupe;
        }
        return 0;
    }
}
