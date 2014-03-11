package com.example.paint;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableRow;

public class MainActivity extends Activity implements OnClickListener, OnTouchListener{
	
	private final int NUMBER_OF_IMAGE_BUTTONS = 3; // number of image buttons used in menu
	private PaintCanvas paintCanvas; 
	private ImageButton[] buttons = new ImageButton[NUMBER_OF_IMAGE_BUTTONS]; 
	private TableRow menuOptions;
	private Button okayColorSelectorButton;
	
	// color selector stuff
	private LinearLayout colorSelector;
	private ColorPicker picker;
	private SVBar svBar;
	private OpacityBar opacityBar;  
	//private SaturationBar saturationBar;
	//private ValueBar valueBar;  

	@Override
	protected void onCreate(Bundle savedInstanceState) {  
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initColorSelector(); // set up complex color selector
		
		// connect to items in xml
		paintCanvas = (PaintCanvas)findViewById(R.id.paint_canvas);
		menuOptions = (TableRow)findViewById(R.id.menuOptionTR);
		okayColorSelectorButton = (Button)findViewById(R.id.okay_color_selector_button);
		int imageButtonIds[] = {R.id.new_canvasIB, R.id.color_selectorIB, R.id.width_selectorIB}; // image button ids
		
		okayColorSelectorButton.setOnClickListener(this);
		// image button initialization
		for (int i = 0; i < NUMBER_OF_IMAGE_BUTTONS; i++) {
			buttons[i] = (ImageButton)findViewById(imageButtonIds[i]);
			buttons[i].setOnClickListener(this);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.new_canvasIB:
			paintCanvas.startNewPainting();
			break;
		case R.id.color_selectorIB:
		case R.id.okay_color_selector_button:
			showColorSelector();
			break;
		case R.id.width_selectorIB:
			
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {	
		if (colorSelector.getVisibility() != View.VISIBLE) { // don't draw or move menu when color selector is being used
			paintCanvas.onTouchEvent(event); // handles the ability to draw
			// Menu animation
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				menuOptions.animate().translationY(-100).setDuration(200); // slide menu up
				break;
			case MotionEvent.ACTION_UP:
				menuOptions.animate().translationY(0).setDuration(200); // slide menu down
				break;
			default:
				return false;
			}
		} 
			
		return true;
	}
	
	public void initColorSelector() {
		colorSelector = (LinearLayout)findViewById(R.id.colorSelectorLL);
		picker = (ColorPicker) findViewById(R.id.picker);
		svBar = (SVBar) findViewById(R.id.svbar);
		opacityBar = (OpacityBar) findViewById(R.id.opacitybar);
		//saturationBar = (SaturationBar) findViewById(R.id.saturationbar);
		//valueBar = (ValueBar) findViewById(R.id.valuebar);
			
		picker.addSVBar(svBar);
		picker.addOpacityBar(opacityBar);
		//picker.addSaturationBar(saturationBar);
		//picker.addValueBar(valueBar); 
	}
	
	// Flips the state of the color selector when the button is pressed
	public void showColorSelector() {
		if (colorSelector.getVisibility() == View.VISIBLE) {
			selectColor();
			colorSelector.setVisibility(View.GONE);
		} else {
			colorSelector.setVisibility(View.VISIBLE);
		}	
	}
	
	public void selectColor() { 
		picker.setOldCenterColor(picker.getColor()); // sets old color in center of picker (on left)
		paintCanvas.setDrawPaint(picker.getColor()); // sets new color of paint
	}

}
