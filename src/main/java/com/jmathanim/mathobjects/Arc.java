/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Vec;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Arc extends JMPathMathObject {

    public double x, y, z;
    public double radiusx, radiusy, angle;
    protected boolean closePath;
    public boolean isCurved;
    public double step;

    public Arc(Point cen, double radius, double angle) {
        this(cen, radius, angle, false);
    }

    public Arc(Point cen, double radius, double angle, boolean isClosed) {
        super();
        isCurved = true;//Default
        step = .05;//Default
        this.x = cen.v.x;
        this.y = cen.v.y;
        this.radiusx = radius;
        this.radiusy = radius;
        this.angle = angle;
        if (isClosed) {
            jmpath.close();
        } else {
            jmpath.open();
        }
        setCurveType(JMPath.CURVED);
        needsRecalcControlPoints = true;
        numInterpolationPoints = 1;//For now, don't interpolate
        computePoints();
        computeJMPathFromVertices();
    }

    @Override
    public Point getCenter() {
        return new Point(x, y);
    }

    @Override
    public void draw(Renderer r) {
        r.setColor(mp.color);
        double rad = mp.getThickness(r);
        if (needsRecalcControlPoints) {
            computeJMPathFromVertices();
        }

//        JMPath c = jmpath.getSlice(drawParam);
//        if (drawParam < 1) {
//            c.open();
//        } else {
//            c.close();
//        }
        r.setColor(mp.color);
        r.setStroke(mp.getThickness(r));
        r.setAlpha(mp.alpha);
        r.drawPath(jmpath);
    }

    public void computePoints() {
        vertices.clear();
        double x0 = x + radiusx;
        double y0 = y;
        double x1, y1;
        for (double alphaC = 0; alphaC < angle; alphaC += step) {
            x1 = x + radiusx * Math.cos(alphaC);
            y1 = y + radiusy * Math.sin(alphaC);
            Point p = new Point(x1, y1);
            vertices.add(new JMPathPoint(p,true,JMPathPoint.TYPE_VERTEX));
        }

    }

    @Override
    public void moveTo(Vec coords) {
        x = coords.x;
        y = coords.y;
        needsRecalcControlPoints = true;
    }

    @Override
    public void shift(Vec shiftVector) {
        x += shiftVector.x;
        y += shiftVector.y;
        needsRecalcControlPoints = true;

    }

    @Override
    public MathObject copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void scale(Point scaleCenter, double sx, double sy, double sz) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //TODO: Needs to recompute center around scaleCenter
//        radiusx*=sx;
//        radiusy*=sy;
//        needsRecalcControlPoints=true;
    }

    @Override
    public void update() {
        computeJMPathFromVertices();
        updateDependents();
    }

    @Override
    public void prepareForNonLinearAnimation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void processAfterNonLinearAnimation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
