package schoolofnet.com.agiledocument.Model.Model.DAO;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import schoolofnet.com.agiledocument.Model.IpAddress;
import schoolofnet.com.agiledocument.Utils.database.DbHandler;

/**
 * Created by Mactus on 23/11/2017.
 */

public class IpAddressDAO {

    private DbHandler handler;

    public IpAddressDAO(DbHandler handler) {
        this.handler = handler;
    }

    public boolean findAddressValid(String ip) {
        try {
            OkHttpClient.Builder b = new OkHttpClient.Builder();
            b.connectTimeout(290, TimeUnit.MILLISECONDS);
            OkHttpClient client = b.build();
            ip = "http://" + ip;
            Request request = new Request.Builder().url(ip+"/connect").get().build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            if (res.equals("true")) {
                saveIpAdress(ip);
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean saveIpAdress(String ip) {
        System.out.println("SALVANDO IP NO SQLITE");
        SQLiteDatabase db = handler.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ipAddress", ip);
        db.update("tbl_ip", cv, "id = ?", new String[]{String.valueOf(1)});
        return true;
    }

    public String getIpAddress() {
        SQLiteDatabase db = handler.getWritableDatabase();
        try {
            Cursor cursor = db.query("tbl_ip", new String[]{"id", "ipAddress"},
                    "id = ?", new String[]{String.valueOf(1)}, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                IpAddress ip = new IpAddress();
                ip.setId(cursor.getInt(0));
                ip.setIpAddress(cursor.getString(1));
                return ip.getIpAddress();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

}
