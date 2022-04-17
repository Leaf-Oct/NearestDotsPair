

import java.awt.Font;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.util.ShapeUtils;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ViolenceDemo {
	private Random r = new Random();
	public int range = 0;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner in = new Scanner(System.in);
//		本类对象，用来调用随机分配坐标的方法。其实可以通过将随机分配坐标的方法转成静态方法而免去这一步
		ViolenceDemo vd = new ViolenceDemo();
		System.out.println("请输入数据规模");
		int number = in.nextInt();
		in.close();
		vd.range = number;
//		三种点
		XYSeries original = new XYSeries("原来的点");
		XYSeries comparing = new XYSeries("正在比较的点");
		XYSeries nearest = new XYSeries("最短距离的点");
//		所有数据点。将它们初始化后添加进散点集合中
		XYDataItem xydi[] = new XYDataItem[number];
		for (int i = 0; i < number; i++) {
			xydi[i] = vd.random();
			original.add(xydi[i]);
		}
//		记录三种类型的点的集合
		XYSeriesCollection data_set = new XYSeriesCollection();
		data_set.addSeries(comparing);
		data_set.addSeries(nearest);
		data_set.addSeries(original);
//		设置字体，以免汉字无法显示
		Font f = new Font("宋体", Font.ITALIC, 20);
//		创建散点图
		JFreeChart chart = ChartFactory.createScatterPlot("最近点对 暴力", "x", "y", data_set, PlotOrientation.VERTICAL, true,
				true, false);
//		设置散点图中的文字字体
		chart.getTitle().setFont(f);
		chart.getLegend().setItemFont(f);
//		将这个散点图可视化放到一个panel中
		ChartPanel panel = new ChartPanel(chart);
//		设置点的形状和大小
		XYPlot p = (XYPlot) chart.getPlot();
		p.getRenderer().setSeriesShape(0, ShapeUtils.createRegularCross(4, 4));
		p.getRenderer().setSeriesShape(1, ShapeUtils.createRegularCross(4, 4));
		p.getRenderer().setSeriesShape(2, ShapeUtils.createRegularCross(4, 4));
//		创建窗体
		JFrame window = new JFrame("最近点对——十月叶");
		window.setBounds(100, 100, 800, 800);
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.setResizable(false);
//		将图标放入窗体中
		window.add(panel);
		window.setVisible(true);

		double min_distance = Double.MAX_VALUE;
//		蛮力法的实现，每0.5秒进行下一个点对的运算
		for (int i = 0; i < number; i++) {

			for (int j = i + 1; j < number; j++) {
//				往散点图上添加正在比较的俩点
				comparing.add(xydi[i]);
				comparing.add(xydi[j]);
				double dis = distance(xydi[i], xydi[j]);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (dis < min_distance) {
					min_distance = dis;
					nearest.clear();
					nearest.add(xydi[i]);
					nearest.add(xydi[j]);
				}
//				比较完后将俩正在比较的点移出
				comparing.clear();
			}
		}
	}

	public XYDataItem random() {
		return new XYDataItem(range * r.nextDouble(), range * r.nextDouble());
	}

	public static double distance(XYDataItem a, XYDataItem b) {
		return Math.sqrt(Math.pow(a.getXValue() - b.getXValue(), 2) + Math.pow(a.getYValue() - b.getYValue(), 2));
	}
}
