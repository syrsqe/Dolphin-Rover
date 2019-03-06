package myGameEngine;
import a2.*;
import java.lang.*;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rage.game.*;
import ray.rml.*;
import net.java.games.input.*;
import ray.rml.Degreef;

//import ray.rml.Matrix4f;
public class ActionMoveLeft extends AbstractInputAction{
    private Camera camera;
    private SceneNode node;
    private Vector3f subPos;
    private float posVector;

    private float frameTime;

    public ActionMoveLeft(Camera c, SceneNode n){
        camera = c;
        node = n;

        frameTime = 0;
    }

    public void performAction(float time, Event event) {
        Component c = event.getComponent();

            frameTime = (MyGame.getFrameTime());
            frameTime = frameTime/500f;
            //System.out.println("elapsed time" + frameTime);
            if (camera.getMode() == 'c') {

                Vector3f u = camera.getRt();
                Vector3f cameraP = camera.getPo();
                float addX = frameTime*u.x();
                float addY = frameTime*u.y();
                float addZ = frameTime*u.z();
                float addedDistance = addX + addY + addZ + MyGame.getDolphinCameraDistance();
                //get total distance from dolphin + added distance

                Vector3f p1 = (Vector3f) Vector3f.createFrom(-addX, -addY, -addZ);
                Vector3f p2 = (Vector3f) cameraP.add((Vector3) p1);


                Vector3f nodeP = (Vector3f) node.getWorldPosition();
                Vector3f subPos = (Vector3f) nodeP.sub(p2);

                float newsSquared = (float)Math.pow(subPos.x(),2) + (float)Math.pow(subPos.y(),2) + (float)Math.pow(subPos.z(),2);
                float newDistResult = (float)Math.sqrt(newsSquared);





                if(newDistResult < 2.0004f){

                    camera.setPo((Vector3f) Vector3f.createFrom(p2.x(), p2.y(), p2.z()));

                }

            } else if (camera.getMode() == 'r') {

                node.moveRight(2*frameTime);

            }

        if(event.getValue() < 0){

        }

    }

}