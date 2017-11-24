package schoolofnet.com.agiledocument;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import me.echodev.resizer.Resizer;
import schoolofnet.com.agiledocument.Model.Model.DAO.IpAddressDAO;
import schoolofnet.com.agiledocument.Utils.Http.Http;
import schoolofnet.com.agiledocument.Utils.database.DbHandler;
import schoolofnet.com.agiledocument.Utils.files.FilesUtils;

public class DocumentActivity extends AppCompatActivity {
    static final int REQUEST_TAKE_PHOTO = 1;
    private LinearLayout ViewDocumentos;
    private IpAddressDAO ipDAO;


    private ImageView img;
    private Uri uriLastPhotoTaked;
    private String caminhoPhotoCurrent;

    private JsonArray documentoJson;
    private int pos_array_Documents;
    private String identificadorDocumento = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        DbHandler handler = new DbHandler(this);
        this.ipDAO = new IpAddressDAO(handler);
        setContentView(R.layout.activity_document);

        this.ViewDocumentos = findViewById(R.id.documentos);
        this.img = findViewById(R.id.img);
        init();
    }

    private void init() {
        try {
            String resultado = getIntent().getStringExtra("documento");
            System.out.println(resultado + "linha 67");
            JsonParser parser = new JsonParser();
            this.documentoJson = new JsonArray();
            this.documentoJson = (JsonArray) parser.parse(resultado);
            buscarImagens();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buscarImagens() {
        for (int i = 0; i < this.documentoJson.size(); i++) {
            JsonElement documento = this.documentoJson.get(i);
            String identificador = documento.getAsJsonObject().get("identificador").getAsString();
            String nomeDoc = documento.getAsJsonObject().get("nome").getAsString();
            boolean arquivo = documento.getAsJsonObject().get("arquivo").isJsonNull();
            System.out.println(arquivo);
            if (!arquivo) {
                String url = this.ipDAO.getIpAddress() + "/getFile/" + identificador;
                System.out.println(url + "\n ^^ linha 86 url");
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
                    setCardViewWithImage(bitmap, nomeDoc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                documento.getAsJsonObject().addProperty("id_view", i);
                setCardViewNull(i, nomeDoc);
            }
        }
    }

    private void setCardViewNull(Integer id_view, String nomeDoc) {

        CardView cardView = new CardView(this);
        cardView.setId(id_view);

        CardView.LayoutParams lp = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 40, 0, 1);
        cardView.setLayoutParams(lp);

        //Title
        LinearLayout linhaTitle = new LinearLayout(this);
        linhaTitle.setLayoutParams(new LinearLayout.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT));
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
        btn_cad.setOnClickListener((view) -> captureImage(view, id_view));

        //Final TextView
        linhaTitle.addView(name);
        linhaTitle.addView(txtNull);
        linhaTitle.addView(btn_cad);
        cardView.addView(linhaTitle);

        this.ViewDocumentos.addView(cardView);
    }

    private void setCardViewWithImage(Bitmap image, String nomeDoc) {
        CardView cardView = new CardView(this);
        CardView.LayoutParams lp = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 40, 0, 1);
        cardView.setLayoutParams(lp);

        //Title
        LinearLayout linhaTitle = new LinearLayout(this);
        linhaTitle.setLayoutParams(new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT));
        linhaTitle.setOrientation(LinearLayout.VERTICAL);
        linhaTitle.setGravity(Gravity.CENTER);
        TextView name = new TextView(this);
        name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        name.setTextSize(18);
        name.setText(nomeDoc);
        //Final Title
        //ImageView
        ImageView img = new ImageView(this);
        img.setImageBitmap(image);
        img.setLayoutParams(new android.view.ViewGroup.LayoutParams(600, 500));

        //Final ImageView
        linhaTitle.addView(name);
        linhaTitle.addView(img);
        cardView.addView(linhaTitle);

        this.ViewDocumentos.addView(cardView);
    }

    private void captureImage(View view, Integer id_view) {
        String identificador = this.documentoJson.get(id_view).getAsJsonObject().get("identificador").getAsString();
        this.identificadorDocumento = identificador;
        dispatchTakePictureIntent();
        this.pos_array_Documents = id_view;
    }


    private boolean dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = new FilesUtils().createImageFile(this);
                this.caminhoPhotoCurrent = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                try {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "schoolofnet.com.android.fileprovider",
                            photoFile);
                    System.out.println("URI=" + photoURI);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    return true;
                } catch (Exception e) {
                    this.dispatchTakePictureIntent();
                }
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int TAKE_PHOTO_CODE = 1;
        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            Uri uri = Uri.fromFile(new File(this.caminhoPhotoCurrent));
            int color = new Color().rgb(32, 46, 174);
            AlertDialog alertConfirmaCorte = new AlertDialog.Builder(this)
                    .setTitle("SRI")
                    .setMessage("Deseja recortar este documento ?")
                    .setPositiveButton("Sim", (dialog, swith) -> {
                        dialog.dismiss();
                        UCrop.Options options = new UCrop.Options();
                        options.setCompressionQuality(100);
                        options.setFreeStyleCropEnabled(true);
                        options.setStatusBarColor(color);
                        // options.setToolbarWidgetColor(color);
                        options.setActiveWidgetColor(color);
                        options.setToolbarColor(color); //Cor da barra Superior
                        options.setToolbarTitle("SRI");
                        UCrop.of(uri, uri).withOptions(options).start(this);
                    })
                    .setNegativeButton("Não", (dialog, swith) -> {
                        try {
                            dialog.dismiss();
                            this.uriLastPhotoTaked = uri;
                            File img = new File(this.uriLastPhotoTaked.getPath());
                            String url = this.ipDAO.getIpAddress() + "/sendFile/" + this.identificadorDocumento;
                            File imgForSend = new Resizer(this)
                                    .setSourceImage(img)
                                    .setQuality(90)
                                    .setTargetLength(720)
                                    .getResizedFile();
                            new Http().enviarDocumento(url, imgForSend);
                            setImageInCardViewAfterPOST(this.pos_array_Documents, imgForSend);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }).show();
        }
        //UCrop Finish
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            this.uriLastPhotoTaked = UCrop.getOutput(data);
            File img = new File(this.uriLastPhotoTaked.getPath());
            String url = this.ipDAO.getIpAddress() + "/sendFile/" + this.identificadorDocumento;
            File imgForSend = null;
            try {
                imgForSend = new Resizer(this)
                        .setQuality(90)
                        .setSourceImage(img)
                        .getResizedFile();
                new Http().enviarDocumento(url, imgForSend);
                setImageInCardViewAfterPOST(this.pos_array_Documents, imgForSend);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (resultCode == UCrop.RESULT_ERROR) {
            System.out.println("new error line 133");
            final Throwable cropError = UCrop.getError(data);
        }
    }

    private void setImageInCardViewAfterPOST(int pos_array_Documents, File imageForSet) {
        JsonElement jsonElement_documento = this.documentoJson.get(pos_array_Documents);
        String campo = jsonElement_documento.getAsJsonObject().get("nome").getAsString();
        CardView cardView = findViewById(pos_array_Documents);
        cardView.removeAllViews();
        LinearLayout linear = new LinearLayout(this);
        linear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linear.setOrientation(LinearLayout.VERTICAL);
        linear.setGravity(Gravity.CENTER);
        TextView txtCampo = new TextView(this);
        txtCampo.setText(campo);
        txtCampo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        txtCampo.setTextSize(18);
        linear.addView(txtCampo);
        ImageView img = new ImageView(this);
        img.setImageURI(Uri.fromFile(imageForSet));
        img.setLayoutParams(new android.view.ViewGroup.LayoutParams(600, 500));
        linear.addView(img);
        cardView.addView(linear);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_out, R.anim.slide_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}