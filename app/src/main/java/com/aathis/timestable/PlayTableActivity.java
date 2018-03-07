package com.aathis.timestable;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PlayTableActivity extends AppCompatActivity {

    int uptoNumber, tableNumber = 5555;
    EditText answerVal = null;
    int randumNumber = 5;
    Random rand = null;
    TextView titleTextTxt, timesNumberTxt, tableNoTxt, textTimer, mainTxtMsgV, resultGreetingTxt = null;
    ImageView greetingImageV = null;
    ProgressBar progressBar = null;
    int progressCount = 0;
    int time = 30;
    CountDownTimer countDownTimer = null;
    long startTime, endTime = 0;
    String playerName = "Player";
    ArrayList<Map<Integer, String>> historyList = null;
    DBHelper dbHelper = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_table);

        Intent intent = getIntent();
        tableNumber = Integer.parseInt(intent.getStringExtra(MainActivity.TABLE_NUMBER));
        uptoNumber = Integer.parseInt(intent.getStringExtra(MainActivity.UPTO_NUMBER));
        playerName = intent.getStringExtra(MainActivity.PLAYER_NAME);

        init();
        createTimer();
        nextNumber();
        startTime = System.currentTimeMillis();
    }

    private void init() {
        dbHelper =new DBHelper(this);

        rand = new Random();
        timesNumberTxt = findViewById(R.id.timesNumber);
        tableNoTxt = findViewById(R.id.tableNo);
        answerVal = findViewById(R.id.answerTxt);
        progressBar = findViewById(R.id.determinateBar);
        greetingImageV = findViewById(R.id.greetingImage);
        textTimer = (TextView) findViewById(R.id.timer);
        answerVal.addTextChangedListener(new EditTextListener());
        answerVal.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mainTxtMsgV = (TextView) findViewById(R.id.mainTxtMsg);
        resultGreetingTxt = findViewById(R.id.resultGreeting);
        answerVal = findViewById(R.id.answerTxt);
        titleTextTxt = findViewById(R.id.titleText);
        makeUpTitle();

        historyList = new ArrayList<>();
    }

    private void makeUpTitle() {
        SpannableString titleStr = new SpannableString(playerName + " is playing table : " + tableNumber);
        int start = titleStr.length() - ((tableNumber + "").length());
        int end = titleStr.length();
        titleStr.setSpan(new RelativeSizeSpan(2f), start, end, 0); // set size
        titleStr.setSpan(new ForegroundColorSpan(Color.RED), start, end, 0);// set color
        titleTextTxt.setText(titleStr);
    }

    private class EditTextListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String ansstr = answerVal.getText().toString();
            int ans = 0;
            if (ansstr != null && ansstr.length() > 0) {
                try {
                    ans = Integer.parseInt(ansstr);
                } catch (NumberFormatException e) { //Handle Exception...
                    Log.e("ERROR:", e.getMessage());
                    resultGreetingTxt.setText("");
                    greetingImageV.setImageResource(android.R.color.transparent);
                    return;
                }
            }
            int correctAnswer = (randumNumber * tableNumber);
            if (ans == (randumNumber * tableNumber)) {
                processCorrectAnswer(ans);
            } else if (ansstr != null && ansstr.length() > 0 && isWrongDigit(ansstr, Integer.toString(correctAnswer))) {
                processWrongAnswer();
            } else {
                resultGreetingTxt.setText("");
                greetingImageV.setImageResource(android.R.color.transparent);
            }
        }

        private void processWrongAnswer() {
            resultGreetingTxt.setText("Oops! wrong.Try again...");
            resultGreetingTxt.setTextColor(Color.RED);
            greetingImageV.setImageResource(R.mipmap.wrong_smiley_1);
            greetingImageV.setAdjustViewBounds(true);
        }

        private void processCorrectAnswer(int ans) {
            String displayString = "(" + randumNumber + " x " + tableNumber + " = " + ans + " )";

            if (progressCount < 100) {
                progressCount +=5;
            }else{
                sendBackResult();
                return;
            }
            nextNumber();
            progressBar.setProgress(progressCount);
            resultGreetingTxt.setText(displayString + " Awesome! Try Next...");
            resultGreetingTxt.setTextColor(Color.GREEN);
            greetingImageV.setImageResource(R.mipmap.thumb_up_emoticon1);
            greetingImageV.setAdjustViewBounds(true);
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    private boolean isWrongDigit(String inputAnswerString, String correctAnswer) {
        for (int i = 0; i < inputAnswerString.length(); i++) {
            if (inputAnswerString.charAt(i) != correctAnswer.charAt(i)) {
                return true;
            }
        }
        return false;
    }

    private void nextNumber() {
        randumNumber = rand.nextInt((uptoNumber + 1));
        if (randumNumber == 0) {
            nextNumber();
        }
        timesNumberTxt.setText(Integer.toString(randumNumber));
        tableNoTxt.setText(Integer.toString(tableNumber));
        answerVal.setText("");

        countDownTimer.start();
        textTimer.setTextColor(Color.GRAY);
        progressBar.animate();


    }

    private void sendBackResult() {
        if (progressBar.getProgress() == 100) {
            String timeTook = finishTable();
            ArrayList<Player> players = dbHelper.getPlayerByName(playerName);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.PREV_REPORT, playerName);
            //intent.putExtra(MainActivity.PREV_REPORT, "Well Done! "+playerName+ "\nYou took " + timeTook +
            //        " for table " + tableNumber + ".\nChoose next table.");
            intent.putParcelableArrayListExtra(MainActivity.PLAYER_GAME_HISTORY_LIST, players);
            countDownTimer.cancel();
            this.finish();
            this.finishActivity(0);
            startActivity(intent);
        }
    }

    private void createTimer() {
        countDownTimer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                long remainingTime = millisUntilFinished / 1000;
                if (remainingTime <= 5) {
                    textTimer.setTextColor(Color.RED);
                }
                textTimer.setText("" + millisUntilFinished / 1000);
            }
            public void onFinish() {
                textTimer.setText("time out!");
                nextNumber();
            }
        };
    }

    public void finishPlaying(View view) {
        countDownTimer.cancel();
        this.finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    public void goToNextLevel(View view) {

        finishTable();
        this.tableNumber += 1;
        this.progressCount = 0;
        progressBar.setProgress(this.progressCount);
        makeUpTitle();
        nextNumber();
        startTime = System.currentTimeMillis();

    }

    private String finishTable() {
        endTime = System.currentTimeMillis();
        long totalTimeTakenInSeconds = (endTime - startTime) / 1000;
        String timeTaken = "";
        if (totalTimeTakenInSeconds > 59) {
            long mins = totalTimeTakenInSeconds / 60;
            long secs = totalTimeTakenInSeconds % 60;
            timeTaken = mins + " minutes & " + secs + " seconds";
        } else {
            timeTaken = totalTimeTakenInSeconds + " seconds";
        }
        HashMap tableMap = new HashMap<Integer, String>();
        String retVal = progressBar.getProgress()+"% in ";

        tableMap.put(tableNumber, timeTaken);
        historyList.add(tableMap);

        Player player = new Player();
        player.setName(playerName);

        player.setDatesPlayed(new Date());
        player.setTimeTakenInMillis(totalTimeTakenInSeconds);
        player.setTable(tableNumber);
        player.setCompletionScale(progressBar.getProgress());
        dbHelper.addPlayer(player);
        return timeTaken;
    }

    public void skipOnce(View view) {
        nextNumber();
    }

}
