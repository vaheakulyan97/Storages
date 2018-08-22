package com.example.hp.json;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private boolean isInternal = true;

    private EditText editFileName;
    private EditText editText;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editFileName = findViewById(R.id.id_file_name);
        editText = findViewById(R.id.id_file_text);
        textView = findViewById(R.id.id_print_text);
        permission();
        isChecked();
        saveButtonOnClick();
        readOnClick();
    }

    private void readOnClick() {
        final Button read = findViewById(R.id.id_read_button);
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInternal) {
                    readInInternalStorage(editFileName.getText().toString());
                } else {
                    readInExternalStorage(editFileName.getText().toString());
                }
            }
        });

    }


    private void readInInternalStorage(String editFileName) {
        try (FileInputStream fis = openFileInput(editFileName);
             InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
             BufferedReader bufferedReader = new BufferedReader(isr)) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            textView.setText(sb.toString());
        } catch (IOException e) {

        }

    }

    private void readInExternalStorage(final String fileName) {
        File extStore = Environment.getExternalStorageDirectory();
        String path = extStore.getAbsolutePath() + File.separator + fileName;
        String s;
        StringBuilder stringBuilder = new StringBuilder();
        File file = new File(path);
        try (FileInputStream fIn = new FileInputStream(file);
             BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn))){
            while ((s = myReader.readLine()) != null) {
                stringBuilder.append(s);
            }
        } catch (IOException e) {
            Log.i("fff", "fail");
        }
        textView.setText(stringBuilder.toString());
    }



    private void saveButtonOnClick() {
        final Button save = findViewById(R.id.id_save_button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInternal) {
                    createInternalStorage();
                } else {
                    saveInExternalStorage();
                }
            }
        });
    }

    private void isChecked() {

        final RadioButton internalButton = findViewById(R.id.id_internal_button);
        final RadioButton externalButton = findViewById(R.id.id_external_button);
        internalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInternal) {
                    externalButton.setChecked(true);
                    internalButton.setChecked(false);
                    isInternal = false;
                } else {
                    externalButton.setChecked(false);
                    internalButton.setChecked(true);
                    isInternal = true;
                }
            }

        });
        externalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isInternal) {
                    externalButton.setChecked(true);
                    internalButton.setChecked(false);
                    isInternal = false;
                } else {
                    externalButton.setChecked(true);
                    internalButton.setChecked(false);
                    isInternal = true;
                }

            }
        });


    }


    private void saveInExternalStorage() {
        File extStore = Environment.getExternalStorageDirectory();
        String path = extStore.getAbsolutePath() + File.separator + editFileName.getText().toString();
        String data = editText.getText().toString();
        File file = new File(path);
        try (FileOutputStream out = new FileOutputStream(file);){
            if (!file.exists()) file.createNewFile();
            OutputStreamWriter myOutWriter = new OutputStreamWriter(out);
            myOutWriter.append(data);
            myOutWriter.close();
        } catch (Exception e) {
            Log.i("fff", "fail");
        }
    }


    private void createInternalStorage() {
        File file = new File(editFileName.getText().toString());
        if (file.exists()) file.mkdir();
        try (FileOutputStream fOut = openFileOutput(file.getPath(), MODE_PRIVATE)) {
            fOut.write(editText.getText().toString().getBytes());
        } catch (Exception e) {
            Log.i("fff", "fail");
        }
        editFileName.findViewById(R.id.id_file_name);
        editText.findViewById(R.id.id_file_text);

    }


    private void permission() {
        ActivityCompat.requestPermissions(
                this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                69);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 69:
                if (grantResults.length > 0
                        && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    permission();
                }
        }
    }

}
