package schoolofnet.com.agiledocument;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import schoolofnet.com.agiledocument.Model.Model.DAO.IpAddressDAO;
import schoolofnet.com.agiledocument.Utils.RequestPermissions;
import schoolofnet.com.agiledocument.Utils.database.DbHandler;

public class MainActivity extends AppCompatActivity {

    private LinearLayout open_qrcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.open_qrcode = findViewById(R.id.open_qrcode);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        this.open_qrcode.setOnClickListener((view) -> {
            openQRCode(view);
        });
        requestPermissionsForAPP();

        //   RequestPermissions permissions = new RequestPermissions();
        // permissions.pedidoDePermissoes(this);
    }

    private void openQRCode(View view) {
        Intent intent = new Intent(this, DecoderActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    private void requestPermissionsForAPP() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    100);
        }
    }

}
