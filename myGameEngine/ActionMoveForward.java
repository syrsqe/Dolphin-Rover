package myGameEngine;

import a2.*;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rage.game.*;
import ray.rml.*;
import net.java.games.input.Event;

public class ActionMoveForward extends AbstractInputAction {
    private Camera camera;
    private SceneNode node;

    private float frameTime;

    public ActionMoveForward(Camera c, SceneNode n) {
        camera = c;
        node = n;

        frameTime = 0;

    }

    public void performAction(float time, Event event) {
        System.out.println("forward called");
        frameTime = (MyGame.getFrameTime()/500f); //get the proper ratio, so that ship is the write speed
        if (camera.getMode() == 'c') {
            Vector3f n = camera.getFd();
            Vector3f cameraP = camera.getPo();
            float addX = frameTime*n.x();
            float addY = frameTime*n.y();
            float addZ = frameTime*n.z();
            float addedDistance = addX + addY + addZ + MyGame.getDolphinCameraDistance();
            Vector3f p1 = (Vector3f) Vector3f.createFrom(addX, addY, addZ);
            Vector3f p2 = (Vector3f) cameraP.add((Vector3) p1);


            Vector3f nodeP = (Vector3f) node.getWorldPosition();
            Vector3f subPos = (Vector3f) nodeP.sub(p2);

            float newsSquared = (float)Math.pow(subPos.x(),2) + (float)Math.pow(subPos.y(),2) + (float)Math.pow(subPos.z(),2);
            float newDistResult = (float)Math.sqrt(newsSquared);



            if(newDistResult < 2.0004f) {
                camera.setPo((Vector3f) Vector3f.createFrom(p2.x(), p2.y(), p2.z()));
            }



        } else if (camera.getMode() == 'r') {
            node.moveForward(2*frameTime);
        }

    }


}
