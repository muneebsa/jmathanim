/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.ConfigUtils;
import com.jmathanim.Utils.Vec;
import java.awt.Color;
import java.util.Properties;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Line extends MathObject {

    String[] DEFAULT_CONFIG = {
        "THICKNESS", ".01",
        "STROKEJOIN", "ROUND"
    };
    Point p1, p2;
    JMPath curve;

    public Line(Point p1, Point p2) {
        this(p1, p2, null);
    }

    public Line(Point p1, Point p2, Properties configParam) {
        super(configParam);
        this.p1 = p1;
        this.p2 = p2;
        ConfigUtils.digest_config(cnf, DEFAULT_CONFIG, configParam);
        computeCurve();
    }

    @Override
    public Vec getCenter() {
        Vec v1 = p1.getCenter();
        Vec v2 = p2.getCenter();
        return v1.addInSite(v2).multInSite(.5);
    }

    public void computeCurve() {
        curve = new JMPath();
        curve.add(p1);
        curve.add(p2);
        curve.computeControlPoints(JMPath.STRAIGHT);
    }

    @Override
    public void draw(Renderer r) {
        Vec v1 = p1.getCenter();
        Vec v2 = p2.getCenter();
        Vec vd=v2.minus(v1);
        Vec v3=v1.add(vd.mult(drawParam));
        r.setColor(Color.BLUE);//TODO: Configs
        r.setStroke(.01);//TODO: COnfig stroke size
        r.setAlpha(alpha);
        r.drawLine(v1.x, v1.y, v3.x, v3.y);

    }

    @Override
    public void moveTo(Vec coords) {
        Vec v1 = p1.getCenter();
        Vec shiftVector = coords.minus(v1);
        shift(shiftVector);

    }

    @Override
    public void shift(Vec shiftVector) {
        p1.shift(shiftVector);
        p2.shift(shiftVector);

    }

    @Override
    public MathObject copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void scale(Vec scaleCenter, double sx, double sy, double sz) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
