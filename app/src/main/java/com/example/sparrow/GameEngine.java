package com.example.sparrow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class GameEngine extends SurfaceView implements Runnable {
    private final String TAG = "SPARROW";

    // game thread variables
    private Thread gameThread = null;
    private volatile boolean gameIsRunning;

    // drawing variables
    private Canvas canvas;
    private Paint paintbrush;
    private SurfaceHolder holder;

    // Screen resolution varaibles
    private int screenWidth;
    private int screenHeight;

    // VISIBLE GAME PLAY AREA
    // These variables are set in the constructor
    int VISIBLE_LEFT;
    int VISIBLE_TOP;
    int VISIBLE_RIGHT;
    int VISIBLE_BOTTOM;

    // SPRITES
    Square bullet;
    int SQUARE_WIDTH = 100;

    Square cage;
    Sprite player;
    Sprite sparrow;
    Sprite cat;

    int randX;
    int randY;



    ArrayList<Square> bullets = new ArrayList<Square>();

    // GAME STATS
    int score = 0;

    public GameEngine(Context context, int screenW, int screenH) {
        super(context);

        // intialize the drawing variables
        this.holder = this.getHolder();
        this.paintbrush = new Paint();

        // set screen height and width
        this.screenWidth = screenW;
        this.screenHeight = screenH;

        // setup visible game play area variables
        this.VISIBLE_LEFT = 20;
        this.VISIBLE_TOP = 10;
        this.VISIBLE_RIGHT = this.screenWidth - 20;
        this.VISIBLE_BOTTOM = (int) (this.screenHeight * 0.8);
        //create random number
        Random rand = new Random();
        randX = rand.nextInt( this.screenWidth );
        if(randX > this.screenWidth)
        {
            randX -=  100;
        }
        else if(randX < 0)
        {
            randX +=  100;
        }
        randY = rand.nextInt(  this.screenHeight);
        if(randY > this.screenHeight)
        {
            randY -=  - 100;
        }
        else if(randY < 0)
        {
            randY +=  + 100;
        }
        // initalize sprites
        Log.d(TAG, "Sparrow: width " + randX);
        Log.d(TAG, "Screen width: width " + this.screenWidth);
        Log.d(TAG, "Sparrow: height " + randY);
        Log.d(TAG, "Screen width: width " + this.screenWidth);
        this.player = new Sprite(this.getContext(), 100, 700, R.drawable.player64);
        this.sparrow = new Sprite(this.getContext(), randX, randY, R.drawable.bird64);
        this.cat = new Sprite(this.getContext(), this.screenWidth-600, this.screenHeight-600, R.drawable.cat64);
        this.cage = new Square(context, this.screenWidth-600, 100, 300);
    }

    @Override
    public void run() {
        while (gameIsRunning == true) {
            updateGame();    // updating positions of stuff
            redrawSprites(); // drawing the stuff
            controlFPS();
        }
    }

    // Game Loop methods
    public void updateGame() {
    }


    public void outputVisibleArea() {
        Log.d(TAG, "DEBUG: The visible area of the screen is:");
        Log.d(TAG, "DEBUG: Maximum w,h = " + this.screenWidth +  "," + this.screenHeight);
        Log.d(TAG, "DEBUG: Visible w,h =" + VISIBLE_RIGHT + "," + VISIBLE_BOTTOM);
        Log.d(TAG, "-------------------------------------");
    }



    public void redrawSprites() {
        if (holder.getSurface().isValid()) {

            // initialize the canvas
            canvas = holder.lockCanvas();
            // --------------------------------

            // set the game's background color
            canvas.drawColor(Color.argb(255,255,255,255));

            // setup stroke style and width
            paintbrush.setStyle(Paint.Style.FILL);
            paintbrush.setStrokeWidth(8);

            // --------------------------------------------------------
            // draw boundaries of the visible space of app
            // --------------------------------------------------------
            paintbrush.setStyle(Paint.Style.STROKE);
            paintbrush.setColor(Color.argb(255, 0, 128, 0));

            canvas.drawRect(VISIBLE_LEFT, VISIBLE_TOP, VISIBLE_RIGHT, VISIBLE_BOTTOM, paintbrush);
            this.outputVisibleArea();

            // --------------------------------------------------------
            // draw player and sparrow
            // --------------------------------------------------------

            // 1. player
            canvas.drawBitmap(this.player.getImage(), this.player.getxPosition(), this.player.getyPosition(), paintbrush);

            // 2. sparrow
            canvas.drawBitmap(this.sparrow.getImage(), this.sparrow.getxPosition(), this.sparrow.getyPosition(), paintbrush);
            //3. cat
            canvas.drawBitmap(this.cat.getImage(), this.cat.getxPosition(), this.cat.getyPosition(), paintbrush);
            //4. Cage
            canvas.drawRect(this.cage.getxPosition(),
                    this.cage.getyPosition(),
                    this.cage.getxPosition()+ this.cage.getWidth(),
                    this.cage.getyPosition()+ this.cage.getWidth(),
                    paintbrush
                    );
            // --------------------------------------------------------
            // draw hitbox on player
            // --------------------------------------------------------
            Rect r = player.getHitbox();
            Rect c = cat.getHitbox();
            paintbrush.setStyle(Paint.Style.STROKE);
            canvas.drawRect(r, paintbrush);
            canvas.drawRect(c,paintbrush);


            // --------------------------------------------------------
            // draw hitbox on player
            // --------------------------------------------------------
            paintbrush.setTextSize(60);
            paintbrush.setStrokeWidth(5);
            String screenInfo = "Screen size: (" + this.screenWidth + "," + this.screenHeight + ")";
            canvas.drawText(screenInfo, 10, 100, paintbrush);

            // --------------------------------
            holder.unlockCanvasAndPost(canvas);
        }

    }

    public void controlFPS() {
        try {
            gameThread.sleep(17);
        }
        catch (InterruptedException e) {

        }
    }


    // Deal with user input
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_DOWN:
                break;
       }
        return true;
    }

    // Game status - pause & resume
    public void pauseGame() {
        gameIsRunning = false;
        try {
            gameThread.join();
        }
        catch (InterruptedException e) {

        }
    }
    public void  resumeGame() {
        gameIsRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

}

