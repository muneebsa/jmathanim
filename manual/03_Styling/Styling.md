[home](https://davidgutierrezrubio.github.io/jmathanim/) [back](../index.html)

# Applying styles

Each `MathObject` has one `MODrawingProperties` object that stores drawing parameters.

# Basic styles

Each object has 2 colors: the draw color (changed with `.drawColor`), used drawing the contour  y the fill color (changed with `.fillColor`), used to fill the object. Each color is stores in a `JMColor`object, with the components red, green, blue and alpha. The `.thickness`method sets the thickness of the stroke used to draw the object.

``` java
Shape r=Shape.regularPolygon(5).fillColor(JMColor.parse("CADETBLUE")).drawColor(JMColor.parse("#041137")).thickness(5);
```

<img src="02_01_colors.png" alt="image-20201105234514407" style="zoom:25%;" />

Here we can see the method `.parse` to define a color. All JavaFX color names are supported, as well as hexadecimal format `#RRGGBBAA` (8 hexadecimal digits), `#RRGGBB` (6 hexadecimal digits) and `#RGB` (4 hexadecimal digits) . Also, both methods to change colors are overloaded so that `drawColor(string)`is equivalent to `drawColor(JMColor.parse(string))`, and the same with `fillColor`.

The `dashStyle`method sets the dash used to draw the outline, chosen from the enum `DashStyle`. Currently, there are 3 different styles, `SOLID`, `DASHED`and `DOTTED`. The following code creates 3 pentagons with these dash styles.

```java
Shape r1 = Shape.regularPolygon(5).thickness(5);
Shape r2=r1.copy().stackTo(r1, Anchor.RIGHT,.1);
Shape r3=r1.copy().stackTo(r2, Anchor.RIGHT,.1);
r1.dashStyle(DashStyle.SOLID);
r2.dashStyle(DashStyle.DASHED);
r3.dashStyle(DashStyle.DOTTED);
add(LaTeXMathObject.make("{\\tt SOLID}").stackTo(r1, Anchor.BY_CENTER));
add(LaTeXMathObject.make("{\\tt DASHED}").stackTo(r2, Anchor.BY_CENTER));
add(LaTeXMathObject.make("{\\tt DOTTED}").stackTo(r3, Anchor.BY_CENTER));
add(r1,r2,r3);
camera.adjustToAllObjects();
waitSeconds(5);
```

<img src="dashStyles.png" alt="image-20201105235906935" style="zoom: 67%;" />

# Saving styles 

A concrete combination of drawing parameters can be saved in styles. The `config`objects stores the saved styles and has methods to manage them. To apply a style to an object, use the method `.style(styleName)`.

```java
Shape triangle=Shape.regularPolygon(3).thickness(5).fillColor("RED");
//Creates style named solidRed
config.createStyleFrom(triangle, "solidRed");
Shape circle=Shape.circle().stackTo(triangle,Anchor.LEFT);
//Apply style to circle
circle.style("solidRed");
add(triangle,circle);
waitSeconds(5);
```

# Configuring the scene

The `Scene` class has an instance of `JMathAnimConfig` class, named `config`, that allows to personalize global aspects of the animation. Most of these methods should be called only on the `setupSketch()`part of the animation. Invoking `config`methods in the `runSketch()`could lead to unpredictable behaviour.

```java
//Methods to adjust output
config.setMediaW(800);//Adjusts width output to 800px
config.setMediaH(600);//Adjusts height output to 600px
config.setFPS(25);//Adjusts frames per second of video output to 25 fps

config.setLowQuality();//Predefined adjusts: 854x480 video, at 30fps
config.setMediumQuality();//Predefined adjusts: 854x480 video, at 30fps
config.setHighQuality();//Predefined adjusts: 1920x1080 video, at 60fps

config.setCreateMovie(true);//Generates a mp4 files with the animation
config.setOutputDir("media");//Specifies output directory at <PROJECT_DIR>\media (this is the default value)
config.setOutputFileName("animation");//Specifies video filename as animationWWW.mp4 where WWW is the width output (by default, the output file name is the name of the scene class)

config.setShowPreviewWindow(true); //Show the preview window (by default: true)


config.setBackgroundColor(JMColor.parse("WHITE)"));//Sets background color to white
config.setBackGroundImage("background.png");//Sets the background image, located at RESOURCES_DIR. If null, no image background is applied
config.setDrawShadow(true); //Apply shadow effect to the scene, using javafx shadow effect
config.setShadowParameters(10,15,15,.5f);//Sets shadow parameters (kernel 10, offsets 15 and 15, shadow alpha .5f)


config.setResourcesDir("c:\\resources");//Specifies resources directory at absolute path c:\resources
```

# The configuration files

## Loading config files

All the settings an definitions can be stored in `XML` files and loaded with the `ConfigLoader`class. This class holds the static method `ConfigLoader.parseFile("file.xml")` . 

Where do JMathAnim look for the files? Well, there are 3 location types that you can specify:

* If the file name starts with "#" it refers to an internal config file included in the library jar.
* If the file name starts with "!" it refers to an absolute path.
* Otherwise, it will look into the `<default resources path>/config/` folder.

By default, the resources path is located at `<your current root project>/resources` folder. So if you want to add resources locally to your project you should create this folder. Of course, you can change the default `resources` folder with the method `config.setResourcesDir(newDir)`.

A typical `resources` folder follows this structure:
```
resources/
├── config/
│   ├── configFile1.xml
│   ├── configFile2.xml
│   ├── ...
└── images/
    ├── image1.png
    ├── image2.png
    ├── image3.svg
    └── ...
```

The `ConfigLoade.parseFile` will look into the `config` folder, and image-related objects like `SVGMathObject`or `JMImage`  will look into the `images` folder.

A few examples:

* the `ConfigLoader.parseFile("file.xml")` command will try to load `file.xml`located at `<your current root project>/resources/config` folder
* the `ConfigLoader.parseFile("#file.xml") ` command will try to load `file.xml` internally stored at the jar library.
* the `ConfigLoader.parseFile("!/home/user/myResources/file.xml") ` command will try to load `file.xml` from the location `/home/user/myResources/file.xml`.

This way, if you want to store all your precious resources in a system-wide scope, you can store them in a folder (say `/home/bob/myJMathAnimResources`) and make JMathAnim to look for resources there with the method `config.setResourcesDir("/home/bob/myJMathAnimResources")` at the beginning of the `setupSketch()` method (Note that the "!" modifier is not needed here).

> Note: The "!" modifier also can be used when specifying a file path in the config files, like background images, for example.

If the program cannot find the file, the logger will report an error but the execution won't be stopped.

Here is an example of a basic config file that I use for previewing, called `preview.xml`. The `<video>` tag controls aspects related to movie output:

```xml
<JMathAnimConfig>
    <video>
        <size width="1066" height="600" fps="30"/>
        <createMovie>false</createMovie>
        <showPreviewWindow>true</showPreviewWindow>
    </video>
</JMathAnimConfig>
```

And this for production, called `productionWithShadow.xml`. The `background` tag controls aspects like image or color background, or shadow effect.

```xml
<JMathAnimConfig>
    <video>
        <size width="1920" height="1080" fps="60"/>
        <createMovie>true</createMovie>
        <outputDir>c:\media</outputDir>
        <showPreviewWindow>false</showPreviewWindow>
    </video>
    <background>
        <shadows kernelSize="8" offsetX="15" offsetY="15" alpha=".5">true</shadows>
        <image>background1080.png</image>
    </background>
</JMathAnimConfig>
```

This way, in the `setupSketch()` method, you can change program behavior just changing the config file loaded.

You can have several config files with different, independent aspects. This is the `light.xml` config I used in examples shown:

```xml
<JMathAnimConfig>
    <include>dots.xml</include>
    <background>
        <color>#FDFDFD</color>
    </background>
    <styles>
        <style name="default">
            <drawColor>black</drawColor>
            <fillColor>#00000000</fillColor>
            <thickness>1</thickness>
        </style>
        <style name="latexdefault">
            <drawColor>black</drawColor>
            <fillColor>black</fillColor>
            <thickness>.5</thickness>
        </style>
        <style name="solidred">
            <drawColor>black</drawColor>
            <fillColor>red</fillColor>
            <thickness>4</thickness>
        </style>
        <style name="solidblue">
            <drawColor>black</drawColor>
            <fillColor>blue</fillColor>
            <thickness>4</thickness>
        </style>
    </styles>
</JMathAnimConfig>
```

The JAR of the JMathAnim library has several predefined config files that you can load with "#" flag in the file name:

* The `ConfigLoader.parseFile("#preview.xml")`  loads settings for previewing the animation, with low resolution of 1066x600 at 30pfs, show preview windows and not creating movie. Ideal for the creation process of the scene.
* The `ConfigLoader.parseFile("#production.xml")` loads settings for generating the final animation, with high resolution 1920x1080 at 60pfs, not showing preview windows and creating a movie. This config should be loaded when the designing process is done and to create the final animation.
* The `ConfigLoader.parseFile("#light.xml")` loads settings for black drawings over a white background. The default colors are black.
* The `ConfigLoader.parseFile("#dark.xml")` loads settings for white drawings over a black background (well, almost black). The default colors are white.

You can check all the internal config files at the [github sources folder](https://github.com/davidgutierrezrubio/jmathanim/tree/master/src/resources/config).

The `<styles>` tag allows defining styles to apply to your animation. There are 3 named styles that are important: `default`, `latexDefault` and `functionGraphDefault` (names are case-sensitive). The style `latexdefault` is applied by default to all `LaTexMathObject`. The style `default` is applied to the rest of MathObjects. If no style with these names are defined, a default style with white stroke and no fill will be applied.

The `<include>` tag that appears at the beginning loads another config files.  In this case, a `dots.xml` file with styles to dots are defined:

```xml
<JMathAnimConfig>  
    <styles>
        <style name="dotRedCircle">
            <drawColor>RED</drawColor>
            <fillColor>TRANSPARENT</fillColor>
            <thickness>.5</thickness>
            <dotStyle>circle</dotStyle>
        </style>
        <style name="dotBlueCross">
            <drawColor>#6ca2e0</drawColor>
            <fillColor>TRANSPARENT</fillColor>
            <thickness>.5</thickness>
            <dotStyle>cross</dotStyle>
        </style>
        <style name="dotYellowPlus">
            <drawColor>#FCE16D</drawColor>
            <fillColor>TRANSPARENT</fillColor>
            <thickness>.5</thickness>
            <dotStyle>plus</dotStyle>
        </style>
    </styles>
</JMathAnimConfig>
```

## Configuration files syntax

As we said, the config files that you can read are in XML format. All files must have a root tag called `<JMathAnimConfig>`. Everything out of this tag is ignored.

Inside this tag, we may have

* The `<include>` tag allows to load another XML config files. For example `<include>#axes_and_functions_dark.xml</include>` (an internal config file included in the jar library) or `<include>myColors.xml</include>` (a file include in the `resources/config` folder).

* The `<video>` tag that controls the output format. Inside this we may  have:

  * The `<size/>`tag  with attributes `width`, `height` and `fps`. For example `<size width="1066" height="600" fps="30"/>`.
  * The `<createMovie>` tag with a boolean value, to determine if actually create a movie file or not. For example `<createMovie>false</createMovie>`.
  * The `<showPreviewWindow>` to show or not the previsualization window. For example `<showPreviewWindow>true</showPreviewWindow>`.
  * The `<outputDir>` tag, that specifies the folder where to save the movie, if created. If this tag is not found, by default the generated movie will save on `ROOT_PROJECT_DIR/media` folder. For example `<outputDir>c:/my_generated_movies</outputDir>`.
  * The `<outputFileName>`tag sets part of the file name of the generated movie. A suffix with the media height is always added to the name. If this is tag is not found, by default the name will be the class name you are using as a subclass for `Scene2D`.

* The `<background>` has tags related with the background and effects to apply:

  * The `<color>` tag sets the background color. You can specify a color using a JavaFX color name case insensitive , like `<color>white</color>` or an hex format `<color>#F0A3C5</color>`. The hex format can be 8 bytes (RGBA), 6 bytes (RGB with alpha 1) or 3 bytes (RGB with alpha 1).
  * The `<image>` tag allows to stablish a background image. You can use the "!" modifier to specify an absolute path. Otherwise, the program will look into `resources/images` folder. Note that no scaling or adjusting is made, so that the image should fit the dimensions of the `<size/>` tag.
  * The `<shadow>` tag adds a shadow effect to every objects you draw in the screen, except for the background image. For example `<shadows kernelSize="8" offsetX="5" offsetY="5" alpha=".7">true</shadows>` sets a shadow with kernel size of 8 (the amount of shadow blurring), the offsets (15,15) of the shadow respect to their origin objects, and the alpha transparency of 70%.

* The `<styles>` tag defines all the styles we may need, each one in the `<style>` subtag. For example:

  ```xml
         <style name="myStyle">
             <drawColor>WHITE</drawColor>
             <fillColor>#f55652</fillColor>
             <thickness>4.5</thickness>
             <dashStyle>SOLID</dashStyle>
             <absoluteThickness>true</absoluteThickness>
             <dotStyle></dotStyle>
          </style>
  ```

  Is pretty self-explanatory. The attribute `name` defines the style name (case sensitive) and the `<drawColor>`, `<fillColor>`, `<thickness>` and `<dashStyle>` define their respective properties.  The `<dashStyle>` may take a value from the `enum DashStyle`, that is `SOLID`, `DASHED`,  or`DOTTED`.

  The `<absoluteThickness>` thickness controls if the thickness of the object will be affected by scaling transformations. By default this is true, so that if you zoom in a circle, for example, its thickness will always be the same. For objects like SVG imports for example, the absolute thickness flag is set to false.

  The `<dotStyle>` is one of the values of the `enum DotStyle` in the `Point` class and sets how a `Point`object with this style will be drawn. Currently, the possible values are `CIRCLE`, `CROSS` and `PLUS`.

[home](https://davidgutierrezrubio.github.io/jmathanim/) [back](../index.html)