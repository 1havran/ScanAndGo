package sk.hppa.scanandgo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    final int PIC_GET_FROM_CAMERA = 1;
    final int PIC_GET_FROM_GALLERY = 2;
    ImageView mIvScan;
    ImageView mIvLogo;
    String pathScanPicture = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIvScan = (ImageView) findViewById(R.id.ivScan);
        mIvLogo = (ImageView) findViewById(R.id.ivLogo);

        mIvLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                Toast.makeText(MainActivity.this, "Hello World", Toast.LENGTH_SHORT);
                //TODO
            }
        });

        mIvScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                pathScanPicture = getImageFromCamera();
            }
        });

        mIvScan.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //TODO
                //Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //startActivityForResult(i, PIC_GET_FROM_GALLERY);
                runOneDrive();
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PIC_GET_FROM_CAMERA && resultCode == RESULT_OK) {
            mIvScan.setImageBitmap(resizePicture(pathScanPicture, mIvScan.getMaxWidth(), mIvScan.getMaxHeight()));
            galleryAddPic(pathScanPicture);
        }
        if (requestCode == PIC_GET_FROM_GALLERY && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();

            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            pathScanPicture = cursor.getString(columnIndex);
            cursor.close();

            mIvScan.setImageBitmap(resizePicture(pathScanPicture, mIvScan.getMaxWidth(), mIvScan.getMaxHeight()));
        }
    }

    private String getImageFromCamera() {
        String result = "";
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT);
            }
            if (photoFile!= null) {
                result = photoFile.getAbsolutePath();
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri photoURI = FileProvider.getUriForFile(this, "sk.hppa.scanandgo.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, PIC_GET_FROM_CAMERA);
            }
        }
        return result;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    private Bitmap resizePicture(String photoPath, int targetW, int targetH) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeFile(photoPath, bmOptions);
    }

    private void galleryAddPic(String photoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    void runOneDrive() {
    }
}
