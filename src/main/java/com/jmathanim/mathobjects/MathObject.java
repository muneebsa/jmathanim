/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.updateableObjects.Updateable;
import java.util.HashSet;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class MathObject implements Drawable, Updateable,Stateable{

    public static final int SLICE_SIMPLE = 1;
    public static final int SLICE_DOUBLE = 2;
    public static final int SLICE_FOUR = 3;

    public final MathObjectDrawingProperties mp;
    protected MathObjectDrawingProperties mpBackup;
    public String label="";
    /**
     * Scenes where this object belongs.
     *
     */
    private HashSet<JMathAnimScene> scenes;
    public boolean visible;
    /**
     * This parameter specifies the amount of object to be drawn 0=none,
     * 1/2=draw half
     */
//    protected double drawParam;

    /**
     * Mathobjects dependent of this. These should be updated with its own method when this object
     * changes. This is designed for Objects with its specific dependency function (MiddlePoint for example)
     */
    public final HashSet<Updateable> dependent;
    
    /**
     * MathObjects children of this (for example: Polygon has Point as vertices)
     */
    public final HashSet<MathObject> children;
    
    public int updateLevel;

//    /**
//     * Mathobjects which this is dependent from. This object should be updated4
//     * when any of this list changes.
//     */
//    public final HashSet<MathObject> ascendent;
//    public final HashSet<MathObject> cousins;
    public MathObject() {
        this(null);
    }

    public MathObject(MathObjectDrawingProperties prop) {
        mp = new MathObjectDrawingProperties();//Default
        mp.digestFrom(prop);
//        ascendent=new HashSet<>();
        dependent = new HashSet<>();
        children = new HashSet<>();
//        cousins=new HashSet<>();
        scenes = new HashSet<>();
        updateLevel=0;
    }

    /**
     * Return center of object. Implementation depends on type of MathObject
     *
     * @return Vec object with center
     */
    public abstract Point getCenter();

    /**
     * Move object so that center is the given coords
     *
     * @param coords Vec with coordinates of new center
     */
    public abstract void moveTo(Vec coords);

    public void moveTo(Point p) {
        moveTo(p.v);
    }

    /**
     * Shift object with the given vector
     *
     * @param shiftVector
     */
    public abstract void shift(Vec shiftVector);

    public void shift(double x, double y) {
        shift(new Vec(x, y));
    }

    /**
     * Scale from center of object (2D version)
     *
     * @param sx
     * @param sy
     */
    public void scale(double sx, double sy) {
        scale(getCenter(), sx, sy, 1);
    }

    /**
     * Scale from center of object (3D version)
     *
     * @param sx
     * @param sy
     * @param sz
     */
    public void scale(double sx, double sy, double sz) {
        scale(getBoundingBox().getCenter(), sx, sy, sz);
    }

    public abstract void scale(Point scaleCenter, double sx, double sy, double sz);

    /**
     * Returns a copy of the object
     *
     * @return copy of object, with identical properties
     */
    abstract public MathObject copy();

    /**
     * Update all necessary componentes of this object to display properly This
     * should be called when any of its subobjects (sides, vertices...) changes
     */

    abstract public void prepareForNonLinearAnimation();

    abstract public void processAfterNonLinearAnimation();


    public void updateDependents() {
        HashSet<Updateable> desC = (HashSet<Updateable>) dependent.clone();
        for (Updateable mob : desC) {
            mob.update();
        }
    }

    public void addScene(JMathAnimScene scen) {
        scenes.add(scen);
//        for (MathObject mob:cousins)
//        {
//            scen.add(mob);
//        }
    }

    public void removeScene(JMathAnimScene scen) {
        scenes.remove(scen);
//         for (MathObject mob:descendent)
//        {
//            mob.removeScene(scen);
//        }
    }

    public void addObjectToScene(MathObject mob) {
        for (JMathAnimScene sce : scenes) {
            mob.addScene(sce);
        }
    }

    /**
     * Sets the drawing parameter. This method alters the drawing parameters of
     * the MathObject so that it displays only partially. It is used for
     * animation ShowCreation, for example
     *
     * @param t Parameter to draw (0=nothing, 1=draw the entire object)
     */
    public abstract void setDrawParam(double t, int sliceType);

    /**
     * Returns the Bounding box with limits of the MathObject
     *
     * @return A Rect with (xmin,ymin,xmax,ymax)
     */
    public abstract Rect getBoundingBox();

    public void setAlpha(double t) {
        setDrawAlpha(t);
        setFillAlpha(t);
    }

    public abstract void setDrawAlpha(double t);

    public abstract void setFillAlpha(double t);
    
    public abstract void registerChildrenToBeUpdated(JMathAnimScene scene);
    public abstract void unregisterChildrenToBeUpdated(JMathAnimScene scene);
    
    @Override
    public int getUpdateLevel(){
        return updateLevel;
    }
    
    @Override
    public void saveState()
    {
        this.mpBackup=this.mp.copy();
    }
    @Override
    public void restoreState(){
        mp.copyFrom(mpBackup);
    }
}
