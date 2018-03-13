package com.aathis.timestable;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String TABLE_NUMBER = "com.aathis.timestable.tableNumber";
    public static final String UPTO_NUMBER = "com.aathis.timestable.uptoNumber";
    public static final String PREV_REPORT = "com.aathis.timestable.previousReport";
    public static final String PLAYER_NAME = "com.aathis.timestable.playerName";
    public static final String PLAYER_GAME_HISTORY_LIST = "com.aathis.timestable.playerGameHistory";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM HH:mm");
    public static final TableRow.LayoutParams tableRowLayoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);

    DBHelper dbHelper = null;
    EditText playerNameTxt, tableNumberTxt, uptoNumberTxt = null;
    Button historyButton = null;
    TextView PreviousReportV = null;
    TableLayout table = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Main_onCreate:", "entering Main Activity...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        Intent intent = getIntent();

        if (intent != null) {
            String prevReport = intent.getStringExtra(PREV_REPORT);
            if (prevReport != null) {
                //PreviousReportV.setText(prevReport);
                PreviousReportV.setText("Wow! " + prevReport + "...Your Achievements!\n");
                //PreviousReportV.setBackgroundResource(R.mipmap.logo_1);
                PreviousReportV.setBackgroundColor(Color.BLACK);
                PreviousReportV.setTextColor(Color.GREEN);
                intent.putExtra(PREV_REPORT, "");

                ArrayList<Player> players = intent.getParcelableArrayListExtra(PLAYER_GAME_HISTORY_LIST);
                showAchievements(players);
            }
        }
    }

    private void showAchievements(ArrayList<Player> players) {

        if (players.size() > 0) {
            showHeader(table);
            playerNameTxt.setText(players.get(0).getName());
        }

        for (int i = 0; i < players.size(); i++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            row.setLayoutParams(lp);

            Player player = players.get(i);

            addTextView(row, DATE_FORMAT.format(player.getDatesPlayed()));
            addTextView(row, "" + player.getTable());
            addTextView(row, player.getTimeTakenInMillis() + " Secs");
            addTextView(row, player.getCompletionScale() + "%");

            if (i % 2 != 0) {
                row.setBackgroundColor(Color.DKGRAY);
            }
            table.addView(row);
        }
    }

    private void showHeader(TableLayout table) {
        TableRow headerRow = new TableRow(this);
        addHeaderTextView(headerRow, "Date Played");
        addHeaderTextView(headerRow, "Table");
        addHeaderTextView(headerRow, "Time Taken");
        addHeaderTextView(headerRow, "Score");
        headerRow.setBackgroundColor(Color.DKGRAY);
        table.addView(headerRow);
    }

    private void addTextView(TableRow row, String text) {
        TextView textView = new TextView(this);
        textView.setTextSize(15);
        textView.setText("" + text);
        textView.setTextColor(Color.GREEN);
        textView.setLayoutParams(tableRowLayoutParams);
        textView.setGravity(Gravity.CENTER);
        if (text.contains("%")) {

            if (text.contains("100%")) {
                textView.setBackgroundColor(Color.GREEN);
                textView.setTextColor(Color.BLACK);
            } else {
                textView.setBackgroundColor(Color.RED);
                textView.setTextColor(Color.BLACK);
            }
        }
        row.addView(textView);
    }

    private void addHeaderTextView(TableRow row, String text) {
        TextView textView = new TextView(this);
        textView.setTextSize(15);
        textView.setText("" + text);
        textView.setTextColor(Color.CYAN);
        textView.setLayoutParams(tableRowLayoutParams);
        textView.setGravity(Gravity.CENTER);

        row.addView(textView);
    }

    public void showHistory(View view) {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        table.removeAllViews();

        String playerName = playerNameTxt.getText().toString();
        ArrayList<Player> players = null;
        if (playerName != null && playerName.length() > 0) {
            players = dbHelper.getPlayerByName(playerName);
        }
        if (players != null && players.size() > 0) {
            PreviousReportV.setText("Wow! " + playerName + "...Your Achievements!\n");
            PreviousReportV.setTextColor(Color.GREEN);
            showAchievements(players);
        } else {
            PreviousReportV.setText("Sorry! No play history available.\n");
            PreviousReportV.setTextColor(Color.RED);

        }
    }



    public void startPlaying(View view) {

        if (tableNumberTxt.getText() != null && uptoNumberTxt.getText() != null) {
            String tableNumber = tableNumberTxt.getText().toString();
            String uptoNumber = uptoNumberTxt.getText().toString();
            String playerName = playerNameTxt.getText().toString();
            if (playerName.equals("")) {
                playerName = "Player";
            }

            if (tableNumber != null && uptoNumber != null && !tableNumber.equals("") && !uptoNumber.equals("")) {
                Intent intent = new Intent(this, PlayTableActivity.class);
                intent.putExtra(TABLE_NUMBER, tableNumber);
                intent.putExtra(UPTO_NUMBER, uptoNumber);
                intent.putExtra(PLAYER_NAME, playerName);
                this.finish();
                startActivity(intent);
            }
        }
    }

    private void init() {
        playerNameTxt = findViewById(R.id.playerName);
        tableNumberTxt = findViewById(R.id.tableNumber);
        uptoNumberTxt = findViewById(R.id.uptoNumber);
        historyButton = findViewById(R.id.historytButton);
        table = findViewById(R.id.playHistory);
        PreviousReportV = findViewById(R.id.mainTxtMsg);
        playerNameTxt.addTextChangedListener(new EditPlayerNameListener());

        dbHelper = new DBHelper(this);
        playerNameTxt.requestFocus();
    }

    private class EditPlayerNameListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (PreviousReportV.getText().equals("Sorry! No play history available.\n")) {
                PreviousReportV.setText("");
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}
