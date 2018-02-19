import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import sim.util.media.chart.TimeSeriesChartGenerator;

import javax.swing.*;
import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by daniel on 21/04/17.
 */
public class XYSeriesPlot {

    static public class DataRecorder extends XYSeries {
        static public int MAX_DATA_LEN = 5000;	// Maximum number of data points to remember
        static public int CHUNK_SIZE = 50;		// number of datapoints to delete at a time

        Callable<XYDataItem> lambda;


        public DataRecorder(String title, Callable<XYDataItem> lambda) {
            super(title, false);
            this.lambda = lambda;
        }

        public void record(double timestamp) {
            try {
                XYDataItem data = lambda.call();
                add(data.getX(), data.getY(), true);
                if(getItemCount() > MAX_DATA_LEN) delete(0,CHUNK_SIZE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    TimeSeriesChartGenerator chart;
    ArrayList<XYSeriesPlot.DataRecorder> dataSeries;


    public XYSeriesPlot(String title, String xAxisLabel, String yAxisLabel) {
        dataSeries = new ArrayList<>(4);
        chart = new TimeSeriesChartGenerator();

        chart.setTitle(title);
        chart.setYAxisLabel(yAxisLabel);
        chart.setXAxisLabel(xAxisLabel);
    }


    public void addToPane(JTabbedPane pane) {
        pane.addTab(chart.getTitle(), chart);
    }


    public XYSeriesPlot addVariable(String title, Callable<XYDataItem> lambda) {
        XYSeriesPlot.DataRecorder series = new XYSeriesPlot.DataRecorder(title, lambda);
        dataSeries.add(series);
        chart.addSeries(series, null);
        return(this);
    }


    public void recordValues(double t) {
        for(XYSeriesPlot.DataRecorder series : dataSeries) {
            series.record(t);
        }
    }

}
