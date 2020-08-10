/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Utils.Vec;
import java.util.ArrayList;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class RegularPolygon extends Polygon {

    int numVertices;
    double side;
    private final Point firstPoint;
    private final ArrayList<Line> radius;
    private final ArrayList<Line> apothem;

    public RegularPolygon(int numVertices, double side) {
        super();
        this.numVertices = numVertices;
        this.side = side;
        firstPoint = new Point(side, 0);
        radius = new ArrayList<Line>();
        apothem = new ArrayList<Line>();
        computeJMPath();
    }

    @Override
    public void computeJMPath() {
        this.vertices.clear();
        jmpath.clear();
        Point newPoint = (Point) firstPoint.copy();
        for (int n = 0; n < numVertices; n++) {
            double alpha = 2 * n * Math.PI / numVertices;
            Vec moveVector = new Vec(side * Math.cos(alpha), side * Math.sin(alpha));
            newPoint = newPoint.add(moveVector);
            newPoint.type=Point.TYPE_VERTEX;
            this.vertices.add(newPoint);
            jmpath.add(newPoint);
        }
        jmpath.close();
        jmpath.curveType = JMPath.STRAIGHT;
        jmpath = jmpath.interpolate(20);

        jmpath.computeControlPoints();
        computeRadiusAndApothems();
    }

    public ArrayList<Line> getRadius() {
        return radius;
    }

    public ArrayList<Line> getApothem() {
        return apothem;
    }

    private void computeRadiusAndApothems() {
        radius.clear();
        apothem.clear();
        Point center = new Point(getCenter());
        for (Point p : jmpath.getPoints()) {//Wrong, because considers interpolated points
            if (p.type == Point.TYPE_VERTEX) {
                radius.add(new Line(center, p));
            }
        }
    }

}