/*
 * Copyright (C) 2020 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.MODrawProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import static com.jmathanim.jmathanim.JMathAnimScene.PI;
import java.util.ArrayList;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Shape extends MathObject {

    public final JMPath jmpath;
    protected final ArrayList<JMPathPoint> vertices;
    private boolean showDebugPoints = false;

    public Shape() {
        this(new JMPath(), null);
    }

    public Shape(JMPath jmpath) {
        this(jmpath, null);
    }

    public Shape(JMPath jmpath, MODrawProperties mp) {
        super(mp);
        vertices = new ArrayList<>();
        this.jmpath = jmpath;
    }

    public Shape(MODrawProperties mp) {
        super(mp);
        vertices = new ArrayList<>();
        jmpath = new JMPath();
    }

    public JMPathPoint getJMPoint(int n) {
        return jmpath.getJMPoint(n);
    }

    public Point getPoint(int n) {
        return jmpath.getJMPoint(n).p;
    }

    public JMPath getPath() {
        return jmpath;
    }

    protected final void computeVerticesFromPath() {
        vertices.clear();
        for (JMPathPoint p : jmpath.jmPathPoints) {
            if (p.type == JMPathPoint.JMPathPointType.VERTEX) {
                vertices.add(p);
            }
        }
    }

    @Override
    public Point getCenter() {
        return getBoundingBox().getCenter();
    }

    public Point getCentroid() {
        Point resul = new Point(0, 0, 0);
        for (JMPathPoint p : jmpath.jmPathPoints) {
            resul.v.x += p.p.v.x;
            resul.v.y += p.p.v.y;
            resul.v.z += p.p.v.z;
        }
        resul.v.x /= jmpath.size();
        resul.v.y /= jmpath.size();
        resul.v.z /= jmpath.size();
        return resul;
    }

//    @Override
//    public <T extends MathObject> T shift(Vec shiftVector) {
//        jmpath.shift(shiftVector);
//        return (T) this;
//    }
    public void removeInterpolationPoints() {
        jmpath.removeInterpolationPoints();
    }

    @Override
    public Shape copy() {
        final MODrawProperties copy = mp.copy();
        Shape resul = new Shape(jmpath.rawCopy(), copy);
        resul.absoluteSize = this.absoluteSize;
        resul.label = this.label + "_copy";
        return resul;
    }

    @Override
    public void draw(Renderer r) {
        if (isVisible()) {
            if (absoluteSize) {
                r.drawAbsoluteCopy(this, getAbsoluteAnchor().v);
            } else {
                r.drawPath(this);
                if (isShowDebugPoints()) {
                    for (int n = 0; n < size(); n++) {
                        r.debugText("" + n, getPoint(n).v);
                    }

                }
            }
        }
    }

    @Override
    public Rect getBoundingBox() {
        return jmpath.getBoundingBox();
    }

    @Override
    public String toString() {
        return label + ":" + jmpath.toString();
    }

    @Override
    public void registerChildrenToBeUpdated(JMathAnimScene scene) {
        for (JMPathPoint p : jmpath.jmPathPoints) {
            scene.registerUpdateable(p.p);
            scene.registerUpdateable(p.cp1);
            scene.registerUpdateable(p.cp2);
        }
    }

    @Override
    public void unregisterChildrenToBeUpdated(JMathAnimScene scene) {
        for (JMPathPoint p : jmpath.jmPathPoints) {
            scene.unregisterUpdateable(p.p);
            scene.unregisterUpdateable(p.cp1);
            scene.unregisterUpdateable(p.cp2);
        }
    }

    @Override
    public void update(JMathAnimScene scene) {
        for (JMPathPoint p : jmpath.jmPathPoints) {
            p.update(scene);
        }
    }

    @Override
    public void restoreState() {
        super.restoreState();
        jmpath.restoreState();
    }

    @Override
    public void saveState() {
        super.saveState();
        jmpath.saveState();
    }

    public <T extends Shape> T merge(Shape sh) {
        JMPath pa = sh.getPath();

        final JMPathPoint jmPoint = jmpath.getJMPoint(0);
        if (jmPoint.isThisSegmentVisible) {
            jmpath.jmPathPoints.add(jmPoint.copy());
            jmPoint.isThisSegmentVisible = false;
        }

        final JMPathPoint jmPoint2 = pa.getJMPoint(0);
        if (jmPoint2.isThisSegmentVisible) {
            pa.jmPathPoints.add(jmPoint2.copy());
            jmPoint2.isThisSegmentVisible = false;
        }
        jmpath.jmPathPoints.addAll(pa.jmPathPoints);
        return (T) this;
    }

    public int size() {
        return jmpath.size();
    }

    //Static methods to build most used shapes
    public static Shape square() {
        return Shape.square(new Point(0, 0), 1);
    }

    public static Shape square(Point A, double side) {

        return Shape.rectangle(A, A.add(new Vec(side, side)));
    }

    public static Shape rectangle(Rect r) {
        return Shape.rectangle(r.getDL(), r.getUR());
    }

    public static Shape segment(Point A, Vec v) {
        return segment(A, A.add(v));
    }

    //Static methods to build most commons shapes
    public static Shape segment(Point A, Point B) {
        Shape obj = new Shape();
        JMathAnimConfig.getConfig().getScene();
        JMPathPoint p1 = JMPathPoint.lineTo(A);
        p1.isThisSegmentVisible = false;
        JMPathPoint p2 = JMPathPoint.lineTo(B);
        obj.jmpath.addJMPoint(p1, p2);
        return obj;
    }

    public static Shape rectangle(Point A, Point B) {
        Shape obj = new Shape();
        JMathAnimConfig.getConfig().getScene();
        JMPathPoint p1 = JMPathPoint.lineTo(A);
        JMPathPoint p2 = JMPathPoint.lineTo(B.v.x, A.v.y);
        JMPathPoint p3 = JMPathPoint.lineTo(B);
        JMPathPoint p4 = JMPathPoint.lineTo(A.v.x, B.v.y);
        obj.jmpath.addJMPoint(p1, p2, p3, p4);
        return obj;
    }

    public static Shape polygon(Point... points) {
        Shape obj = new Shape();
        for (Point newPoint : points) {
            JMPathPoint p = JMPathPoint.lineTo(newPoint);
            obj.getPath().addJMPoint(p);
        }
        return obj;
    }

    public static Shape regularPolygon(int numsides) {
        return regularPolygon(numsides, new Point(0, 0), 1);
    }

    public static Shape regularPolygon(int numsides, Point A, double side) {
        Shape obj = new Shape();
        Point newPoint = (Point) A.copy();
        for (int n = 0; n < numsides; n++) {
            double alpha = 2 * n * Math.PI / numsides;
            Vec moveVector = new Vec(side * Math.cos(alpha), side * Math.sin(alpha));
            newPoint = newPoint.add(moveVector);
            JMPathPoint p = JMPathPoint.lineTo(newPoint);
            obj.getPath().addJMPoint(p);
        }
        return obj;
    }

    public static Shape arc(double angle) {
        Shape obj = new Shape();
        double x1, y1;
        int nSegs = 4;
        int segsForFullCircle = (int) (2 * PI * nSegs / angle);
        double cte = 4d / 3 * Math.tan(.5 * Math.PI / segsForFullCircle);
        for (int n = 0; n < nSegs + 1; n++) {
            double alphaC = angle * n / nSegs;
            x1 = Math.cos(alphaC);
            y1 = Math.sin(alphaC);
            Point p = new Point(x1, y1);
            Vec v1 = new Vec(-y1, x1);

            v1.multInSite(cte);
            Point cp1 = p.add(v1);
            Point cp2 = p.add(v1.multInSite(-1));
            JMPathPoint jmp = JMPathPoint.curveTo(p);
            jmp.cp1.copyFrom(cp1);
            jmp.cp2.copyFrom(cp2);
            obj.jmpath.addJMPoint(jmp);
        }
//        obj.getPath().generateControlPoints();
//        obj.getPath().jmPathPoints.remove(0);
//        obj.getPath().jmPathPoints.remove(-1);
        obj.getPath().getJMPoint(0).isThisSegmentVisible = false;//Open path
//        obj.getJMPoint(0).cp1.v.copyFrom(obj.getJMPoint(0).p.v);
//        obj.getJMPoint(-1).cp2.v.copyFrom(obj.getJMPoint(-1).p.v);
        return obj;
    }

    public static Shape circle() {
        Shape obj = new Shape();
        double x1, y1;
        int numSegments = 4;
        double step = Math.PI * 2 / numSegments;
        double cte = 4d / 3 * Math.tan(.5 * Math.PI / numSegments);
        for (double alphaC = 0; alphaC < 2 * Math.PI; alphaC += step) {
            x1 = Math.cos(alphaC);
            y1 = Math.sin(alphaC);
            Point p = new Point(x1, y1);
            Vec v1 = new Vec(-y1, x1);

            v1.multInSite(cte);
            Point cp1 = p.add(v1);
            Point cp2 = p.add(v1.multInSite(-1));
            JMPathPoint jmp = JMPathPoint.curveTo(p);
            jmp.cp1.copyFrom(cp1);
            jmp.cp2.copyFrom(cp2);
            obj.jmpath.addJMPoint(jmp);
        }
        return obj;
    }

    public static Shape circle(Point center, double radius) {
        return circle().scale(radius).shift(center.v);
    }

    public boolean isShowDebugPoints() {
        return showDebugPoints;
    }

    public void setShowDebugPoints(boolean showDebugPoints) {
        this.showDebugPoints = showDebugPoints;
    }

}
