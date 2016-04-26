package pl.osik.autyzm.dzieci;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;

public class DzieciStatisticsActivity extends AppCompatActivity {

    @Bind(R.id.wykres)
    LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dzieci_statistics);
        ButterKnife.bind(this);


        ArrayList<Entry> linia1 = new ArrayList<>();
        Entry e = new Entry(100, 0);
        Entry e2 = new Entry(200, 1);
        linia1.add(e);
        linia1.add(e2);
        LineDataSet set1 = new LineDataSet(linia1, "Linia pierwsza");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("1.Q"); xVals.add("2.Q");
        LineData data = new LineData(xVals, dataSets);
        chart.setData(data);
        chart.setNoDataTextDescription("Dziecko nie przeszło jeszcze żadnej lekcji");
        chart.setPinchZoom(true);
        chart.animateX(1500, Easing.EasingOption.EaseInOutQuart);
        chart.invalidate();
    }
}
