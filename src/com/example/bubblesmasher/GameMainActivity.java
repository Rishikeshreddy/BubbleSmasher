package com.example.bubblesmasher;

import java.util.ArrayList;
import java.util.Random;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;

public class GameMainActivity extends Activity implements OnTouchListener {
	// Create GameView object.
	GameView gameview;
	// Create new bitmap for background image
	Bitmap background = null;
	// Create new Bitmap for bubble.
	Bitmap bubble = null;
	Bitmap bubblelife = null;
	Bitmap bubblespecial = null;
	Bitmap bubbledirty = null;
	Bitmap bubblecurrent = null;
	//Create String arraylist 	
	private static ArrayList<String> tracklist = new ArrayList<String>();

	// Create thread to pause and resume game
	Thread threadGame = null;
	// create float variables for bubble x position and y position
	float bubbleX, bubbleY;
	// to generate random number for random bubblr speed
	int randomY = 20;
	int randomX = 30;
	// to store score
	int Score = 0;
	// to set range of x position
	int speedX = 1;
	int rangeX = 370;
	// to set range of y position
	int speedY = 10;
	int rangeY = 20;
	// to set lives
	int life = 3;
	// to set waterlevel
	int waterlevel = 800;
	// to set bubble rounds
	int bubbleround = 0;
	// check status of bubble
	boolean isbubblerunning = false;
	// to enable and disable touch of bubble
	boolean istouchenable = true;
	//Create int to increase special bubble touch count
	int bubblespecialcount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Make full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// Create new GameView
		gameview = new GameView(this);
		gameview.setOnTouchListener(this);
		// Initialize background bitmap
		// Initialize bubble1 bitmap
		CreateBubbles();
		// Set gameview as ContentView for GameMainActivity
		bubbleX = bubbleY = 0;
		setContentView(gameview);
	}

	//Cretae method to create all bubbles and background bitmap
	public void CreateBubbles() {
		background = BitmapFactory.decodeResource(getResources(), R.drawable.background1);
		bubble = BitmapFactory.decodeResource(getResources(), R.drawable.bubble2);
		bubblelife = BitmapFactory.decodeResource(getResources(), R.drawable.bubblelife);
		bubblespecial = BitmapFactory.decodeResource(getResources(), R.drawable.bubblespecial);
		bubbledirty = BitmapFactory.decodeResource(getResources(), R.drawable.bubbledirty);
		bubblecurrent = bubble;
	}

	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.game_main, menu);
	// return true;
	// }

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

	// Create new SurfaceView class
	public class GameView extends SurfaceView implements Runnable {
		// Create SurfaceHolder to manage SurfaceView Activities
		SurfaceHolder holder;
		// Create boolean to check the status of game
		boolean isItOk = false;

		public GameView(Context context) {
			super(context);
			holder = getHolder();
			// TODO Auto-generated constructor stub
		}

		public void run() {
			if (threadGame == null) {
				threadGame = new Thread(this);
				threadGame.start();
			}
			// threadGame.start();
			while (isItOk == true) {
				// Perform Canvas Drawing
				if (!holder.getSurface().isValid()) {
					continue;
				}
				// Create Canvas to draw bitmaps
				Canvas canvas = holder.lockCanvas();
				GameStarts(canvas);
				holder.unlockCanvasAndPost(canvas);
			}
			// TODO Auto-generated method stub
		}

		public void GameStarts(Canvas canvas)// Game start method
		{
			isbubblerunning = true;
			// Set whole canvas background color
			canvas.drawARGB(255, 255, 255, 255);
			// Create paint to set colors on canvas
			Paint paint = new Paint();
			if (life > 0){
		        speedY = IncreaseSpeedY(speedY,Score);
		        rangeY = IncreaseRangeY(rangeY,Score);
				setGameConditions();
			}
			else {
				bubbleX = -500;
				bubbleY = -500;
				canvas.drawBitmap(bubblecurrent, bubbleX, bubbleY, null);
			   // gameview.pause();
			}

			if (life < 1) {
				bubbleX = -500;
				bubbleY = -500;
				canvas.drawBitmap(bubblecurrent, bubbleX, bubbleY, null);
			}

			float bottomheight = canvas.getHeight() - 1050;
			// Draw background bitmap with rect on canvas
			canvas.drawBitmap(background, new Rect(0, 0, canvas.getWidth(), (int) (canvas.getHeight() - bottomheight)),
					new Rect(0, 0, canvas.getWidth(), (int) (canvas.getHeight() - bottomheight)), paint);

			// Draw bubble bitmap on canvas
			// Log.i("Start drwing bubble", ".....");
			if (bubblecurrent == null) {
				Log.i("bubble null", "yes");
				bubblecurrent = bubble;
			}
			canvas.drawBitmap(bubblecurrent, bubbleX, bubbleY, null);
			// Log.i("Start drwing bubble", "Success");
			paint.setColor(Color.GREEN);
			DrawLifeCircles(canvas, paint);

			paint.setColor(Color.RED);
			paint.setTextSize(50);
			paint.setStyle(Style.FILL);
			paint.setShadowLayer(10f, 10f, 10f, Color.RED);
			// create condition to check score is zero or not. draw score on
			// canvas
			if (life > 0)
				canvas.drawText(String.valueOf(Score), 10, 60, paint);
			else {
				canvas.drawText("Game over \nYour Score is : " + String.valueOf(Score), 10, 60, paint);

				canvas.drawBitmap(bubblecurrent, bubbleX, bubbleY, null);
				// gameview.pause();

			}
			// Set water color to paint
			paint.setColor(Color.rgb(135, 206, 250));
			// Draw waterlevel on canvas
			canvas.drawRect(0, 1050, canvas.getWidth(), canvas.getHeight(), paint);
		}

		// Create method to draw life circles accourding to left lives
		public void DrawLifeCircles(Canvas canvas, Paint paint) {
			// create lives according to left lives
			if (life == 5) {
				canvas.drawCircle(canvas.getWidth() / 1.63f, canvas.getHeight() / 25, 15, paint);
				canvas.drawCircle(canvas.getWidth() / 1.43f, canvas.getHeight() / 25, 15, paint);
				canvas.drawCircle(canvas.getWidth() / 1.27f, canvas.getHeight() / 25, 15, paint);
				canvas.drawCircle(canvas.getWidth() / 1.15f, canvas.getHeight() / 25, 15, paint);
				canvas.drawCircle(canvas.getWidth() / 1.05f, canvas.getHeight() / 25, 15, paint);
			} else if (life == 4) {
				canvas.drawCircle(canvas.getWidth() / 1.43f, canvas.getHeight() / 25, 15, paint);
				canvas.drawCircle(canvas.getWidth() / 1.27f, canvas.getHeight() / 25, 15, paint);
				canvas.drawCircle(canvas.getWidth() / 1.15f, canvas.getHeight() / 25, 15, paint);
				canvas.drawCircle(canvas.getWidth() / 1.05f, canvas.getHeight() / 25, 15, paint);
			} else if (life == 3) {
				canvas.drawCircle(canvas.getWidth() / 1.27f, canvas.getHeight() / 25, 15, paint);
				canvas.drawCircle(canvas.getWidth() / 1.15f, canvas.getHeight() / 25, 15, paint);
				canvas.drawCircle(canvas.getWidth() / 1.05f, canvas.getHeight() / 25, 15, paint);
			} else if (life == 2) {
				paint.setColor(Color.YELLOW);
				canvas.drawCircle(canvas.getWidth() / 1.15f, canvas.getHeight() / 25, 15, paint);
				canvas.drawCircle(canvas.getWidth() / 1.05f, canvas.getHeight() / 25, 15, paint);
			} else if (life == 1) {
				paint.setColor(Color.RED);
				canvas.drawCircle(canvas.getWidth() / 1.05f, canvas.getHeight() / 25, 15, paint);
			} else if (life == 0)
				isbubblerunning = false;
		}
		
		//Create method to improve speed when score improves
		public int IncreaseSpeedY(int speedy,int score){
			if (score>50)
				return speedy+5;
			else if(score>100)
				return speedy+10;
			else if (score>200)
				return speedy+15;
			else if (score>500)
				return speedy+25;
			return speedy;
		}
		
		//Create method to improve range of speed when score improves
        public int IncreaseRangeY(int rangey,int score){
        	if (score>50)
				return rangey+5;
			else if(score>100)
				return rangey+10;
			else if (score>200)
				return rangey+15;
			else if (score>500)
				return rangey+25;
			return rangey;
		}

		//Create method to set various game conditions
		public void setGameConditions() {
			// check bubble y position is less then waterlevel or not
			if (bubbleY < waterlevel && life > 0 && isbubblerunning) {
				bubbleY = bubbleY + randomY;
			} else {
				bubbleY = 0;
				if (life < 1) {
					isbubblerunning = false;
					bubbleX = -500;
					bubbleY = -500;
				} else
					tracklist = TrackGame(bubbleround, bubblecurrent, tracklist);
				// create condition to check current bubble is dirty bubble or life then dont decrease life
				if (bubblecurrent == bubble || bubblecurrent == bubblespecial)
					life--;
				
				// increase bubble rounds
				bubbleround++;

				// Choosebubble
				bubblecurrent = Choosebubble(bubbleround, life, Score);
				// generate a number to set bubble speed
				randomY = (int) ((Math.random() * rangeY) + speedY);
				// generate a number to set x position of bubble
				randomX = (int) ((Math.random() * rangeX) + speedX);
				bubbleX = randomX;

			}
		}
       
		//Create method to store every bubble name in sequence it comes
		public ArrayList<String> TrackGame(int round, Bitmap bitmap, ArrayList<String> list) {
			String str = Integer.toString(round);
			if (bitmap == bubble)
				list.add("bubble");
			else if (bitmap == bubblespecial)
				list.add("special");
			else if (bitmap == bubbledirty)
				list.add("dirty");
			else if (bitmap == bubblelife)
				list.add("life");
			Log.i("TrackGame", " Save into arraylist ");
			if (round % 50 == 0) {
				for (String strtmp : list)
					Log.i("Track list", strtmp);
			}
			return list;
		}
		
        // Create method which choose next bubble for game 
		public Bitmap Choosebubble(int rounds, int lives, int score) {
			Bitmap tmpbit = null;
			int limit = 0;

			Random r = new Random();
			int Low = 0;
			int High = 5;
			int bubblenum = r.nextInt(High - Low) + Low;
			Log.i("Random Number", "" + bubblenum + "");
            
			//Create method if rounds are more then five then they start choosing the other bubbles
			if (rounds > 5) {
				if (bubblenum == 2) {
					limit = Choosebubblerotationlimits(rounds, "dirty", score);
					Log.i("Choose bubble dirty process ...  ", " Random number " + bubblenum + " Limit " + limit + " ");
					if (Choosebubblevarification(tracklist, "dirty", limit)) {
						Log.i("Choose bubble dirty verified ...  ",
								" Random number " + bubblenum + " Limit " + limit + " ");
						tmpbit = bubbledirty;
					} else {
						Log.i("Choose bubble dirty failed ...  ",
								" Random number " + bubblenum + " Limit " + limit + " ");
						Choosebubble(rounds, lives, score);
					}
				} else if (bubblenum == 3 && rounds >= 10) {
					limit = Choosebubblerotationlimits(rounds, "special", score);
					Log.i("Choose bubble special process ...  ",
							" Random number " + bubblenum + " Limit " + limit + " ");
					if (Choosebubblevarification(tracklist, "special", limit)) {
						Log.i("Choose bubble special verified ...  ",
								" Random number " + bubblenum + " Limit " + limit + " ");
						tmpbit = bubblespecial;
					} else {
						Log.i("Choose bubble special failed ...  ",
								" Random number " + bubblenum + " Limit " + limit + " ");
						Choosebubble(rounds, lives, score);
					}
				} else if (bubblenum == 1) {
					Log.i("Choose bubble verified  ", " no num");
					tmpbit = bubble;
				} else if (bubblenum == 4 && rounds >= 15 && lives <= 4) {
					limit = Choosebubblerotationlimits(rounds, "life", score);
					Log.i("Choose bubble life process ...  ", " Random number " + bubblenum + " Limit " + limit + " ");
					if (Choosebubblevarification(tracklist, "life", limit)) {
						Log.i("Choose bubble life verified ...  ",
								" Random number " + bubblenum + " Limit " + limit + " ");
						tmpbit = bubblelife;
					} else {
						Log.i("Choose bubble life failed ...  ",
								" Random number " + bubblenum + " Limit " + limit + " ");
						Choosebubble(rounds, lives, score);
					}

				} else {
					Log.i("Choose bubble verified  ", " no num");
					tmpbit = bubble;
				}
			} else
				tmpbit = bubble;
			return tmpbit;
		}

		// Create method to check chosen bubble came before and if yes then check when came and return true or false according to their limits 
		public boolean Choosebubblevarification(ArrayList<String> list, String strbubblename, int checklimits) {
			if (list.size() < checklimits) {
				checklimits = list.size();
			} else
				Log.i("BubbleVarification for " + strbubblename + "", "checklimits are more then ");

			for (int i = list.size() - 1; i > (list.size() - 1) - checklimits; i--) {
				Log.i("bubble varification",
						"checklimits : " + checklimits + ", bubblename: " + strbubblename + ", checking limits: "
								+ ((list.size() - 1) - checklimits) + ", list size: " + (list.size() - 1) + "");
				if (strbubblename.equals(list.get(i))) {
					Log.i("BubbleVarification for " + strbubblename + "", " failed ");
					return false;
				}
			}
			return true;
		}

		//Decrease the possibilities to come for life and special bubble and increase the possibilities to come dirty bubble when score increases
		public int Choosebubblerotationlimits(int rounds, String strbubblename, int score) {
			if (strbubblename.equals("dirty")) {
				if (score > 50)
					return 4;
				else if (score > 100)
					return 3;
				else if (score > 200)
					return 2;
				else if (score > 500)
					return 1;
				else
					return 5;
			} else if (strbubblename.equals("special")) {
				if (score > 50)
					return 10;
				else if (score > 100)
					return 15;
				else if (score > 200)
					return 20;
				else if (score > 500)
					return 30;
				else
					return 10;
			} else if (strbubblename.equals("life")) {
				if (score > 50)
					return 15;
				else if (score > 100)
					return 20;
				else if (score > 200)
					return 25;
				else if (score > 500)
					return 50;
				else
					return 15;
			}
			return 0;
		}

		public void pause() {
			isItOk = false;
			while (true) {
				try {
					threadGame.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}
			threadGame = null;
		}

		public void resume() {
			isItOk = true;
			if (threadGame == null) {
				threadGame = new Thread(this);
				threadGame.start();
			}
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

		switch (me.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// Get x and y points of touch when user's finger touch on screen
			// Log.i("Touch Points", "X : " + touchX + " Y: " + touchY);
			// Getbubblelocation(bubbleX,bubbleY);
			break;
		case MotionEvent.ACTION_UP:
			Getbubblelocation(bubbleX, bubbleY);
			if (isTouchinRadius(me.getX(), me.getY(), bubbleX, bubbleY, bubble) && istouchenable) {
				if (bubblecurrent == bubblespecial)
					istouchenable = true;
				else
					istouchenable = false;
				TouchChanges();
			}
			// Get x and y points of touch when user take away finger from srenn
			// Log.i("Touch Points", "X : " + touchX + " Y: " + touchY);
			break;
		case MotionEvent.ACTION_MOVE:
			// Get x and y points of touch when user move finger from one point
			// to another
			// Log.i("Touch Points", "X : " + touchX + " Y: " + touchY);
			// Getbubblelocation(bubbleX,bubbleY);
			break;
		}
		return true;
	}

	public void Getbubblelocation(float bubbleX, float bubbleY) {
		Log.i("Bubble Location when user touch ", "bubbleX: " + bubbleX + " bubbleY: " + bubbleY);
	}

	// create method to check touch is on bubble radius or not
	public boolean isTouchinRadius(float xPoint, float yPoint, float bubbleX, float bubbleY, Bitmap objbubble) {
		float xEnd = bubbleX + objbubble.getWidth();
		float yEnd = bubbleY + objbubble.getHeight();
		Log.i("Touch Points", "Touch x = " + xPoint + " Touch y = " + yPoint + " x = " + bubbleX + " y = " + bubbleY
				+ " x end = " + xEnd + " y end = " + yEnd);
		if (xPoint >= bubbleX && xPoint <= xEnd) {
			if (yPoint >= bubbleY && yPoint <= yEnd) {
				return true;
			} else
				return false;
		} else
			return false;
	}

	// if touch is on bubble radius then make bubble disappear and make new bubble to come from top
	public void TouchChanges() {
		//Create condition for special bubble
		if (bubblecurrent == bubblespecial && bubblespecialcount < 3) {
			bubblespecialcount++;
			Log.i("Special bubble touch", " Count: " + bubblespecialcount + "");
		} else {
			bubbleY = -100;
			randomY = (int) ((Math.random() * rangeY) + speedY);
			randomX = (int) ((Math.random() * rangeX) + speedY);
			bubbleX = randomX;
			//Create condition if current bubble is special bubble then score increase by 5 and if current bubble is dirty bubble the score decrease by 2
			if (bubblecurrent == bubblespecial && bubblespecialcount == 3) {
				Score = Score + 5;
				bubblespecialcount = 0;
			} else if (bubblecurrent == bubblelife)
				life = life + 1;
			else if (bubblecurrent == bubbledirty){
				life--;
				if(life==0){
					bubbleX=-500;
					bubbleY=-500;
				}
			}
			else
				Score++;
			bubbleround++;
				istouchenable = true;
			//Store bubble names into arraylist
			tracklist = gameview.TrackGame(bubbleround, bubblecurrent, tracklist);
			//Choose new bubble
			bubblecurrent = gameview.Choosebubble(bubbleround, life, Score);
			Log.i("Score : ", String.valueOf(Score));
			try {
				Thread.sleep(00);
			
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}