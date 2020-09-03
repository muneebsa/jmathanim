/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.awt.Color;
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
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class LaTeXMathObject extends SVGMathObject {

    private final String text;
    private File latexFile;
    private String baseFileName;
    private File outputDir;

    /**
     *
     * @param scene
     * @param text
     */
    public LaTeXMathObject(JMathAnimScene scene, String text) {
        super(scene);
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
        if (jmps.size() > 0)//Move UL to (0,0) by default
        {
            Rect r = getBoundingBox();
            this.shift(-r.xmin, -r.ymax);
            r = getBoundingBox();
            System.out.println(r);
        }
        //Default color
        setColor(Color.WHITE);
    }

    /**
     * Prepare LaTeX file and compile it
     */
    private void generateLaTeXDocument() throws IOException {
        //TODO: Add necessary packages here (UTF8?)
        //How to avoid having to write 2 backslashs??
        String beginDocument = "\\documentclass[preview]{standalone}\n"
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
        BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        while ((line = bri.readLine()) != null) {
            System.out.println(line);
        }
        bri.close();
        while ((line = bre.readLine()) != null) {
            System.out.println(line);
        }
        bre.close();
        p.waitFor();

    }

    private void setColor(Color color) {
        for (JMPathMathObject p : jmps) {
            p.mp.thickness = .0001;
            p.setColor(color);
            p.mp.fill = true;
            p.setFillColor(color); //LaTeX Objects should have by default same fill and draw color
        }
    }

}