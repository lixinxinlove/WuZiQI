package com.love.lixinxin.wuzhiqi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * 版    权:
 * 版    本：1.0
 * 创建时间: on 2016/4/2-8:48
 * 作    者: 李鑫鑫
 * 描    述: 自定义View
 */
public class WuZiQi extends View {

    private int mPanelWidth;
    private float mLineHeight;
    private int MAX_LINE = 10;
    private int MAX_COUNT_IN_LINE = 5;    //五子棋

    private Paint mPaint = new Paint();

    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;

    private float ratioPieceOfLineHeight = 3 * 1.0f / 4;

    private boolean mIsWhite = true;    //指向 白棋先下  或轮到白棋了
    private ArrayList<Point> mWhiteArray = new ArrayList<>();   //放白棋的坐标
    private ArrayList<Point> mBlackArray = new ArrayList<>();   //放黑棋的坐标

    private boolean mIsGameOver;
    private boolean mIsWhiteWinner;

    public WuZiQi(Context context, AttributeSet attrs) {
        super(context, attrs);
      //  setBackgroundColor(0x70ff0000);
        init();
    }

    private void init() {

        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.mipmap.stone_w2);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.mipmap.stone_b1);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize, heightSize);

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }

        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE;

        int pieceWidth = (int) (mLineHeight * ratioPieceOfLineHeight);

        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mIsGameOver) {
            return false;
        }
        int action = event.getAction();

        if (action == event.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point p = getValidPoint(x, y);

            if (mWhiteArray.contains(p) || mBlackArray.contains(p)) {
                return false;
            }

            if (mIsWhite) {
                mWhiteArray.add(p);
            } else {
                mBlackArray.add(p);
            }
            invalidate();
            mIsWhite = !mIsWhite;
        }

        return true;
    }

    private Point getValidPoint(int x, int y) {
        return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoart(canvas);
        drawPieces(canvas);
        checkGameOver();

    }

    /**
     * 判断游戏是否结束
     */
    private void checkGameOver() {
        boolean whiteWin = checkFiveInLine(mWhiteArray);
        boolean blackWin = checkFiveInLine(mBlackArray);

        if (whiteWin || blackWin) {
            mIsGameOver = true;
            mIsWhiteWinner = whiteWin;

            String text = mIsWhiteWinner ? "白棋胜" : "黑棋胜";

            Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkFiveInLine(ArrayList<Point> points) {

        boolean win = false;
        for (Point p : points) {
            int x = p.x;
            int y = p.y;
            win = checkHorizontal(x, y, points);
        }


        return win;
    }

    /**
     * 判断棋子横向是否胜利
     *
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkHorizontal(int x, int y, ArrayList<Point> points) {

        int count = 1;
        //左
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == 5) {
            return true;
        }
        //右
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y))) {
                count++;
            } else {
                break;
            }
        }

        if (count == 5) {
            return true;
        }

        return false;
    }

    /**
     * 画棋子
     *
     * @param canvas
     */
    private void drawPieces(Canvas canvas) {

        for (int i = 0, n = mWhiteArray.size(); i < n; i++) {
            Point whitePoint = mWhiteArray.get(i);
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight,
                    (whitePoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight, null);
        }

        for (int i = 0, n = mBlackArray.size(); i < n; i++) {
            Point blackPoint = mBlackArray.get(i);
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight,
                    (blackPoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight, null);
        }
    }

    /**
     * 画棋盘
     *
     * @param canvas
     */
    private void drawBoart(Canvas canvas) {
        int w = mPanelWidth;
        float lintHeight = mLineHeight;

        for (int i = 0; i < MAX_LINE; i++) {
            int startX = (int) (lintHeight / 2);
            int endX = (int) (w - lintHeight / 2);
            int y = (int) ((0.5 + i) * lintHeight);
            canvas.drawLine(startX, y, endX, y, mPaint);
            canvas.drawLine(y, startX, y, endX, mPaint);
        }

    }


    public void start(){
        mWhiteArray.clear();
        mBlackArray.clear();
        mIsGameOver=false;
        mIsWhiteWinner=false;
        invalidate();
    }


    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVWE = "instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";

    //保存数据
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundel = new Bundle();
        bundel.putParcelable(INSTANCE, super.onSaveInstanceState());
        bundel.putBoolean(INSTANCE_GAME_OVWE, mIsGameOver);
        bundel.putParcelableArrayList(INSTANCE_WHITE_ARRAY, mWhiteArray);
        bundel.putParcelableArrayList(INSTANCE_BLACK_ARRAY, mBlackArray);
        return bundel;
    }

    //回复数据
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {

            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            mIsGameOver = bundle.getBoolean(INSTANCE_GAME_OVWE);
            mWhiteArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);

            // invalidate();

            return;
        }
        super.onRestoreInstanceState(state);


    }
}










