/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.jmathanim;

import com.jmathanim.Cameras.Camera2D;
import com.jmathanim.Renderers.Java2DRenderer;
import com.jmathanim.Utils.ConfigUtils;
import java.util.Properties;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class Scene2D extends JMathAnimScene {

    protected Java2DRenderer renderer;
    protected Camera2D camera;


    public Scene2D() {
        super();
        

    }

    public void createRenderer(){
        fps = conf.fps;
        dt=1./fps;
        renderer = new Java2DRenderer(this);
        camera=renderer.getCamera();
        SCRenderer=renderer;
        SCCamera=camera;
    }
    
}