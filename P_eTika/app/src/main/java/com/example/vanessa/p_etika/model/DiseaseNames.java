package com.example.vanessa.p_etika.model;

/**
 * Created by Vanessa on 31/12/2017.
 */

public class DiseaseNames {

    public String disease_name;
    public boolean checked;

    public DiseaseNames(String disease_name, boolean checked) {
        this.disease_name = disease_name;
        this.checked = checked;
    }

    public String getDisease_name() {
        return disease_name;
    }

    public void setDisease_name(String disease_name) {
        this.disease_name = disease_name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
