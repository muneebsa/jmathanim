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
package com.jmathanim.Animations;

import com.jmathanim.Animations.Strategies.Transform.FunctionSimpleInterpolateTransform;
import com.jmathanim.Animations.Strategies.Transform.HomothecyTransform;
import com.jmathanim.Animations.Strategies.Transform.MultiShapeTransform;
import com.jmathanim.Animations.Strategies.Transform.Optimizers.NullOptimizationStrategy;
import com.jmathanim.Animations.Strategies.Transform.PointInterpolationCanonical;
import com.jmathanim.Animations.Strategies.Transform.PointInterpolationSimpleShapeTransform;
import com.jmathanim.Animations.Strategies.Transform.RotateAndScaleXYTransform;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.MODrawProperties;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.FunctionGraph;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Transform extends Animation {

    public enum TransformMethod {
        INTERPOLATE_SIMPLE_SHAPES_BY_POINT,
        INTERPOLATE_POINT_BY_POINT,
        HOMOTHECY_TRANSFORM,
        ROTATE_AND_SCALEXY_TRANSFORM,
        FUNCTION_INTERPOLATION,
        MULTISHAPE_TRANSFORM, GENERAL_AFFINE_TRANSFORM
    }

    public final MathObject mobjDestiny;
    public MathObject mobjTransformed;
    private MODrawProperties propBase;
    private TransformMethod transformMethod;
    private boolean shouldOptimizePathsFirst;
    public boolean forceChangeDirection;
    private boolean isFinished;
    private Animation transformStrategy;

    public static Transform make(double runTime, MathObject ob1, MathObject ob2) {
        return new Transform(runTime, ob1, ob2);
    }

    public Transform(double runTime, MathObject ob1, MathObject ob2) {
        super(runTime);
        mobjTransformed = ob1;
        mobjDestiny = ob2;
        transformMethod = null;
        shouldOptimizePathsFirst = true;
        forceChangeDirection = false;
        isFinished = false;
        optimizeStrategy = null;
    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        //Determine optimal transformation

        //Should use an homothecy instead of point-to-point interpolation 
        //in the following cases:
        //2 segments/lines or segment/line
        //2 circles/ellipses
        //2 regular polygons with same number of sides
        if (transformMethod == null) {
            determineTransformStrategy();
        }
        createTransformStrategy();

        if (!shouldOptimizePathsFirst) {
            transformStrategy.setOptimizationStrategy(new NullOptimizationStrategy());
        } else {
            transformStrategy.setOptimizationStrategy(null);
        }
        //Variable strategy should have proper strategy to transform
        //If method is null means that user didn't force one
        transformStrategy.setLambda(lambda);
        transformStrategy.initialize(scene);

    }

    private void determineTransformStrategy() {
        if (mobjTransformed instanceof Line) {
            mobjTransformed = ((Line) mobjTransformed).toSegment(JMathAnimConfig.getConfig().getCamera(), 2);
        }
        if ((mobjTransformed instanceof MultiShapeObject) && (mobjDestiny instanceof MultiShapeObject)) {
            transformMethod = TransformMethod.MULTISHAPE_TRANSFORM;
            return;
        }
        if ((mobjTransformed instanceof FunctionGraph) && (mobjDestiny instanceof FunctionGraph)) {
            transformMethod = TransformMethod.FUNCTION_INTERPOLATION;
            return;
        }
        if ((mobjTransformed instanceof Shape) && (mobjDestiny instanceof Shape)) {
            Shape shTr = (Shape) mobjTransformed;
            Shape shDst = (Shape) mobjDestiny;
            double epsilon = 0.00000001;

            if (TransformStrategyChecker.testDirectHomothecyTransform(shTr, shDst, epsilon)) {
                transformMethod = TransformMethod.HOMOTHECY_TRANSFORM;
                shouldOptimizePathsFirst = true;
                return;
            }
            if (TransformStrategyChecker.testRotateScaleXYTransform(shTr, shDst, epsilon)) {
                transformMethod = TransformMethod.ROTATE_AND_SCALEXY_TRANSFORM;
                shouldOptimizePathsFirst = true;
                return;
            }
            if (TransformStrategyChecker.testGeneralAffineTransform(shTr, shDst, epsilon)) {
                transformMethod = TransformMethod.GENERAL_AFFINE_TRANSFORM;
                shouldOptimizePathsFirst = true;
                return;
            }
            //If 2 simple, closed curves, I have something simpler in mind...
            if ((shTr.getPath().getNumberOfConnectedComponents() == 0) && (shDst.getPath().getNumberOfConnectedComponents() == 0)) {
                transformMethod = TransformMethod.INTERPOLATE_SIMPLE_SHAPES_BY_POINT;
                shouldOptimizePathsFirst = true;
                return;
            }
        }
        //Nothing previous worked...try with the most general method
        transformMethod = TransformMethod.INTERPOLATE_POINT_BY_POINT;

    }

    private void createTransformStrategy() {
        //Now I choose strategy
        switch (transformMethod) {
            case MULTISHAPE_TRANSFORM:
                transformStrategy = new MultiShapeTransform(runTime, (MultiShapeObject) mobjTransformed, (MultiShapeObject) mobjDestiny);
                JMathAnimScene.logger.info("Transform method: Multishape");
                break;
            case INTERPOLATE_SIMPLE_SHAPES_BY_POINT:
                transformStrategy = new PointInterpolationSimpleShapeTransform(runTime, (Shape) mobjTransformed, (Shape) mobjDestiny);
                JMathAnimScene.logger.info("Transform method: Point interpolation between 2 simple closed curves");
                break;
            case INTERPOLATE_POINT_BY_POINT:
                transformStrategy = new PointInterpolationCanonical(runTime, (Shape) mobjTransformed, (Shape) mobjDestiny);
                JMathAnimScene.logger.info("Transform method: Point interpolation between 2 curves");
                break;
            case HOMOTHECY_TRANSFORM:
                transformStrategy = new HomothecyTransform(runTime, (Shape) mobjTransformed, (Shape) mobjDestiny);
                JMathAnimScene.logger.info("Transform method: Homothecy");

                break;
            case ROTATE_AND_SCALEXY_TRANSFORM:
                transformStrategy = new RotateAndScaleXYTransform(runTime, (Shape) mobjTransformed, (Shape) mobjDestiny);
                JMathAnimScene.logger.info("Transform method: Rotate and Scale XY");
                break;
            case GENERAL_AFFINE_TRANSFORM:
                Shape shORig = (Shape) mobjTransformed;
                Shape shDest = (Shape) mobjDestiny;
                AnimationGroup ag = new AnimationGroup();
                Point A = shORig.getPoint(0);//TODO: Take better points (as far as possible)
                Point B = shORig.getPoint(1);
                Point C = shORig.getPoint(2);
                Point D = shDest.getPoint(0);
                Point E = shDest.getPoint(1);
                Point F = shDest.getPoint(2);
                ag.add(Commands.affineTransform(runTime, A, B, C, D, E, F, shORig));
                ag.add(Commands.setMP(runTime, shDest.getMp(), shORig).setUseObjectState(false));
                transformStrategy = ag;
                JMathAnimScene.logger.info("Transform method: General affine transform");
                break;
            case FUNCTION_INTERPOLATION:
                transformStrategy = new FunctionSimpleInterpolateTransform(runTime, (FunctionGraph) mobjTransformed, (FunctionGraph) mobjDestiny);
                JMathAnimScene.logger.info("Transform method: Interpolation of functions");
                break;
        }
    }

    @Override
    public void finishAnimation() {
        if (isFinished) {
            return;
        } else {
            isFinished = true;
        }
        transformStrategy.finishAnimation();
        //Remove fist object and add the second to the scene
        scene.add(mobjDestiny);
        scene.remove(mobjTransformed);
    }

    /**
     * Sets if paths should be optimized in any available way, before doing the
     * animation
     *
     * @param shouldOptimizePathsFirst True if should optimize.
     * @return This object
     */
    public Transform setOptimizePaths(boolean shouldOptimizePathsFirst) {
        this.shouldOptimizePathsFirst = shouldOptimizePathsFirst;
        return this;
    }

    /**
     * Return the current transform method determined
     *
     * @return A value of the enum {@link TransformMethod}
     */
    public TransformMethod getTransformMethod() {
        return transformMethod;
    }

    /**
     * Forces to use a specified transform method. Forcing a transform method
     * may give unpredictable results.
     *
     * @param transformMethod The transform method,defined in enum
     * {@link TransformMethod}
     * @return This object
     */
    public Transform setTransformMethod(TransformMethod transformMethod) {
        this.transformMethod = transformMethod;
        return this;
    }

    @Override
    public void doAnim(double t) {
        //Nothing to do here, it delegates trough processAnimation()
    }

    @Override
    public boolean processAnimation() {
        return transformStrategy.processAnimation();
    }

}
