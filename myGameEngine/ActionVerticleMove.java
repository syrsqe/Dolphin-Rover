package myGameEngine;
import a2.*;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rage.game.*;
import ray.rml.*;
import net.java.games.input.*;

public class ActionVerticleMove extends AbstractInputAction{
    private Camera camera;
    private SceneNode node;

    private float frameTime;

    public ActionVerticleMove(Camera c, SceneNode n){
        camera = c;
        node = n;
        frameTime = 0;


    }

    public void performAction(float time, Event event){

        Vector3f n = camera.getFd();
        Vector3f p = camera.getPo();

        frameTime = (MyGame.getFrameTime()/500f);

        Component c = event.getComponent();
        System.out.println(event.getValue());
        if(c.getName().equals("S") || (event.getValue() > 0.1)){
            Vector3f p1 = (Vector3f) Vector3f.createFrom(-frameTime*n.x(), -frameTime*n.y(), -frameTime*n.z());
            Vector3f p2 = (Vector3f) p.add((Vector3)p1);
            if(camera.getMode() == 'c'){

                Vector3f nodeP = (Vector3f) node.getWorldPosition();
                Vector3f subPos = (Vector3f) nodeP.sub(p2);

                float newsSquared = (float)Math.pow(subPos.x(),2) + (float)Math.pow(subPos.y(),2) + (float)Math.pow(subPos.z(),2);
                float newDistResult = (float)Math.sqrt(newsSquared);



                if(newDistResult < 2.0004f){
                    camera.setPo((Vector3f) Vector3f.createFrom(p2.x(), p2.y(), p2.z()));
                }


            }else if (camera.getMode() == 'r') {
                node.moveForward(-2*frameTime);
            }
        }
        if(c.getName().equals("W") || (event.getValue() < -0.1)){
            Vector3f p1 = (Vector3f) Vector3f.createFrom(frameTime*n.x(), frameTime*n.y(), frameTime*n.z());
            Vector3f p2 = (Vector3f) p.add((Vector3)p1);
        if (camera.getMode() == 'c') {



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


}