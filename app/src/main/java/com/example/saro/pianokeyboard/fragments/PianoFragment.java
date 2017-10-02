package com.example.saro.pianokeyboard.fragments;

import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.example.saro.pianokeyboard.MainActivity;
import com.example.saro.pianokeyboard.R;
import com.example.saro.pianokeyboard.pianoview.Piano;
import com.example.saro.pianokeyboard.pianoview.Piano.PianoKeyListener;

/**
 * Created by Saro on 8/2/2015.
 */
public class PianoFragment extends Fragment {
    private MediaPlayer _mediaPlayer;
    private int _numberOctave;
    private static final String DEBUG_TAG = "PianoView";

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    private PianoKeyListener onPianoKeyPress =
            new PianoKeyListener() {
                @Override
                public void keyPressed(int id, int action) {
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            Log.i(DEBUG_TAG, "Key pressed: " + id);
                            Log.d("Number of Key ", id + "");
                            setSound(id);
                            break;
                        case MotionEvent.ACTION_UP:
                            System.out.println("Key Pressed called for action up");
                            break;
                    }
                }
            };

    Piano piano;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.piano_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        piano = (Piano) view.findViewById(R.id.pianoView);
        piano.setPianoKeyListener(onPianoKeyPress);

        if (_mediaPlayer != null) {
            _mediaPlayer.release();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d("NO is into onStart", _numberOctave + "");
    }

    public void setSound(int id) {
        Log.d("setSoundNote is called", id + "");
        String mp3Name = null;
        _numberOctave = (int) Math.rint(id / 12);

        Log.d("N octave into sSound", _numberOctave + "");
        id %= 12;
        switch (MainActivity.instrument) {
            case Acoustic:
                if (piano.black_key_indexes.contains(id)) {
                    id = _numberOctave * 5 + determineSharpSoundNumber(id);
                    mp3Name = "@raw/sh_" + id;
                } else {
                    id = _numberOctave * 7 + determinePitchSoundNumber(id);
                    mp3Name = "@raw/ptch_" + id;
                }
                break;
            case Xylophone:
            case Synthesis:
            case MusicBox:
                if (piano.black_key_indexes.contains(id)) {
                    id = _numberOctave * 5 + determineSharpSoundNumber(id);
                    mp3Name = "@raw/sh_" + id + "_x";
                } else {
                    id = _numberOctave * 7 + determinePitchSoundNumber(id);
                    mp3Name = "@raw/ptch_" + id + "_x";
                }
                break;
            case Saxophone:
                if (piano.black_key_indexes.contains(id % 12)) {
                    id = _numberOctave * 5 + determineSharpSoundNumber(id);
                    mp3Name = "@raw/sax_" + id + "_s";
                } else {
                    id = _numberOctave * 7 + determinePitchSoundNumber(id);
                    mp3Name = "@raw/sax_" + id + "_p";
                }
                break;
        }
        Log.d("mp3Name is", mp3Name);

        Resources resource = getResources();
        int idSound = resource.getIdentifier(mp3Name, null, getActivity().getPackageName());
        if (idSound != 0) {
            _mediaPlayer = MediaPlayer.create(getActivity(), idSound);
            _mediaPlayer.start();
            _mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
        }
    }

    private int determineSharpSoundNumber(int id) {

        switch (id) {
            case 1:
                return 1;
            case 3:
                return 2;
            case 6:
                return 3;
            case 8:
                return 4;
            case 10:
                return 5;
        }

        return -1;
    }

    private int determinePitchSoundNumber(int id) {

        switch (id) {
            case 0:
                return 1;
            case 2:
                return 2;
            case 4:
                return 3;
            case 5:
                return 4;
            case 7:
                return 5;
            case 9:
                return 6;
            case 11:
                return 7;
        }

        return -1;
    }

    public void invalidatePiano() {
        piano.invalidate();
    }
}
