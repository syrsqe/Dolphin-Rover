package myGameEngine;
import a2.*;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rage.game.*;
import ray.rml.*;
import net.java.games.input.*;
import ray.rml.Degreef;
//import ray.rml.Matrix4f;
public class ActionSideMove extends AbstractInputAction{
    private Camera camera;
    private SceneNode node;
    private boolean hitedge;

    private float frameTime;

    public ActionSideMove(Camera c, SceneNode n){
        camera = c;
        node = n;

        frameTime = 0;

    }

    public void performAction(float time, Event event){
        Component c = event.getComponent();
        //make sure either D is pressed or right value of joystick is pressed
        Vector3f u = camera.getRt();
        Vector3f p = camera.getPo();

        frameTime = (MyGame.getFrameTime()/500f);
        System.out.println(event.getValue());
        if(c.getName().equals("D") || (event.getValue() < 0.2)){
            Vector3f p1 = (Vector3f) Vector3f.createFrom(frameTime*u.x(), frameTime*u.y(), frameTime*u.z());
            Vector3f p2 = (Vector3f) p.add((Vector3)p1);
            if(camera.getMode() == 'c'){


                Vector3f nodeP = (Vector3f) node.getWorldPosition();
                Vector3f subPos = (Vector3f) nodeP.sub(p2);


                float newsSquared = (float)Math.pow(subPos.x(),2) + (float)Math.pow(subPos.y(),2) + (float)Math.pow(subPos.z(),2);
                float newDistResult = (float)Math.sqrt(newsSquared);


                if(newDistResult < 2.0004f){

                    camera.setPo((Vector3f)Vector3f.createFrom(p2.x(),p2.y(),p2.z()));
                }
            }else if(camera.getMode() == 'r'){
                node.moveRight(-2*frameTime);
            }
        }
        if(c.getName().equals("A") || event.getValue() > -0.2){
            Vector3f p1 = (Vector3f) Vector3f.createFrom(-frameTime*u.x(), -frameTime*u.y(), -frameTime*u.z());
            Vector3f p2 = (Vector3f) p.add((Vector3)p1);
            frameTime = (MyGame.getFrameTime());
            frameTime = frameTime/500f;
            //System.out.println("elapsed time" + frameTime);
            if (camera.getMode() == 'c') {

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
        }


    }


}