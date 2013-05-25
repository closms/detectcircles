DetectCircles
=============

ImageJ plugin that uses the Hough transform to extract circle parameters
from an image.

Requirements
------------

- ImageJ 1.46r (or later) - http://rsb.info.nih.gov/ij/



How to set up an Eclipse workspace
----------------------------------

Start in a clean directory.

Install IJ and IJ source.

1. `wget http://rsbweb.nih.gov/ij/download/zips/ij146.zip`
2. `unzip ij146.zip`
3. `wget -P ImageJ http://imagej.nih.gov/ij/download/src/ij146r-src.zip`

Checkout the code.

1. `git clone git://github.com/closms/detectcircles.git`

Set up the Eclipse workspace.

1. Open the directory detectcircles as an Eclipse workspace
2. In Eclipse, Import a new project.  Use 'Existing Projects into Workspace'
   as the import source.  Use directory 'DetectCircles' (note the capitols)
   as the project root directory.
3. Add a new classpath variable in your workspace called IJ_LOC, which points
   to the directory where you installed IJ and the source
4. Add a new run/debug configuration based on Java Application
    * Name: `ImageJ`
    * On the Main tab
      * Project: `DetectCircles`
      * Main class: `ij.ImageJ`
    * On the Arguments tab:
      * VM arguments: `-Xms256m -Xmx512m -Dplugins.dir=${workspace_loc}/DetectCircles`


Now you can code/debug and step through the IJ source.

