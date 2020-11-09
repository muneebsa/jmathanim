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
package com.jmathanim.Animations.Strategies.Transform;

import com.jmathanim.Animations.AffineJTransform;
import com.jmathanim.Animations.Animation;
import com.jmathanim.Animations.ApplyCommand;
import com.jmathanim.Animations.commands.Commands;
import com.jmathanim.Utils.MODrawProperties;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class HomothecyStrategyTransform extends Animation {
    
    ApplyCommand anim;
    private final Shape mobjTransformed;
    private final Shape mobjDestiny;
    private final MODrawProperties mpBase;
    
    public HomothecyStrategyTransform(double runtime, Shape mobjTransformed, Shape mobjDestiny) {
        super(runtime);
        this.mobjTransformed = mobjTransformed;
        this.mobjDestiny = mobjDestiny;
        mpBase = mobjTransformed.mp.copy();
        
    }
    
    @Override
    public void initialize() {
        Point a = this.mobjTransformed.getPoint(0);
        Point b = this.mobjTransformed.getPoint(1);
        Point c = this.mobjDestiny.getPoint(0);
        Point d = this.mobjDestiny.getPoint(1);
        anim = Commands.homothecy(runTime, a, b, c, d, this.mobjTransformed);
        anim.initialize();
        
    }
    
    @Override
    public boolean processAnimation() {
        return anim.processAnimation();
    }
    
    @Override
    public void doAnim(double t, double lt) {
        anim.doAnim(t, lt);
        mobjTransformed.mp.interpolateFrom(mpBase, mobjDestiny.mp, lt);
    }
    
    @Override
    public void finishAnimation() {
        anim.finishAnimation();
    }
    
    @Override
    public void addObjectsToScene(JMathAnimScene scene) {
    }
    
}