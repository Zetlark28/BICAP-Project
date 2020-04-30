package it.unimib.bicap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import it.unimib.bicap.service.JsonBuilder;

@RunWith(RobolectricTestRunner.class)
public class JsonBuilderTest {

    private JsonBuilder jsonBuilder = JsonBuilder.getJsonBuilder();

    @Test
    public void creaPasso_Ok() throws JSONException {
        JSONObject result = jsonBuilder.creaPasso("questionario","linkquestionario",1);
        Assert.assertEquals("questionario", result.getString("tipo"));
        Assert.assertEquals("linkquestionario", result.getString("link"));
        Assert.assertEquals(1,result.getInt("completato"));
    }

    @Test
    public void creaProgetto_Ok() throws JSONException {
        JSONObject result = jsonBuilder.creaProgetto("nome1","descrizione1","autore1",0);
        Assert.assertEquals("nome1", result.getString("nome"));
        Assert.assertEquals("descrizione1", result.getString("descrizione"));
        Assert.assertEquals("autore1", result.getString("autore"));
        Assert.assertEquals(0,result.getInt("completato"));

    }

    @Test
    public void aggiungiPassoAllaLista_Ok() throws JSONException {
        JSONObject passo1 = jsonBuilder.creaPasso("questionario","link questionario1",0);
        JSONObject passo2 = jsonBuilder.creaPasso("pdf","link pdf",0);

        JSONArray listaPassi = new JSONArray();
        jsonBuilder.aggiungiPassoAllaLista(listaPassi,passo1);
        Assert.assertEquals(1,listaPassi.length());
        jsonBuilder.aggiungiPassoAllaLista(listaPassi,passo2);
        Assert.assertEquals(2,listaPassi.length());
    }

    @Test
    public void aggiungiListaPassi_Ok() throws JSONException {
        JSONObject progetto = new JSONObject();
        JSONArray passi = new JSONArray();
        JSONObject passo1 =new JSONObject();
        passi.put(passo1);
        jsonBuilder.aggiungiListaPassi(progetto,passi);
        Assert.assertEquals(1,progetto.getJSONArray("passi").length());
    }
}
