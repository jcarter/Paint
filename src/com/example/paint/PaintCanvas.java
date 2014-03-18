package com.example.paint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff.Mode;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.BoringLayout.Metrics;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class PaintCanvas extends View {
	
	private Paint canvasPaint;
	protected Paint drawPaint;
	private int defaultPaintColor = 0xff81ff00; // lime green to match default in holopicker
	private int previousColor;
	private int strokeWidth = 20;
	private Canvas canvas;
	private Bitmap canvasBitmap;
	private Path drawPath;
	protected int currentWidth;
	protected int currentHeight;
	protected boolean eraserEnabled = false;
 
	public PaintCanvas(Context context) {
		this(context, null, 0);
	}

	public PaintCanvas(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PaintCanvas(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		drawPath = new Path();
		drawPaint = new Paint();
		canvasPaint = new Paint();
		canvasPaint.setColor(Color.WHITE);
		canvasPaint.setStyle(Paint.Style.FILL);
		
		drawPaint.setColor(defaultPaintColor);
		drawPaint.setStrokeWidth(strokeWidth); // set initial brush size to 20
		drawPaint.setAntiAlias(true); // makes lines smoother
		drawPaint.setStyle(Paint.Style.STROKE); // make strokes
		drawPaint.setStrokeJoin(Paint.Join.ROUND); // sets curves around bends to round
		drawPaint.setStrokeCap(Paint.Cap.ROUND); // rounds off end of stroke
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888); // initialize bitmap to empty - http://developer.android.com/guide/topics/graphics/2d-graphics.html
		canvas = new Canvas(canvasBitmap); //sets the canvas to a bitmap. Use later to pull in images and draw on
		startNewPainting();
		
		// use to center stuff later, if needed
		currentWidth = w;
		currentHeight = h;
	}
	
	// Keep as short as possible. Get in, get out
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas); 
		canvas.drawBitmap(canvasBitmap, currentWidth - canvasBitmap.getWidth(), currentHeight - canvasBitmap.getHeight(), canvasPaint);
		canvas.drawPath(drawPath, drawPaint);
	}
	
	/* GETTERS */
	public Path getDrawPath() {return drawPath;}
	public Canvas getCanvas() {return canvas;}
	public Paint getDrawPaint() {return drawPaint;}
	public boolean isErasing() {return eraserEnabled;}
	public Bitmap getBitmap() {return canvasBitmap;}
	
	/* SETTERS */
	public void setDrawPaint(int color) {drawPaint.setColor(color);}
	public void setDrawWidth(float width) {drawPaint.setStrokeWidth(width);}
	public void setEraserState(boolean eraserIsEnabled) {eraserEnabled = eraserIsEnabled;}
	public void setPreviousColor(int color) {previousColor = color;}
	
	// Clears the whole canvas. Left the clear mode in case I need it later.
	public void startNewPainting() {
		//canvas.drawColor(0, Mode.CLEAR); // http://stackoverflow.com/questions/6956838/how-to-erase-previous-drawing-on-canvas
		canvas.drawColor(Color.WHITE);
		invalidate();
	}
	
	public void setupEraser(ImageButton button) {
		// if eraser is enabled, disable it
		if (eraserEnabled) {
			eraserEnabled = false;
			button.setImageResource(R.drawable.brush);
			drawPaint.setColor(previousColor);
			//drawPaint.setXfermode(null);
		} else { // eraser not enabled, make it enabled
			button.setImageResource(R.drawable.eraser);
			//Log.i("WIDTH_BUTTON", "color: " + Color.red(canvasBitmap.getPixel(100, 100)) + Color.green(canvasBitmap.getPixel(100, 100)) + Color.blue(canvasBitmap.getPixel(100, 100)));
			//drawPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
			previousColor = drawPaint.getColor();
			drawPaint.setColor(Color.WHITE);
			eraserEnabled = true;
		}
	}
	
	// Handles the painting
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float xTouch = event.getX();
		float yTouch = event.getY();
		
		//Log.i("X & Y", xTouch + " & " + yTouch);
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			drawPath.moveTo(xTouch, yTouch); // on press down, go to starting point
			break;
		case MotionEvent.ACTION_MOVE:
			drawPath.lineTo(xTouch, yTouch); // on movement, draw line to where user moves to
			break;
		case MotionEvent.ACTION_UP:
			canvas.drawPath(drawPath, drawPaint); // writes the newly drawn stroke to the canvas
			drawPath.reset(); // reset path for the next stroke
			break;
		default:
			return false;
		}
		
		invalidate(); // update canvas
		return true;
	}

	// Displays the real time size of the brush or eraser 
	public void updateWidthSizeDisplay(TextView widthDisplay) {
		if (eraserEnabled) {
			widthDisplay.setText("Eraser Size: " + drawPaint.getStrokeWidth());
		} else {
			widthDisplay.setText("Brush Size: " + drawPaint.getStrokeWidth());
		}
	}

	
	// Saves the bitmap into the gallery
	public void saveImage(ContentResolver contentResolver, Context context) {
		
		// make camera folder in DCIM if not already created. It must be created first. Took forever to find out that
		// Android 4.4 doesn't create it by default
		// https://android.googlesource.com/platform/packages/providers/MediaProvider/+/92013781ab1573eee3d5d75682f320fe3a6076f2%5E!/
		// http://stackoverflow.com/questions/20164558/android-mediastore-images-media-insertimage-works-on-kitkat-only-after-using-cam
		File imageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
		if (!imageDir.mkdirs()) {
			Log.i("IMAGE", "Failed to make directory. It might already exist.");
		}	
		
		this.setDrawingCacheEnabled(true);
		
		// add bitmap to gallery with random name
		String imageToSave = MediaStore.Images.Media.insertImage(
			    contentResolver, this.getDrawingCache(),
			    UUID.randomUUID().toString()+".png", "Paint app drawing");
		
		// toasts
		if(imageToSave!=null){
		    Toast savedToast = Toast.makeText(context, 
		        "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
		    savedToast.show();
		}
		else{
		    Toast unsavedToast = Toast.makeText(context, 
		        "Sorry. Image could not be saved.", Toast.LENGTH_SHORT);
		    unsavedToast.show();
		}
		this.destroyDrawingCache();
		
	}
}
