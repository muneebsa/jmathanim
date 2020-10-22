/*
 * Copyright (C) 2020 David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
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
package com.jmathanim.mathobjects.updateableObjects;

import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class MiddlePoint extends Point {

    private Point p1, p2;
    private double lambda;

    public MiddlePoint(Point p1, Point p2) {
        this(p1, p2, .5);
    }

    public MiddlePoint(Point p1, Point p2, double lambda) {
        super();
        this.p1 = p1;
        this.p2 = p2;
        this.lambda = lambda;
    }

    @Override
    public void update() {
        this.v.copyFrom(p1.v.interpolate(p2.v, lambda));
    }

    @Override
    public int getUpdateLevel() {
        return Math.max(p1.getUpdateLevel(), p2.getUpdateLevel()) + 1;
    }

    @Override
    public void registerChildrenToBeUpdated(JMathAnimScene scene) {
        scene.registerObjectToBeUpdated(p1);
        scene.registerObjectToBeUpdated(p2);
    }

}
