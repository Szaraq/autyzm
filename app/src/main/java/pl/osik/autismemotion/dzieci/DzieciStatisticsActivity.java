package pl.osik.autismemotion.dzieci;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

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
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autismemotion.R;
import pl.osik.autismemotion.helpers.AppHelper;
import pl.osik.autismemotion.sql.Dziecko;
import pl.osik.autismemotion.sql.Odpowiedz;
import pl.osik.autismemotion.sql.User;

public class DzieciStatisticsActivity extends AppCompatActivity implements YAxisValueFormatter, ValueFormatter, View.OnClickListener {

    private HashMap<String, String> dziecko;
    private String imieINazwisko;

    /* PDF Fields */
    Document document;
    private String FILE_NAME;
    private String OUTPUT_FILE;
    private Font titleFont;

    @Bind(R.id.chart_container)
    FrameLayout chartContainer;
    @Bind(R.id.container)
    LinearLayout container;
    @Bind(R.id.save)
    FloatingActionButton saveButton;
    @Bind(R.id.wykres)
    LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dzieci_statistics);
        ButterKnife.bind(this);
        final Intent intent = getIntent();
        dziecko = (HashMap) intent.getSerializableExtra(Dziecko.TABLE_NAME);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imieINazwisko = dziecko.get(Dziecko.COLUMN_IMIE) + " " + dziecko.get(Dziecko.COLUMN_NAZWISKO);
        getSupportActionBar().setTitle(imieINazwisko);

        FILE_NAME = getString(R.string.dzieci_statistics_pdf_title) + "-" + imieINazwisko + ".pdf";
        OUTPUT_FILE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/" + FILE_NAME;
        titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.NORMAL, new BaseColor(getResources().getColor(R.color.colorPrimary)));

        createChart(chart);
        saveButton.setOnClickListener(this);
        resizeChart();
        if(chart.isEmpty()) saveButton.setVisibility(View.GONE);
    }

    private void createChart(LineChart chart) {
        final YAxis yaxis = chart.getAxisLeft();
        final YAxis yaxis2 = chart.getAxisRight();
        final XAxis xaxis = chart.getXAxis();

        yaxis2.setEnabled(false);
        yaxis.setAxisMinValue(0);
        yaxis.setAxisMaxValue(1.2f);
        yaxis.setValueFormatter(this);

        int x_ANGLE = 315;
        xaxis.setLabelRotationAngle(x_ANGLE);
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xaxis.setDrawGridLines(false);
        chart.setDescription(getString(R.string.dzieci_statistics_postep_w_nauce));
        final ArrayList<Entry> linia = new ArrayList<>();
        final ArrayList<String> xVals = new ArrayList<String>();
        final LinkedHashMap<String, Float> statistics = Dziecko.getStatistics(Integer.parseInt(dziecko.get(Dziecko.COLUMN_ID)));
        if(statistics.size() > 0) {
            xVals.add("");
            int count = 1;
            for (Map.Entry<String, Float> entry : statistics.entrySet()) {
                xVals.add(entry.getKey());
                Entry e = new Entry(rescale(entry.getValue()), count++);
                linia.add(e);
            }
            xVals.add("");
            final LineDataSet set1 = new LineDataSet(linia, getString(R.string.dzieci_statistics_liczba_punktow));
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            final ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            final LineData data = new LineData(xVals, dataSets);
            data.setValueFormatter(this);
            chart.setData(data);
            chart.setPinchZoom(true);
            chart.animateX(1500, Easing.EasingOption.EaseInOutQuart);
        }
        chart.setNoDataText(getString(R.string.dzieci_statistics_brak_lekcji));
        chart.invalidate();

    }

    /* Trzeba przeskalować wynik na % */
    private float rescale(float points) {
        return points / Odpowiedz.MAX_VAL;
    }

    /* Creating PDF */
    private boolean saveAsPdf() {
        try {
            final File file = new File(OUTPUT_FILE);
            Log.d("Stats", OUTPUT_FILE);
            file.getParentFile().mkdirs();
            file.createNewFile();
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
        } catch (IOException e) {
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
        final Paragraph main = new Paragraph();
        main.add(new Paragraph(getString(R.string.dzieci_statistics_pdf_title) + " (" + imieINazwisko + ")", titleFont));
        addEmptyLine(main, 1);

        /* Add chart */
        final Bitmap bitmap = chart.getChartBitmap();
        final ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);
        final byte[] chartByteArray = bitmapStream.toByteArray();
        try {
            final Image image = Image.getInstance(chartByteArray);
            int RESIZE_HEIGHT = 100;
            int RESIZE_WIDTH = 100;
            image.scaleToFit(PageSize.A4.getHeight() - RESIZE_WIDTH, PageSize.A4.getWidth() - RESIZE_HEIGHT);
            int MARGIN_TOP = 50;
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
        final NumberFormat format = NumberFormat.getPercentInstance();
        return format.format(value);
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return getFormattedValue(value, null);
    }

    /* FAB Onclick */
    @Override
    public void onClick(View v) {
        String popupText = "";

        if(saveAsPdf()) {
            popupText = getString(R.string.dzieci_statistics_pdf_save_ok) + FILE_NAME;
        } else {
            popupText = getString(R.string.dzieci_statistics_pdf_save_error) + FILE_NAME;
        }
        AppHelper.showMessage(container, popupText, Snackbar.LENGTH_LONG);
    }

    private void resizeChart() {
        final int MARGIN_WIDTH = 150;
        final int MARGIN_HEIGHT = 300;
        final Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        final ViewGroup.LayoutParams params = chartContainer.getLayoutParams();

        if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            params.height = size.y - MARGIN_HEIGHT;
            params.width = (int) (params.height * 1.5);
        } else {
            params.width = size.x - MARGIN_WIDTH;
            params.height = (int) (params.width / 1.5);
        }
        chartContainer.setLayoutParams(params);
        container.invalidate();
        chartContainer.invalidate();
        chart.invalidate();
    }
}
