/*
 * jfreechart-builder-demo: a demonstration app for jfreechart-builder
 * 
 * (C) Copyright 2020, by Matt E.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.jfcbuilder.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ui.TextAnchor;

import com.jfcbuilder.builders.ChartBuilder;
import com.jfcbuilder.builders.LineBuilder;
import com.jfcbuilder.builders.OhlcPlotBuilder;
import com.jfcbuilder.builders.OhlcSeriesBuilder;
import com.jfcbuilder.builders.VolumeXYPlotBuilder;
import com.jfcbuilder.builders.VolumeXYTimeSeriesBuilder;
import com.jfcbuilder.builders.XYArrowBuilder;
import com.jfcbuilder.builders.XYPlotBuilder;
import com.jfcbuilder.builders.XYTextBuilder;
import com.jfcbuilder.builders.XYTimeSeriesBuilder;
import com.jfcbuilder.builders.types.BuilderConstants;
import com.jfcbuilder.builders.types.DohlcvSeries;
import com.jfcbuilder.builders.types.Orientation;
import com.jfcbuilder.demo.data.providers.AscendingDateTimeGenerator;
import com.jfcbuilder.demo.data.providers.IDateTimeSeriesProvider;
import com.jfcbuilder.demo.data.providers.IDohlcvProvider;
import com.jfcbuilder.demo.data.providers.RandomDohlcvGenerator;
import com.jfcbuilder.demo.data.providers.numeric.Sinusoid;
import com.jfcbuilder.demo.data.providers.numeric.Sma;
import com.jfcbuilder.demo.data.providers.numeric.StochasticOscillator;
import com.jfcbuilder.demo.data.providers.numeric.StochasticOscillator.StochData;

/**
 * Test class for demonstrating JFreeChartBuilder capabilities. Contains a main application that
 * generates some test data and instantiates a window that displays charts built using the builder
 * framework. There is a <b><i>Demonstrations</i></b> drop-down menu from which you can pick the
 * different charts.
 */
public class JFreeChartBuilderDemo {

  private static final Stroke SOLID_LINE = BuilderConstants.SOLID_LINE;
  
  private static final Color DARK_GREEN = new Color(0, 100, 0);
  
  public static void main(String[] args) {

    // Prepare the application data to be plotted ...
    
    final LocalDateTime endDate = LocalDateTime.now();
    final LocalDateTime startDate = endDate.minus(18, ChronoUnit.MONTHS);

    
    final Set<DayOfWeek> ohlcvSkipDays = Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

    IDateTimeSeriesProvider timeProvider = AscendingDateTimeGenerator.instance();

    final long[] ohlcvDates = timeProvider.getDateTimes(startDate, endDate, ChronoUnit.DAYS,
        ohlcvSkipDays);
    
    IDohlcvProvider dohlcvProvider = RandomDohlcvGenerator.instance();
    
    final DohlcvSeries dohlcv = dohlcvProvider.getDohlcv(ohlcvDates);

    final double[] sma20 = Sma.calculate(20, dohlcv.closes());
    final double[] sma50 = Sma.calculate(50, dohlcv.closes());
    final double[] sma200 = Sma.calculate(200, dohlcv.closes());
    final double[] volSma90 = Sma.calculate(90, dohlcv.volumes());

    final int K = 14;
    final int D = 3;
    StochData stoch = StochasticOscillator.calculate(K, D, dohlcv.highs(), dohlcv.lows(), dohlcv.closes());

    final int ohlcEndIndex = dohlcv.dates().length - 1;
    final int ohlcStartIndex = (int) Math.max(0.0, ohlcEndIndex * 0.75); // ~25% of the actual data
    
    final Set<DayOfWeek> sinusoidSkipDays = Collections.emptySet();
    
    final long[] sinusoidDays = timeProvider.getDateTimes(startDate, endDate, ChronoUnit.DAYS,
        sinusoidSkipDays);
    
    final int numSinusoidDayElems = sinusoidDays.length;
    
    final double[] sinDaily1 = Sinusoid.getRandSeries(60.0, numSinusoidDayElems);
    final double[] sinDaily2 = Sinusoid.getRandSeries(60.0, numSinusoidDayElems);
    final double[] sinDaily3 = Sinusoid.getRandSeries(60.0, numSinusoidDayElems);
    final double[] sinDaily4 = Sinusoid.getRandSeries(60.0, numSinusoidDayElems);
    
    final int sinusoidDailyEndIndex = numSinusoidDayElems - 1;
    final int sinusoidDailyStartIndex = (int) Math.max(0.0, sinusoidDailyEndIndex * 0.2); // 80% of data
    
    
    final LocalDateTime endHour = LocalDateTime.now();
    final LocalDateTime startHour = endHour.minus(8, ChronoUnit.HOURS);
    
    final long[] sinusoidMinutes = timeProvider.getDateTimes(startHour, endHour, ChronoUnit.MINUTES,
        sinusoidSkipDays);
    
    final int numSinusoiMinuteElems = sinusoidMinutes.length;
    
    final double[] sinMinute1 = Sinusoid.getRandSeries(40.0, numSinusoiMinuteElems);
    final double[] sinMinute2 = Sinusoid.getRandSeries(60.0, numSinusoiMinuteElems);
    final double[] sinMinute3 = Sinusoid.getRandSeries(80.0, numSinusoiMinuteElems);
    final double[] sinMinute4 = Sinusoid.getRandSeries(100.0, numSinusoiMinuteElems);
    
    final int sinusoidMinuteEndIndex = numSinusoiMinuteElems - 1;
    final int sinusoidMinuteStartIndex = 0; // All data
    
    
    // Now generate the demo charts!
    
    List<JFreeChart> charts = new ArrayList<>();
    
    final int arrowIndex = (int) (0.75 * sinusoidDays.length);
    final double arrowX = (double) sinusoidDays[arrowIndex];
    final double arrowY = sinDaily1[arrowIndex];
    final String arrowTxt = String.format("%.1f", arrowY);
    
    final int stockEventIndex = dohlcv.dates().length - 10;
    final long stockEventDate = dohlcv.dates()[stockEventIndex];
    final double stockEventPrice = dohlcv.highs()[stockEventIndex];
    final double stockEventVolume = dohlcv.volumes()[stockEventIndex];
    
    charts.add(
      ChartBuilder.instance()
        .title("Simple Time Series With Annotations")
        .timeData(sinusoidDays)
        .indexRange(sinusoidDailyStartIndex, sinusoidDailyEndIndex)
        .xyPlot(XYPlotBuilder.instance()
          .series(XYTimeSeriesBuilder.instance().name("Amplitude").data(sinDaily1).color(Color.BLUE).style(SOLID_LINE))
          .annotation(XYArrowBuilder.instance().x(arrowX).y(arrowY).angle(180.0).color(Color.RED).text(arrowTxt))
          .annotation(XYArrowBuilder.instance().x(arrowX).y(arrowY).angle(0.0).color(Color.RED))
          .annotation(XYTextBuilder.instance().x(arrowX).y(arrowY).color(DARK_GREEN)
            .text("This value!").textPaddingLeft(5).textAlign(TextAnchor.BASELINE_LEFT).angle(90.0)))
        .build()
      );
    
    charts.add(
      ChartBuilder.instance()
        .title("Multi Daily Time Series")
        .timeData(sinusoidDays)
        .indexRange(sinusoidDailyStartIndex, sinusoidDailyEndIndex)
        .xyPlot(XYPlotBuilder.instance().yAxisName("Values")
          .series(XYTimeSeriesBuilder.instance().data(sinDaily1).color(Color.BLUE).style(SOLID_LINE))
          .series(XYTimeSeriesBuilder.instance().data(sinDaily2).color(Color.RED).style(SOLID_LINE))
          .series(XYTimeSeriesBuilder.instance().data(sinDaily3).color(DARK_GREEN).style(SOLID_LINE))
          .series(XYTimeSeriesBuilder.instance().data(sinDaily4).color(Color.MAGENTA).style(SOLID_LINE)))
        .build()
      );
    
    charts.add(
      ChartBuilder.instance()
        .title("Multi Plot Minute Time Series")
        .timeData(sinusoidMinutes)
        .indexRange(sinusoidMinuteStartIndex, sinusoidMinuteEndIndex)
        
        .xyPlot(XYPlotBuilder.instance().yAxisName("Values")
          .series(XYTimeSeriesBuilder.instance().data(sinMinute1).color(Color.BLUE).style(SOLID_LINE))
          .series(XYTimeSeriesBuilder.instance().data(sinMinute2).color(Color.RED).style(SOLID_LINE))
          .series(XYTimeSeriesBuilder.instance().data(sinMinute3).color(DARK_GREEN).style(SOLID_LINE))
          .series(XYTimeSeriesBuilder.instance().data(sinMinute4).color(Color.MAGENTA).style(SOLID_LINE)))
        
        .xyPlot(XYPlotBuilder.instance().yAxisName("Amplitudes")
          .series(XYTimeSeriesBuilder.instance().data(sinMinute2).color(Color.GRAY).style(SOLID_LINE))
          .series(XYTimeSeriesBuilder.instance().data(sinMinute3).color(Color.LIGHT_GRAY).style(SOLID_LINE)))
        
        .xyPlot(XYPlotBuilder.instance().yAxisName("Series 1")
            .series(XYTimeSeriesBuilder.instance().data(sinMinute1).color(Color.BLUE).style(SOLID_LINE)))
        
        .xyPlot(XYPlotBuilder.instance().yAxisName("Series 2")
            .series(XYTimeSeriesBuilder.instance().data(sinMinute2).color(Color.RED).style(SOLID_LINE)))
        
        .xyPlot(XYPlotBuilder.instance().yAxisName("Series 3")
            .series(XYTimeSeriesBuilder.instance().data(sinMinute3).color(DARK_GREEN).style(SOLID_LINE)))
        
        .build()
      );
    
    charts.add(
      ChartBuilder.instance()
  
        .title("Stock Chart Time Series With Weekend Gaps, Lines, and Annotations")
        .timeData(dohlcv.dates())
        .indexRange(ohlcStartIndex, ohlcEndIndex)
    
        .xyPlot(OhlcPlotBuilder.instance().yAxisName("Price").plotWeight(3)
            
          .series(OhlcSeriesBuilder.instance().ohlcv(dohlcv).upColor(Color.WHITE).downColor(Color.RED))
          .series(XYTimeSeriesBuilder.instance().name("MA(20)").data(sma20).color(Color.MAGENTA).style(SOLID_LINE))
          .series(XYTimeSeriesBuilder.instance().name("MA(50)").data(sma50).color(Color.BLUE).style(SOLID_LINE))
          .series(XYTimeSeriesBuilder.instance().name("MA(200)").data(sma200).color(Color.RED).style(SOLID_LINE))
          
          .annotation(XYArrowBuilder.instance().x(stockEventDate).y(stockEventPrice).angle(270.0).color(DARK_GREEN)
            .textAlign(TextAnchor.BOTTOM_CENTER).text(String.format("%.2f", stockEventPrice)))
          
          .line(LineBuilder.instance().orientation(Orientation.HORIZONTAL).atValue(dohlcv.closes()[0])
            .color(Color.LIGHT_GRAY).style(SOLID_LINE)))
    
        
        .xyPlot(VolumeXYPlotBuilder.instance().yAxisName("Volume").plotWeight(1)
          .series(VolumeXYTimeSeriesBuilder.instance().ohlcv(dohlcv).closeUpSeries().color(Color.WHITE))
          .series(VolumeXYTimeSeriesBuilder.instance().ohlcv(dohlcv).closeDownSeries().color(Color.RED))
          .series(XYTimeSeriesBuilder.instance().name("MA(90)").data(volSma90).color(Color.BLUE).style(SOLID_LINE))
          
          .annotation(XYArrowBuilder.instance().x(stockEventDate).y(stockEventVolume).angle(270.0).color(DARK_GREEN)
            .textAlign(TextAnchor.BOTTOM_CENTER).text(String.format("%.0f", stockEventVolume)))
          
          .line(LineBuilder.instance().orientation(Orientation.HORIZONTAL).atValue(dohlcv.volumes()[0])
            .color(DARK_GREEN).style(SOLID_LINE)))
    
        
        .xyPlot(XYPlotBuilder.instance().yAxisName("Stoch").yAxisRange(0.0, 100.0).yAxisTickSize(50.0).plotWeight(1)
            
          .series(XYTimeSeriesBuilder.instance().name("K(" + K + ")").data(stoch.getPctK()).color(Color.RED).style(SOLID_LINE))
          .series(XYTimeSeriesBuilder.instance().name("D(" + D + ")").data(stoch.getPctD()).color(Color.BLUE).style(SOLID_LINE))
          .line(LineBuilder.instance().orientation(Orientation.HORIZONTAL).atValue(80.0).color(Color.BLACK).style(SOLID_LINE))
          .line(LineBuilder.instance().orientation(Orientation.HORIZONTAL).atValue(50.0).color(Color.BLUE).style(SOLID_LINE))
          .line(LineBuilder.instance().orientation(Orientation.HORIZONTAL).atValue(20.0).color(Color.BLACK).style(SOLID_LINE)))
    
        .build()
    );

    launchChartDemoWindow(charts);
  }

  /**
   * Helper method to build a GUI for showcasing the demo charts.
   * 
   * @param chartMap Map of chart names (keys) to JFreeChart objects (values)
   * @throws HeadlessException If a problem occurs.
   */
  protected static void launchChartDemoWindow(List<JFreeChart> charts)
      throws HeadlessException {
    ChartPanel panel = new ChartPanel(null);

    JFrame frame = new JFrame(ChartBuilder.class.getSimpleName());
    frame.add(panel);
    frame.setSize(new Dimension(800, 600));
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JMenuBar menuBar = new JMenuBar();
    frame.setJMenuBar(menuBar);
    JMenu demoMenu = new JMenu("Demonstrations");
    menuBar.add(demoMenu);

    ButtonGroup group = new ButtonGroup();
    JRadioButtonMenuItem item;

    for (JFreeChart chart : charts) {
      item = new JRadioButtonMenuItem(chart.getTitle().getText());
      item.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (e.getSource() instanceof JRadioButtonMenuItem) {
            JRadioButtonMenuItem context = (JRadioButtonMenuItem) e.getSource();
            context.setSelected(true);
            panel.setChart(chart);
          }
        }
      });
      group.add(item);
      demoMenu.add(item);

      if (panel.getChart() == null) {
        item.doClick();
      }
    }

    frame.setVisible(true);
  }
}
