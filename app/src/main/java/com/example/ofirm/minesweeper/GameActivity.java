package com.example.ofirm.minesweeper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends Activity implements SensorEventListener{

    private GridView gridView;
    private boolean GameStart;
    private boolean gameOver;
    private CellAdapter adapter;

    private ArrayList<Cell> cells = new ArrayList<>();
    private ArrayList<Cell> openCells = new ArrayList<>();
    private ArrayList<Cell> mineds = new ArrayList<>();
    private ArrayList<Cell> notMineds = new ArrayList<>();
    private ArrayList<Cell> flags = new ArrayList<>();

    private Button SmileButton;

    private int row;
    private int col;
    private int level;
    private int ScoreLevel;
    private int numberOfMinesInGame;
    private int numberOfOpenCell;
    private int scoreTime;

    private Sensor senAccelerometer;
    private SensorManager senSensorManager;

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 100;
    private TextView textX, textY, textZ;

    private  TextView alert;
    private TextView flagesView;
    private DataBase db  = new DataBase(this);
    private ArrayList<Score> scores = new ArrayList<>();

    private long startTime = 0L;
    private TextView TimerView;
    private Handler customHandler = new Handler();
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity_game);


        /////////////////CODE FROM HERE!!!!////////////

        ///////location
        //mlocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        gridView = (GridView)findViewById(R.id.GridView);

        // choose the level
        chooseLevel();
        Log.d("level = ",String.valueOf(level));
        // start the smile button and the listener
        onButtonClickListener();

        gridView.setNumColumns(col);
        TimerView = (TextView)findViewById(R.id.timerValue);
        flagesView = (TextView)findViewById(R.id.flagCounter);
        flagesView.setText(String.format("%d",numberOfMinesInGame));//flagesView.setText(""+numberOfMinesInGame);

        for (int i=0; i < col*row; i++){
            Cell c = new Cell(this);
            c.setPosition(i);
            cells.add(c);
        }

        adapter = new CellAdapter(this, cells);
        gridView.setAdapter(adapter);

        // enable gridView to scroll up and down
        gridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }
        });

        // all the gridView element is now ClickListener
        gridView.setOnItemClickListener(itemClickedListener);
        gridView.setOnItemLongClickListener(longItemClickedListener);

        textX = (TextView)findViewById(R.id.Xcoord);
        textY = (TextView)findViewById(R.id.Ycoord);
        textZ = (TextView)findViewById(R.id.Zcoord);
        alert = (TextView)findViewById(R.id.moveAlert);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);


    }//end onCreate

    private void setRow(int row) {
        this.row = row;
    }

    private void setCol(int col) {
        this.col = col;
    }

    private void onButtonClickListener(){
        SmileButton = (Button)findViewById(R.id.smile);
        SmileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isGameStart()){
                    exitPopUpMessage();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("back!!!!!", "-------");
        if(isGameStart()){
            exitPopUpMessage();
            return true;
        }
        else {
            return super.onKeyDown(keyCode, event);
        }
    }



    AdapterView.OnItemLongClickListener longItemClickedListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if(!isGameStart()) {
                startTime();
                setRandomMines(position, level);
                putNumbers();
            }

            Cell cell = cells.get(position);

            Log.d("pos is: ", ""+position);
            Log.d("get pos is: ", ""+cell.getPosition());

            if (cell.getClickable()){

                    if (cell.getStatus() != Cell.FLAGGED && flags.size()<numberOfMinesInGame ) {
                        cell.setStatus(Cell.FLAGGED);
                        cell.setClickable(false);
                        flags.add(cell);
                        vibration(300);
                    }
                    else if(cell.getStatus() == Cell.FLAGGED) {
                        cell.setStatus(Cell.COVERED);
                        flags.remove(cell);
                        vibration(300);
                    }
                }

                if( numberOfMinesInGame - flags.size() == 0) {
                    boolean allTrue=true;
                    for (int i = 0; i < flags.size(); i++) {
                        if(!flags.get(i).getIsMined()) {
                            allTrue = false;
                        }
                    }
                    if(allTrue){
                        winGame();
                    }
                }

            String s1 = String.valueOf(numberOfMinesInGame-flags.size());
            flagesView.setText(s1);

            cell.setClickable(true);


            adapter.notifyDataSetChanged();

            return true;
        }
    };



    public void vibration(int timeInMilliseconds){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(timeInMilliseconds);
    }



    ////////////--------TO SEE IF THIS IS IN THE RIGHT PLACE--------////////////
    AdapterView.OnItemClickListener itemClickedListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Cell cell = cells.get(position);
            if (cell.getStatus() != Cell.FLAGGED) {

                cell.setClickable(false);
                Log.d("prss on: ", " " + position);

                if (!isGameStart()) {
                    startTime();
                    setRandomMines(position, level);
                    putNumbers();
                }

                if (cell.getIsMined()) {
                    setEndGame(position);
                }

                if (cell.getNumberOfMines() > 0) {
                    cell.setStatus(Cell.NUMBER);
                    numberOfOpenCell++;
                    openCells.add(cell);
                }

                if (cell.getNumberOfMines() == 0) {
                    openRec(cell);
                }

                //String s1 = String.valueOf(numberOfMinesInGame-flags.size());
                String s1 = String.format("%d",numberOfMinesInGame-flags.size());
                flagesView.setText(s1);
                //flagesView.setText(""+(numberOfMinesInGame-flags.size()));
                adapter.notifyDataSetChanged();
                Log.d("numberOfOpenCell: ", " " + numberOfOpenCell);

                if(row*col-numberOfMinesInGame == numberOfOpenCell)
                    winGame();
            }
        }


    };
    Animation.AnimationListener failedAnimation = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            failedPopUpMessage();
            gridView.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };


    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            long updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            scoreTime = secs;
            int minutes = secs / 60;
            secs = secs % 60;
            String time = String.valueOf(minutes)+":"+String.format("%02d",secs);

            TimerView.setText(time);
            customHandler.postDelayed(this, 0);
        }

    };

    private void chooseLevel(){
        Bundle b = getIntent().getExtras();
        level = b.getInt("level");
        switch (level){
            case R.string.Easy:
                setRow(getResources().getInteger(R.integer.easy_level_row));
                setCol(getResources().getInteger(R.integer.easy_level_col));
                setNumberOfMinesInGame(getResources().getInteger(R.integer.easy_level_bomb));
                ScoreLevel = 1;
                break;
            case R.string.Medium:
                setRow(getResources().getInteger(R.integer.medium_level_row));
                setCol(getResources().getInteger(R.integer.medium_level_col));
                setNumberOfMinesInGame(getResources().getInteger(R.integer.medium_level_bomb));
                ScoreLevel = 2;
                break;
            case R.string.Hard:
                setRow(getResources().getInteger(R.integer.hard_level_row));
                setCol(getResources().getInteger(R.integer.hard_level_col));
                setNumberOfMinesInGame(getResources().getInteger(R.integer.hard_level_bomb));
                ScoreLevel = 3;
                break;
        }

    }

    private void openRec(Cell cell) {

    //            //  -n-1     -n      -n+1
    //            //  -1      |__|      +1
    //            //  n-1       n       n+1


        if(cell.getStatus()== Cell.NUMBER || cell.getStatus() == Cell.EMPTY)
            return;

        int tempPosition = cell.getPosition();

        //step 1
        if(tempPosition%col != 0 && tempPosition > col - 1){
            //_|
            recHelp(cell,-col,-1);
        }

        //step 2
        if(tempPosition > col-1){
            //|_|
            recHelp(cell,-col,0);
        }

        //step 3
        if(tempPosition%col != col - 1 && tempPosition > col - 1){
            //|_
            recHelp(cell,-col,1);
        }

        //step 4
        if(tempPosition%col != col - 1){
            //|-_
            recHelp(cell,0,1);
        }

        //step 5
        if(tempPosition%col != col - 1 && tempPosition < row * col - col) {
            //|-
            recHelp(cell,col,1);
        }

        //step 6
        if(tempPosition < row * col - col){
            //|-|
            recHelp(cell,col,0);
        }

        //step 7
        if(tempPosition%col != 0 && tempPosition < row * col - col) {
            //-|
            recHelp(cell,col,-1);
        }

        //step 8
        if(tempPosition%col != 0) {
            //-_|
            recHelp(cell,0,-1);
        }


        if(cell.getStatus()!=Cell.EMPTY) {
            if(cell.getStatus() == Cell.FLAGGED){
                flags.remove(cell);
            }
            cell.setStatus(Cell.EMPTY);
            numberOfOpenCell++;
            openCells.add(cell);
        }

    }

    private void recHelp(Cell cell, int n1, int n2){

        Cell c = cells.get(cell.getPosition() + n1 + n2);

        if (c.getStatus() != Cell.NUMBER){
            if(c.getNumberOfMines() > 0){
                if (c.getStatus() == Cell.FLAGGED){
                    flags.remove(c);
                }
                c.setStatus(Cell.NUMBER);
                numberOfOpenCell++;
                openCells.add(c);
            }else if (c.getStatus() != Cell.EMPTY) {
                if(cell.getStatus() != Cell.EMPTY) {
                    if(cell.getStatus() == Cell.FLAGGED){
                        flags.remove(cell);
                    }
                    cell.setStatus(Cell.EMPTY);
                    numberOfOpenCell++;
                    openCells.add(cell);
                }
                openRec(c);
            }
        }

    }

    private void putNumbers() {
        for(int i=0 ; i< mineds.size() ; i++){
            int pos = mineds.get(i).getPosition();


            if(pos < row*col-col){
                //|-|
                putNumbersHelp(pos,col,0);
            }

            if(pos > col-1){
                //|_|
                putNumbersHelp(pos,-col,0);
            }

            if(pos%col != col-1){
                //|-_
                putNumbersHelp(pos,0,1);
            }

            if(pos%col != 0){
                //-_|
                putNumbersHelp(pos,0,-1);
            }

            if(pos%col != 0 && pos < row*col-col){
                //-|
                putNumbersHelp(pos,col,-1);
            }

            if (pos%col != col-1 && pos > col-1){
                //|_
                putNumbersHelp(pos,-col,1);
            }

            if(pos%col != 0 && pos > col-1){
                //_|
                putNumbersHelp(pos,-col,-1);
            }

            if(pos%col != col-1 && pos < row*col-col){
                //|-
                putNumbersHelp(pos,col,1);
            }

        }

    }

    private void putNumbersHelp(int pos, int n1, int n2){
        if(!cells.get(pos+n1+n2).getIsMined())
            cells.get(pos+n1+n2).increaseNumberOfMines();
    }

    private void setEndGame(int position) {
//// TODO: 04/01/2016 to remove coment
        startFailedAnimation();
        stopTimer();
        senSensorManager.unregisterListener(this);
        db.close();
        vibration(1000);
        gameOver = true;
        gridView.setEnabled(false);
        SmileButton.setBackgroundResource(R.drawable.poop);
        revealAllMines(position);

    }

    private void stopTimer() {
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);
    }

    private void startTime(){
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
    }

    private void winGame(){
        //// TODO: 27/12/2015 put anim in func

        //randomTestWin();
        //// TODO: 02/01/2016 to bring back after test!
        stopTimer();
        senSensorManager.unregisterListener(this);
        db.close();
        vibration(1000);
        gameOver = true;
        gridView.setEnabled(false);
        SmileButton.setBackgroundResource(R.drawable.sunglasses);
        revealAllFlags();
        revealAll();
        StartWinAnimation();


    }
    public void StartWinAnimation(){
        Animation winAnim =  AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_win_animation);
        ImageView winImage = (ImageView)findViewById(R.id.winImage);
        winImage.setAnimation(winAnim);
        MediaPlayer media = MediaPlayer.create(getApplicationContext(), R.raw.win);
        media.start();
        winAnim.setDuration(media.getDuration() + 4000);
        winAnim.start();
        winAnim.setAnimationListener(winAnimation);
    }
    Animation.AnimationListener winAnimation = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            scores = db.getAllScoresByLevel(ScoreLevel);
            db.close();
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (newHighScore(scores,scoreTime))
                newHighScorePopUpMessage();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };


    private void randomTestWin(){
        Random r = new Random();
        scoreTime = r.nextInt(500 - 5) + 5;

        stopTimer();
        gameOver = true;
        gridView.setEnabled(false);
        SmileButton.setBackgroundResource(R.drawable.sunglasses);
        revealAll();
        scores = db.getAllScoresByLevel(ScoreLevel);
        db.close();
        if (newHighScore(scores,scoreTime))
            newHighScorePopUpMessage();
    }

    public boolean newHighScore(ArrayList<Score> scores,int scoreTime){
            if (scores.size() == 0)
                return true;
            if (Score.MAX_SCORES > scores.size())
                return true;
            if (Score.MAX_SCORES == scores.size() && (scores.get(scores.size()-1).getTime() > scoreTime))
                return true;

        return false;
    }

    public void saveScoreToDataBase(String name){
        if (this.scores.size() == Score.MAX_SCORES)
            this.db.deleteScore(scores.get(Score.MAX_SCORES-1));
        Score score = new Score();
        score.setTime(scoreTime);
        score.setLevel(ScoreLevel);
        score.setName(name);
        Bundle b = getIntent().getExtras();
        //// TODO: 02/01/2016 to bring back after test!
        Double lat = b.getDouble("latitude");
        Double lon = b.getDouble("longitude");
        Log.d("GA lat", lat +" ");
        Log.d("GA lon", lon +" ");

//        Random r = new Random();
//        lat = 32.077075+(32.096273-32.077075)*r.nextDouble();
//        lon = 34.780312+(34.834642-34.780312)*r.nextDouble();

        score.setLatitude(lat);
        score.setLogitude(lon);
        this.db.addScore(score);

        Log.d("lat is:" + lat, " ");
        Log.d("lon is:" + lon," ");

    }

    private void revealAll(){
        for(int i=0; i<row*col;i++){
            if(cells.get(i).getClickable())
                cells.get(i).setStatus(Cell.NUMBER);
        }
    }

    private void revealAllFlags() {
        for(int i=0 ; i< numberOfMinesInGame; i++){
            mineds.get(i).setStatus(Cell.FLAGGED);
        }
    }

    private void revealAllMines(int position) {
        for(int i=0; i<flags.size(); i++){
            if(!flags.get(i).getIsMined())
                flags.get(i).setStatus(Cell.WRONG_MINED);
        }
        for(int i=0 ; i< numberOfMinesInGame; i++){
            mineds.get(i).setStatus(Cell.MINED);
        }
        cells.get(position).setStatus(Cell.RED_MINE);
    }

    private boolean isGameStart(){
        return GameStart;
    }

    private void setGameStart(){
        GameStart = true;
        gameOver = false;
    }

    private void setRandomMines(int position, int level) {
        int i;
        int numberOfMines = this.numberOfMinesInGame;
        Log.d("the position is:",""+position);
        Random rand = new Random();
        while(numberOfMines > 0){
            i = rand.nextInt(row*col);
            Cell cell = cells.get(i);
            if(!cell.getIsMined() && i!=position) {
                mineds.add(cell);
                Log.d("you have mine in", "" + i);
                numberOfMines--;
                cell.setMined();
                //game_cell.isMined = true;
                cell.decreaseNumberOfMines();
            }
        }
        for(int j=0;j<row*col;j++){
            if(!cells.get(j).getIsMined())
                notMineds.add(cells.get(j));
        }
        setGameStart();
    }

    public void setNumberOfMinesInGame(int numberOfMinesInGame) {
        this.numberOfMinesInGame = numberOfMinesInGame;
    }

    public void startFailedAnimation(){
        MediaPlayer media = MediaPlayer.create(getApplicationContext(), R.raw.failed);
        media.start();
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.failed_animation);
        for (int i=0; i < row*col;i++) {
            gridView.getChildAt(i).setAnimation(anim);

        }
        anim.setDuration(2000);
        anim.start();
        anim.setStartOffset(media.getDuration());
        anim.setAnimationListener(failedAnimation);
    }

    public void newHighScorePopUpMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        builder.setTitle(R.string.WinDialogTitle);
        final EditText inputText = new EditText(this);
        builder.setView(inputText);

        //to add max len of text
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(10);
        inputText.setFilters(FilterArray);
        //

        builder.setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                String name = inputText.getText().toString();
                saveScoreToDataBase(name);
            }
        });
        builder.setView(inputText);
        AlertDialog alertDialog = builder.create();
        //alertDialog.setTitle(R.string.ResetDialogTitle);
        alertDialog.show();


    }

    public void exitPopUpMessage(){
            stopTimer();
            AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
            builder.setMessage(R.string.ResetDialogContent).setCancelable(false).setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).setNegativeButton(R.string.cancelButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    if (!gameOver)
                        startTime();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.setTitle(R.string.ResetDialogTitle);
            alertDialog.show();
    }

    public void failedPopUpMessage(){
        stopTimer();
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        builder.setMessage(R.string.FailedDialogContent).setCancelable(false).setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setNegativeButton(R.string.cancelButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (!gameOver)
                    startTime();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle(R.string.FailedDialogTitle);
        alertDialog.show();
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                System.exit(0);
            }
        });
    }

    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if(GameStart)
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 300) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    //// TODO: 03/01/2016
                    alert.setTextColor(Color.RED);
                    alert.setVisibility(View.VISIBLE);
                    bringBackCells();
                    Log.d("move to mach","#########");
                }

                last_x = x;
                last_y = y;
                last_z = z;

                textX.setText(String.format("%f",last_x));
                textY.setText(String.format("%f",last_y));
                textZ.setText(String.format("%f",last_z));
            }

        }



    }

    private void bringBackCells() {
        Log.d("mine in game ",numberOfMinesInGame+" ");
        Log.d("mine ",mineds.size()+" ");
        Random r = new Random();
        int randomCell;
        Cell tempCell;

        if(openCells.size()!=0){
            randomCell = r.nextInt(openCells.size());
            tempCell = openCells.remove(randomCell);

            for(int i=0;i<notMineds.size();i++){
                if(notMineds.get(i)==tempCell){
                    notMineds.remove(i);
                }
            }

            setNumberOfMinesInGame(numberOfMinesInGame + 1);

            tempCell.setStatus(Cell.COVERED);
            tempCell.setClickable(true);
            tempCell.setNumberOfMines(-1);
            tempCell.setMined();

            numberOfOpenCell--;

            String s1 = String.valueOf(numberOfMinesInGame-flags.size());
            flagesView.setText(s1);
            mineds.add(tempCell);
            for(int i=0;i<notMineds.size();i++){
                notMineds.get(i).setNumberOfMines(0);
            }
            putNumbers();
            for(int i=0;i<notMineds.size();i++){
                if(notMineds.get(i).getNumberOfMines()>0 && notMineds.get(i).getStatus()==Cell.EMPTY){
                    notMineds.get(i).setStatus(Cell.NUMBER);
                }
            }

        }else{
            Log.d("No open cells"," ");
            if(notMineds.size()==0){
                setEndGame(0);//0?
            }else{
                randomCell = r.nextInt(notMineds.size());
                tempCell = notMineds.remove(randomCell);

                setNumberOfMinesInGame(numberOfMinesInGame + 1);

                tempCell.setNumberOfMines(-1);
                tempCell.setMined();
                String s1 = String.valueOf(numberOfMinesInGame-flags.size());
                flagesView.setText(s1);

                mineds.add(tempCell);

                for(int i=0;i<notMineds.size();i++){
                    notMineds.get(i).setNumberOfMines(0);
                }
                putNumbers();
            }

        }
        adapter.notifyDataSetChanged();


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
