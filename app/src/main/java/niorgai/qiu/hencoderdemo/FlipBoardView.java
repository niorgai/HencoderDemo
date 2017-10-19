package niorgai.qiu.hencoderdemo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by jianqiu on 10/14/17.
 */
public class FlipBoardView extends View {

    private static final long DURATION_ONE = 1000;
    private static final long DURATION_TWO = 1500;
    private static final long DURATION_THREE = 1000;

    private Bitmap mBitmap;
    private Paint mPaint;
    private Camera mCamera;

    private Point mCenterPoint;
    private ValueAnimator mRightDegreeAnimator;
    private int rightDegree;
    private ValueAnimator mSweepAnimator;
    private int sweep;
    private RectF sweepRectF;
    private Path sweepPath;
    private ValueAnimator mTopDegreeAnimator;
    private int topDegree;

    public FlipBoardView(Context context) {
        this(context, null);
    }

    public FlipBoardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlipBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_flip_board);
        mPaint = new Paint();
        mCamera = new Camera();
        mCenterPoint = new Point();
        mRightDegreeAnimator = ValueAnimator.ofInt(0, 45)
                .setDuration(DURATION_ONE);
        mRightDegreeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                rightDegree = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        mRightDegreeAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mSweepAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mRightDegreeAnimator.start();

        mSweepAnimator = ValueAnimator.ofInt(270, 0)
                .setDuration(DURATION_TWO);
        mSweepAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mSweepAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sweep = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        mSweepAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mTopDegreeAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mTopDegreeAnimator = ValueAnimator.ofInt(0, 45)
                .setDuration(DURATION_THREE);
        mTopDegreeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                topDegree = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        mTopDegreeAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mRightDegreeAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        sweepPath = new Path();
        sweepRectF = new RectF(0, 0, getWidth(), getHeight());
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        int bitmapWidth = mBitmap.getWidth();
        int bitmapHeight = mBitmap.getHeight();
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        mCenterPoint.x = centerX - bitmapWidth / 2;
        mCenterPoint.y = centerY - bitmapHeight / 2;

        if (mRightDegreeAnimator.isRunning()) {

            //左边
            canvas.save();
            canvas.clipRect(0, 0, centerX, getHeight());
            canvas.drawBitmap(mBitmap, mCenterPoint.x, mCenterPoint.y, mPaint);
            canvas.restore();

            //右边
            canvas.save();
            canvas.clipRect(centerX, 0, getWidth(), getHeight());
            mCamera.save();
            mCamera.rotateY(-rightDegree);
            canvas.translate(centerX, centerY);
            mCamera.applyToCanvas(canvas);
            canvas.translate(-centerX, -centerY);
            mCamera.restore();
            canvas.drawBitmap(mBitmap, mCenterPoint.x, mCenterPoint.y, mPaint);
            canvas.restore();
        }

        if (mSweepAnimator.isRunning()) {
            canvas.save();
            sweepPath.reset();
            sweepRectF.set(0, 0, getWidth(), getHeight());
            sweepPath.addArc(sweepRectF, sweep, 180);
            sweepPath.lineTo(centerX, centerY);
            sweepPath.close();
            canvas.clipPath(sweepPath, Region.Op.DIFFERENCE);
            canvas.drawBitmap(mBitmap, mCenterPoint.x, mCenterPoint.y, mPaint);
            canvas.restore();

            canvas.save();
            canvas.clipPath(sweepPath);
            mCamera.save();
            mCamera.rotate(getRotateXBySweep(sweep), getRotateYBySweep(sweep), 0);
            canvas.translate(centerX, centerY);
            mCamera.applyToCanvas(canvas);
            canvas.translate(-centerX, -centerY);
            mCamera.restore();
            canvas.drawBitmap(mBitmap, mCenterPoint.x, mCenterPoint.y, mPaint);
            canvas.restore();
        }

        if (mTopDegreeAnimator.isRunning()) {
            //上面
            canvas.save();
            canvas.clipRect(0, 0, getWidth(), centerY);
            mCamera.save();
            mCamera.rotateX(-topDegree);
            canvas.translate(centerX, centerY);
            mCamera.applyToCanvas(canvas);
            mCamera.restore();
            canvas.translate(-centerX, -centerY);
            canvas.drawBitmap(mBitmap, mCenterPoint.x, mCenterPoint.y, mPaint);
            canvas.restore();

            //下面
            canvas.save();
            canvas.clipRect(0, centerY, getWidth(), getHeight());
            mCamera.save();
            mCamera.rotateX(45);
            canvas.translate(centerX, centerY);
            mCamera.applyToCanvas(canvas);
            canvas.translate(-centerX, -centerY);
            mCamera.restore();
            canvas.drawBitmap(mBitmap, mCenterPoint.x, mCenterPoint.y, mPaint);
            canvas.restore();
        }
    }

    private float getRotateXBySweep(float sweep) {
        if (sweep <= 0) {
            return sweep / 2 + 45;
        } else if (sweep < 180) {
            return sweep / -2f + 45f;
        } else {
            return sweep / 2 - 135f;
        }
    }

    private float getRotateYBySweep(float sweep) {
        if (sweep <= 90) {
            return sweep / 2;
        } else {
            return sweep / -2f + 90f;
        }
    }
}
