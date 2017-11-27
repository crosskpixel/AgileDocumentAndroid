package schoolofnet.com.agiledocument;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import schoolofnet.com.agiledocument.Model.Model.DAO.IpAddressDAO;
import schoolofnet.com.agiledocument.Utils.Http.Http;
import schoolofnet.com.agiledocument.Utils.database.DbHandler;

public class DecoderActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {

    private ZBarScannerView zbar;
    private IpAddressDAO ipDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DbHandler handler = new DbHandler(this);
        this.ipDAO = new IpAddressDAO(handler);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_decoder);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        scan();
    }

    private void scan() {
        this.zbar = new ZBarScannerView(getApplicationContext());
        setContentView(this.zbar);
        this.zbar.setResultHandler(this);
        this.zbar.startCamera();
    }

    @Override
    public void handleResult(Result result) {
        this.zbar.stopCamera();
        Http http = new Http();
        System.out.println(result.getContents());
        String url = this.ipDAO.getIpAddress() + "/getFields/" + result.getContents();
        http.builder(url);
        try {
            http.GET();
            String resultado = http.getResult();
            System.out.println(resultado);
            if (resultado != "false" && resultado != null) {
                Intent intent = new Intent(this, DocumentActivity.class);
                intent.putExtra("documento", resultado);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Documento não encontrado", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        this.zbar.stopCamera();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.zbar.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.zbar.startCamera();
    }
}
