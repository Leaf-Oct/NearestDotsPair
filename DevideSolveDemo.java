

import java.awt.Font;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

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

public class DevideSolveDemo {
	private static Random r = new Random();
//	随机分配点的坐标范围
	public static int range = 0;
//	所有的点
	public static XYDataItem xydi[];
//	不同类型的点的集合
	public static XYSeries original = new XYSeries("原来的点");
	public static XYSeries comparing = new XYSeries("正在比较的点");
	public static XYSeries left_points = new XYSeries("左边的点");
	public static XYSeries right_points = new XYSeries("右边的点");
	public static XYSeries mid_points = new XYSeries("中间的点");
	public static XYSeries nearest = new XYSeries("最短距离的点");
	public static double min_distance = Double.MAX_VALUE;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner in = new Scanner(System.in);
		System.out.println("请输入数据规模");
		int number = in.nextInt();
//		随机坐标的随机范围与数据规模一致
		range = number;
		xydi = new XYDataItem[number];
//		将所有的点添加进图表上点的集合中
		for (int i = 0; i < number; i++) {
			xydi[i] = random();
			original.add(xydi[i]);
		}
//		按x坐标排序，方便二分
		Arrays.sort(xydi, new Comparator<XYDataItem>() {
			@Override
			public int compare(XYDataItem o1, XYDataItem o2) {
				double result = o1.getXValue() - o2.getXValue();
				if (result > 0) {
					return 1;
				} else if (result < 0) {
					return -1;
				}
				return 0;
			}
		});
//		jfreechart的数据集
		XYSeriesCollection data_set = new XYSeriesCollection();
		data_set.addSeries(comparing);
		data_set.addSeries(mid_points);
		data_set.addSeries(nearest);
		data_set.addSeries(left_points);
		data_set.addSeries(right_points);
		data_set.addSeries(original);
//		字体，防jfreechart显示不出中文
		Font f = new Font("宋体", Font.ITALIC, 20);
//		创建散点图的图表对象
		JFreeChart chart = ChartFactory.createScatterPlot("最近点对 分治", "x", "y", data_set, PlotOrientation.VERTICAL, true,
				true, false);
//		设置字体
		chart.getTitle().setFont(f);
		chart.getLegend().setItemFont(f);
//		将这个散点图可视化放到一个panel中
		ChartPanel panel = new ChartPanel(chart);
//		设置点的形状和大小
		XYPlot p = (XYPlot) chart.getPlot();
		p.getRenderer().setSeriesShape(0, ShapeUtils.createRegularCross(4, 4));
		p.getRenderer().setSeriesShape(1, ShapeUtils.createRegularCross(4, 4));
		p.getRenderer().setSeriesShape(2, ShapeUtils.createRegularCross(4, 4));
		p.getRenderer().setSeriesShape(3, ShapeUtils.createRegularCross(4, 4));
		p.getRenderer().setSeriesShape(4, ShapeUtils.createRegularCross(4, 4));
		p.getRenderer().setSeriesShape(5, ShapeUtils.createRegularCross(4, 4));

		JFrame window = new JFrame("最近点对——十月叶");
		window.setBounds(100, 100, 800, 800);
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.setResizable(false);
//		将panel放入窗体中
		window.add(panel);
		window.setVisible(true);
		System.out.println("按任意键开始");
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		in.close();
		devide(0, number - 1);
		System.out.println("比较结束");
	}
//	下面就是分治法的内容了。就只是在特定的时候加点，删点，休眠一段时间
	public static double devide(int left, int right) {
		if (right - left + 1 <= 3) {
//			double dis=Double.MAX_VALUE-1;
			double local_min=Double.MAX_VALUE-1;
			System.out.println("小于等于3个点，暴力计算");
			for (int i = left; i <= right; i++) {
				for (int j = i + 1; j <= right; j++) {
					comparing.add(xydi[i]);
					comparing.add(xydi[j]);
					double dis = distance(xydi[i], xydi[j]);
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (dis < local_min) {
						local_min = dis;
						if(local_min<min_distance) {
							nearest.clear();
							nearest.add(xydi[i]);
							nearest.add(xydi[j]);
							min_distance=local_min;
						}
					}
					comparing.clear();
				}
			}
			return local_min;
		}
		int mid = (left + right) / 2;
		for (int i = left; i <= mid; i++) {
			left_points.add(xydi[i]);
		}
		for (int i = mid + 1; i <= right; i++) {
			right_points.add(xydi[i]);
		}
		System.out.println("二分");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		right_points.clear();
		left_points.clear();
		double left_min = devide(left, mid);
		double right_min = devide(mid + 1, right);
		double min = Math.min(left_min, right_min);
		List<XYDataItem> left_in_distance = new ArrayList<XYDataItem>();
		List<XYDataItem> right_in_distance = new ArrayList<XYDataItem>();
		int search_point_in_distance = left;
		while (xydi[search_point_in_distance].getXValue() < xydi[mid].getXValue() - min) {
			search_point_in_distance++;
		}
		while (xydi[search_point_in_distance].getXValue() <= xydi[mid].getXValue()) {
			left_in_distance.add(xydi[search_point_in_distance]);
			mid_points.add(xydi[search_point_in_distance]);
			search_point_in_distance++;
		}
		while (search_point_in_distance <= right) {
			if(xydi[search_point_in_distance].getXValue() < xydi[mid].getXValue() + min) {
				right_in_distance.add(xydi[search_point_in_distance]);
				mid_points.add(xydi[search_point_in_distance]);
				search_point_in_distance++;
			}
			else {
				break;
			}
		}
//		按y排序
		left_in_distance.sort(new Comparator<XYDataItem>() {
			@Override
			public int compare(XYDataItem o1, XYDataItem o2) {
				double result = o1.getYValue() - o2.getYValue();
				if (result > 0) {
					return -1;
				} else if (result < 0) {
					return 1;
				}
				return 0;
			}
		});
		right_in_distance.sort(new Comparator<XYDataItem>() {
			@Override
			public int compare(XYDataItem o1, XYDataItem o2) {
				double result = o1.getYValue() - o2.getYValue();
				if (result > 0) {
					return -1;
				} else if (result < 0) {
					return 1;
				}
				return 0;
			}
		});
		System.out.println("展示中间在最小距离内的点");
		System.out.println("左边"+left_in_distance.size()+"个");
		System.out.println("右边"+right_in_distance.size()+"个");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mid_points.clear();
		double distance = min;
		System.out.println("开始比较中间的点");
		for (XYDataItem l : left_in_distance) {
			for (XYDataItem r : right_in_distance) {
				System.out.println("比一次");
				comparing.add(l);
				comparing.add(r);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				comparing.clear();
				if (l.getYValue() - r.getYValue() < -min) {
					continue;
				}
				if (l.getYValue() - r.getYValue() > min) {
					break;
				}
				distance = distance(l, r);
				if (distance < min) {
					min = distance;
					if(min<min_distance) {
						min_distance=min;
						nearest.clear();
						nearest.add(l);
						nearest.add(r);
					}
				}
			}
		}
		return min;
	}

	public static XYDataItem random() {
		return new XYDataItem(range * r.nextDouble(), range * r.nextDouble());
	}

	public static double distance(XYDataItem a, XYDataItem b) {
		return Math.sqrt(Math.pow(a.getXValue() - b.getXValue(), 2) + Math.pow(a.getYValue() - b.getYValue(), 2));
	}
}
