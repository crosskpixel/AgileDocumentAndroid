package schoolofnet.com.requestshttp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import schoolofnet.com.requestshttp.Utils.Http.Http;

public class AddContactActivity extends AppCompatActivity {

    private EditText txtName;
    private EditText txtSobrenome;
    private EditText txtEmail;
    private EditText txtTelefone;

    private Button btn_save;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact);

        txtName = findViewById(R.id.txt_nome);
        txtSobrenome = findViewById(R.id.txt_sobrenome);
        txtEmail = findViewById(R.id.txt_email);
        txtTelefone = findViewById(R.id.txt_telefone);
        this.btn_save = findViewById(R.id.btn_save);

        btn_save.setOnClickListener((view) -> {
            try {
                save(view);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

    }


    public void save(View view) throws JSONException {

        JSONObject json = new JSONObject();
        json.put("nome", "Igor Ferreira Praxedes");

        Http http = new Http();
        http.builder("http://192.168.1.185:3000/android")
                .POST(json.toString());


    }

}
