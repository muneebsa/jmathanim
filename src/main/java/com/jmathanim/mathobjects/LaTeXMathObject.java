/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.Utils.JMathAnimConfig;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class LaTeXMathObject extends SVGMathObject {

    private final String text;
    private File latexFile;
    private String baseFileName;
    private File outputDir;
    //Default scale for latex objects (relative to screen height)
    //This factor represents % of height relative to the screen that a "X" character has
    public static final double DEFAULT_SCALE_FACTOR = .025;

    /**
     * Creates a new LaTeX generated text
     *
     * @param text The text to be compiled. Backslashes in Java strings should
     * be writen with "\\"
     */
    public LaTeXMathObject(String text) {
        super();
        mp.loadFromStyle("latexdefault");
        this.text = text;

        try {
            generateLaTeXDocument();
            File f = new File(compileLaTeXFile());
            importSVG(f);
        } catch (IOException ex) {
            Logger.getLogger(LaTeXMathObject.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LaTeXMathObject.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (shapes.size() > 0)//Move UL to (0,0) by default
        {
//            Rect r = getBoundingBox();
//            this.shift(-r.xmin, -r.ymax);
//            r = getBoundingBox();
            putAt(new Point(0, 0), Anchor.UL);
        }
        int n = 0;
        for (Shape sh : shapes) {//label them
            sh.label = String.valueOf(n);
            n++;
        }
        //Default color
//        setColor(mp.drawColor);
//        this.setAbsoluteSize();
//        this.setAbsolutAnchorPoint(Anchor.UL);//Default

        //Scale
        //An "X" character in LaTeX has 110 pixels height.
        //This object should be scaled by default to extend over 10% of the screen
        //use screen sizes as this object has an absolute size by default
        Camera cam = JMathAnimConfig.getConfig().getFixedCamera();
        int h = cam.screenHeight;
        double hm = cam.getMathView().getHeight();
        double sc = DEFAULT_SCALE_FACTOR * 10 / 6.807795;
        this.scale(getBoundingBox().getUL(), sc, sc);

    }

    /**
     * Prepare LaTeX file and compile it
     */
    private void generateLaTeXDocument() throws IOException {
        //TODO: Add necessary packages here (UTF8?)
        //How to avoid having to write 2 backslashs??
        String beginDocument = "\\documentclass[preview]{standalone}\n"
                +"\\usepackage{xcolor}\n"
                + "\\begin{document}\n";

        String endDocument = "\\end{document}";

        String fullDocument = beginDocument + this.text + "\n" + endDocument;
        String hash = getMd5(fullDocument);
        hash = hash.substring(hash.length() - 8);
        outputDir = new File("tex");
        baseFileName = outputDir.getCanonicalPath() + "\\" + hash;
        latexFile = new File(baseFileName + ".tex");
        FileWriter fw;
        PrintWriter pw;
        try {
            fw = new FileWriter(latexFile);
            pw = new PrintWriter(fw);
            pw.print(fullDocument);
            pw.close();
        } catch (IOException ex) {
            Logger.getLogger(LaTeXMathObject.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String getMd5(String input) {
        try {

            // Static getInstance method is called with hashing MD5 
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest 
            //  of an input digest() return array of byte 
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation 
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value 
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } // For specifying wrong message digest algorithms 
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String compileLaTeXFile() throws IOException, InterruptedException {
        String svgFilename = baseFileName + ".svg";
        File svgFile = new File(svgFilename);
        if (!svgFile.exists()) {//If file is already created, don't do it again
            File dviFile = new File(baseFileName + ".dvi");
            String od = outputDir.getCanonicalPath();
            runExternalCommand("latex -output-directory=" + od + " " + this.latexFile.getCanonicalPath());
            System.out.println("Done compiling " + latexFile.getCanonicalPath());
            runExternalCommand("dvisvgm -n1 " + dviFile.getCanonicalPath());
            System.out.println("Done converting " + dviFile.getCanonicalPath());
        }
        return svgFilename;
    }

    public void runExternalCommand(String command) throws IOException, InterruptedException {
        String line;
        String[] ar = {};
        Process p = Runtime.getRuntime().exec(command, null, outputDir);
        BufferedReader bre;
        try (BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = bri.readLine()) != null) {
                System.out.println(line);
            }
        }
        while ((line = bre.readLine()) != null) {
            System.out.println(line);
        }
        bre.close();
        p.waitFor();

    }

    private void setColor(JMColor color) {
        for (Shape p : shapes) {
            p.mp.thickness = .0001;
            p.drawColor(color);
            p.mp.setFillAlpha(1);
            p.fillColor(color); //LaTeX Objects should have by default same fill and draw color
        }
    }

}
