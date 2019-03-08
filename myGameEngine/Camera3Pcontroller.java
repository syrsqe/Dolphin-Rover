package  myGameEngine;
import a2.*;
import net.java.games.input.*;
import ray.rage.rendersystem.shader.*;
import java.awt.event.*;
import java.io.*;
import java.util.*; //for iterator
import java.lang.*;
import ray.rage.scene.controllers.RotationController;
import ray.rage.*;
import ray.rage.game.*;
import ray.rage.rendersystem.*;
import ray.rage.rendersystem.Renderable.*;
import ray.rage.scene.*;
import ray.rage.scene.Camera.Frustum.*;
import ray.rml.*;
import net.java.games.input.Event;
import ray.rage.rendersystem.gl4.GL4RenderSystem;
import ray.rage.asset.texture.*;
import ray.rage.asset.*;
import ray.input.*;
import ray.input.action.*;
import ray.rage.rendersystem.states.*;
import ray.rage.asset.texture.*;
import ray.rage.util.BufferUtil;
import java.nio.*;
import ray.rage.asset.material.*;
import java.awt.Robot;
import java.awt.Canvas;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.AWTException;
import java.awt.event.*;



public class Camera3Pcontroller implements MouseMotionListener, MouseListener {
    private Camera camera; //the camera being controlled
    private SceneNode cameraN; //the node the camera is attached to
    private SceneNode target; //the target the camera looks at
    private float cameraAzimuth; //rotation of camera around Y axis
    private float cameraElevation; //elevation of camera above target
    private float radias; //distance between camera and target
    private Vector3 targetPos; //target’s position in the world
    private Vector3 worldUpVec;
    private boolean isRecentering; // for robot
    private RenderWindow rw; //for mouse
    private RenderSystem rs; //for mouse
    private Robot robot; //for robot
    private Canvas canvas;
    private float prevMouseX, prevMouseY, curMouseX, curMouseY, centerX, centerY;
    private float frameTime;
    private boolean usesController;

    public Camera3Pcontroller(Camera cam, SceneNode camN, SceneNode targ, String controllerName, InputManager im, RenderWindow rWin, RenderSystem rSys, boolean contrU) {
        camera = cam;
        cameraN = camN;
        target = targ;
        cameraAzimuth = 305.0f; // start from BEHIND and ABOVE the target
        cameraElevation = 20.0f; // elevation is in degrees
        radias = 2.0f;
        worldUpVec = Vector3f.createFrom(0.0f, 1.0f, 0.0f);
        usesController = contrU;
        setupInput(im, controllerName);
        this.rw = rWin;
        this.rs = rSys;

        if (usesController == false) {
            initMouseMode(rs, rw);
        }


        updateCameraPosition();

    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    // Updates camera position: computes azimuth, elevation, and distance
    // relative to the target in spherical coordinates, then converts those
    // to world Cartesian coordinates and setting the camera position
    public void updateCameraPosition() {
        double theta = Math.toRadians(cameraAzimuth); // rot around target
        double phi = Math.toRadians(cameraElevation); // altitude angle
        double x = radias * Math.cos(phi) * Math.sin(theta);
        double y = radias * Math.sin(phi);
        double z = radias * Math.cos(phi) * Math.cos(theta);
        cameraN.setLocalPosition(Vector3f.createFrom
                ((float) x, (float) y, (float) z).add(target.getWorldPosition()));
        cameraN.lookAt(target, worldUpVec);


    }

    private void setupInput(InputManager im, String cn) {

        String kbName = im.getKeyboardName();
        Action orbitAAction = new OrbitAroundAction();
        Action orbitEAction = new OrbitElevationAction();
        Action orbitRAction = new OrbitRadiasAction();
        Action rotateHorizontalAction = new rotateNodeHorizontal();
        Action rotateleftAction = new ActionRotateLeft();
        Action moveVerticleAction = new ActionVerticleMove();
        Action zoomInAction = new ZoomInAction();
        Action zoomOutAction = new ZoomOutAction();
        if (usesController == true) {
            im.associateAction(cn, net.java.games.input.Component.Identifier.Axis.RX, orbitAAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            im.associateAction(cn, net.java.games.input.Component.Identifier.Axis.RY, orbitEAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            im.associateAction(cn, net.java.games.input.Component.Identifier.Axis.POV, orbitRAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            im.associateAction(cn, net.java.games.input.Component.Identifier.Axis.X, rotateHorizontalAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            im.associateAction(cn, net.java.games.input.Component.Identifier.Axis.Y, moveVerticleAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        } else if (usesController == false) {
            im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.RIGHT, rotateHorizontalAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.LEFT, rotateleftAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.ADD, zoomInAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.SUBTRACT, zoomOutAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        }

    }

    public void initMouseMode(RenderSystem r, RenderWindow w) {
        System.out.println("initMouse Called");
        rw = w;
        rs = r;
        rw.addMouseMotionListener(this);
        Viewport v = rw.getViewport(0);
        int left = rw.getLocationLeft();
        int top = rw.getLocationTop();
        int widt = v.getActualScissorWidth();
        int hei = v.getActualScissorHeight();
        int centerX = left + widt / 2;
        int centerY = top + hei / 2;
        isRecentering = false;
        try // note that some platforms may not support the Robot class
        {
            robot = new Robot();
        } catch (AWTException ex) {
            throw new RuntimeException("Couldn't create Robot!");
        }
        recenterMouse();
        prevMouseX = centerX; // 'prevMouse' defines the initial
        prevMouseY = centerY; // mouse position


        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor faceCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
        canvas = rs.getCanvas();
        canvas.setCursor(faceCursor);
    }

    private void recenterMouse() {// use the robot to move the mouse to the center point.
// Note that this generates one MouseEvent.

        Viewport v = rw.getViewport(0);
        int left = rw.getLocationLeft();
        int top = rw.getLocationTop();
        int widt = v.getActualScissorWidth();
        int hei = v.getActualScissorHeight();
        centerX = left + widt / 2;
        centerY = top + hei / 2;
        isRecentering = true;
        canvas = rs.getCanvas();
        robot.mouseMove((int) centerX, (int) centerY);
    }

    public void mouseDragged(MouseEvent e) {
       // System.out.println("mouseDragged");
    }

    public void mouseMoved(MouseEvent e) {// if robot is recentering and the MouseEvent location is in the center,
// then this event was generated by the robot

        if (isRecentering && centerX == e.getXOnScreen() && centerY == e.getYOnScreen()) {
            isRecentering = false;
        } // mouse recentered, recentering complete
        else { // event was due to a user mouse-move, and must be processed
            curMouseX = e.getXOnScreen();
            curMouseY = e.getYOnScreen();
            float mouseDeltaX = prevMouseX - curMouseX;
            float mouseDeltaY = prevMouseY - curMouseY;
            yaw(mouseDeltaX);
            pitch(mouseDeltaY);
            prevMouseX = curMouseX;
            prevMouseY = curMouseY;
// tell robot to put the cursor to the center (since user just moved it)
            recenterMouse();
            prevMouseX = centerX; //reset prev to center
            prevMouseY = centerY;
        }
    }

    public void pitch(float mouseDeltaY) {
        float rotAmount;
        frameTime = (MyGame.getFrameTime() / 60f);
        if (mouseDeltaY < 0.0 && cameraElevation < 50) {
            rotAmount = 3 * frameTime;
        } else if (mouseDeltaY > -0.0 && cameraElevation > 10) {
            rotAmount = -3 * frameTime;
        } else {
            rotAmount = 0.0f;
        }

        cameraElevation += rotAmount;
        cameraElevation = cameraElevation % 360;
        updateCameraPosition();
    }

    public void yaw(float mouseDeltaX) {
        float rotAmount;
        //frameTime = (MyGame.getFrameTime()/60f);
        if (mouseDeltaX < -0.0) {
            rotAmount = -3 * frameTime;
        } else if (mouseDeltaX > 0.0) {
            rotAmount = 3 * frameTime;
        } else {
            rotAmount = 0.0f;
        }
        cameraAzimuth += rotAmount;
        cameraAzimuth = cameraAzimuth % 360;
        updateCameraPosition();
    }

    private class OrbitAroundAction extends AbstractInputAction { // Moves the camera around the target (changes camera azimuth).
        public void performAction(float time, net.java.games.input.Event evt) {
            float rotAmount;
            frameTime = (MyGame.getFrameTime() / 60f);
            if (evt.getValue() < -0.2) {
                rotAmount = 3 * frameTime;
            } else {
                if (evt.getValue() > 0.2) {
                    rotAmount = -3 * frameTime;
                } else {
                    rotAmount = 0.0f;
                }
            }
            cameraAzimuth += rotAmount;
            cameraAzimuth = cameraAzimuth % 360;

            updateCameraPosition();

        }
    }
    // similar for OrbitRadiasAction, OrbitElevationAction

    private class OrbitElevationAction extends AbstractInputAction { // Moves the camera around the target (changes camera azimuth).

        public void performAction(float time, net.java.games.input.Event evt) {
            float rotAmount = 0.0f;
            frameTime = (MyGame.getFrameTime() / 60f);
            float predictedElevationUp = cameraElevation + (3*frameTime);
            float predictedElevationDown = cameraElevation + (-3*frameTime);
            //move up
            if (evt.getValue() < -0.4f && predictedElevationUp < 50f ) {

                    //System.out.println(evt.getValue());
                    rotAmount = (3 * frameTime);

                //move down
            } else if (evt.getValue() > 0.4f && predictedElevationDown > 10f ) {

                    rotAmount = (-3 * frameTime);


            } else {
                rotAmount = 0.0f;
            }
            System.out.println(cameraElevation);
            cameraElevation += rotAmount;

            updateCameraPosition();
        }
    }

    private class OrbitRadiasAction extends AbstractInputAction { // Moves the camera around the target (changes camera azimuth).

        public void performAction(float time, net.java.games.input.Event evt) {

            float rotAmount;
            frameTime = (MyGame.getFrameTime() / 60f);
            if (evt.getValue() == 0.25f && radias >= 0.65f) {
                rotAmount = -0.02f;
            } else {
                if (evt.getValue() == 0.75 && radias <= 3.5f) {
                    rotAmount = 0.02f;
                } else {
                    rotAmount = 0.0f;
                }
            }
            radias += rotAmount;
            updateCameraPosition();


        }
    }

    private class rotateNodeHorizontal extends AbstractInputAction {
        public void performAction(float time, net.java.games.input.Event evt) {
            Component c = evt.getComponent();
            frameTime = (MyGame.getFrameTime() / 60f);

            float rotAmount = 0;

            if (c.getName().equals("Right") || (evt.getValue() > 0.2)) {

                target.yaw((Degreef.createFrom(-3 * frameTime)));
                //cameraN.yaw((Degreef.createFrom(-3*frameTime)));
                rotAmount = (-3 * frameTime);


            }
            if (c.getName().equals("Left") || (evt.getValue() < -0.2)) {

                target.yaw((Degreef.createFrom(3 * frameTime)));
                //cameraN.yaw((Degreef.createFrom(3*frameTime)));
                rotAmount = (3 * frameTime);

            }

            cameraAzimuth += rotAmount;
            cameraAzimuth = cameraAzimuth % 360;

            updateCameraPosition();

        }
    }

    private class ActionVerticleMove extends AbstractInputAction {

        private float frameTime;

        public void performAction(float time, Event event) {
            frameTime = (MyGame.getFrameTime() / 500f);
            Component c = event.getComponent();
            if (event.getValue() > 0.1) {
                target.moveForward(-2 * frameTime);
                //cameraN.moveForward(-2*frameTime);

            } else if (event.getValue() < -0.1) {

                target.moveForward(2 * frameTime);
                //cameraN.moveForward(2*frameTime);
            }
            updateCameraPosition();

        }


    }


    public class ActionRotateLeft extends AbstractInputAction {

        float rotAmount = 0;

        public void performAction(float time, Event event) {
            frameTime = (MyGame.getFrameTime() / 60f);
            target.yaw((Degreef.createFrom(3 * frameTime)));
            //cameraN.yaw((Degreef.createFrom(3*frameTime)));
            rotAmount = (3 * frameTime);

            cameraAzimuth += rotAmount;
            cameraAzimuth = cameraAzimuth % 360;

            updateCameraPosition();

            //camera.setPo((Vector3f)Vector3f.createFrom(p2.x(),p2.y(),p2.z()));
        }
    }

    private class ZoomInAction extends AbstractInputAction { // Moves the camera around the target (changes camera azimuth).

        public void performAction(float time, net.java.games.input.Event evt) {

            float rotAmount = 0;
            frameTime = (MyGame.getFrameTime() / 60f);
            if (radias >= 0.65f) {
                rotAmount = -0.02f;
            }


            radias += rotAmount;
            updateCameraPosition();


        }
    }

    private class ZoomOutAction extends AbstractInputAction { // Moves the camera around the target (changes camera azimuth).

        public void performAction(float time, net.java.games.input.Event evt) {

            float rotAmount = 0;
            frameTime = (MyGame.getFrameTime() / 60f);
            if (radias <= 3.5f) {
                rotAmount = 0.02f;
            }


            radias += rotAmount;
            updateCameraPosition();


        }

    }

    public boolean getUsesController(){
        return usesController;
    }
}

