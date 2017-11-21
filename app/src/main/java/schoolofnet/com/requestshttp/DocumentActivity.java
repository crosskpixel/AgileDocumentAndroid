package schoolofnet.com.requestshttp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import schoolofnet.com.requestshttp.Utils.Http.Http;

import static schoolofnet.com.requestshttp.MainActivity.REQUEST_IMAGE_CAPTURE;

public class DocumentActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1; //


    private JsonArray documentoJson;

    private LinearLayout divDocumentos;

    private ImageView img;

    private Uri photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        this.divDocumentos = findViewById(R.id.documentos);
        this.img = findViewById(R.id.img);

        String resultado = getIntent().getStringExtra("documento");
        JsonParser parser = new JsonParser();
        this.documentoJson = new JsonArray();
        this.documentoJson = (JsonArray) parser.parse(resultado);
        buscarImagens();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    private void buscarImagens() {

        for (JsonElement documento : this.documentoJson) {
            String identificador = documento.getAsJsonObject().get("identificador").getAsString();
            String nomeDoc = documento.getAsJsonObject().get("nome").getAsString();
            boolean arquivo = documento.getAsJsonObject().get("arquivo").isJsonNull();
            System.out.println(arquivo);
            if (!arquivo) {
                String url = "http://192.168.2.63/getFile/" + identificador;
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
                    setCardViewWithImage(bitmap, nomeDoc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                setCardViewNull(identificador, nomeDoc);
            }
        }

    }

    private void setCardViewNull(String identificador, String nomeDoc) {

        CardView cardView = new CardView(this);

        CardView.LayoutParams lp = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 40, 0, 1);
        cardView.setLayoutParams(lp);

        //Title
        LinearLayout linhaTitle = new LinearLayout(this);
        linhaTitle.setLayoutParams(new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT));
        linhaTitle.setOrientation(LinearLayout.VERTICAL);
        TextView name = new TextView(this);
        name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        name.setTextSize(18);
        name.setText(nomeDoc);
        //Final Title

        //TextView

        TextView txtNull = new TextView(this);
        txtNull.setText("Não foi encontrado nenhuma Imagem !!!");
        txtNull.setTextSize(10);
        txtNull.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        txtNull.setBackgroundColor(Color.GRAY);

        Button btn_cad = new Button(this);
        btn_cad.setText("CAPTURAR");
        btn_cad.setBackgroundResource(R.color.colorPrimary);
        btn_cad.setOnClickListener((view) -> captureImage(view, identificador));

        //Final TextView
        linhaTitle.addView(name);
        linhaTitle.addView(txtNull);
        linhaTitle.addView(btn_cad);
        cardView.addView(linhaTitle);

        this.divDocumentos.addView(cardView);

    }

    private void captureImage(View view, String identificador) {
        //openCamera();
        dispatchTakePictureIntent();
        File file = new File(this.mCurrentPhotoPath);
        this.img.setImageURI(Uri.fromFile(file));
    }

    private void setCardViewWithImage(Bitmap image, String nomeDoc) {
        CardView cardView = new CardView(this);
        CardView.LayoutParams lp = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);
        lp.setMargins(20, 50, 0, 1);
        cardView.setLayoutParams(lp);

        //Title
        LinearLayout linhaTitle = new LinearLayout(this);
        linhaTitle.setLayoutParams(new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT));
        linhaTitle.setOrientation(LinearLayout.VERTICAL);
        TextView name = new TextView(this);
        name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        name.setTextSize(18);
        name.setText(nomeDoc);
        //Final Title
        //ImageView
        ImageView img = new ImageView(this);
        img.setImageBitmap(image);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        img.setLayoutParams(new android.view.ViewGroup.LayoutParams(600, 400));

        //Final ImageView
        linhaTitle.addView(name);
        linhaTitle.addView(img);
        cardView.addView(linhaTitle);

        this.divDocumentos.addView(cardView);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            this.photoURI = data.getData();
            System.out.println(this.photoURI);
            this.img.setImageURI(this.photoURI);

            UCrop.Options options = new UCrop.Options();
            options.setCompressionQuality(100);
            options.setMaxBitmapSize(99999);

            UCrop.of(this.photoURI, this.photoURI).withOptions(options)
                    .withMaxResultSize(1366, 768)
                    .start(this);
        }

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "schoolofnet.com.android.FileProvider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    String mCurrentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


}