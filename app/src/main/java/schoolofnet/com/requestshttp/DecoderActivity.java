package schoolofnet.com.requestshttp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.JsonObject;

import javax.xml.parsers.DocumentBuilderFactory;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import schoolofnet.com.requestshttp.Utils.Http.Http;


public class DecoderActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {

    private Button btn_scan;

    private ZBarScannerView zbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decoder);

        this.btn_scan = findViewById(R.id.btn_scan);
        this.btn_scan.setOnClickListener((view) -> {
            scan();
        });

    }

    private void scan() {
        this.zbar = new ZBarScannerView(getApplicationContext());
        setContentView(this.zbar);
        this.zbar.setResultHandler(this);
        this.zbar.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.zbar.stopCamera();
    }


    @Override
    public void handleResult(Result result) {
        Http http = new Http();
        System.out.println(result.getContents());
        http.builder("http://192.168.2.63/getCampos/" + result.getContents());
        http.GET();
        String resultado = http.getResult();
        if (resultado != "false") {
            System.out.println(resultado);
            this.zbar.stopCamera();
            Intent intent = new Intent(this, DocumentActivity.class);
            intent.putExtra("documento", resultado);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Documento não encontrado", Toast.LENGTH_SHORT).show();
        }
    }

}
