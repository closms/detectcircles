package DetectCircles;

public class DCParameters {

	public double minD = 70;
	public double maxD = 90;
	public double minScore = 90.;
	public boolean smooth = false;
	public boolean show_hough = false;

	static final DCParameters instance = new DCParameters();

	public static DCParameters getInstance() {
		return instance;
	}
	
}
