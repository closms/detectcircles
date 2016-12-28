package DetectCircles;

import java.awt.Color;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import ij.Executer;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Menus;
import ij.gui.GenericDialog;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;


public class Detect_Circles implements PlugInFilter  {

	String arg;
	ImagePlus imp;
	Overlay O;
	double maxval;
	ImagePlus edimp;
	Overlay edO;
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		this.arg = arg;
		this.imp = imp;
		
		return DOES_8G;
	}

	@Override
	public void run(ImageProcessor ip) {		
		
		/* parameters */
		int minEdgeVal = 1;

		//These parameters are in pixels.
		int minR, maxR;
		
		int W, H;
		
		Calibration cal = imp.getCalibration();

		DCParameters p = DCParameters.getInstance();
		
		GenericDialog gd = new GenericDialog("Detect Circles");
		gd.addNumericField("Min_Diameter", p.minD*cal.pixelWidth, 3);
		gd.addNumericField("Max_Diameter", p.maxD*cal.pixelWidth, 3);
		gd.addNumericField("Min_Score", p.minScore, 0);
		gd.addCheckbox("Show_Hough_Space", p.show_hough);
		gd.showDialog();
		if (gd.wasCanceled()) return;
		
		/* clear out any roi's and results from a previous run. */
		ResultsTable rt = Analyzer.getResultsTable();
		if (rt == null) {
		    rt = new ResultsTable();
		    Analyzer.setResultsTable(rt);
		}
		rt.reset();
		imp.setOverlay(null);
		
		/* get the parameters that the user entered. */
		
		p.minD = (double)gd.getNextNumber();
		p.maxD = (double)gd.getNextNumber();
		p.minScore = (double)gd.getNextNumber();
		p.show_hough = (boolean) gd.getNextBoolean();

		minR = (int)(p.minD/cal.pixelWidth)/2;
		maxR = (int)(p.maxD/cal.pixelWidth)/2;
		
		W = ip.getWidth();
		H = ip.getHeight();
		
		ImageStack IS = new ImageStack(W, H);

		ImageProcessor nip = new ByteProcessor(ip, false);

		nip.findEdges();

		if (p.show_hough) {
			edimp = new ImagePlus("edge detection", new ByteProcessor(nip, false));
			edimp.show();
			edO = edimp.getOverlay();
			if (edO == null) {
				edO = new Overlay();
				edimp.setOverlay(edO);
			}
		}
		
		Overlay O = this.imp.getOverlay();
		if (O == null) {
			O = new Overlay();
			this.imp.setOverlay(O);
		}
		
		List<double[][]> ALL = null;
		if (p.show_hough) {
			ALL = new LinkedList<double[][]>();
		}
		double[][] Scores = new double[W][H];
		int[][] Radiuses = new int[W][H];
		
		maxval = 0;

		double[][] Z = new double[W][H];

		for (int R = minR; R <= maxR; R++) {

			if (p.show_hough) {
				Z = new double[W][H];
			}
			else {
				for(int i = 0; i < W; i++) {
					Arrays.fill(Z[i], 0);
				}
			}
			
			/* traverse the image and update the accumulator. */
			for (int y = 0; y < H; y++) {
				for (int x = 0; x < W; x++) {
					double I = nip.getPixel(x, y);
					
					if (I > minEdgeVal) {
						/* This pixel is possibly on a circle.  Update the accumulator */
						/* Compute the score by dividing the value in the accumulator
						 * by the circumference.  This way, the scores for circles of
						 * different radiuses are comparable.
						 */
						I /= (Math.PI * 2 * R);
						this.drawCircle(Z, x, y, R, I);
					}
				}
			}

			/* go through the best score list and update it */
			for (int y = 0; y < H; y++) {
				for (int x = 0; x < W; x++) {
					double S = Z[x][y];
					if (S > p.minScore && S > Scores[x][y]) {
						Scores[x][y] = S;
						Radiuses[x][y] = R;
					}
				}
			}

			if (p.show_hough) {
				ALL.add(Z);
			}
			IJ.showProgress((R-minR), (maxR-minR+1));
		}

		/* Go through the scores, for each hit, delete the other hits
		 * that are within its radius, that scored less.
		 */
		for (int y = 0; y < H; y++) {
			for (int x = 0; x < W; x++) {

				if (Scores[x][y] > 0) {
					int R = Radiuses[x][y];
					
					for (int j = Math.max(0,y-R+1); j < Math.min(H, y+R); j++) {
						for (int i = Math.max(0, x-R+1); i < Math.min(W, x+R); i++) {
							if (i==x && j==y) {
								/* don't compare scores with yourself. */
								continue;
							}
							if (Scores[i][j] <= Scores[x][y]) {
								/* this point's score is less, delete him. */
								Scores[i][j] = 0;
								Radiuses[i][j] = 0;
							}
						}
					}
				}
			}
		}
		
		/* draw the circles we found.  Using the best score list. */
		for (int y = 0; y < H; y++) {
			for (int x = 0; x < W; x++) {
				if (Scores[x][y] > 0) {
					int R = Radiuses[x][y];
					double S = Scores[x][y];
                    if (IJ.debugMode) IJ.log("HIT. Z["+x+"]["+y+"] = "+Scores[x][y]+" -> ("+x+", "+y+")@"+R);

                    //The Roi unit is always pixels.
                    Roi r = new OvalRoi(x-R, y-R, 2*R, 2*R);
                    // Set a magic string in the name, so the circle toggle
                    // tool can distinguish between ROIs that were detected
                    // by this code from all other ROIs.
                    r.setName("DetectCircles:"+x+":"+y+":"+R+":"+S);
                    r.setStrokeColor(Color.red);
                    O.add(r);
                    if (p.show_hough) {
                    	edO.add(r);
                    }

                    //The unit of the result table is the image scale unit.
                    rt.incrementCounter();
                    rt.addValue("x", x*cal.pixelWidth);
                    rt.addValue("y", y*cal.pixelWidth);
                    rt.addValue("Diameter", ((double)2*R)*cal.pixelWidth);
                    rt.addValue("Score", Scores[x][y]);
				}
			}
		}

		rt.show("Results");

		if (p.show_hough) {
			/* find the max value in the accumulator space */
			maxval = 0;
			for(double[][] a: ALL) {
				for (int h = 0; h < H; h++) {
					for (int w = 0; w < W; w++) {
						if (a[w][h] > maxval) { maxval = a[w][h]; }
					}
				}
			}
			/* there is a different accumulator for each radius.
			 * add them all to an ImageStack */
			for(double[][] a: ALL) {
				ADD(IS, a);
			}
		}

		
		if (p.show_hough) {
			ImagePlus IP = new ImagePlus("Hough Space", IS);
			IP.show();
		}
		
		Hashtable table = Menus.getCommands();
		String cmd = Toggle_Circles.class.getName().replaceFirst("^[^.]*.", "").replace('_', ' ');
		String className = (String)table.get(cmd);
		IJ.runPlugIn(cmd, className, arg);
	}

	
	private void ADD(ImageStack IS, double[][] Z) {
		int W = Z.length;
		int H = Z[0].length;

		
		byte[] Y = new byte[W*H];
		for (int h = 0; h < H; h++) {
			for (int w = 0; w < W; w++) {
				Y[h*W+w] = (byte)((Z[w][h]/maxval) * 255.0);
			}
		}
		ImageProcessor IP = new ByteProcessor(W, H, Y);
		IS.addSlice(IP);		
	}

	private void drawCircle(double[][] img, int cx, int cy, int R, double val)
	{
		/* For a typical image, this method will be called 13 million times. */
		
		int W = img.length;
		int H = img[0].length;
		
		int f = 1 - R;
		int ddF_x = 1;
		int ddF_y = -2 * R;
		int x = 0;
		int y = R;
		
		if (cy+R < H)  { img[cx][cy+R] += val; }
		if (cy-R >= 0) { img[cx][cy-R] += val; }
		if (cx+R < W)  { img[cx+R][cy] += val; }
		if (cx-R >= 0) { img[cx-R][cy] += val; }
		
		while (x < y) {
			if (f >= 0) {
				y--;
				ddF_y += 2;
				f += ddF_y;
			}
			x++;
			ddF_x += 2;
			f += ddF_x;
			
		    if (cx+x <  W && cy+y <  H) { img[cx + x][cy + y] += val; }
		    if (cx-x >= 0 && cy+y <  H) { img[cx - x][cy + y] += val; }
		    if (cx+x <  W && cy-y >= 0) { img[cx + x][cy - y] += val; }
		    if (cx-x >= 0 && cy-y >= 0) { img[cx - x][cy - y] += val; }
		    if (cx+y <  W && cy+x <  H) { img[cx + y][cy + x] += val; }
		    if (cx-y >= 0 && cy+x <  H) { img[cx - y][cy + x] += val; }
		    if (cx+y <  W && cy-x >= 0) { img[cx + y][cy - x] += val; }
		    if (cx-y >= 0 && cy-x >= 0) { img[cx - y][cy - x] += val; }

		}
	}
}

