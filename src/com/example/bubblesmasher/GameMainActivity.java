package com.example.bubblesmasher;

import com.example.bubblesmasher.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;

public class GameMainActivity extends Activity implements OnTouchListener {
	//Create GameView object.
	GameView gameview;
	//Create new bitmap for background image
	Bitmap background = null;
	//Create new Bitmap for bubble.
	Bitmap bubble1 = null;
	//Create thread to pause and resume game
	Thread threadGame=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Make full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//Create new GameView
		gameview = new GameView(this);
		gameview.setOnTouchListener(this);
		// Initialize background bitmap
		background = BitmapFactory.decodeResource(getResources(),R.drawable.background1);
		//Initialize bubble1 bitmap
		bubble1 = BitmapFactory.decodeResource(getResources(),R.drawable.bubble2);
		//Set gameview as ContentView for GameMainActivity
		setContentView(gameview);	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game_main, menu);
		return true;
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		gameview.resume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		gameview.pause();
	}

	//Create new SurfaceView class
	public class GameView extends SurfaceView implements Runnable {
		// Create SurfaceHolder to manage SurfaceView Activities
		SurfaceHolder holder;
		//Create boolean to check the status of game
		boolean isItOk = false;

		public GameView(Context context) {
			super(context);
			holder = getHolder();
			// TODO Auto-generated constructor stub
		}

		public void run() {
			while (isItOk==true){
				// Perform Canvas Drawing
				if(!holder.getSurface().isValid()){
					continue;
				}
				//Create Canvas to draw bitmaps
				Canvas canvas = holder.lockCanvas();
				GameStarts(canvas);
				holder.unlockCanvasAndPost(canvas);
			}
			// TODO Auto-generated method stub
		}

		public void GameStarts(Canvas canvas)// Game start method
		{
			//Set whole canvas background color
			canvas.drawARGB(255, 255, 255, 255);
			//Create paint to set colors on canvas
			Paint paint=new Paint();
			float bottomheight = canvas.getHeight()-1050;
			// Draw background bitmap with rect on canvas
			canvas.drawBitmap(background, new Rect(0,0,canvas.getWidth(),(int) (canvas.getHeight()-bottomheight)), new Rect(0,0,canvas.getWidth(),(int) (canvas.getHeight()-bottomheight)), paint);;
			// Draw bubble bitmap on canvas
			canvas.drawBitmap(bubble1, 200, 200, null);
			paint.setColor(Color.GREEN);
			// Creating leaves
			canvas.drawCircle(canvas.getWidth()/1.27f,canvas.getHeight()/25 , 15, paint);
			canvas.drawCircle(canvas.getWidth()/1.15f,canvas.getHeight()/25 , 15, paint);
			canvas.drawCircle(canvas.getWidth()/1.05f,canvas.getHeight()/25 , 15, paint);
			//Set water color to paint
			paint.setColor(Color.rgb( 135, 206, 250));
			// Draw waterlevel on canvas
			canvas.drawRect(0, 1050, canvas.getWidth(), canvas.getHeight(), paint);
		}

		public void pause(){
			isItOk=false;
			while(true){
				try{
					threadGame.join();
				}
				catch(InterruptedException e){
					e.printStackTrace();
				}
				break;
			}
			threadGame=null;
		}

		public void resume(){
			isItOk = true;
			threadGame = new Thread(this);
			threadGame.start();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public boolean onTouch(View v, MotionEvent me) {
		// TODO Auto-generated method stub
		switch(me.getAction()){
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		}
		return true;
	}
}
