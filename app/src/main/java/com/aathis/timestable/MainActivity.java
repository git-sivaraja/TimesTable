package com.aathis.timestable;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String TABLE_NUMBER ="com.aathis.timestable.tableNumber" ;
    public static final String UPTO_NUMBER ="com.aathis.timestable.uptoNumber" ;
    public static final String PREV_REPORT ="com.aathis.timestable.previousReport" ;
    public static final String PLAYER_NAME ="com.aathis.timestable.playerName" ;
    public static final String PLAYER_GAME_HISTORY_LIST ="com.aathis.timestable.playerGameHistory" ;
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy:HH:mm");
    public static final TableRow.LayoutParams tableRowLayoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Main_onCreate:", "entering Main Activity...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView PreviousReportV = findViewById(R.id.mainTxtMsg);
        EditText playerNameTxt = (EditText) findViewById(R.id.playerName);
        playerNameTxt.requestFocus();

        Intent intent = getIntent();

        if (intent!=null ){
            String prevReport = intent.getStringExtra(PREV_REPORT);
            if (prevReport !=null){
               //PreviousReportV.setText(prevReport);
                PreviousReportV.setText("Wow! "+prevReport+"...Your Achievements!\n");
                //PreviousReportV.setBackgroundResource(R.mipmap.logo_1);
                PreviousReportV.setBackgroundColor(Color.BLACK);
                PreviousReportV.setTextColor(Color.GREEN);
                intent.putExtra(PREV_REPORT, "");

                ArrayList<Player> players = intent.getParcelableArrayListExtra(PLAYER_GAME_HISTORY_LIST);
                TableLayout table = (TableLayout) findViewById(R.id.playHistory);

                if (players.size() >0) {
                    showHeader(table);
                }

                for(int i=0;i<players.size();i++)
                {
                    TableRow row=new TableRow(this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                    row.setLayoutParams(lp);

                    Player player =  players.get(i);
                    playerNameTxt.setText(player.getName());

                    addTextView(row, DATE_FORMAT.format(player.getDatesPlayed()));
                    addTextView(row, ""+player.getTable());
                    addTextView(row, player.getTimeTakenInMillis()+" Secs");

                    if(i % 2 != 0) {
                        row.setBackgroundColor(Color.DKGRAY);
                    }
                    table.addView(row);
                }
            }
        }
    }

    private void showHeader(TableLayout table) {
        TableRow headerRow = new TableRow(this);
        addHeaderTextView(headerRow, "Date Played");
        addHeaderTextView(headerRow, "Table Number");
        addHeaderTextView(headerRow, "Time Taken");
        headerRow.setBackgroundColor(Color.DKGRAY);
        table.addView(headerRow);
    }

    private void addTextView( TableRow row, String text) {
        TextView textView = new  TextView(this);
        textView.setTextSize(15);
        textView.setText(""+text);
        textView.setTextColor(Color.GREEN);
        textView.setLayoutParams(tableRowLayoutParams);
        textView.setGravity(Gravity.CENTER);
        row.addView(textView);
    }

    private void addHeaderTextView( TableRow row, String text) {
        TextView textView = new  TextView(this);
        textView.setTextSize(15);
        textView.setText(""+text);
        textView.setTextColor(Color.CYAN);
        textView.setLayoutParams(tableRowLayoutParams);
        textView.setGravity(Gravity.CENTER);

        row.addView(textView);
    }

    /** Called when the user taps the Start button */
    public void startPlaying(View view) {

        EditText playerNameTxt = (EditText) findViewById(R.id.playerName);
        EditText tableNumberTxt = (EditText) findViewById(R.id.tableNumber);
        EditText uptoNumberTxt = (EditText) findViewById(R.id.uptoNumber);

        if (tableNumberTxt.getText() !=null && uptoNumberTxt.getText() !=null) {
            String tableNumber = tableNumberTxt.getText().toString();
            String uptoNumber = uptoNumberTxt.getText().toString();
            String playerName = playerNameTxt.getText().toString();
            if (playerName.equals("")){
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
}
