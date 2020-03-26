package com.tutaf.whichpersonisreal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.github.ybq.android.spinkit.style.Circle;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.github.ybq.android.spinkit.style.Pulse;
import com.github.ybq.android.spinkit.style.RotatingCircle;
import com.github.ybq.android.spinkit.style.RotatingPlane;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.github.ybq.android.spinkit.style.Wave;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.WrongMethodTypeException;
import java.util.Random;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import mehdi.sakout.aboutpage.AboutPage;

public class MainActivity extends AppCompatActivity {
    int mRealImage;
    ImageView imageViewReal;
    ImageView imageViewFake;
    int mRightAnswersCount = 0;
    int mWrongAnswersCount = 0;
    TextView mSessionRight;
    TextView mSessionWrong;
    TextView mTotalRight;
    TextView mTotalWrong;
    TextView mTotalGuessRate;
    TextView mSessionGuessRate;
    SpinKitView mCover1;
    SpinKitView mCover2;
    SharedPreferences mPreferences;
    String APP_PREFERENCES = "DEFAULT_SHARED_PREFS";
    String PREFERENCES_RIGHT = "RIGHT";
    String PREFERENCES_WRONG = "WRONG";
    boolean mFakeLoaded = false;
    boolean mRealLoaded = false;
    boolean mAnswered = false;
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSessionRight = findViewById(R.id.textViewRightSession);
        mSessionWrong = findViewById(R.id.textViewWrongSession);
        mTotalRight = findViewById(R.id.textViewRightTotal);
        mTotalWrong = findViewById(R.id.textViewWrongTotal);
        mTotalGuessRate = findViewById(R.id.textViewGuessingPercentageTotal);
        mSessionGuessRate = findViewById(R.id.textViewGuessingPercentageSession);
        mCover1 = findViewById(R.id.spinkitCover1);
        mCover2 = findViewById(R.id.spinkitCover2);
        mPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        setAnswersCount();
        loadImages();

        AppRate.with(this)
                .setInstallDays(0) // default 10, 0 means install day.
                .setLaunchTimes(3) // default 10
                .setRemindInterval(1) // default 1
                .setShowLaterButton(true) // default true
                .setDebug(false) // default false
                /*.setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        //https://play.google.com/store/apps/details?id=com.tutaf.whichpersonisreal
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                })*/
                .monitor();

        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);
    }

    public int randomNumber(int min, int max) {
        int diff = max - min;
        Random random = new Random();
        int i = random.nextInt(diff + 1);
        i += min;
        return i;
    }

    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.next:
                loadImages();
                imageViewReal.setBackgroundColor(getResources().getColor(R.color.white));
                imageViewFake.setBackgroundColor(getResources().getColor(R.color.white));
                break;
            case R.id.imageView:
                if (!mAnswered) {
                    if (imageViewReal.getId() == R.id.imageView) {
                        Toast.makeText(this, getString(R.string.right), Toast.LENGTH_SHORT).show();
                        addRight();
                    } else {
                        Toast.makeText(this, getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                        addWrong();
                    }
                    imageViewReal.setBackgroundColor(getResources().getColor(R.color.rightAnswer));
                    imageViewFake.setBackgroundColor(getResources().getColor(R.color.wrongAnswer));
                    mAnswered = true;
                }
                break;
            case R.id.imageView2:
                if (!mAnswered) {
                    if (imageViewReal.getId() == R.id.imageView2) {
                        Toast.makeText(this, getString(R.string.right), Toast.LENGTH_SHORT).show();
                        addRight();

                    } else {
                        Toast.makeText(this, getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                        addWrong();
                    }
                    imageViewReal.setBackgroundColor(getResources().getColor(R.color.rightAnswer));
                    imageViewFake.setBackgroundColor(getResources().getColor(R.color.wrongAnswer));
                    mAnswered = true;
                }
                break;
        }

    }

    public void loadImages () {
        mAnswered = false;
        if (randomNumber(1, 2) == 1) {
            mRealImage = 1;
            imageViewReal = findViewById(R.id.imageView);
            imageViewFake = findViewById(R.id.imageView2);
        } else {
            mRealImage = 2;
            imageViewReal = findViewById(R.id.imageView2);
            imageViewFake = findViewById(R.id.imageView);
        }
        imageViewReal.setBackgroundColor(getResources().getColor(R.color.white));
        imageViewFake.setBackgroundColor(getResources().getColor(R.color.white));
        changeLoadingAnimation();
        mFakeLoaded = false; mRealLoaded = false;
        imageViewFake.setVisibility(View.INVISIBLE);
        imageViewReal.setVisibility(View.INVISIBLE);
        mCover1.setVisibility(View.VISIBLE);
        mCover2.setVisibility(View.VISIBLE);

        try {
            AssetManager am = this.getAssets();
            InputStream is = am.open("fake.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            int fakeImageNumber = randomNumber(1, 23713);
            int i = 1;
            String str = null;
            String imageLinkFake;

            while (i <= fakeImageNumber) {
                str = in.readLine();
                i++;
            }
            in.close();
            imageLinkFake = "http://www.whichfaceisreal.com/fakeimages/" + str;

            Picasso.get()
                    .load(imageLinkFake)
                    .placeholder(R.drawable.image_icon)
                    .error(R.drawable.broken_image_icon)
                    .noFade()
                    .into(imageViewFake, new Callback() {
                        @Override
                        public void onSuccess() {
                            mFakeLoaded = true;
                            if (mFakeLoaded & mRealLoaded) {
                                imageViewFake.setVisibility(View.VISIBLE);
                                imageViewReal.setVisibility(View.VISIBLE);
                                mCover1.setVisibility(View.INVISIBLE);
                                mCover2.setVisibility(View.INVISIBLE);
                            }
                        }
                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(MainActivity.this, getString(R.string.error_loading_image),
                                    Toast.LENGTH_SHORT).show();
                            imageViewFake.setVisibility(View.VISIBLE);
                            imageViewReal.setVisibility(View.VISIBLE);
                            mCover1.setVisibility(View.INVISIBLE);
                            mCover2.setVisibility(View.INVISIBLE);
                        }
                    });
            Picasso.get()
                    .load("http://www.whichfaceisreal.com/realimages/"+String.format("%05d", randomNumber(1, 69999))+".jpeg")
                    .placeholder(R.drawable.image_icon)
                    .error(R.drawable.broken_image_icon)
                    .noFade()
                    .into(imageViewReal, new Callback() {
                        @Override
                        public void onSuccess() {
                            mRealLoaded = true;
                            if (mFakeLoaded & mRealLoaded) {
                                imageViewFake.setVisibility(View.VISIBLE);
                                imageViewReal.setVisibility(View.VISIBLE);
                                mCover1.setVisibility(View.INVISIBLE);
                                mCover2.setVisibility(View.INVISIBLE);
                            }
                        }
                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(MainActivity.this, getString(R.string.error_loading_image),
                                    Toast.LENGTH_SHORT).show();
                            imageViewFake.setVisibility(View.VISIBLE);
                            imageViewReal.setVisibility(View.VISIBLE);
                            mCover1.setVisibility(View.INVISIBLE);
                            mCover2.setVisibility(View.INVISIBLE);
                        }
                    });

        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.error_file_read), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void setAnswersCount() {
        mSessionRight.setText(getString(R.string.tv_right, mRightAnswersCount));
        mSessionWrong.setText(getString(R.string.tv_wrong, mWrongAnswersCount));
        int totalRight = mPreferences.getInt(PREFERENCES_RIGHT, 0);
        int totalWrong = mPreferences.getInt(PREFERENCES_WRONG, 0);
        try {
            mSessionGuessRate.setText(getString(R.string.guessing_percentage, ((mRightAnswersCount*100) / (mWrongAnswersCount + mRightAnswersCount)))+"%");
        } catch (Exception e) {
            mSessionGuessRate.setText(getString(R.string.guessing_percentage, 0)+"%");
        }
        try {
            mTotalGuessRate.setText(getString(R.string.guessing_percentage, ((totalRight * 100) / (totalRight + totalWrong))) + "%");
        } catch (Exception e) {
            mTotalGuessRate.setText(getString(R.string.guessing_percentage, 0)+"%");
        }
        mTotalRight.setText(getString(R.string.tv_right, totalRight));
        mTotalWrong.setText(getString(R.string.tv_wrong, totalWrong));
    }

    public void addRight() {
        mRightAnswersCount++;
        int totalRight = mPreferences.getInt(PREFERENCES_RIGHT, 0);
        mPreferences.edit().putInt(PREFERENCES_RIGHT, totalRight+1).apply();
        setAnswersCount();
    }

    public void addWrong() {
        mWrongAnswersCount++;
        int totalWrong = mPreferences.getInt(PREFERENCES_WRONG, 0);
        mPreferences.edit().putInt(PREFERENCES_WRONG, totalWrong+1).apply();
        setAnswersCount();
    }

    void changeLoadingAnimation() {
        Sprite animation = null;
        Sprite animation2 = null;
        int i = randomNumber(0, 7);
        switch (i) {
            case 0:
                animation = new DoubleBounce();
                animation2 = new DoubleBounce();
                break;
            case 1:
                animation = new Wave();
                animation2 = new Wave();
                break;
            case 2:
                animation = new WanderingCubes();
                animation2 = new WanderingCubes();
                break;
            case 3:
                animation = new ChasingDots();
                animation2 = new ChasingDots();
                break;
            case 4:
                animation = new ThreeBounce();
                animation2 = new ThreeBounce();
                break;
            case 5:
                animation = new Circle();
                animation2 = new Circle();
                break;
            case 6:
                animation = new CubeGrid();
                animation2 = new CubeGrid();
                break;
            case 7:
                animation = new FadingCircle();
                animation2 = new FadingCircle();
                break;
        }
        mCover1.setIndeterminateDrawable(animation);
        mCover2.setIndeterminateDrawable(animation2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.info:
                View aboutPage = new AboutPage(this)
                        .isRTL(false)
                        .setImage(R.mipmap.ic_launcher_round)
                        .setDescription(getString(R.string.description))
                        .addGroup("Connect us")
                        .addEmail("chernishoff.15@gmail.com")
                        .addPlayStore("com.tutaf.whichpersonisreal")
                        .addGitHub("tutaf")
                        .create();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(aboutPage);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.press_again_to_exit),
                    Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }
}
