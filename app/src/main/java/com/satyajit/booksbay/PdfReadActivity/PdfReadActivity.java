package com.satyajit.booksbay.PdfReadActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.satyajit.booksbay.R;

import java.io.File;

public class PdfReadActivity extends AppCompatActivity {


    File file;
    com.github.barteksc.pdfviewer.PDFView pdfView;
    int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_read);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {


                file = new File(extras.getString("FILE"));
                flag = extras.getInt("NM",0);

            }
        }


        initUI();

        loadPdf();


    }

    void initUI(){

        pdfView = findViewById(R.id.pdfView);


    }

    void loadPdf(){



        if (flag == 1) {


            pdfView.fromFile(file)
                    .spacing(0)
                    .autoSpacing(false)
                    .nightMode(true)
                    .pageFitPolicy(FitPolicy.WIDTH) // mode to fit pages in the view
                    .fitEachPage(false)
                    .load();

        }
        else {


            pdfView.fromFile(file)
                    .spacing(0)
                    .autoSpacing(false)
                    .pageFitPolicy(FitPolicy.WIDTH) // mode to fit pages in the view
                    .fitEachPage(false)
                    .load();
        }




    }

}
