/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Curve;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.JMC;
import com.jmathanim.Utils.Vec;
import java.util.ArrayList;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Arc extends MathObject {

    public double x, y, z;
    public double radius, angle;
    protected boolean closePath;

    public Arc(double x, double y, double radius, double angle) {
        super();
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.angle = angle;
        setDrawParam(1);//Draw parameter to 1, draw the full arc
        closePath = false;

    }

    @Override
    public Vec getCenter() {
        return new Vec(x, y);
    }

    @Override
    public void draw(Renderer r) {
        if (drawParam >= 1) {
            drawParam = 1;
        }
        double x0 = x + radius;
        double y0 = y;
        double x1, y1;
        r.setStroke(.01);//TODO: COnfig stroke size
        r.setAlpha(alpha);
//        r.createPath(x0, y0);//QUITADO
        //Compute an optimal alpha, depending on the screen?
        Curve curve=new Curve();
        for (double alpha = 0; alpha < angle * drawParam; alpha += 0.25) {
            x1 = x + radius * Math.cos(alpha);
            y1 = y + radius * Math.sin(alpha);
            curve.add(new Vec(x1,y1));

//            r.addPointToPath(x1, y1);//QUITADO
        }
        if (closePath & drawParam == 1) {
            curve.close();
        }
        curve.setTension(.4d);
        curve.computeControlPoints(JMC.CURVED);
        r.drawPath(curve);
    }

    @Override
    public void moveTo(Vec coords) {
        x = coords.x;
        y = coords.y;
    }

    @Override
    public void shift(Vec shiftVector) {
        x += shiftVector.x;
        y += shiftVector.y;
    }

    @Override
    public MathObject copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
