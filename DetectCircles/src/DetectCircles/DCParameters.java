package DetectCircles;

public class DCParameters {

	public double minD = 50.;
	public double maxD = 150.;
	public double minScore = 200.;
	public boolean show_hough = false;

	static final DCParameters instance = new DCParameters();

	public static DCParameters getInstance() {
		return instance;
	}
	
}
