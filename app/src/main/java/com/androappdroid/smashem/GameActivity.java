package com.androappdroid.smashem;

import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout linearLayoutGameArea;
    private int cellCount=5;
    private int[] arrayCellID;
    private Button buttonStartGame;
    private TextView textViewScore,textViewMissHits,textViewVersion;
    private Handler handlerGameClock;
    private int delay = 1*1000; //1 second=1000 milisecond, 15*1000=15seconds
    private Runnable runnable;
    //private boolean isGameActive=false;
    private int gameState=0;
    private ImageView imageViewSelected;
    private int prevPos=-1,totalScore=0,totalMissHits=3,currentPosition=-1;
    private Dialog dialogGameOver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        linearLayoutGameArea=findViewById(R.id.ll_ga_game);
        buttonStartGame=findViewById(R.id.btn_start_game);
        textViewScore=findViewById(R.id.tv_score_game);
        textViewMissHits=findViewById(R.id.tv_mh_game);
        textViewVersion=findViewById(R.id.tv_version_game);

        init();
        setVersion();

        buttonStartGame.setOnClickListener(this);
    }

    private void setVersion() {
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            int verCode = pInfo.versionCode;
            textViewVersion.setText("Version "+version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void init() {

        dialogGameOver=DialogManager.getGameOverDialog(this);
        Button buttonGameOver=dialogGameOver.findViewById(R.id.btn_go_cdgo);
        buttonGameOver.setOnClickListener(this);

        linearLayoutGameArea.post(new Runnable() {
            @Override
            public void run() {
                linearLayoutGameArea.setMinimumHeight(linearLayoutGameArea.getMeasuredWidth());
            }
        });

        arrayCellID=new int[cellCount*cellCount];
        //timerGameClock=new Timer();
        int counter=-1;
        for(int i = 0; i < cellCount; i++){

            LinearLayout linearLayoutHorizontal=new LinearLayout(this);
            LinearLayout.LayoutParams layoutParamsHorizontal=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParamsHorizontal.weight=1.0f;
            linearLayoutHorizontal.setLayoutParams(layoutParamsHorizontal);

            for(int j=0;j<cellCount;j++)
            {
                counter=counter+1;
                final ImageView imageView=new ImageView(this);
//                if(i==1)
//                    imageView.setImageResource(R.drawable.gp_selected);
//                else
                    imageView.setImageResource(R.drawable.gp_unselected);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                //imageView.setPadding(25,25,25,25);
                LinearLayout.LayoutParams layoutParamsImageView=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1.0f);
                layoutParamsImageView.setMargins(12,0,12,25);
                imageView.setLayoutParams(layoutParamsImageView);
                imageView.setId(counter);
                arrayCellID[counter]=counter;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(gameState==1)
                        {
                            if(view.getId()==currentPosition)
                            {
                                totalScore=totalScore+1;
                                textViewScore.setText(""+totalScore);
                                imageView.setImageResource(R.drawable.gp_clicked);
                                nextLevel(totalScore);
                            }
                            else
                            {
                                deductFromMissHits();
                            }
                        }
                    }
                });
                //imageView.setEnabled(false);
                linearLayoutHorizontal.addView(imageView);

            }

            linearLayoutGameArea.addView(linearLayoutHorizontal);
        }

    }

    private void deductFromMissHits() {
        if(totalMissHits>1)
        {
            totalMissHits=totalMissHits-1;
            textViewMissHits.setText(""+totalMissHits);
        }
        else
        {
            totalMissHits=totalMissHits-1;
            textViewMissHits.setText(""+totalMissHits);
            stopGame();
        }
    }

    private void nextLevel(int score) {
        switch (score)
        {
            case 15 : delay=750;
            break;

            case 30 : delay=500;
            break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_start_game :
                if(gameState==0)
                    startGame();
                else if(gameState==1)
                    stopGame();
                else
                    resetGame();
                break;

            case R.id.btn_go_cdgo :
                dismissGameOver();
        }
    }

    private void dismissGameOver() {
        dialogGameOver.dismiss();
    }

    private void resetGame() {
        imageViewSelected.setImageResource(R.drawable.gp_unselected);
        totalScore=0;
        totalMissHits=3;
        textViewScore.setText(""+totalScore);
        textViewMissHits.setText(""+totalMissHits);
        buttonStartGame.setText("Start");
        gameState=0;
    }

    private void stopGame() {
        handlerGameClock.removeCallbacks(runnable);
        buttonStartGame.setText("Reset");
        gameState=2;
        showGameOver();
    }

    private void showGameOver() {
        dialogGameOver.show();
    }

    private void startGame() {

        handlerGameClock = new Handler();

        totalScore=0;
        currentPosition=-1;
        totalMissHits=3;
        delay=1000;
        gameState=1;
        textViewMissHits.setText(""+totalMissHits);
        buttonStartGame.setText("Stop");
        final Random random=new Random();

        handlerGameClock.postDelayed( runnable = new Runnable() {
            public void run() {

                if(prevPos!=-1)
                {
                    resetImageState(imageViewSelected);
                }
                int pos=random.nextInt(cellCount*cellCount);
                currentPosition=pos;
                Log.e("Random number",pos+"");
                imageViewSelected=findViewById(arrayCellID[pos]);
                imageViewSelected.setImageResource(R.drawable.gp_selected);
                //imageViewSelected.setEnabled(true);
                prevPos=pos;
                handlerGameClock.postDelayed(runnable, delay);

            }
        }, delay);

    }

    private void resetImageState(ImageView imageView) {
        imageView.setImageResource(R.drawable.gp_unselected);
        //imageView.setEnabled(false);
    }
}
