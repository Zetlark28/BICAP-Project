package it.unimib.bicap;

import com.google.android.material.button.MaterialButton;

public class ExampleItem {
    private String textNome;
    private String textEmail;

    public ExampleItem(String textNome, String textEmail) {
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
