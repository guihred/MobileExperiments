package red.guih.games.puzzle;


import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;

import java.io.InputStream;

import red.guih.games.BaseActivity;
import red.guih.games.R;
import red.guih.games.db.UserRecord;

public class PuzzleActivity extends BaseActivity {

    public static final String SELECT_PHOTO_TAG = "SELECT_PHOTO";
    private static final int SELECT_PHOTO = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        ActionBar a = getSupportActionBar();
        if (a != null) {
            a.setTitle(R.string.puzzle);
            a.setDisplayHomeAsUpEnabled(true);
        }
        setUserPreferences();
    }

    private void setUserPreferences() {
        PuzzleView.setPuzzleDimensions(getUserPreference(R.string.size, PuzzleView.puzzleWidth));
        PuzzleView.setImage(getUserPreference(R.string.image, R.drawable.mona_lisa));

        String photoUri = getUserPreference(R.string.photo, null);
        if (photoUri != null) {
            loadURI(Uri.parse(photoUri));
        }

    }

    private void loadURI(Uri uri) {
        if (uri != null && uri.getPath() != null) {
            if (ActivityCompat
                    .checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, SELECT_PHOTO);
            }
            Log.i(SELECT_PHOTO_TAG, uri.toString());
            Log.i(SELECT_PHOTO_TAG, uri.getEncodedPath());
            Log.i(SELECT_PHOTO_TAG, uri.getPath().replaceAll(".+:", ""));
            try (InputStream imageStream = getContentResolver().openInputStream(uri)) {
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                PuzzleView.setImage(selectedImage);
                addUserPreference(R.string.photo, "file:" + uri.getPath().replaceAll(".+:", ""));
                recreate();
            } catch (Exception e) {
                Log.e(SELECT_PHOTO_TAG, e.getMessage(), e);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_records, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int i = item.getItemId();
        if (i == R.id.config) {
            showConfig();
            return true;
        } else if (i == R.id.records) {
            showRecords(PuzzleView.puzzleWidth, UserRecord.PUZZLE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showConfig() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.puzzle_config_dialog);
        dialog.setTitle(R.string.config);
        // set the custom minesweeper_dialog components - text, image and button
        Spinner spinner = dialog.findViewById(R.id.spinner1);
        NumberPicker seekBar = dialog.findViewById(R.id.number);
        seekBar.setValue(PuzzleView.puzzleWidth);
        Button dialogButton = dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom minesweeper_dialog
        dialogButton.setOnClickListener(v -> onClickConfigButton(dialog, spinner));
        dialog.show();
    }

    private void onClickConfigButton(Dialog dialog, Spinner spinner) {
        NumberPicker seekBar = dialog.findViewById(R.id.number);
        int progress = seekBar.getValue();
        int selectedItemPosition = spinner.getSelectedItemPosition();
        PuzzleView.setPuzzleDimensions(progress);
        addUserPreference(R.string.size, progress);

        dialog.dismiss();
        PuzzleView.setImage(null);
        if (selectedItemPosition == 0) {
            PuzzleView.setImage(R.drawable.mona_lisa);
        } else if (selectedItemPosition == 2) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            return;
        } else {
            PuzzleView.setImage(R.drawable.mona_lisa);
        }
        addUserPreference(R.string.image, PuzzleView.puzzleImage);

        recreate();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK) {
            loadURI(imageReturnedIntent.getData());
        }
    }

}
