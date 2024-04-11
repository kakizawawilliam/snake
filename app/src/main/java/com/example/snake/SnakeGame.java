package com.example.snake;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import java.util.Random;

public class SnakeGame extends Activity {
    private static final int WIDTH = 1208;
    private static final int HEIGHT = 2200;
    private static final int UNIT_SIZE = 92;
    private static final int GAME_UNITS = (WIDTH * HEIGHT) / UNIT_SIZE;
    private static final int DELAY = 75;

    private final int[] snakeX = new int[GAME_UNITS];
    private final int[] snakeY = new int[GAME_UNITS];
    private int bodyParts = 3;
    private int foodX;
    private int foodY;
    private char direction = 'R';
    private boolean running = false;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new SnakeView(this));
        startGame();
    }

    private void startGame() {
        newFood();
        running = true;
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                move();
                checkFood();
                checkCollisions();
                setContentView(new SnakeView(SnakeGame.this));
                handler.postDelayed(this, DELAY);
            }
        }, DELAY);
    }

    private void move() {
        for (int i = bodyParts; i > 0; i--) {
            snakeX[i] = snakeX[i - 1];
            snakeY[i] = snakeY[i - 1];
        }

        switch (direction) {
            case 'U':
                snakeY[0] -= UNIT_SIZE;
                break;
            case 'D':
                snakeY[0] += UNIT_SIZE;
                break;
            case 'L':
                snakeX[0] -= UNIT_SIZE;
                break;
            case 'R':
                snakeX[0] += UNIT_SIZE;
                break;
        }

        // 蛇が画面外に出た場合の処理
        if (snakeX[0] >= WIDTH) {
            snakeX[0] = 0;
        } else if (snakeX[0] < 0) {
            snakeX[0] = WIDTH - UNIT_SIZE;
        }

        if (snakeY[0] >= HEIGHT) {
            snakeY[0] = 0;
        } else if (snakeY[0] < 0) {
            snakeY[0] = HEIGHT - UNIT_SIZE;
        }

        // 蛇の座標をタイルのマス目に合わせる
        snakeX[0] = (int) (Math.round(snakeX[0] / (double) UNIT_SIZE) * UNIT_SIZE);
        snakeY[0] = (int) (Math.round(snakeY[0] / (double) UNIT_SIZE) * UNIT_SIZE);
    }


    private void checkFood() {
        if ((snakeX[0] == foodX) && (snakeY[0] == foodY)) {
            bodyParts++;
            newFood();
        }
    }

    private void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if ((snakeX[0] == snakeX[i]) && (snakeY[0] == snakeY[i])) {
                running = false;
            }
        }
        if (snakeX[0] < 0) {
            running = false;
        }
        if (snakeX[0] >= WIDTH) {
            running = false;
        }
        if (snakeY[0] < 0) {
            running = false;
        }
        if (snakeY[0] >= HEIGHT) {
            running = false;
        }
        if (!running) {
            handler.removeCallbacksAndMessages(null);
        }

    }

    private void newFood() {
        Random rand = new Random();
        // ゲームエリア内でランダムな位置に餌を配置
        foodX = rand.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
        foodY = rand.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (direction != 'R') {
                    direction = 'L';
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (direction != 'L') {
                    direction = 'R';
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (direction != 'D') {
                    direction = 'U';
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (direction != 'U') {
                    direction = 'D';
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


    public class SnakeView extends View implements OnTouchListener {
        private Paint paint = new Paint();
        private Bitmap tileBitmap;
        private int tileSize;
        private Bitmap headBitmap;
        private Bitmap bodyBitmap;
        private Bitmap upButtonBitmap;
        private Bitmap downButtonBitmap;
        private Bitmap leftButtonBitmap;
        private Bitmap rightButtonBitmap;

        private int buttonSize; // ボタンのサイズ
        private int buttonPadding; // ボタンの間隔

        public SnakeView(Context context) {
            super(context);
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL);
            setOnTouchListener(this);

            buttonSize = 200; // ボタンのサイズを調整
            buttonPadding = 30; // ボタン間の間隔

            // コントローラボタン
            upButtonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.up_button);
            downButtonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.down_button);
            leftButtonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.left_button);
            rightButtonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.right_button);

            // 画像のサイズを調整
            upButtonBitmap = Bitmap.createScaledBitmap(upButtonBitmap, buttonSize, buttonSize, false);
            downButtonBitmap = Bitmap.createScaledBitmap(downButtonBitmap, buttonSize, buttonSize, false);
            leftButtonBitmap = Bitmap.createScaledBitmap(leftButtonBitmap, buttonSize, buttonSize, false);
            rightButtonBitmap = Bitmap.createScaledBitmap(rightButtonBitmap, buttonSize, buttonSize, false);

            // snake design
            headBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.head);
            bodyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.body);

            // 蛇の頭のサイズを調整
            headBitmap = Bitmap.createScaledBitmap(headBitmap, UNIT_SIZE, UNIT_SIZE, false);

            // 蛇の体のサイズを調整
            bodyBitmap = Bitmap.createScaledBitmap(bodyBitmap, UNIT_SIZE, UNIT_SIZE, false);

            // チェックのタイル画像の読み込み
            Bitmap originalTile = BitmapFactory.decodeResource(getResources(), R.drawable.map);

            // タイルのサイズ
            tileSize = 184; // 適切なサイズに変更する必要があります

            // タイル画像のリサイズ
            tileBitmap = Bitmap.createScaledBitmap(originalTile, tileSize, tileSize, false);
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // タイルを画面全体に描画
            int screenWidth = canvas.getWidth();
            int screenHeight = canvas.getHeight();

            for (int x = 0; x < screenWidth; x += tileSize) {
                for (int y = 0; y < screenHeight; y += tileSize) {
                    canvas.drawBitmap(tileBitmap, x, y, paint);
                }
            }

            // 餌を描画
            if (foodX < 0 || foodX >= screenWidth || foodY < 0 || foodY >= screenHeight) {
                // 餌が画面外にある場合は再設定
                newFood();
            }
            canvas.drawRect(foodX, foodY, foodX + UNIT_SIZE, foodY + UNIT_SIZE, paint);

            // コントローラーボタンを描画
            int buttonLeft = screenWidth / 2 - buttonSize / 2;
            int buttonTop = screenHeight - buttonSize * 2 - buttonPadding * 2;
            canvas.drawBitmap(upButtonBitmap, buttonLeft, buttonTop, paint);

            buttonTop += buttonSize + buttonPadding;
            canvas.drawBitmap(downButtonBitmap, buttonLeft, buttonTop, paint);

            buttonLeft -= buttonSize + buttonPadding;
            buttonTop -= buttonSize + buttonPadding;
            canvas.drawBitmap(leftButtonBitmap, buttonLeft, buttonTop, paint);

            buttonLeft += buttonSize * 2 + buttonPadding * 2;
            canvas.drawBitmap(rightButtonBitmap, buttonLeft, buttonTop, paint);

            // 蛇の頭を描画
            switch (direction) {
                case 'U':
                    headBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.head);
                    break;
                case 'D':
                    headBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.head_down);
                    break;
                case 'L':
                    headBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.head_left);
                    break;
                case 'R':
                    headBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.head_right);
                    break;
            }
            headBitmap = Bitmap.createScaledBitmap(headBitmap, UNIT_SIZE, UNIT_SIZE, false);
            canvas.drawBitmap(headBitmap, snakeX[0], snakeY[0], paint);

            // 蛇の体を描画
            for (int i = 1; i < bodyParts; i++) {
                canvas.drawBitmap(bodyBitmap, snakeX[i], snakeY[i], paint);
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            float touchX = event.getX();
            float touchY = event.getY();

            // タッチイベントがACTION_DOWN（ボタンを押した瞬間）の場合のみ処理を行う
            if (action == MotionEvent.ACTION_DOWN) {
                // 上ボタンをタッチした場合かつ蛇の方向が下向きでない場合
                if (touchX >= getWidth() / 2 - buttonSize / 2 &&
                        touchX <= getWidth() / 2 + buttonSize / 2 &&
                        touchY >= getHeight() - buttonSize * 2 - buttonPadding * 2 &&
                        touchY <= getHeight() - buttonSize * 1 - buttonPadding * 2 && direction != 'D') {
                    direction = 'U'; // 方向を上に設定
                    move(); // 蛇を動かす
                    return true;
                }
                // 下ボタンをタッチした場合かつ蛇の方向が上向きでない場合
                else if (touchX >= getWidth() / 2 - buttonSize / 2 &&
                        touchX <= getWidth() / 2 + buttonSize / 2 &&
                        touchY >= getHeight() - buttonSize - buttonPadding &&
                        touchY <= getHeight() - buttonPadding && direction != 'U') {
                    direction = 'D'; // 方向を下に設定
                    move(); // 蛇を動かす
                    return true;
                }
                // 左ボタンをタッチした場合かつ蛇の方向が右向きでない場合
                else if (touchX >= getWidth() / 2 - buttonSize * 2 - buttonPadding * 2 &&
                        touchX <= getWidth() / 2 - buttonPadding * 2 &&
                        touchY >= getHeight() - buttonSize * 2 - buttonPadding &&
                        touchY <= getHeight() - buttonPadding && direction != 'R') {
                    direction = 'L'; // 方向を左に設定
                    move(); // 蛇を動かす
                    return true;
                }
                // 右ボタンをタッチした場合かつ蛇の方向が左向きでない場合
                else if (touchX >= getWidth() / 2 + buttonPadding &&
                        touchX <= getWidth() / 2 + buttonSize * 2 + buttonPadding * 2 &&
                        touchY >= getHeight() - buttonSize * 2 - buttonPadding &&
                        touchY <= getHeight() - buttonPadding && direction != 'L') {
                    direction = 'R'; // 方向を右に設定
                    move(); // 蛇を動かす
                    return true;
                }
            }

            return true; // イベントが処理されたことを返す
        }







    }


}
