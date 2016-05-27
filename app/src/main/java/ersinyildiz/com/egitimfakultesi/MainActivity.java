package ersinyildiz.com.egitimfakultesi;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout SiyahBeyaz, GriTon, Kirmizi, Yesil, Mavi;
    ImageView imgCapture;
    public Bitmap bitmap;
    int PICTURE_RESULT = 1;
    Uri imageUri;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Toast.makeText(MainActivity.this, "Refresh", Toast.LENGTH_SHORT).show();
        if(item.getItemId() == R.id.share_item){
            Bitmap icon = bitmap;
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
            try {
                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
            share.putExtra(Intent.EXTRA_TEXT, "@egitimfakultesi");
            startActivity(Intent.createChooser(share, "Share Image"));
        }
        return super.onOptionsItemSelected(item);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SiyahBeyaz = (RelativeLayout) findViewById(R.id.rltSiyahBeyaz);
        GriTon = (RelativeLayout) findViewById(R.id.rltGri);
        Kirmizi = (RelativeLayout) findViewById(R.id.rltKirmizi);
        Yesil = (RelativeLayout) findViewById(R.id.rltYesil);
        Mavi = (RelativeLayout) findViewById(R.id.rltMavi);
        imgCapture = (ImageView) findViewById(R.id.imgCapture); 

        SiyahBeyaz.setOnClickListener(this);
        GriTon.setOnClickListener(this);
        Kirmizi.setOnClickListener(this);
        Yesil.setOnClickListener(this);
        Mavi.setOnClickListener(this);
        imgCapture.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        
        switch (v.getId()){
            case R.id.imgCapture:
                Toast.makeText(MainActivity.this, "Resme tıklandı", Toast.LENGTH_SHORT).show();
                takeCapture();
                break;
            
            case R.id.rltSiyahBeyaz:
                Toast.makeText(MainActivity.this, "Siyah beyaz ton", Toast.LENGTH_SHORT).show();
                Bitmap b = doBlackWhite(bitmap);
                imgCapture.setImageBitmap(b);
                Toast.makeText(MainActivity.this, "Gri Ton", Toast.LENGTH_SHORT).show();
                break;
            
            case R.id.rltGri:
                Bitmap bz = doGreyscale(bitmap);
                imgCapture.setImageBitmap(bz);
                Toast.makeText(MainActivity.this, "Gri Ton", Toast.LENGTH_SHORT).show();
                break;
            
            case R.id.rltKirmizi:
                break;
            
            case R.id.rltYesil:
                break;
            
            case R.id.rltMavi:
                break;
            
        }
        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICTURE_RESULT && resultCode == RESULT_OK){
            try {
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);
                imgCapture.setImageBitmap(thumbnail);
                bitmap = thumbnail;
                String imageurl = getRealPathFromURI(imageUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void takeCapture(){

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PICTURE_RESULT);

    }

    public static Bitmap doGreyscale(Bitmap src) {
        // constant factors
        final double GS_RED = 0.299;
        final double GS_GREEN = 0.587;
        final double GS_BLUE = 0.114;

        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        // pixel information
        int A, R, G, B;
        int pixel;

        // get image size
        int width = src.getWidth();
        int height = src.getHeight();

        // scan through every single pixel
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get one pixel color
                pixel = src.getPixel(x, y);
                // retrieve color of all channels
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                // take conversion up to one single value
                //R = G = B = (int)(GS_RED * R + GS_GREEN * G + GS_BLUE * B);
                R = G = B = ( R + G + B ) / 3;
                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return bmOut;
    }


    public static Bitmap doBlackWhite(Bitmap src) {
        // constant factors
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        // pixel information
        int A, R, G, B;
        int pixel;

        // get image size
        int width = src.getWidth();
        int height = src.getHeight();

        // scan through every single pixel
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get one pixel color
                pixel = src.getPixel(x, y);
                // retrieve color of all channels
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                // take conversion up to one single value
                //R = G = B = (int)(GS_RED * R + GS_GREEN * G + GS_BLUE * B);
                int average =  ( R + G + B ) / 3;

                if(average > 128){
                    R = G = B = 255;
                }else{
                    R = G = B = 0;
                }
                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return bmOut;
    }


}

