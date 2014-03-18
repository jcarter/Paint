/*
 * Author: Justin Carter
 * Date: 3/12/2014
 * Assignment #2
 * 
 * Features:
 * 1. Complex color picker
 * 		- Uses an imported library called HoloColorPicker by Lars Werkman
 * 		- Created in the activity_main.xml
 * 		- Used in MainActivity.java
 * 		- Methods include initColorSelector(), showColorSelector(), selectColor()
 * 
 * 2. Dynamic width preview selector. Shows the current color selected and adjusts 
 * 	  the size of the preview line's width in real time.
 * 		- Created in activity_main.xml
 * 		- Made in WidthSelector.java (which inherits from PaintCanvas) and used in MainActivity.java
 * 		- Method used to change line in real time - onProgressChanged()
 * 
 * 3. Self hiding menu bar when drawing
 * 		- Created in activity_main.xml
 * 		- Hides and shows menu using onTouch
 * 
 * 4. Eraser mode by long click on the brush button
 * 		- Set up in setLongClickButton in MainActivity
 * 
 * 5. Save the drawing to the device's gallery
 * 		- Created in PaintCanvas's saveImage()
 * 		- Used in MainActivity's saveDrawingPrompt()
 */
package com.example.paint;

import java.io.File;
import java.util.UUID;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, OnTouchListener, OnSeekBarChangeListener {
	
	FrameLayout masterLayout;
	private final int NUMBER_OF_IMAGE_BUTTONS = 4; // number of image buttons used in menu
	private PaintCanvas paintCanvas; 
	private ImageButton[] buttons = new ImageButton[NUMBER_OF_IMAGE_BUTTONS]; 
	private TableRow menuOptions;
	private Button okayColorSelectorButton;
	private Button okayWidthSelectorButton;
	private boolean optionIsChosen = false;
	private TextView widthDisplay;
	
	// width selector stuff
	private LinearLayout widthSelector;
	private WidthSelector widthPicker;
	private SeekBar widthBar;
	 
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
		
		masterLayout = (FrameLayout)findViewById(R.id.masterLayoutFL);		
		initColorSelector(); // set up complex color selector
		
		widthSelector = (LinearLayout)findViewById(R.id.widthSelectorLL);
		widthPicker = (WidthSelector)findViewById(R.id.widthSelector);
		widthBar = (SeekBar)findViewById(R.id.widthSB);
		widthDisplay = (TextView)findViewById(R.id.widthTV);
		widthBar.setOnSeekBarChangeListener(this);
		
		// connect to items in xml
		paintCanvas = (PaintCanvas)findViewById(R.id.paint_canvas);
		paintCanvas.setOnTouchListener(this);
		menuOptions = (TableRow)findViewById(R.id.menuOptionTR);
		okayColorSelectorButton = (Button)findViewById(R.id.okay_color_selector_button);
		okayWidthSelectorButton = (Button)findViewById(R.id.okay_width_selector_button);
		int imageButtonIds[] = {R.id.new_canvasIB, R.id.color_selectorIB, R.id.width_selectorIB, R.id.saveIB}; // image button ids
		
		okayColorSelectorButton.setOnClickListener(this);
		okayWidthSelectorButton.setOnClickListener(this);
		// image button initialization
		for (int i = 0; i < NUMBER_OF_IMAGE_BUTTONS; i++) {
			buttons[i] = (ImageButton)findViewById(imageButtonIds[i]);
			buttons[i].setOnClickListener(this);
			
			// adds the long click listener for the width selector button
			if (imageButtonIds[i] == R.id.width_selectorIB) {
				setLongClickButton(buttons[i]);
			}
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
			newPaintingPrompt();
			break;
		case R.id.color_selectorIB:
		case R.id.okay_color_selector_button:
			showColorSelector();
			break;
		case R.id.width_selectorIB:
		case R.id.okay_width_selector_button:
			showWidthSelector();
			break;
		case R.id.saveIB:
			savePaintingPrompt();
			
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {	
		if (!optionIsChosen) { // don't draw or move menu until color & width selector is gone
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
		colorSelector.setOnTouchListener(this);
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
			optionIsChosen = false;
		} else {
			if (!optionIsChosen) { // make sure another option is not currently being used
				colorSelector.setVisibility(View.VISIBLE);
				optionIsChosen = true;
			}
		}	
	}
	
	// Sets the color chosen in the color picker to be used for the paintCanvas
	public void selectColor() { 
		picker.setOldCenterColor(picker.getColor()); // sets old color in center of picker (on left)
		paintCanvas.setDrawPaint(picker.getColor()); // sets new color of paint
		widthPicker.setDrawPaint(picker.getColor()); // sets color of paint for width picker
		paintCanvas.setPreviousColor(picker.getColor());
		
		// If eraser is enabled, allow the user to pick a color but make sure it stills paints white
		if (paintCanvas.eraserEnabled) {
			paintCanvas.setDrawPaint(Color.WHITE);
			Toast reminder = Toast.makeText(getApplicationContext(), 
					"Don't forget that you're still in eraser mode!", Toast.LENGTH_LONG);	
			reminder.show();
		}
	}
	
	// Flips the state of the width selector when the button is pressed
	public void showWidthSelector() {
		if (widthSelector.getVisibility() == View.VISIBLE) {
			widthSelector.setVisibility(View.GONE);
			optionIsChosen = false;
		} else {
			if (!optionIsChosen) { // make sure another option is not currently being used
				paintCanvas.updateWidthSizeDisplay(widthDisplay);
				widthSelector.setVisibility(View.VISIBLE);
				optionIsChosen = true;
			}
		}
	}

	// Methods needed by the WidthSelector class
	@Override
	public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {	
		paintCanvas.setDrawWidth(progress); // sets the user's brush size
		widthPicker.reDrawLine(bar, progress, paintCanvas.getDrawPaint().getColor()); // sets the visual feedback on the brush size adjuster
		
		// Update size of brush/eraser display
		paintCanvas.updateWidthSizeDisplay(widthDisplay);
	}
	@Override
	public void onStartTrackingTouch(SeekBar bar) {}
	@Override
	public void onStopTrackingTouch(SeekBar bar) {}
	
	// Used to swap between brush and eraser
	public void setLongClickButton(ImageButton b) {
		final ImageButton button = b; // make final image button to allow the onLongClick to use it
		button.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (v.getId() == R.id.width_selectorIB) {
					Log.i("WIDTH_BUTTON", "Long Pressed");
					paintCanvas.setupEraser(button);
					paintCanvas.updateWidthSizeDisplay(widthDisplay);
					return true;
				}
				return false;
			}
		});
	}

	// Prompts the user dialog to verify that they want to save the drawing
	public void savePaintingPrompt() {
		AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
		saveDialog.setTitle("Save Painting");
		saveDialog.setMessage("Save Painting to Device Gallery?");
		saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
		    public void onClick(DialogInterface dialog, int which){
		        // save drawing
		    	paintCanvas.saveImage(getContentResolver(), getApplicationContext());
		    }
		});
		saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
		    public void onClick(DialogInterface dialog, int which){
		        dialog.cancel();
		    }
		});
		saveDialog.show();
	}

	// Prompts the user dialog to verify that they want to start a new drawing
	public void newPaintingPrompt() {
		AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
		saveDialog.setTitle("Start New Painting");
		saveDialog.setMessage("Are You Sure?");
		saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
		    public void onClick(DialogInterface dialog, int which){
		        // new drawing
		    	paintCanvas.startNewPainting();
		    }
		});
		saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
		    public void onClick(DialogInterface dialog, int which){
		        dialog.cancel();
		    }
		});
		saveDialog.show();
	}
}
