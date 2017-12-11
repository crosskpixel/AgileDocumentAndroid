package schoolofnet.com.agiledocument;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import schoolofnet.com.agiledocument.Model.Model.DAO.IpAddressDAO;
import schoolofnet.com.agiledocument.Utils.database.DbHandler;

public class SettingsActivity extends AppCompatActivity {

    private Button btn_save_ipAddress;
    private EditText txtIpAddress;

    private String actualIpAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        this.txtIpAddress = findViewById(R.id.txtIpAddress);
        this.btn_save_ipAddress = findViewById(R.id.btn_save_ipaddress);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        DbHandler db = new DbHandler(this);
        IpAddressDAO ipDAO = new IpAddressDAO(db);
        this.actualIpAddress = ipDAO.getIpAddress();
        if (this.actualIpAddress.length() == 0 || this.actualIpAddress == null || this.actualIpAddress == "null") {
            this.txtIpAddress.setText("");
            this.btn_save_ipAddress.setEnabled(false);
            this.btn_save_ipAddress.setBackgroundColor(Color.parseColor("#9ba9ff"));
        } else {
            this.txtIpAddress.setText(this.actualIpAddress.replace("http://", ""));
        }
        init();
    }

    private void init() {
        this.btn_save_ipAddress.setOnClickListener((view) -> processIpAddress(view));
        this.txtIpAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (after == 0) {
                    btn_save_ipAddress.setBackgroundColor(Color.parseColor("#9ba9ff"));
                    btn_save_ipAddress.setEnabled(false);
                } else {
                    btn_save_ipAddress.setEnabled(true);
                    btn_save_ipAddress.setBackgroundColor(Color.parseColor("#4960f1"));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void processIpAddress(View view) {
        String ip = this.txtIpAddress.getText().toString();
        if (ip != "") {
            try {
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
                    Toast.makeText(this, "I could not find the server for this address", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
