package swingersparadise.app.solutions.novatech.pro.swingersparadise.register.fragments;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.register.adapters.CountryItemsAdapter;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.register.fragments.listeners.SetErrorListener;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.utils.Country;

import static swingersparadise.app.solutions.novatech.pro.swingersparadise.utils.ArrayUtils.getPosition;

public class Credentials extends Fragment implements SetErrorListener {

    EditText display_name,password, conf_password, email_address, reffered_by, mobile_number, country_code,city,province;
    Spinner country,profile_type;
    SharedPreferences spref;
    SharedPreferences.Editor editor;
    List<Country> countryList;
    LinearLayout parent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.credentials, container, false);
        countryList = Country.getList(getActivity());
        spref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = spref.edit();


        display_name = view.findViewById(R.id.display_name);
       // username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        conf_password = view.findViewById(R.id.conf_password) ;
        email_address = view.findViewById(R.id.email_address);
        reffered_by = view.findViewById(R.id.reffered_by);
        country = view.findViewById(R.id.country);
        profile_type = view.findViewById(R.id.profile_type);
        country_code = view.findViewById(R.id.country_code);
        mobile_number = view.findViewById(R.id.mobile_number);
        parent = view.findViewById(R.id.parent);
        province  = view.findViewById(R.id.province);
        city  = view.findViewById(R.id.city);



        ArrayList<Country> countries = new ArrayList<>();
        countries.addAll(countryList);

        CountryItemsAdapter adapter = new CountryItemsAdapter(getActivity().getApplicationContext(), countries, true);
        country.setAdapter(adapter);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(), R.layout.spinner_textview_no_bg,
                getActivity().getResources().getStringArray(R.array.profile_type));
        profile_type.setAdapter(adapter2);



        country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Country c = countryList.get(i);
                if(spref.contains("country"))
                    editor.remove("country").commit();

                editor.putString("country", c.getName()).commit();

                if(spref.contains("dial_code"))
                    editor.remove("dial_code").commit();

                editor.putString("dial_code", c.getDial_code()).commit();


                if(spref.contains("selected_country"))
                    editor.remove("selected_country").commit();

                editor.putInt("selected_country", ((CountryItemsAdapter) country.getAdapter()).getPosition(c)).commit();


                country_code.setText(c.getDial_code());

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }


        });

        profile_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(spref.contains("profile_type"))
                    editor.remove("profile_type").commit();

                if(!"Profile Type".equals(profile_type.getSelectedItem().toString())){
                    editor.putString("profile_type", profile_type.getSelectedItem().toString()).commit();

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }


        });

        country.setSelection(getLocale(), true);

        display_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasfocus) {
                if(!hasfocus && !TextUtils.isEmpty(display_name.getText()) ){

                    if(spref.contains("display_name"))
                        editor.remove("display_name").commit();

                    editor.putString("display_name", display_name.getText().toString()).commit();

                }

            }
        });



        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasfocus) {
                if(!hasfocus && !TextUtils.isEmpty(password.getText()) ){

                    if(spref.contains("password"))
                        editor.remove("password").commit();

                    editor.putString("password", password.getText().toString()).commit();

                }

            }
        });
        conf_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasfocus) {
                if(!hasfocus && !TextUtils.isEmpty(conf_password.getText().toString()) ){

                    if(!spref.contains("password")) {
                        password.setError("Password is not set");
                        return;
                    }

                    String pass = spref.getString("password", "");
                    if(!pass.equals(conf_password.getText().toString())) {
                        conf_password.setError("Confirm Password does not match");
                        return;

                    }
                    editor.putString("conf_password", conf_password.getText().toString()).commit();

                }

            }
        });


        province.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasfocus) {
                if(!hasfocus && !TextUtils.isEmpty(province.getText()) ){

                    if(spref.contains("province"))
                        editor.remove("province").commit();

                    editor.putString("province", province.getText().toString()).commit();

                }

            }
        });

        city.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasfocus) {
                if(!hasfocus && !TextUtils.isEmpty(city.getText()) ){

                    if(spref.contains("city"))
                        editor.remove("city").commit();

                    editor.putString("city", city.getText().toString()).commit();

                }

            }
        });


        email_address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasfocus) {
                if(!hasfocus && !TextUtils.isEmpty(email_address.getText()) ){

                    if(spref.contains("email_address"))
                        editor.remove("email_address").commit();

                    editor.putString("email_address", email_address.getText().toString()).commit();

                }

            }
        });

        mobile_number.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasfocus) {
                if(!hasfocus && !TextUtils.isEmpty(mobile_number.getText()) ){

                    if(spref.contains("mobile_number"))
                        editor.remove("mobile_number").commit();

                    editor.putString("mobile_number", mobile_number.getText().toString()).commit();

                }

            }
        });

        reffered_by.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasfocus) {
                if(!hasfocus && !TextUtils.isEmpty(reffered_by.getText()) ){

                    if(spref.contains("reffered_by"))
                        editor.remove("reffered_by").commit();

                    editor.putString("reffered_by", reffered_by.getText().toString()).commit();

                }

            }
        });
        loadData();


        return view;
    }
    private void loadData(){
        display_name.setText(spref.contains("display_name") ? spref.getString("display_name","") :"");
        province.setText(spref.contains("province") ? spref.getString("province","") :"");
        city.setText(spref.contains("city") ? spref.getString("city","") :"");
       // username.setText(spref.contains("username") ? spref.getString("username","") :"");
        profile_type.setSelection(!spref.contains("profile_type") ? 0: getPosition(spref.getString("profile_type",""), getActivity().getResources().getStringArray(R.array.profile_type)), true );
        password.setText(spref.contains("password") ? spref.getString("password","") :"");
        conf_password.setText(spref.contains("conf_password") ? spref.getString("conf_password","") :"");
        email_address.setText(spref.contains("email_address") ? spref.getString("email_address","") :"");
        mobile_number.setText(spref.contains("mobile_number") ? spref.getString("mobile_number","") :"");
        if(spref.contains("selected_country")) {
            country.setSelection(spref.getInt("selected_country", 0));
        }

        reffered_by.setText(spref.contains("reffered_by") ? spref.getString("reffered_by","") :"");
    }



    @Override
    public void setError() {
        if(!spref.contains("display_name"))
            display_name.setError("Display name is required");

        if(!spref.contains("email_address"))
            email_address.setError("Username is required");

        if(!spref.contains("password"))
            password.setError("password is required");

        if(!spref.contains("conf_password"))
            conf_password.setError("You must confirm the password");


        if(!spref.contains("profile_type")){
            Snackbar snackbar = Snackbar.make(parent, "The Profile type is required", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sbView.setBackground(getActivity().getDrawable(R.drawable.snackbar_error));
            } else {
                sbView.setBackground(getActivity().getResources().getDrawable(R.drawable.snackbar_error));
            }

            snackbar.show();
        }


        if(!spref.contains("mobile_number"))
            mobile_number.setError("Mobile Number Missing");

    }

    @Override
    public void setData() {

    }
    public int  getLocale() {
        Locale locale = Locale.getDefault();
        for(Country c : countryList){
            if(c.getCode().equals(locale.getCountry())){
                return countryList.indexOf(c);
            }
        }
        return 0;
    }
}
