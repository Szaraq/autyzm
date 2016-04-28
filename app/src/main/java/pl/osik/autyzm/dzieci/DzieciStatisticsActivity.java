package pl.osik.autyzm.dzieci;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.sql.Dziecko;

public class DzieciStatisticsActivity extends AppCompatActivity implements YAxisValueFormatter, ValueFormatter {

    private final int X_ANGLE = 315;
    private int id;

    @Bind(R.id.wykres)
    LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dzieci_statistics);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        id = intent.getIntExtra(Dziecko.COLUMN_ID, 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Dziecko.getImieINazwiskoByID(id));
        createChart();
    }

    private void createChart() {
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
        LinkedHashMap<String, Float> statistics = Dziecko.getStatistics(id);
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

        chart.setNoDataText(getString(R.string.dzieci_statistics_brak_lekcji));
        chart.invalidate();
    }

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
}
