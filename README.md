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

* [Download version 1.2](https://github.com/closms/detectcircles/wiki/releases/Detect_Circles-1.2.jar)  (md5sum: `987dd7ed7c4801f924f147fdc9fcb647`)
* [Download version 1.1](https://github.com/closms/detectcircles/wiki/releases/Detect_Circles-1.1.jar)  (md5sum: `a01b5d1750c10ba3019446174c7f32ea`)
* [Download version 1.0](https://github.com/closms/detectcircles/wiki/releases/Detect_Circles-1.0.jar)  (md5sum: `7d1e6102e7a0b37d5a675b0cff772921`)


Changelog
---------

2016-12-21 Michael Closson <closms@gmail.com>

* Release [v1.2](https://github.com/closms/detectcircles/wiki/releases/Detect_Circles-1.2.jar)
* Scope of 1.2 is described in commit d0911cff63388962ebd6955eb24a333cdcad8ad3

2013-07-21 Michael Closson <closms@gmail.com>

* [Fix issue #2](https://github.com/closms/detectcircles/issues/2)
* Re-release it as version 1.1.  Code tag it as v1.1.1

2013-07-06 Michael Closson <closms@gmail.com>

* [Fix issue #1](https://github.com/closms/detectcircles/issues/1)


Known Issues
------------

* There can only be one results table.  If the plugin is used on more than one image at a time, the behaviour of the results table is undefined.
* The detected roi's can be changed after the detection plugin runs, but the coordinates in the results table will not be updated.


How to set up an Eclipse workspace
----------------------------------

1. (assumption) Eclipse and EGit are installed and configured. There is a good tutorial on Eclipse, Git and Github [here](http://www.vogella.com/tutorials/EclipseGit/article.html).
2. Checkout DetectCircles with EGit.
  * File -> Import -> Git -> Projects From Git.
  * **Source Git Repository** Any URI that is compatible with Github will work.
  * **Branch Selection** Clone the only branch in the repository, `master`.
  * **Local Destination** It is safe to use the default values in this dialog window
  * **Select a wizard to use for importing projects** Use Import existing Eclipse projects
  * **Import Projects** It is safe to use the default values in this dialog window
3. Add a new classpath variable with the name `IJ_LOC` so that Eclipse can find ImageJ and the ImageJ source code.
  * The build path expects to find `ij.jar` at `IJ_LOC/ij.jar`.
  * The build path expects to find the ImageJ source code at `IJ_LOC/ij146r-src.zip`.
4. Add a new run/debug configuration based on Java Application
  * Name: `ImageJ`
  * On the Main tab
    * Project: `detectcircles`
    * Main class: `ij.ImageJ`
  * On the Arguments tab:
    * VM arguments: `-Xms256m -Xmx512m -Dplugins.dir=${project_loc}/DetectCircles`

Now you can code/debug and step through the IJ source.

