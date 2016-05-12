package pl.osik.autyzm.dzieci;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Environment;
import android.print.pdf.PrintedPdfDocument;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.sql.Dziecko;
import pl.osik.autyzm.sql.User;

public class DzieciStatisticsActivity extends AppCompatActivity implements YAxisValueFormatter, ValueFormatter, View.OnClickListener {


    private final int X_ANGLE = 315;
    private HashMap<String, String> dziecko;
    private String imieINazwisko;

    /* PDF Fields */
    Document document;
    private String FILE_NAME;
    private String OUTPUT_FILE;
    private Font titleFont;
    private final int RESIZE_WIDTH = 100;
    private final int RESIZE_HEIGHT = 100;
    private final int MARGIN_TOP = 50;
    private LineChart chartPdf = new LineChart(this);

    @Bind(R.id.container)
    LinearLayout container;
    @Bind(R.id.save)
    FloatingActionButton saveButton;
    @Bind(R.id.wykres)
    LineChart chart;
    @Bind(R.id.wykres_invisible)
    LineChart chart2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dzieci_statistics);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        dziecko = (HashMap) intent.getSerializableExtra(Dziecko.TABLE_NAME);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imieINazwisko = dziecko.get(Dziecko.COLUMN_NAZWISKO) + " " + dziecko.get(Dziecko.COLUMN_IMIE);
        getSupportActionBar().setTitle(imieINazwisko);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            chart2.setVisibility(View.GONE);
        //TODO drugi wykres

        FILE_NAME = getString(R.string.dzieci_statistics_pdf_title) + ".pdf";
        //FINALLY Uncomment
        //OUTPUT_FILE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/" + FILE_NAME;
        OUTPUT_FILE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + ".pdf";
        titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.NORMAL, new BaseColor(getResources().getColor(R.color.colorPrimary)));

        createChart(chart);
        createChart(chartPdf);
        saveButton.setOnClickListener(this);
    }

    private void createChart(LineChart chart) {
        YAxis yaxis = chart.getAxisLeft();
        YAxis yaxis2 = chart.getAxisRight();
        XAxis xaxis = chart.getXAxis();

        yaxis2.setEnabled(false);
        yaxis.setAxisMinValue(0);
        yaxis.setAxisMaxValue(1.2f);
        yaxis.setValueFormatter(this);

        xaxis.setLabelRotationAngle(X_ANGLE);
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xaxis.setDrawGridLines(false);
        chart.setDescription(getString(R.string.dzieci_statistics_postep_w_nauce));
        ArrayList<Entry> linia = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<String>();
        LinkedHashMap<String, Float> statistics = Dziecko.getStatistics(Integer.parseInt(dziecko.get(Dziecko.COLUMN_ID)));
        if(statistics.size() > 0) {
            xVals.add("");
            int count = 1;
            for (Map.Entry<String, Float> entry : statistics.entrySet()) {
                xVals.add(entry.getKey());
                Entry e = new Entry(entry.getValue(), count++);
                linia.add(e);
            }
            xVals.add("");
            LineDataSet set1 = new LineDataSet(linia, getString(R.string.dzieci_statistics_liczba_punktow));
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(xVals, dataSets);
            data.setValueFormatter(this);
            chart.setData(data);
            chart.setPinchZoom(true);
            chart.animateX(1500, Easing.EasingOption.EaseInOutQuart);
        }

        if(chart.getId() == chartPdf.getId()) {
            chart.setMinimumHeight(2500);
        }
        chart.setNoDataText(getString(R.string.dzieci_statistics_brak_lekcji));
        chart.invalidate();


    }

    /* Creating PDF */
    private boolean saveAsPdf() {
        try {
            document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, new FileOutputStream(OUTPUT_FILE));
            document.open();
            addMetaData();
            addContent();
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
            return false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        Log.d("saveAsPdf", OUTPUT_FILE);
        return true;
    }

    private void addMetaData() {
        document.addTitle(getString(R.string.dzieci_statistics_pdf_title));
        document.addSubject(imieINazwisko);
        document.addAuthor(User.getCurrentName());
        document.addCreator(getString(R.string.app_name));
    }

    private void addContent() throws DocumentException {
        Paragraph main = new Paragraph();
        main.add(new Paragraph(getString(R.string.dzieci_statistics_pdf_title), titleFont));
        addEmptyLine(main, 1);

        /* Add chart */
        LineChart chart = chartPdf;
        Bitmap bitmap = chart.getChartBitmap();
        ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);
        byte[] chartByteArray = bitmapStream.toByteArray();
        try {
            Image image = Image.getInstance(chartByteArray);
            image.scaleToFit(PageSize.A4.getHeight() - RESIZE_WIDTH, PageSize.A4.getWidth() - RESIZE_HEIGHT);
            image.setAbsolutePosition(0, MARGIN_TOP);
            main.add(image);
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        document.add(main);
    }

    private void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
    /* END Creating PDF */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        NumberFormat format = NumberFormat.getPercentInstance();
        return format.format(value);
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return getFormattedValue(value, null);
    }

    /* FAB Onclick */
    @Override
    public void onClick(View v) {
        TextView popupText = new TextView(this);
        Button insidePopupButton = new Button(this);
        LinearLayout layoutOfPopup = new LinearLayout(this);
        insidePopupButton.setText(getString(R.string.button_ok));
        popupText.setPadding(0, 0, 0, 20);
        layoutOfPopup.setOrientation(LinearLayout.VERTICAL);
        layoutOfPopup.addView(popupText);
        layoutOfPopup.addView(insidePopupButton);
        final PopupWindow popupMessage = new PopupWindow(layoutOfPopup, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupMessage.setContentView(layoutOfPopup);
        insidePopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMessage.dismiss();
            }
        });

        if(saveAsPdf()) {
            popupText.setText(getString(R.string.dzieci_statistics_pdf_save_ok) + FILE_NAME);
        } else {
            popupText.setText(getString(R.string.dzieci_statistics_pdf_save_error) + FILE_NAME);
        }

        popupMessage.showAtLocation(container, Gravity.CENTER, 0, 0);
    }
}
