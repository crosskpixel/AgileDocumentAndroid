package schoolofnet.com.agiledocument;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import schoolofnet.com.agiledocument.Model.Model.DAO.IpAddressDAO;
import schoolofnet.com.agiledocument.Utils.database.DbHandler;

public class SettingsActivity extends AppCompatActivity {

    private Button btn_save_ipAddress;
    private Button test;
    private EditText txtIpAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.test = findViewById(R.id.test);
        this.txtIpAddress = findViewById(R.id.txtIpAddress);
        this.btn_save_ipAddress = findViewById(R.id.btn_save_ipaddress);
        init();
    }

    private void init() {
        this.btn_save_ipAddress.setOnClickListener((view) -> processIpAddress(view));
        this.test.setOnClickListener((view) -> {
            DbHandler handler = new DbHandler(this);
            IpAddressDAO ipDAO = new IpAddressDAO(handler);
            System.out.println(ipDAO.getIpAddress());
        });
    }

    private void processIpAddress(View view) {
        try {
            String ip = this.txtIpAddress.getText().toString();
            System.out.println(ip);
            DbHandler handler = new DbHandler(this);
            IpAddressDAO ipDAO = new IpAddressDAO(handler);
            if (ipDAO.findAddressValid(ip)) {
                Toast.makeText(this, "Address Configured with Successful !!! " +
                        "\n Thanks for use AgileDocument", Toast.LENGTH_LONG).show();
                Thread.sleep(500);
                onBackPressed();
            } else {
                this.txtIpAddress.setText("");
                if (ip.contains(":")) {
                    Toast.makeText(this, "This ip not is Valid", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Port not search !!!", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
