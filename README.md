DetectCircles
=============

ImageJ plugin that uses the Hough transform to extract circle parameters
from an image.


Requirements
------------

- ImageJ 1.46r (or later) - http://rsb.info.nih.gov/ij/


Releases
--------

To use the plugin.  Place the release jar file in the ImageJ plugins
directory.

* [Download version 1.1](https://github.com/closms/detectcircles/wiki/releases/Detect_Circles-1.1.jar)  (md5sum: `c2af86e0ea29f38ee434df26352af135`)
* [Download version 1.0](https://github.com/closms/detectcircles/wiki/releases/Detect_Circles-1.0.jar)  (md5sum: `7d1e6102e7a0b37d5a675b0cff772921`)


Known Issues
------------

* There can only be one results table.  If the plugin is used on more than one image at a time, the behaviour of the results table is undefined.
* The detected roi's can be changed after the detection plugin runs, but the coordinates in the results table will not be updated.


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

