package co.edu.unal.nlarranagac.reto3;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import co.edu.unal.nlarranagac.reto3.logic.TicTacToeGame;

public class AndroidTicTacToeActivity extends AppCompatActivity {

    private static TicTacToeGame mGame;
    private TextView mInfoTextView;
    private boolean mGameOver;
    private TextView mPlayerWinsCounterTextView;
    private TextView mComputerWinsCounterTextView;
    private TextView mTiesCounterTextView;
    private int mPlayerWinsCounter;
    private int mComputerWinsCounter;
    private int mTiesCounter;

    private boolean playerGoesFirst;

    private BoardView mBoardView;

    // Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {

            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if (!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos))	{

                int status = mGame.checkForWinner();
                if(status == TicTacToeGame.NO_WINNER_OR_TIE_YET){
                    mInfoTextView.setTextColor(Color.rgb(200,0,0));
                    mInfoTextView.setText(R.string.android_turn);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    status = mGame.checkForWinner();
                }

                if(status == TicTacToeGame.NO_WINNER_OR_TIE_YET){
                    mInfoTextView.setTextColor(Color.rgb(0,200,0));
                    mInfoTextView.setText(R.string.player_turn);
                }
                else if(status == TicTacToeGame.TIE){

                    mInfoTextView.setTextColor(Color.rgb(255,255,255));
                    mInfoTextView.setBackgroundColor(Color.rgb(0,0,200));
                    mInfoTextView.setText(R.string.tie);
                    mGameOver = true;
                    mTiesCounter++;
                    mTiesCounterTextView.setText(Integer.toString(mTiesCounter));
                }
                else if(status == TicTacToeGame.PLAYER_WON){
                    mInfoTextView.setTextColor(Color.rgb(255,255,255));
                    mInfoTextView.setBackgroundColor(Color.rgb(0,200,0));
                    mInfoTextView.setText(R.string.player_won);
                    mGameOver = true;
                    mPlayerWinsCounter++;
                    mPlayerWinsCounterTextView.setText(Integer.toString(mPlayerWinsCounter));
                }
                else if(status == TicTacToeGame.COMPUTER_WON){
                    mInfoTextView.setTextColor(Color.rgb(255,255,255));
                    mInfoTextView.setBackgroundColor(Color.rgb(200,0,0));
                    mInfoTextView.setText(R.string.android_won);
                    mGameOver = true;
                    mComputerWinsCounter++;
                    mComputerWinsCounterTextView.setText(Integer.toString(mComputerWinsCounter));
                }
            }
            return false;
        }
    };

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;

    private void startNewGame(){
        mGame.clearBoard();
        mBoardView.invalidate();
        mInfoTextView.setBackgroundColor(Color.rgb(255,255,255));


        if(playerGoesFirst){
            playerGoesFirst = false;
            mInfoTextView.setTextColor(Color.rgb(0, 200, 0));
            mInfoTextView.setText(R.string.player_goes_first);
        }
        else{
            playerGoesFirst = true;
            mInfoTextView.setTextColor(Color.rgb(200, 0, 0));
            mInfoTextView.setText(R.string.android_goes_first);
            int move = mGame.getComputerMove();
            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
            mInfoTextView.setTextColor(Color.rgb(0, 200, 0));
            mInfoTextView.setText(R.string.player_turn);
        }
        mGameOver = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_tic_tac_toe);

        mPlayerWinsCounter = mComputerWinsCounter = mTiesCounter = 0;

        mBoardView = (BoardView) findViewById(R.id.board);

        mGame = new TicTacToeGame();
        mBoardView.setGame(mGame);
        mBoardView.setOnTouchListener(mTouchListener);

        mInfoTextView = (TextView) findViewById(R.id.tv_info);
        mPlayerWinsCounterTextView = (TextView) findViewById(R.id.tv_player_counter);
        mComputerWinsCounterTextView = (TextView) findViewById(R.id.tv_computer_counter);
        mTiesCounterTextView = (TextView) findViewById(R.id.tv_tie_counter);

        playerGoesFirst= true;
        startNewGame();
    }

    private boolean setMove(char player, int location){
        if(mGame.setMove(player, location)){
            mBoardView.invalidate();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    public void showDifficultyDialog(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogFragment difficultyFragment = new DifficultyListDialogFragment();
        difficultyFragment.show(fragmentManager, "difficulty");
    }

    public void showQuitDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.quit_alert)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AndroidTicTacToeActivity.this.finish();
                    }
                })
                .setNegativeButton(R.string.no, null);
        builder.create().show();
    }

    public void showAboutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Context context = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.about_dialog, null);
        builder.setView(layout);
        builder.setPositiveButton(R.string.ok, null);
        builder.create().show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.item_new_game:
                startNewGame();
                return true;
            case R.id.item_ai_difficulty:
                showDifficultyDialog();
                return true;
            case R.id.item_quit:
                showQuitDialog();
                return true;
            case R.id.item_about:
                showAboutDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class DifficultyListDialogFragment extends DialogFragment {

        private static final int LEVEL_EASY = 0;
        private static final int LEVEL_HARDER = 1;
        private static final int LEVEL_EXPERT = 2;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.chose_difficulty)
                    .setSingleChoiceItems(R.array.difficulties, LEVEL_EASY, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String message = "";
                            if(i == LEVEL_EASY){
                                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
                                message = getResources().getString(R.string.Easy);
                            }
                            else if(i == LEVEL_HARDER){
                                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
                                message = getResources().getString(R.string.Harder);
                            }
                            else if(i == LEVEL_EXPERT){
                                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
                                message = getResources().getString(R.string.Expert);
                            }
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        }
                    });
            return builder.create();
        }
    }
}