package it.unimib.bicap;

import com.google.android.material.button.MaterialButton;

public class ItemSearch {
    private String textNome;
    private String textEmail;

    public ItemSearch(String textNome, String textEmail) {
        this.textNome = textNome;
        this.textEmail = textEmail;
    }

    public String getTextNome() {
        return textNome;
    }

    public String getTextEmail() {
        return textEmail;
    }
}
