package schoolofnet.com.requestshttp.Utils.Http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Http {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient okClient;
    private String url;
    private String resultado;

    public Http() {
        this.okClient = new OkHttpClient();
    }

    public Http builder(String url) {
        this.url = url;
        return this;
    }

    public void GET() {
        Request request = new Request.Builder().url(this.url).build();
        try {
            Response response = okClient.newCall(request).execute();
            this.resultado = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void POST(String jsonParam) {
        try {
            RequestBody body = RequestBody.create(JSON, jsonParam);

            Request request = new Request.Builder()
                    .url(this.url)
                    .post(body)
                    .build();
            Response response = okClient.newCall(request).execute();
            this.resultado = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public JsonObject getResultJSON() {
        JsonParser parser = new JsonParser();
        JsonObject obj = new JsonObject();
        obj = (JsonObject) parser.parse(this.resultado);
        return obj;
    }

    public Document getResultXML() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            InputSource src = new InputSource(new StringReader(this.resultado));
            Document doc = builder.parse(src);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getResult() {
        return this.resultado;
    }

}
