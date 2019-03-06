package myGameEngine;
import ray.rage.scene.*;
import ray.rage.scene.controllers.*;
import ray.rml.*;
public class BounceController extends AbstractController
{
    private float bounceRate = .06f; // move rate
    private float cycleTime = 250f;
    private float totalTime = 0.0f;
    private float direction = 1.0f;
    private float bounceAmt;
    @Override
    protected void updateImpl(float elapsedTimeMillis)
    { totalTime += elapsedTimeMillis;
        bounceAmt = direction * bounceRate;
        if (totalTime > cycleTime)
        { direction = -direction;

            totalTime = 0.0f;
        }
        for (Node n : super.controlledNodesList)
        { Vector3 curPos = n.getLocalPosition();
            if(curPos.y() + bounceAmt >= -0.5){
                n.moveUp(bounceAmt);
            }

        }
    }
}