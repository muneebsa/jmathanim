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
package com.jmathanim.Animations.Strategies.ShowCreation;

import com.jmathanim.Animations.AnimationGroup;
import com.jmathanim.Animations.ShowCreation;
import com.jmathanim.Animations.Strategies.TransformStrategy;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class GroupCreationStrategy extends TransformStrategy {

    private final MathObjectGroup group;
    private AnimationGroup anim;
    private final double runtime;

    public GroupCreationStrategy(double runtime,MathObjectGroup group, JMathAnimScene scene) {
        super(scene);
        this.group=group;
        this.runtime=runtime;
    }

    @Override
    public void prepareObjects() {
        anim=new AnimationGroup();
        for (MathObject obj:group.getObjects()){
        anim.add(new ShowCreation(runtime, obj));
    }
        anim.initialize();
        
    }

    @Override
    public void applyTransform(double t, double lt) {
        anim.doAnim(t, lt);
    }

    @Override
    public void finish() {
        anim.finishAnimation();
    }

    @Override
    public void addObjectsToScene() {
    }

}
