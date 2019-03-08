package a2;
import java.util.concurrent.ThreadLocalRandom;
import myGameEngine.*;
import ray.rage.rendersystem.shader.*;
import java.awt.*;
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
import ray.rage.asset.*;
import ray.input.*;
import ray.input.action.*;
import ray.rage.rendersystem.states.*;
import ray.rage.asset.texture.*;
import ray.rage.util.BufferUtil;
import java.nio.*;
import ray.rage.asset.material.*;
import java.util.List;
import ray.rage.scene.controllers.OrbitController;





public class MyGame extends VariableFrameRateGame {

	GL4RenderSystem rs;
	private float elapsTime = 0.0f;
	private static float eTime;
	String timeLeftStr, scoreStr, dispStr, dispStr2, objTime;
	int TimeLeft, score= 0;
    int objSpecTime = 120;
    private InputManager im;
    private Action quitGameAction, moveBackwardAction, moveForwardAction, moveRightAction, moveLeftAction, incrementCounterAction, rotateUpAction, rotateDownAction, rotateRightAction, rotateLeftAction, toggleDolphinAction, moveSideAction, moveVerticleAction, rotateHorizontalAction, rotateVerticleAction, powerAction;
    private static Camera camera, camera2;
    private static SceneNode dolphinN, dolphin2N, cameraN, camera2N, cameraNode1, specialItemN, rootNode;
    private Iterator<SceneNode> planetIterator;
    private SceneManager tempManager;
    private boolean  win, loss;
    private float earthDistance = 10, planet1Distance = 10, planet2Distance = 10, specialDistance = 10;
    private SceneNode earthN, shapeN, planet2N, planet3N, planetGroup1N, planetGroup2N, dolphinPlanet1N, dolphinPlanet2N;
    private List<SceneNode> freePlanets, moons;
    private static float distEarthResult, distPlanet1Result, distPlanet2Result, superPowerTime, superPowerTime2;
    private static boolean superPower, superPower2;
    ManualObject specialItem;
    private static Game game;
    private Camera3Pcontroller orbitController1, orbitController2;
    private RenderWindow rWin;
    private BounceController bC; //for bounce controller nodes
    private WarpController wC;  //for warp controller nodes
    private  boolean hasBeenAdded [], player1Grabbed[], player2Grabbed[];
    private int player1Score, player2Score;
    private boolean gameOver,player1Wins, gameTied;
    private OrbitController orC;
    private Viewport botViewport, topViewport ;



//2 arrays of planets 50 eacg
//scale them really small
//first player approaches planets and they bounce
//second  player, they rotate/
//they then proceed to orbit around dolphin that collected them
// one point each planet collected



    public static void main(String[] args) {
        game = new MyGame();
        try {
            game.startup();
            game.run();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            game.shutdown();
            game.exit();
        }
    }

    protected void setupWindowViewports(RenderWindow rw)
    { rw.addKeyListener(this);
        topViewport = rw.getViewport(0);
        topViewport.setDimensions(.50f, .0f, 1.0f, .499f); // B,L,W,H
        topViewport.setClearColor(new Color(0.2589f, .8745f, .9569f));
        botViewport = rw.createViewport(.0f, .0f, 1.0f, .499f);
        botViewport.setClearColor(new Color(0.2589f, .8745f, .9569f));
    }

    @Override
    protected void setupCameras(SceneManager sm, RenderWindow rw) {
        rootNode = sm.getRootSceneNode();
        camera = sm.createCamera("MainCamera", Projection.PERSPECTIVE);

        rw.getViewport(0).setCamera(camera);
        cameraN = rootNode.createChildSceneNode(camera.getName()+"Node");
        cameraN.attachObject(camera);
        camera.setMode('r');
        camera.getFrustum().setFarClipDistance(1000.0f);

        camera2 = sm.createCamera("MainCamera2", Projection.PERSPECTIVE);
		
		camera.setRt((Vector3f)Vector3f.createFrom(1.0f, 0.0f, 0.0f));
		camera.setUp((Vector3f)Vector3f.createFrom(0.0f, 1.0f, 0.0f));
		camera.setFd((Vector3f)Vector3f.createFrom(0.0f, 0.0f, -1.0f));
        rw.getViewport(1).setCamera(camera2);
        camera2N = rootNode.createChildSceneNode(camera2.getName() + "Node");
        camera2N.attachObject(camera2);
        camera2.setMode('r');
        camera2.getFrustum().setFarClipDistance(1000.0f);
        camera2N.moveLeft(2.0f);

        camera2.setRt((Vector3f)Vector3f.createFrom(1.0f, 0.0f, 0.0f));
        camera2.setUp((Vector3f)Vector3f.createFrom(0.0f, 1.0f, 0.0f));
        camera2.setFd((Vector3f)Vector3f.createFrom(0.0f, 0.0f, -1.0f));
		
		camera.setPo((Vector3f)Vector3f.createFrom(0.0f, 0.0f, 0.0f));
        camera2.setPo((Vector3f)Vector3f.createFrom(0.0f, 0.0f, 0.0f));

        rs =(GL4RenderSystem) sm.getRenderSystem(); //rendersystem for mouse in orbit controller
        rWin = rw;





    }
	
    @Override
    protected void setupScene(Engine eng, SceneManager sm) throws IOException {
        im = new GenericInputManager();
        /*Entities    */



        gameOver = false;
        gameTied = false;
        player1Wins = false;
        hasBeenAdded = new boolean[40];
        player1Grabbed = new boolean[40];
        player2Grabbed = new boolean[40];
        for(boolean bol: hasBeenAdded){
            bol = false;
        }

        win = false;
        loss = true;
        Entity dolphinE = sm.createEntity("myDolphin", "dolphinHighPoly.obj");
        dolphinE.setPrimitive(Primitive.TRIANGLES);

        Entity dolphin2E = sm.createEntity("myDolphin2", "dolphinHighPoly.obj");
        dolphin2E.setPrimitive(Primitive.TRIANGLES);

        freePlanets = new ArrayList<SceneNode>();
        moons = new ArrayList<SceneNode>();



        RenderSystem rs = sm.getRenderSystem();
        TextureManager tm = eng.getTextureManager();

        Entity earthE = sm.createEntity("earth", "earth.obj");

        Entity planetE = sm.createEntity("planet", "sphere.obj");
        Texture moonTexture = tm.getAssetByPath("moon.jpeg");
        TextureState state = (TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
        state.setTexture(moonTexture);
        planetE.setRenderState(state);

        Entity planet2E = sm.createEntity("planet2", "sphere.obj");


        Texture planet2Texture = tm.getAssetByPath("blue.jpeg");

        TextureState p2State = (TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
        p2State.setTexture(planet2Texture);
        planet2E.setRenderState(p2State);




        /*Nodes */
        dolphin2N = sm.getRootSceneNode().createChildSceneNode("myDolphin2Node");
        dolphin2N.moveBackward(2.0f);
        dolphin2N.moveRight(2.0f);
        dolphin2N.attachObject(dolphin2E);
        dolphin2N.yaw((Degreef.createFrom(125.0f))); //sets dolphin strait

        dolphinN = sm.getRootSceneNode().createChildSceneNode("myDolphinNode");
        dolphinN.moveBackward(2.0f);
        dolphinN.attachObject(dolphinE);
        dolphinN.yaw((Degreef.createFrom(125.0f))); //sets dolphin strait

        float randomRight = ThreadLocalRandom.current().nextInt(-20, 20);
        float randomBackward = ThreadLocalRandom.current().nextInt(-50, 50);
        float randomUp = ThreadLocalRandom.current().nextInt(-15, 15);

        planetGroup1N = sm.getRootSceneNode().createChildSceneNode("planetGroup1");
        planetGroup2N = sm.getRootSceneNode().createChildSceneNode("planetGroup2");
        dolphinPlanet1N = sm.getRootSceneNode().createChildSceneNode("dolphinPlanet1");
        dolphinPlanet2N = sm.getRootSceneNode().createChildSceneNode("dolphinPlanet2");



        bC  = new BounceController();
        sm.addController(bC);



//
//        bC.setEnabled(false);
//
//        bC.addNode(dolphinPlanet1N);
//
        wC = new WarpController();
        sm.addController(wC);

          //create earths

        //children added to planet nodes for planet hierarchy requirement
        for(int i=0; i < 20; i++){
            Entity earthE1 = sm.createEntity("earth" + i, "earth.obj");
            randomRight = ThreadLocalRandom.current().nextInt(-40, 40);
            randomBackward = ThreadLocalRandom.current().nextInt(-40, 40);
            randomUp = ThreadLocalRandom.current().nextInt(-15, 15);
            SceneNode earthNode = planetGroup1N.createChildSceneNode("earthNode"+i);
            earthNode.attachObject(earthE1);
            earthNode.moveBackward(randomBackward);
            earthNode.moveRight(randomRight);
            earthNode.scale(0.05f,0.05f,0.05f);

            //bC.addNode(earthNode);
            freePlanets.add(earthNode);

        }
        for(int i=0; i < 20; i++){
            Entity moonE = sm.createEntity("moon"+ i, "sphere.obj");
            Texture moonTexture1 = tm.getAssetByPath("moon.jpeg");
            TextureState state1 = (TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
            state.setTexture(moonTexture1);
            moonE.setRenderState(state1);

            randomRight = ThreadLocalRandom.current().nextInt(-40, 40);
            randomBackward = ThreadLocalRandom.current().nextInt(-40, 40);
            randomUp = ThreadLocalRandom.current().nextInt(-15, 15);
            SceneNode moonNode = planetGroup2N.createChildSceneNode("moonNode"+i);
            moonNode.attachObject(moonE);
            moonNode.moveBackward(randomBackward);
            moonNode.moveRight(randomRight);
            moonNode.scale(0.1f,0.1f,0.1f);
            freePlanets.add(moonNode);

        }





        orC = new OrbitController(dolphinN, 2.0f, 1.0f);


        sm.addController(orC);



        specialItemN = dolphinN.createChildSceneNode("SpecialItemNode");

        sm.getAmbientLight().setIntensity(new Color(.5f, .5f, .5f));

        // make manual objects – in this case a pyramid
        specialItem = makePyramid(eng, sm);
        specialItemN.scale(0.1f, 0.1f, 0.1f);
        specialItemN.moveForward(1.5f);
        specialItemN.moveUp(0.6f);

		
		Light plight = sm.createLight("testLamp1", Light.Type.POINT);
		plight.setAmbient(new Color(.3f, .3f, .3f));
        plight.setDiffuse(new Color(.7f, .7f, .7f));
		plight.setSpecular(new Color(1.0f, 1.0f, 1.0f));
        plight.setRange(5f);
		
		SceneNode plightNode = sm.getRootSceneNode().createChildSceneNode("plightNode");
        plightNode.attachObject(plight);


        ManualObject shape = makeShape(eng, sm);
        shapeN = sm.getRootSceneNode().createChildSceneNode("ShapeNode");
        shapeN.scale(0.8f, 0.8f, 0.8f);

        shapeN.moveUp(1.4f);
        shapeN.attachObject(shape);




        //coordinates


        //ground plane
        ManualObject ground = makeGround(eng,sm);
        SceneNode groundN = sm.getRootSceneNode().createChildSceneNode("ground");
        groundN.attachObject(ground);
        groundN.scale(1000f,1.0f, 1000f);
        groundN.moveUp(0.5f);


       // RotationController rc = new RotationController(Vector3f.createUnitVectorY(),0.02f);
        //rc.addNode(earthN);
        //rc.addNode(planet2N);
        //rc.addNode(planet3N);
        //sm.addController(rc);
        setupOrbitCameras(eng, sm);
        setupInputs(sm);


        //getsceneNodes
        tempManager = sm;
        objSpecTime = 50;   //set time to win to 70




    }

    protected void setupOrbitCameras(Engine eng, SceneManager sm)
    {
        String gpName = im.getFirstGamepadName();
        orbitController1 = new Camera3Pcontroller(camera, cameraN, dolphinN, gpName,  im, rWin,rs, false);
        orbitController2 = new Camera3Pcontroller(camera2, camera2N, dolphin2N, gpName,im, rWin,rs, true);
    }


    @Override
    protected void update(Engine engine) {
        rs = (GL4RenderSystem) engine.getRenderSystem();
        //checkNodeDistance();

        Vector3f dolphinNPos = (Vector3f) dolphinN.getWorldPosition();

        eTime = engine.getElapsedTimeMillis();
        elapsTime += engine.getElapsedTimeMillis();
        TimeLeft = (objSpecTime-Math.round(elapsTime/1000.0f));
        scoreStr = Integer.toString(score);

        timeLeftStr = Integer.toString(TimeLeft);
        objTime = Integer.toString(objSpecTime);

        dispStr = "Countdown: " + (timeLeftStr) + " Score: " + player2Score;
        dispStr2 = "Countdown: " + (timeLeftStr) + " Score: " + player1Score;
        int hud2NewPos = (topViewport.getActualBottom() + 15);

        // after 60 seconds, see who has more points, whoever does, wins
       // finished bool && player1wins bool
        if(gameOver == false){
            rs.setHUD(dispStr, 15, 15);
            rs.setHUD2(dispStr2, 15, hud2NewPos);
        }else if(gameOver == true && player1Wins == true && gameTied == false){
            rs.setHUD2("You won, your final score is: " + player1Score, 15, hud2NewPos);
            rs.setHUD("You lost, your final score is: " + player2Score, 15, 15);
            bC.removeAllNodes();

            int currentIndex;
            for(SceneNode node: freePlanets){
                currentIndex = freePlanets.indexOf(node);
                if(player1Grabbed[currentIndex] == true){

                    orC.addNode(node);
                    player1Grabbed[currentIndex] = false;
                }
            }
        }else if(gameOver == true && player1Wins == false && gameTied == false){
            rs.setHUD2("You Lost, your final score is: " + player1Score, 15, hud2NewPos);
            rs.setHUD("You won, your final score is: " + player2Score, 15, 15);
            int currentIndex;

            wC.removeAllNodes();
            for(SceneNode node: freePlanets){
                currentIndex = freePlanets.indexOf(node);
                if(player2Grabbed[currentIndex] == true){
                    orC.setTarget(dolphin2N);
                    orC.addNode(node);
                    player2Grabbed[currentIndex] = false;
                }
            }
        }else{
            int currentIndex;
            rs.setHUD2("You tied! The plantes have been returned to the origin, Your Score: " + player1Score, 15, hud2NewPos);
            rs.setHUD("You tied! The plantes have been returned to the origin, YourScore: " + player2Score, 15, 15);
            wC.removeAllNodes();
            bC.removeAllNodes();
            for(SceneNode node: freePlanets){
                currentIndex = freePlanets.indexOf(node);
                if(player2Grabbed[currentIndex] == true || player1Grabbed[currentIndex] == true){
                    orC.setTarget(shapeN);
                    orC.addNode(node);
                    player2Grabbed[currentIndex] = false;
                    player1Grabbed[currentIndex] = false;
                }
            }
        }



        if(TimeLeft <= 0){
            gameOver = true;
            if(player1Score > player2Score){
                player1Wins = true;
                gameTied = false;
            }else if(player2Score > player1Score){
                player1Wins = false;
                gameTied = false;
            }
            else{
                gameTied = true;
            }
        }
//        if(TimeLeft <= -2 && loss == true){
//            this.shutdown();
//
//        }
//        if(superPower == true && superPowerTime >= 0){
//            //eTime *= 3;
//            float tempSecond = TimeLeft - (TimeLeft-1);
//            superPowerTime -= tempSecond/100 ;
//        }else{
//            superPower = false;
//        }


        dolphinN.update();
        dolphin2N.update();
        im.update(elapsTime);
        orbitController1.updateCameraPosition();
        orbitController2.updateCameraPosition();
        planetDistances();




	}



    private void planetDistances(){
        Vector3f dolphin1Pos = (Vector3f) dolphinN.getWorldPosition();
        Vector3f dolphin2Pos = (Vector3f) dolphin2N.getWorldPosition();
        int currentIndex;

        //array to tell weather node has been added to controller


        for(SceneNode node: freePlanets){
            //check distances between plantes and dolphin1

            //player 1

            Vector3f nodePos = (Vector3f) node.getWorldPosition();
            Vector3f subVec = (Vector3f) dolphin1Pos.sub(nodePos);
            float squaredVec = (float) Math.pow(subVec.x(), 2) + (float) Math.pow(subVec.y(), 2) + (float) Math.pow(subVec.z(), 2);
            float d1result  = (float) Math.sqrt(squaredVec);

            currentIndex = freePlanets.indexOf(node);
            if((d1result < 1.0f) && hasBeenAdded[currentIndex] == false && gameOver == false){

                bC.addNode(node);
                player1Score++;
                hasBeenAdded[currentIndex] = true;
                player1Grabbed[currentIndex] = true;
            } else if(d1result > 1.0f){
                //bC.removeNode(node);
                //hasBeenAdded[currentIndex] = false;
            }

            //player 2
            //check distances between plantes and dolphin2
            Vector3f subVec2 = (Vector3f) dolphin2Pos.sub(nodePos);
            float squaredVec2 = (float) Math.pow(subVec2.x(), 2) + (float) Math.pow(subVec2.y(), 2) + (float) Math.pow(subVec2.z(), 2);
            float d2result  = (float) Math.sqrt(squaredVec2);
            if((d2result < 1.0f) && hasBeenAdded[currentIndex] == false && gameOver == false){
                wC.addNode(node);
                player2Score++;
                hasBeenAdded[currentIndex] = true;
                player2Grabbed[currentIndex] = true;
            } else if(d2result > 1.0f){
                //bC.removeNode(node);
                //hasBeenAdded[currentIndex] = false;
            }






        }
    }




    public static float getPlanet1Distance() {
        return distPlanet1Result;
    }

    public static float getPlanet2Distance() {
        return distPlanet2Result;
    }
    public float getDistEarthResult(){
        return distEarthResult;
    }

    public void incrementCounter(){
        score++;
    }
    public void shutdown() {
        System.out.println("shutdown requested");
        game.setState(Game.State.STOPPING);
    }



    //magnitude of distance between camera and dolphin
    public static float getDolphinCameraDistance(){
        Vector3f cameraP = camera.getPo();
        Vector3f dolphinP = (Vector3f) dolphinN.getWorldPosition();
        Vector3f subPos = (Vector3f) dolphinP.sub(cameraP);
        float squared = (float)Math.pow(subPos.x(),2) + (float)Math.pow(subPos.y(),2) + (float)Math.pow(subPos.z(),2);
        float distResult = (float)Math.sqrt(squared);
        return distResult;

    }
    public static float getFrameTime(){
        return eTime;
    }




    protected void setupInputs(SceneManager sm) {

        String kbName = im.getKeyboardName();
        String gpName = im.getFirstGamepadName();
        //String mName = im.getMouseName();

        System.out.println(kbName);

        dolphinN.moveRight(3);

        quitGameAction = new QuitGameAction(this);


        //movement
        moveForwardAction = new ActionMoveForward(camera, dolphinN);
        moveRightAction = new ActionMoveRight(camera, dolphinN);
        moveLeftAction = new ActionMoveLeft(camera, dolphinN);
        moveBackwardAction = new ActionMoveBackward(camera, dolphinN);
        moveSideAction = new ActionSideMove(camera, dolphin2N);


//        //rotation



        //toggles



        if(orbitController1.getUsesController() == false){
            im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.Y, quitGameAction, InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
            //im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.C, powerAction, InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
            im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.W, moveForwardAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.D, moveRightAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

            im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.A, moveLeftAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.S, moveBackwardAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

            //camera rotation
            im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.UP, rotateUpAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.DOWN, rotateDownAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        }

        if(orbitController2.getUsesController() == true){
            im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._2, powerAction, InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
            im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._0, incrementCounterAction, InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
            im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.Z, moveSideAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        }

        //Movement


    }


    //used from his notes for extra activity object
    protected ManualObject makePyramid(Engine eng, SceneManager sm) throws IOException {
        ManualObject pyr = sm.createManualObject("Pyramid");
        ManualObjectSection pyrSec = pyr.createManualSection("PyramidSection");
        pyr.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
        float[] vertices = new float[]
                { -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f, //front
                        1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, //right
                        1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, //back
                        -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f, //left
                        -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, //LF
                        1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f //RR
                };



        float[] texcoords = new float[]
                { 0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
                        0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
                        0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
                        0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                        1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f
                };
        float[] normals = new float[]
                { 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, 1.0f, -1.0f,
                        -1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f
                };
        int[] indices = new int[] { 0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17};

        FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
        FloatBuffer texBuf = BufferUtil.directFloatBuffer(texcoords);
        FloatBuffer normBuf = BufferUtil.directFloatBuffer(normals);
        IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
        pyrSec.setVertexBuffer(vertBuf);
        pyrSec.setTextureCoordsBuffer(texBuf);
        pyrSec.setNormalsBuffer(normBuf);
        pyrSec.setIndexBuffer(indexBuf);
        Texture tex = eng.getTextureManager().getAssetByPath("chain-fence.jpeg");
        TextureState texState = (TextureState)sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
        texState.setTexture(tex);
        FrontFaceState faceState = (FrontFaceState) sm.getRenderSystem().createRenderState(RenderState.Type.FRONT_FACE);
        pyr.setDataSource(DataSource.INDEX_BUFFER);
        pyr.setRenderState(texState);
        pyr.setRenderState(faceState);
        return pyr;
    }
    protected ManualObject makeGround(Engine eng, SceneManager sm) throws IOException {
        ManualObject ground = sm.createManualObject("Ground");
        ManualObjectSection groundSec = ground.createManualSection("GroundSection");
        ground.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
        float[] vertices = new float[]
                { -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, //first triangle
                        1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, //first triangle


                };



        float[] texcoords = new float[]
                { 0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f, 0.5f, 0.0f,

                };
        float[] normals = new float[]
                { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,

                };
        int[] indices = new int[] { 0,1,2,3,4,5};

        FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
        FloatBuffer texBuf = BufferUtil.directFloatBuffer(texcoords);
        FloatBuffer normBuf = BufferUtil.directFloatBuffer(normals);
        IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
        groundSec.setVertexBuffer(vertBuf);
        groundSec.setTextureCoordsBuffer(texBuf);
        groundSec.setNormalsBuffer(normBuf);
        groundSec.setIndexBuffer(indexBuf);
        Texture tex = eng.getTextureManager().getAssetByPath("ground.jpg");
        TextureState texState = (TextureState)sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
        texState.setTexture(tex);
        FrontFaceState faceState = (FrontFaceState) sm.getRenderSystem().createRenderState(RenderState.Type.FRONT_FACE);
        ground.setDataSource(DataSource.INDEX_BUFFER);
        ground.setRenderState(texState);
        ground.setRenderState(faceState);
        return ground;
    }
    protected ManualObject makeShape(Engine eng, SceneManager sm) throws IOException {
        ManualObject shape = sm.createManualObject("Shape");
        ManualObjectSection shapeSec = shape.createManualSection("ShapeSection");
        shape.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
        //shape
        float[] vertices = new float[]
                {-1.0f,1.0f,0.5f,1.0f,1.0f,0.5f,0.0f,2.0f,0.0f,//topfront
                        1.0f,1.0f,0.5f,1.0f,1.0f,-0.5f,0.0f,2.0f,0.0f,//topright
                        1.0f,1.0f,-0.5f, -1.0f,1.0f,-0.5f,0.0f,2.0f,0.0f,//top back
                        -1.0f,1.0f,-0.5f,-1.0f,1.0f,0.5f,0.0f,2.0f, 0.0f,//topLeft
                        1.0f,1.0f,0.5f,-1.0f,1.0f,0.5f,0.0f,-2.0f,0.0f,//bottomfront
                        1.0f,1.0f,-0.5f,1.0f,1.0f,0.5f,0.0f,-2.0f,0.0f,//bottomright
                         -1.0f,1.0f,-0.5f,1.0f,1.0f,-0.5f,0.0f,-2.0f,0.0f,//bottom back
                        -1.0f,1.0f,0.5f,-1.0f,1.0f,-0.5f,0.0f,-2.0f, 0.0f//bottomLeft


                };
        float[] texcoords = new float[]
                    { 0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f, //topfront
                            0.25f, 0.0f, 0.75f,0.0f,0.5f,1.0f, //topright
                            0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f, //topback
                            0.25f, 0.0f, 0.75f,0.0f,0.5f,1.0f, //topleft
                            0.25f, 0.0f, 0.75f, 0.0f, 0.5f, 1.0f, //bottomfront
                            0.4f, 0.0f, 0.6f,0.0f,0.5f,1.0f, //bottomright
                            0.25f, 0.0f, 0.75f, 0.0f, 0.5f, 1.0f, //bottomback
                            0.4f, 0.0f, 0.6f,0.0f,0.5f,1.0f //boottomleft





                };
        float[] normals = new float[]
                {         0.0f,2.0f,0.5f,0.0f,2.0f,0.5f,0.0f,2.0f,0.5f,//topfront
                        1.0f,2.0f,0.0f,1.0f,2.0f,0.0f,1.0f,2.0f,0.0f,//topright
                        0.0f,2.0f,-0.5f,0.0f,2.0f,-0.5f,0.0f,2.0f,-0.5f,//topback
                        -1.0f,2.0f,0.0f,-1.0f,2.0f,0.0f,-1.0f,2.0f,0.0f, //topleft
                        0.0f,0.0f,0.5f,0.0f,0.0f,0.5f,0.0f,0.0f,0.5f,//bottomfront
                        1.0f,0.0f,0.0f,1.0f,0.0f,0.0f,1.0f,0.0f,0.0f,//bottomright
                        0.0f,0.0f,-0.5f,0.0f,0.0f,-0.5f,0.0f,0.0f,-0.5f,//bottomback
                        -1.0f,0.0f,0.0f,-1.0f,0.0f,0.0f,-1.0f,0.0f,0.0f //bottomleft




                };
        int[] indices = new int[] { 0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20, 21,22,23};//,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41};

        FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
        FloatBuffer texBuf = BufferUtil.directFloatBuffer(texcoords);
        FloatBuffer normBuf = BufferUtil.directFloatBuffer(normals);
        IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
        shapeSec.setVertexBuffer(vertBuf);
        shapeSec.setTextureCoordsBuffer(texBuf);
        shapeSec.setNormalsBuffer(normBuf);
        shapeSec.setIndexBuffer(indexBuf);
        Texture tex = eng.getTextureManager().getAssetByPath("hexagons.jpeg");
        TextureState texState = (TextureState)sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
        texState.setTexture(tex);
        Material mat1 = sm.getMaterialManager().getAssetByPath("default.mtl");
        mat1.setEmissive(Color.WHITE);
        shapeSec.setMaterial(mat1);
        FrontFaceState faceState = (FrontFaceState) sm.getRenderSystem().createRenderState(RenderState.Type.FRONT_FACE);
        shape.setDataSource(DataSource.INDEX_BUFFER);
        shape.setRenderState(texState);
        shape.setRenderState(faceState);
        shape.setMaterial(mat1);
        return shape;
    }
    protected ManualObject makeXLine(Engine eng, SceneManager sm) throws IOException {
        ManualObject line = sm.createManualObject("Line");
        ManualObjectSection lineSec = line.createManualSection("LineSection");
        line.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
        SceneNode xNode = sm.getRootSceneNode();



        float [] vertices = new float[]
        { -1000.0f,0.0f, 0.0f,
                1000.0f,0.0f,0.0f

        };
        int indices[]  = new int[]{0,1};
        FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
        IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
        lineSec.setVertexBuffer(vertBuf);
        lineSec.setIndexBuffer(indexBuf);


        Material mat = sm.getMaterialManager().getAssetByPath("default.mtl");

        mat.setEmissive(Color.WHITE);
        Texture tex = eng.getTextureManager().getAssetByPath("bright-red.jpeg");
        TextureState tstate = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);

        tstate.setTexture(tex);

        lineSec.setRenderState(tstate);

        lineSec.setMaterial(mat);

        line.setPrimitive(Primitive.LINES);
        return line;


    }
    protected ManualObject makeYLine(Engine eng, SceneManager sm) throws IOException {
        ManualObject yLine = sm.createManualObject("YLine");
        ManualObjectSection yLineSec = yLine.createManualSection("YLineSection");
        yLine.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
        SceneNode yNode = sm.getRootSceneNode();



        float [] vertices = new float[]
                { 0.0f,-1000.0f, 0.0f,
                        0.0f,1000.0f,0.0f

                };
        int indices[]  = new int[]{0,1};
        FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
        IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
        yLineSec.setVertexBuffer(vertBuf);
        yLineSec.setIndexBuffer(indexBuf);


        Material mat = sm.getMaterialManager().getAssetByPath("default.mtl");

        mat.setEmissive(Color.WHITE);
        Texture tex = eng.getTextureManager().getAssetByPath("bright-blue.jpeg");
        TextureState tstate = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);

        tstate.setTexture(tex);

        yLineSec.setRenderState(tstate);

        yLineSec.setMaterial(mat);
        yLine.setPrimitive(Primitive.LINES);
        return yLine;


    }

    protected ManualObject makeZLine(Engine eng, SceneManager sm) throws IOException {
        ManualObject zLine = sm.createManualObject("ZLine");
        ManualObjectSection zLineSec = zLine.createManualSection("ZLineSection");
        zLine.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
        SceneNode zNode = sm.getRootSceneNode();

        float [] vertices = new float[]
                { 0.0f,0.0f, 1000.0f,
                        0.0f,0.0f,-1000.0f

                };
        int indices[]  = new int[]{0,1};
        FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
        IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
        zLineSec.setVertexBuffer(vertBuf);
        zLineSec.setIndexBuffer(indexBuf);


        Material mat = sm.getMaterialManager().getAssetByPath("default.mtl");

        mat.setEmissive(Color.WHITE);
        Texture tex = eng.getTextureManager().getAssetByPath("bright-green.jpeg");
        TextureState tstate = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);

        tstate.setTexture(tex);

        zLineSec.setRenderState(tstate);

        zLineSec.setMaterial(mat);

        zLine.setPrimitive(Primitive.LINES);
        return zLine;


    }
}
