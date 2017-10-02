package com.example.saro.pianokeyboard;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.example.saro.pianokeyboard.common.Constants;
import com.example.saro.pianokeyboard.common.Instrument;
import com.example.saro.pianokeyboard.common.view.StableHorizontalScrollView;
import com.example.saro.pianokeyboard.fragments.PianoFragment;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

public class MainActivity extends FragmentActivity
        implements View.OnClickListener {
    private ImageButton _imageButtonPlayLeft, _imageButtonPlayRight;
    private ImageButton _imageButtonNextLeft, _imageButtonNextRight;
    private int _scrollMaxSize;
    private int _vResizableMaxSize;
    private int _sbSize;
    private StableHorizontalScrollView _horizontalScrollView;
    private Resources _currRes;
    public float _density;
    private SeekBar _seekBar;
    private int _progress;
    private int _lprogress;
    private View _vWhite;
    private ImageButton _ibPianoInstruments;
    private ImageButton _ibAcoustic;
    private ImageButton _ibSynthesis;
    private ImageButton _ibMusicBox;
    private ImageButton _ibXylophone;
    private ImageButton _ibSaxophone;
    private ImageButton _btnMenu;
    private ImageButton _ibMinusKey;
    private ImageButton _ibPlusKey;
    private TextView _tvVisibleKeys;
    private PianoFragment _fPiano;

    public static Instrument instrument = Instrument.Acoustic;
    public static int screenWidth, screenHeight;
    public static int countVisibleKeys;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hiding the title bar has to happen before the view is created
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            _fPiano = new PianoFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.llPianoFragment, _fPiano, Constants.Piano_Fragment)
                    .commit();
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        System.out.println("screenWidth is " + screenWidth);
        System.out.println("screenHeight is " + screenHeight);
        countVisibleKeys = 10;
        int pitchWidthInPixels = screenWidth / countVisibleKeys;
        _scrollMaxSize = 25 * pitchWidthInPixels;

        _currRes = getResources();
        _density = _currRes.getDisplayMetrics().density;
        System.out.println("_density is " + _density);


        _lprogress = 0;
        _progress = 0;
        _seekBar = (SeekBar) findViewById(R.id.seekBar);
        _imageButtonPlayLeft = (ImageButton) findViewById(R.id.left_play);
        _imageButtonPlayRight = (ImageButton) findViewById(R.id.right_play);
        _imageButtonNextLeft = (ImageButton) findViewById(R.id.left_next);
        _imageButtonNextRight = (ImageButton) findViewById(R.id.right_next);
        _horizontalScrollView = (StableHorizontalScrollView) findViewById(R.id.stableHorizontalScrollView);
        _ibPianoInstruments = (ImageButton) findViewById(R.id.ibInstruments);
        _btnMenu = (ImageButton) findViewById(R.id.menu_button);
        _ibMinusKey = (ImageButton) findViewById(R.id.ibMinusKey);
        _ibPlusKey = (ImageButton) findViewById(R.id.ibPlusKey);
        _tvVisibleKeys = (TextView) findViewById(R.id.tvVisibleKeys);

        String visibleKeys = getString(R.string.tv_visible_keys)+ " " + countVisibleKeys;
        _tvVisibleKeys.setText(visibleKeys);

        _imageButtonPlayLeft.setOnClickListener(this);
        _imageButtonPlayRight.setOnClickListener(this);
        _imageButtonNextRight.setOnClickListener(this);
        _imageButtonNextLeft.setOnClickListener(this);

        //setting white (transparent) layout width
        _vWhite = findViewById(R.id.vWhite);
        final View vResizable = findViewById(R.id.vResizable);

        _sbSize = (int) _currRes.getDimension(R.dimen.seek_bar_width);
        System.out.println("seek_bar_width " + _sbSize);
        int llWhiteWidth = _sbSize * countVisibleKeys / 35;//(int) _currRes.getDimension(R.dimen.v_white_width);

        _vResizableMaxSize = _sbSize - llWhiteWidth;
        //_seekBar.setMax(25);
        System.out.println("Max progress is " + _seekBar.getMax());

        LinearLayout.LayoutParams vWhiteParams = (LinearLayout.LayoutParams) _vWhite.getLayoutParams();
        vWhiteParams.width =  llWhiteWidth;
        _vWhite.setLayoutParams(vWhiteParams);

        _seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                _progress = seekBar.getProgress();

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) vResizable.getLayoutParams();
                layoutParams.width = progress * _vResizableMaxSize / 100;
                vResizable.setLayoutParams(layoutParams);

                handleHSV(_progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
               /* _progress = seekBar.getProgress();
                handleHSV(_progress);*/
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                /*_progress = seekBar.getProgress();
                handleHSV(_progress);*/
            }
        });

        _ibPianoInstruments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = getLayoutInflater();
                View promptsView = li.inflate(R.layout.prompts, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(promptsView.getContext());

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                // create alert dialog
                final AlertDialog alertDialog = alertDialogBuilder.create();

                _ibAcoustic = (ImageButton) promptsView.findViewById(R.id.ibAcoustic);
                _ibSynthesis = (ImageButton) promptsView.findViewById(R.id.ibSyntez);
                _ibMusicBox = (ImageButton) promptsView.findViewById(R.id.ibMusicBox);
                _ibXylophone = (ImageButton) promptsView.findViewById(R.id.ibXylophone);
                _ibSaxophone = (ImageButton) promptsView.findViewById(R.id.ibSaxophone);

                _ibAcoustic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //
                        instrument = Instrument.Acoustic;
                        System.out.println(instrument);
                        alertDialog.dismiss();
                    }
                });

                _ibSynthesis.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        instrument = Instrument.Synthesis;
                        alertDialog.dismiss();
                    }
                });

                _ibMusicBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        instrument = Instrument.MusicBox;
                        alertDialog.dismiss();
                    }
                });

                _ibXylophone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        instrument = Instrument.Xylophone;
                        alertDialog.dismiss();
                    }
                });

                _ibSaxophone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        instrument = Instrument.Saxophone;
                        alertDialog.dismiss();
                    }
                });
                //show it
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(true);
            }
        });

        _btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               LinearLayout llMenuFromRightSide = (LinearLayout) findViewById(R.id.llMenuFromRightSide);
                llMenuFromRightSide.setVisibility((llMenuFromRightSide.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
            }
        });


        _ibMinusKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (countVisibleKeys > 6) {
                    --countVisibleKeys;


                    String visibleKeys = getString(R.string.tv_visible_keys) + " " + countVisibleKeys;
                    _tvVisibleKeys.setText(visibleKeys);

                    correctCountVisibleKeys();
                    _fPiano.invalidatePiano();
                    //_horizontalScrollView.invalidate();
                }
            }
        });


        _ibPlusKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (countVisibleKeys < 35) {
                    ++countVisibleKeys;


                    String visibleKeys = getString(R.string.tv_visible_keys) + " " + countVisibleKeys;
                    _tvVisibleKeys.setText(visibleKeys);

                    correctCountVisibleKeys();
                    _fPiano.invalidatePiano();
                    //_horizontalScrollView.invalidate();
                }
            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        //Toast.makeText(this, "Click is invoked and Progress is " + _progress, Toast.LENGTH_SHORT).show();

        if (!checkProgressBounds(view)) {
            return;
        }

        switch (view.getId()) {
            case R.id.right_play:
                _progress += 4;
                _seekBar.setProgress(_progress);
                break;
            case R.id.left_play:
                _progress -= 4;
                _seekBar.setProgress(_progress);
                break;
            case R.id.right_next:
                _progress += 40;
                _seekBar.setProgress(_progress);
                break;
            case R.id.left_next:
                _progress -= 40;
                _seekBar.setProgress(_progress);
                break;
        }
    }

    public void handleHSV(final int progress) {

        final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (_horizontalScrollView.getWidth() > 0) {
                    // do scrolling here
                    System.out.println("getMaxScrollAmount is " + _horizontalScrollView.getMaxScrollAmount());
                    System.out.println("_horizontalScrollView width is " + _horizontalScrollView.getWidth());
                    System.out.println("last progrss is " + _lprogress);
                    System.out.println("progress is " + progress);
                    System.out.println("density is " + _density);

                    if (_lprogress != progress) {
                        int scrollSize = progress * _scrollMaxSize / 100;
                        _horizontalScrollView.smoothScrollTo(scrollSize, 0);
                    }

                    _lprogress = progress;
                } else {
                    mainThreadHandler.post(this);
                }
            }
        });
    }

    private boolean checkProgressBounds(View v) {

        if (_progress <= 0 && (v.getId() == R.id.left_play || v.getId() == R.id.left_next)) {
            return false;
        } else if (_progress >= 100 && (v.getId() == R.id.right_play || v.getId() == R.id.right_next)) {
            return false;
        }

        return true;
    }

    private void correctCountVisibleKeys() {
        int pitchWidthInPixels = screenWidth / countVisibleKeys;
        _scrollMaxSize = 25 * pitchWidthInPixels;

        int llWhiteWidth = _sbSize * countVisibleKeys / 35;//(int) _currRes.getDimension(R.dimen.v_white_width);

        _vResizableMaxSize = _sbSize - llWhiteWidth;

        LinearLayout.LayoutParams vWhiteParams = (LinearLayout.LayoutParams) _vWhite.getLayoutParams();
        vWhiteParams.width =  llWhiteWidth;
        _vWhite.setLayoutParams(vWhiteParams);

        _seekBar.setProgress(_progress);
    }
}
