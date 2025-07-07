/*
 * 개인프로젝트 - DrawingView.java
 * 개발자 : 컴퓨터공학과 20192161 황도균
 * 20192161@office.deu.ac.kr
 */

package deu.cpt.p20192161;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {
    private float previousX;
    private float previousY;
    private Canvas canvas; // 캔버스
    private Bitmap bitmap; // 종이
    private Paint paint; // 페인트

    public DrawingView(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 초기 점 찍기
                canvas.drawPoint(x, y, paint);
                break;
            case MotionEvent.ACTION_MOVE:
                // 이전 좌표에서 현재 좌표까지 선 그리기
                canvas.drawLine(previousX, previousY, x, y, paint);
                break;
        }
        previousX = x;
        previousY = y;

        invalidate();
        return true;
    }
    public Bitmap getBitmap() {
        Bitmap returnedBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(returnedBitmap);
        draw(canvas);
        return returnedBitmap;
    }

    public void clearCanvas() {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        invalidate();
    }
}
