package DetectCircles;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.StringTokenizer;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.measure.Calibration;
import ij.plugin.tool.PlugInTool;


public class Toggle_Circles extends PlugInTool {
	
	public void mousePressed(ImagePlus imp, MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int offscreenX = imp.getWindow().getCanvas().offScreenX(x);
		int offscreenY = imp.getWindow().getCanvas().offScreenY(y);
		DCResultsTable rt = DCResultsTable.getInstance();
		
		Calibration cal = imp.getCalibration();
		
		/* find the overlay the was clicked. */
		int nO = imp.getOverlay().size();
		for (int i = 0; i < nO; i++) {
			Roi r = imp.getOverlay().get(i);
			Rectangle rec = r.getBounds();
			if (rec.contains(offscreenX, offscreenY)) {
				// Recover the circle parameters.
				// The unit of these parameters is pixels.
				StringTokenizer st = new StringTokenizer(r.getName(), ":");
				double rx = Double.parseDouble(st.nextToken());
				double ry = Double.parseDouble(st.nextToken());
				double rr = Double.parseDouble(st.nextToken());
				double rs = Double.parseDouble(st.nextToken());
				
				Color c = r.getStrokeColor();

				if (c.equals(Color.red)) {
					// Remove the entry from the results table.
					// The result table uses the unit of the table.
					int row = rt.findRow(rx*cal.pixelWidth, ry*cal.pixelWidth, 2*rr*cal.pixelWidth, rs);
					if (row >=0) {
						rt.deleteRow(row);
					}
					r.setStrokeColor(Color.blue);
				}
				else {
					// Add back the value to the result table.
                    rt.incrementCounter();
                    rt.addValue("x", rx*cal.pixelWidth);
                    rt.addValue("y", ry*cal.pixelWidth);
                    rt.addValue("Diameter", ((double)2*rr) * cal.pixelWidth);
                    rt.addValue("Score", rs);
                    
					r.setStrokeColor(Color.red);
				}
			}
		}
		imp.getCanvas().repaint();
		rt.show();
	}

	public String getToolName() {
		return "Circle Toggle Tool";
	}
}
