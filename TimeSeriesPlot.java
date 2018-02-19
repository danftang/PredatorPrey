
import java.util.ArrayList;
import java.util.concurrent.Callable;

import javax.swing.JTabbedPane;

import org.jfree.data.xy.XYSeries;
import sim.util.media.chart.TimeSeriesChartGenerator;

public class TimeSeriesPlot {

	static public class DataRecorder extends XYSeries {
		static public int MAX_DATA_LEN = 5000;	// Maximum number of data points to remember
		static public int CHUNK_SIZE = 50;		// number of datapoints to delete at a time

		Callable<Double> lambda;

		public DataRecorder(String title, Callable<Double> lambda) {
			super(title, false);
			this.lambda = lambda;
		}

		public void record(double timestamp) {
			try {
				add(timestamp, lambda.call(), true);
				if(getItemCount() > MAX_DATA_LEN) delete(0,CHUNK_SIZE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	TimeSeriesChartGenerator 	chart;
	ArrayList<DataRecorder>		dataSeries;


	public TimeSeriesPlot(String title, String xAxisLabel, String yAxisLabel) {
		dataSeries = new ArrayList<>(4);
		chart = new TimeSeriesChartGenerator();
		
        chart.setTitle(title);
        chart.setYAxisLabel(yAxisLabel);
        chart.setXAxisLabel(xAxisLabel);
	}


	public void addToPane(JTabbedPane pane) {
        pane.addTab(chart.getTitle(), chart);
	}


	public TimeSeriesPlot addVariable(String title, Callable<Double> lambda) {
		DataRecorder series = new DataRecorder(title, lambda);
		dataSeries.add(series);
		chart.addSeries(series, null);
		return(this);
	}


	public void recordValues(double t) {
		for(DataRecorder series : dataSeries) {
			series.record(t);
		}
	}
}
