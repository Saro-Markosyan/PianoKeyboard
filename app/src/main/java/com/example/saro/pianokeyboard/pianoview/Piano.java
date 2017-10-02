package com.example.saro.pianokeyboard.pianoview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.saro.pianokeyboard.MainActivity;
import com.example.saro.pianokeyboard.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


/**
 * Created by Saro on 7/12/2015.
 */
public class Piano extends View {

    private static final int defaultKeyCount = 12;
    private TreeMap<Integer, Key> keymap_white;
    private TreeMap<Integer, Key> keymap_black;
    private TreeMap<Integer, Finger> fingers;
    public ArrayList<Integer> black_key_indexes = new ArrayList<Integer>(Arrays.asList(1, 3, 6, 8, 10));
    private int _desiredWidth;
    private int _desiredHeight; private int white_key_resource_id;
    private int black_key_resource_id;
    public float _density;
    private Resources _currRes;


    public Piano(Context context) {
        this(context, null);
    }

    public Piano(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Piano(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        int black_key_resource_id = R.drawable.key_sharp;
        int white_key_resource_id = R.drawable.key_pitch;
        int key_count = defaultKeyCount;

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PianoView, 0, 0);
            black_key_resource_id = a.getResourceId(R.styleable.PianoView_blackKeyDrawable, R.drawable.key_sharp);
            white_key_resource_id = a.getResourceId(R.styleable.PianoView_whiteKeyDrawable, R.drawable.key_pitch);
            key_count = a.getInt(R.styleable.PianoView_keyCount, defaultKeyCount); // ?
            a.recycle();
        }

        initPiano(white_key_resource_id, black_key_resource_id, key_count);
    }

    public Piano(Context context, int white_key_resource_id, int black_key_resource_id, int key_count) {
        super(context);
        initPiano(white_key_resource_id, black_key_resource_id, key_count);
    }

    private void initPiano(int white_key_resource_id, int black_key_resource_id, int key_count) {

        this.fingers = new TreeMap<Integer, Finger>();
        this.white_key_resource_id = white_key_resource_id;
        this.black_key_resource_id = black_key_resource_id;
        this.keymap_white = new TreeMap<Integer, Key>();
        this.keymap_black = new TreeMap<Integer, Key>();

        for(int i = 0; i < key_count; i++) {
            if(black_key_indexes.contains(i % 12)) {
                keymap_black.put(i, new Key(i, this));
            } else {
                keymap_white.put(i, new Key(i, this));
            }
        }

        _currRes = getResources();
        _density = _currRes.getDisplayMetrics().density;
        //_desiredWidth = (int) _currRes.getDimension(R.dimen.width_pitch) * keymap_white.size();
        _desiredWidth = MainActivity.screenWidth / 10 * keymap_white.size();
    }

    public void setPianoKeyListener(PianoKeyListener listener){
        for(Key k : this.keymap_black.values()){
            k.setPianoKeyListener(listener);
        }
        for(Key k : this.keymap_white.values()){
            k.setPianoKeyListener(listener);
        }
    }

    public interface PianoKeyListener {
        public void keyPressed(int id, int action);
    }

    private Drawable drawKey(Canvas canvas, Boolean pressed, int resource_id, int bounds_l, int bounds_t, int bounds_r, int bounds_b) throws NotFoundException, XmlPullParserException, IOException {
        Drawable key = Drawable.createFromXml(_currRes, _currRes.getXml(resource_id));
        key.setState(pressed ? new int[] {android.R.attr.state_pressed} : new int[] {-android.R.attr.state_pressed});
        key.setBounds(bounds_l, bounds_t, bounds_r, bounds_b);
        key.draw(canvas);
        return key;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        /*final int wPitch = (int) _currRes.getDimension(R.dimen.width_pitch);
        final int wSharp = (int) _currRes.getDimension(R.dimen.width_sharp);*/
        int differenceKeysWidth = (int) (25 * _density);
        final int wPitch = MainActivity.screenWidth / MainActivity.countVisibleKeys;
        final int wSharp = wPitch - differenceKeysWidth;
        int KEY_HEIGHT = this.getHeight();
        _desiredHeight = KEY_HEIGHT;
        int counter = 0;

        System.out.println("canvas WIDTH is " + canvas.getWidth() + "");
        System.out.println("KEY_WIDTH pitch is " + wPitch + "");
        Log.d("keymap Pitch size", this.keymap_white.size() + "");
        Log.d("keymap Sharp size", this.keymap_black.size() + "");

        try {

            for (Entry<Integer, Key> key : this.keymap_white.entrySet()) {
                int bounds_left = counter * wPitch;
                int bounds_top = 0;
                int bounds_right = (counter * wPitch) + wPitch;
                int bounds_bottom = KEY_HEIGHT;
                Drawable white_key = this.drawKey(canvas, key.getValue().isPressed(), white_key_resource_id, bounds_left, bounds_top, bounds_right, bounds_bottom);
                key.getValue().setDrawable(white_key);
                counter++;
            }

            counter = 0;

            System.out.println("KEY_WIDTH sharp is " + wSharp + "");

            for (Entry<Integer, Key> key : keymap_black.entrySet()) {
                if( ((counter - 2) % 7 == 0) || ((counter - 6) % 7 == 0)) {
                    counter++;
                }

                int bounds_left = (counter * wPitch) + (wPitch - (wSharp / 2));
                int bounds_top = 0;
                int bounds_right = (counter * wPitch) + (wSharp + (wPitch - (wSharp / 2)));
                int bounds_bottom = (KEY_HEIGHT * 60) / 100;
                Drawable black_key = this.drawKey(canvas, key.getValue().isPressed(), black_key_resource_id, bounds_left, bounds_top, bounds_right, bounds_bottom);
                key.getValue().setDrawable(black_key);
                counter++;
            }

            this.setOnTouchListener(new KeyPressListener(fingers, keymap_white, keymap_black));

        } catch (Exception e) {
            Toast.makeText(this.getContext(), "Error drawing keys", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = 100;
        int desiredHeight = 100;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(_desiredWidth, height);
    }

    private class KeyPressListener implements OnTouchListener {
        private TreeMap<Integer, Finger> fingers;
        private TreeMap<Integer, Key> keymap_white;
        private TreeMap<Integer, Key> keymap_black;

        public KeyPressListener(TreeMap<Integer, Finger> fingers, TreeMap<Integer, Key> keymap_pitch, TreeMap<Integer, Key> keymap_sharp) {
            this.fingers = fingers;
            this.keymap_white = keymap_pitch;
            this.keymap_black = keymap_sharp;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            System.out.println("called onThouch event ");
            int action = event.getActionMasked();

            switch (action) {
                case MotionEvent.ACTION_MOVE: handleActionPointerMove(event); System.out.println("Action move work"); break;
                case MotionEvent.ACTION_DOWN: pushKeyDown(event); System.out.println("Action down work"); break;
                case MotionEvent.ACTION_UP: handleActionUp(event); System.out.println("Action up work"); break;
                case MotionEvent.ACTION_POINTER_DOWN: pushKeyDown(event); System.out.println("Action pointer down work"); break;
                case MotionEvent.ACTION_POINTER_UP: handleActionUp(event); System.out.println("Action pointer up work"); break;
            }

            return true;
        }

        public void handleActionPointerMove(MotionEvent event) {
            for(int i = 0; i < event.getPointerCount(); i++) {
                handlePointerIndex(i, event);
            }
        }

        private void pushKeyDown(MotionEvent event) {
            //int pointer_index = event.getPointerId(event.getActionIndex());
            int pointer_index = event.getActionIndex();
            int pointer_id = event.getPointerId(pointer_index);
            Key key = isPressingKey(event.getX(pointer_index), event.getY(pointer_index));

            if(!fingers.containsKey(pointer_id)) {
                Finger finger = new Finger();
                finger.press(key);
                fingers.put(pointer_id, finger);
            }
        }

        public void handleActionUp(MotionEvent event) {
            //int pointer_index = event.getPointerId(event.getActionIndex());
            int pointer_index = event.getActionIndex();
            int pointer_id = event.getPointerId(pointer_index);
            if(fingers.containsKey(pointer_id)) {
                fingers.get(pointer_id).lift();
                fingers.remove(pointer_id);
            }
        }



        private void handlePointerIndex(int index, MotionEvent event) {
            int pointer_id = event.getPointerId(index);
            //has it moved off a key?
            Key key = isPressingKey(event.getX(index), event.getY(index));
            Finger finger = fingers.get(pointer_id);

            if(key == null) {
                finger.lift();
            } else
            if(!finger.isPressing(key)) {
                finger.lift();
                finger.press(key);
            }
        }

        private Key isPressingKey(float xpos, float ypos) {
            Key pressing_key;

            if((pressing_key = isPressingKeyInSet(xpos, ypos, keymap_black)) != null) {
                return pressing_key;
            } else {
                return isPressingKeyInSet(xpos, ypos, keymap_white);
            }
        }

        private Key isPressingKeyInSet(float xpos, float ypos, TreeMap<Integer, Key> keyset) {
            for(Entry<Integer, Key> entry : keyset.entrySet()) {
                if(entry.getValue() != null && entry.getValue().getDrawable() != null && entry.getValue().getDrawable().getBounds().contains((int)xpos, (int)ypos)) {
                    return entry.getValue();
                }
            }

            return null;
        }
    }

}

