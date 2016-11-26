package com.bruce.circlelayout_w.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.bruce.circlelayout_w.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

public class CircleLayout extends ViewGroup {
	private OnItemClickListener onItemClickListener = null;
	private OnItemSelectedListener onItemSelectedListener = null;
	private OnCenterClickListener onCenterClickListener = null;
	private OnRotationFinishedListener onRotationFinishedListener = null;

	private OnMoveLeftListener onMoveLeftListener = null;

	private Bitmap imageOriginal, imageScaled;
	private Matrix matrix;

	private int circleWidth, circleHeight;
	private int radius = 0;

	private int maxChildWidth = 0;
	private int maxChildHeight = 0;
	private int childWidth = 0;
	private int childHeight = 0;

	private GestureDetector gestureDetector;
	private boolean[] quadrantTouched;

	private int speed = 25;
	private float angle = 90;
	private float firstChildPos = 90;
	private boolean isRotating = true;

	private int tappedViewsPostition = -1;
	private View tappedView = null;

	private int selected = 0;

	private ObjectAnimator animator;

	private boolean hasMove = false;

	public CircleLayout(Context context) {
		this(context, null);
	}

	public CircleLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CircleLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return super.onInterceptTouchEvent(ev);
	}

	protected void init(AttributeSet attrs) {
		gestureDetector = new GestureDetector(getContext(), new MyGestureListener());
		quadrantTouched = new boolean[] { false, false, false, false, false };

		if (attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CircleLayout);

			angle = a.getInt(R.styleable.CircleLayout_firstChildPosition, (int) angle);
			firstChildPos = angle;

			speed = a.getInt(R.styleable.CircleLayout_speed, speed);
			isRotating = a.getBoolean(R.styleable.CircleLayout_isRotating, isRotating);

			if (imageOriginal == null) {
				int picId = a.getResourceId(R.styleable.CircleLayout_circleBackground, -1);

				if (picId != -1) {
					imageOriginal = BitmapFactory.decodeResource(getResources(), picId);
				}
			}

			a.recycle();

			// Initialize the matrix only once
			if (matrix == null) {
				matrix = new Matrix();
			} else {
				matrix.reset();
			}
			setWillNotDraw(false);
		}
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle % 360;
		setChildAngles();
	}

	public View getSelectedItem() {
		return (selected >= 0) ? getChildAt(selected) : null;
	}

	public int getSelected() {
		return selected;
	}

	public void setSelected(int position) {
		this.selected = position;
		setChildAngles();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// The sizes of the ViewGroup
		circleHeight = getHeight();
		circleWidth = getWidth();

		if (imageOriginal != null) {
			// Scaling the size of the background image
			if (imageScaled == null) {
				float sx = (((radius + childWidth / 4) * 2) / (float) imageOriginal.getWidth());
				float sy = (((radius + childWidth / 4) * 2) / (float) imageOriginal.getHeight());

				matrix = new Matrix();
				matrix.postScale(sx, sy);
				imageScaled = Bitmap.createBitmap(imageOriginal, 0, 0, imageOriginal.getWidth(), imageOriginal.getHeight(), matrix, false);
			}

			if (imageScaled != null) {
				// Move the background to the center
				int cx = (circleWidth - imageScaled.getWidth()) / 2;
				int cy = (circleHeight - imageScaled.getHeight()) / 2;

				Canvas g = canvas;
				canvas.rotate(0, circleWidth / 2, circleHeight / 2);
				g.drawBitmap(imageScaled, cx, cy, null);
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		maxChildWidth = 0;
		maxChildHeight = 0;

		int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);
		int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() == GONE) {
				continue;
			}
			child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

			maxChildWidth = Math.max(maxChildWidth, child.getMeasuredWidth());
			maxChildHeight = Math.max(maxChildHeight, child.getMeasuredHeight());
		}
		childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(maxChildWidth, MeasureSpec.EXACTLY);
		childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(maxChildHeight, MeasureSpec.EXACTLY);

		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() == GONE) {
				continue;
			}
			child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
		}
		setMeasuredDimension(resolveSize(maxChildWidth, widthMeasureSpec), resolveSize(maxChildHeight, heightMeasureSpec));
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int layoutWidth = r - l;
		int layoutHeight = b - t;
		final int childCount = getChildCount();
		int left, top;
		radius = (layoutWidth <= layoutHeight) ? layoutWidth / 3 : layoutHeight / 3;

		childWidth = (int) (radius / 1.6);
		childHeight = (int) (radius / 1.6);

		float angleDelay = 360.0f / getChildCount();

		for (int i = 0; i < childCount; i++) {
			final CircleImageView child = (CircleImageView) getChildAt(i);
			if (child.getVisibility() == GONE) {
				continue;
			}

			if (angle > 360) {
				angle -= 360;
			} else {
				if (angle < 0) {
					angle += 360;
				}
			}
			child.setAngle(angle);
			child.setPosition(i);
			left = Math.round((float) (((layoutWidth / 2) - childWidth / 2) + radius * Math.cos(Math.toRadians(angle))));
			top = Math.round((float) (((layoutHeight / 2) - childHeight / 2) + radius * Math.sin(Math.toRadians(angle))));

			child.layout(left, top, left + childWidth, top + childHeight);
			angle += angleDelay;
		}
	}

	private void rotateViewToCenter(CircleImageView view) {
		if (isRotating) {
			float destAngle = (float) (firstChildPos - view.getAngle());

			if (destAngle < 0) {
				destAngle += 360;
			}

			if (destAngle > 180) {
				destAngle = -1 * (360 - destAngle);
			}
			animateTo(angle + destAngle, 5000 / speed);
		}
	}

	public void rotateViewToCenter(int i) {
		if (i < 0 || i > getChildCount()) {
			return;
		}
		if (!hasMove) {
			moveToLeft();
			enlargeView();
		}
		CircleImageView child = (CircleImageView) getChildAt(i);
		rotateViewToCenter(child);
	}

	private void rotateButtons(float degrees) {
		angle += degrees;
		setChildAngles();
	}

	private void animateTo(float endDegree, long duration) {
		if (animator != null && animator.isRunning() || Math.abs(angle - endDegree) < 1) {
			return;
		}

		animator = ObjectAnimator.ofFloat(CircleLayout.this, "angle", angle, endDegree);
		animator.setDuration(duration);
		animator.setInterpolator(new DecelerateInterpolator());
		animator.addListener(new AnimatorListener() {
			private boolean wasCanceled = false;

			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if (wasCanceled) {
					return;
				}

				if (onRotationFinishedListener != null) {
					CircleImageView view = (CircleImageView) getSelectedItem();
					onRotationFinishedListener.onRotationFinished(view, view.getName());
				}
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				wasCanceled = true;
			}
		});
		animator.start();
	}

	private void stopAnimation() {
		if (animator != null && animator.isRunning()) {
			animator.cancel();
			animator = null;
		}
	}

	private void setChildAngles() {
		if (!hasMove) {
			moveToLeft();
			enlargeView();
		}
		int left, top, childCount = getChildCount();
		float angleDelay = 360.0f / childCount;
		float localAngle = angle;

		for (int i = 0; i < childCount; i++) {
			if (localAngle > 360) {
				localAngle -= 360;
			} else {
				if (localAngle < 0) {
					localAngle += 360;
				}
			}

			final CircleImageView child = (CircleImageView) getChildAt(i);
			if (child.getVisibility() == GONE) {
				continue;
			}
			left = Math.round((float) (((circleWidth / 2) - childWidth / 2) + radius * Math.cos(Math.toRadians(localAngle))));
			top = Math.round((float) (((circleHeight / 2) - childHeight / 2) + radius * Math.sin(Math.toRadians(localAngle))));

			child.setAngle(localAngle);

			float distance = Math.abs(localAngle - firstChildPos);
			float halfangleDelay = angleDelay / 2;
			boolean isFirstItem = distance < halfangleDelay || distance > (360 - halfangleDelay);
			if (isFirstItem && selected != child.getPosition()) {
				selected = child.getPosition();
				if (onItemSelectedListener != null && isRotating) {
					onItemSelectedListener.onItemSelected(child, child.getName());
				}
			}
			if (selected == child.getPosition()) {
				child.enlargeView();
			} else {
				child.shrinkView();
			}
			child.layout(left, top, left + childWidth, top + childHeight);
			localAngle += angleDelay;
		}

	}

	/**
	 * @return The angle of the unit circle with the image views center
	 */
	private double getPositionAngle(double xTouch, double yTouch) {
		double x = xTouch - (circleWidth / 2d);
		double y = circleHeight - yTouch - (circleHeight / 2d);

		switch (getPositionQuadrant(x, y)) {
		case 1:
			return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
		case 2:
		case 3:
			return 180 - (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
		case 4:
			return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
		default:
			return 0;
		}
	}

	/**
	 * @return The quadrant of the position
	 */
	private static int getPositionQuadrant(double x, double y) {
		if (x >= 0) {
			return y >= 0 ? 1 : 4;
		} else {
			return y >= 0 ? 2 : 3;
		}
	}

	// Touch helpers
	private double touchStartAngle;
	private boolean didMove = false;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isEnabled()) {
			gestureDetector.onTouchEvent(event);
			if (isRotating) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					for (int i = 0; i < quadrantTouched.length; i++) {
						quadrantTouched[i] = false;
					}
					stopAnimation();
					touchStartAngle = getPositionAngle(event.getX(), event.getY());
					didMove = false;
					break;
				case MotionEvent.ACTION_MOVE:
					double currentAngle = getPositionAngle(event.getX(), event.getY());
					rotateButtons((float) (touchStartAngle - currentAngle));
					touchStartAngle = currentAngle;
					didMove = true;
					break;
				case MotionEvent.ACTION_UP:
					if (didMove) {
						rotateViewToCenter((CircleImageView) getChildAt(selected));
					}
					break;
				}
			}
			quadrantTouched[getPositionQuadrant(event.getX() - (circleWidth / 2), circleHeight - event.getY() - (circleHeight / 2))] = true;
			return true;
		}
		return false;
	}

	private class MyGestureListener extends SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (!isRotating) {
				return false;
			}
			// get the quadrant of the start and the end of the fling
			int q1 = getPositionQuadrant(e1.getX() - (circleWidth / 2), circleHeight - e1.getY() - (circleHeight / 2));
			int q2 = getPositionQuadrant(e2.getX() - (circleWidth / 2), circleHeight - e2.getY() - (circleHeight / 2));

			if ((q1 == 2 && q2 == 2 && Math.abs(velocityX) < Math.abs(velocityY))
					|| (q1 == 3 && q2 == 3)
					|| (q1 == 1 && q2 == 3)
					|| (q1 == 4 && q2 == 4 && Math.abs(velocityX) > Math.abs(velocityY))
					|| ((q1 == 2 && q2 == 3) || (q1 == 3 && q2 == 2))
					|| ((q1 == 3 && q2 == 4) || (q1 == 4 && q2 == 3))
					|| (q1 == 2 && q2 == 4 && quadrantTouched[3])
					|| (q1 == 4 && q2 == 2 && quadrantTouched[3])) {
				// the inverted rotations
				animateTo(getCenteredAngle(angle - (velocityX + velocityY) / 25), 25000 / speed);
			} else {
				// the normal rotation
				animateTo(getCenteredAngle(angle + (velocityX + velocityY) / 25), 25000 / speed);
			}

			return true;
		}

		private float getCenteredAngle(float angle) {
			float angleDelay = 360 / getChildCount();
			float localAngle = angle % 360;

			if (localAngle < 0) {
				localAngle = 360 + localAngle;
			}

			for (float i = firstChildPos; i < firstChildPos + 360; i += angleDelay) {
				float locI = i % 360;
				float diff = localAngle - locI;
				if (Math.abs(diff) < angleDelay / 2) {
					angle -= diff;
					break;
				}
			}

			return angle;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			tappedViewsPostition = pointToPosition(e.getX(), e.getY());
			if (tappedViewsPostition >= 0) {
				tappedView = getChildAt(tappedViewsPostition);
				tappedView.setPressed(true);
			} else {
				float centerX = circleWidth / 2;
				float centerY = circleHeight / 2;

				if (e.getX() < centerX + (childWidth / 2)
						&& e.getX() > centerX - childWidth / 2
						&& e.getY() < centerY + (childHeight / 2)
						&& e.getY() > centerY - (childHeight / 2)) {
					if (onCenterClickListener != null) {
						onCenterClickListener.onCenterClick();
						return true;
					}
				}
			}

			if (tappedView != null) {
				CircleImageView view = (CircleImageView) (tappedView);
				if (selected == tappedViewsPostition) {
					if (onItemClickListener != null) {
						onItemClickListener.onItemClick(tappedView, view.getName());
					}
				} else {
					rotateViewToCenter(view);
					if (!isRotating) {
						if (onItemSelectedListener != null) {
							onItemSelectedListener.onItemSelected(tappedView, view.getName());
						}

						if (onItemClickListener != null) {
							onItemClickListener.onItemClick(tappedView, view.getName());
						}
					}
				}
				return true;
			}
			return super.onSingleTapUp(e);
		}

		private int pointToPosition(float x, float y) {
			for (int i = 0; i < getChildCount(); i++) {
				View item = (View) getChildAt(i);
				if (item.getLeft() < x && item.getRight() > x
						& item.getTop() < y && item.getBottom() > y) {
					return i;
				}
			}
			return -1;
		}
	}

	public void moveToLeft() {

		float distance = getLeft() + getWidth() / 2 - 50;
		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "TranslationX", -distance).setDuration(100);
		objectAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator arg0) {

			}

			@Override
			public void onAnimationRepeat(Animator arg0) {

			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				hasMove = true;
				if (onMoveLeftListener != null) {
					onMoveLeftListener.onMove(getSelectedName());
				}
			}

			@Override
			public void onAnimationCancel(Animator arg0) {

			}
		});
		objectAnimator.start();
	}

	public void resetView() {
		if (!hasMove)
			return;
		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "TranslationX", 15).setDuration(150);
		objectAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				hasMove = false;
			}
		});
		objectAnimator.start();
		ViewHelper.setScaleX(this, 1f);
		ViewHelper.setScaleY(this, 1f);
		((CircleImageView) getChildAt(selected)).shrinkView();
		for (int i = 0; i < getChildCount(); i++) {
			((CircleImageView) getChildAt(i)).initView();
		}
	}

	public boolean hasMove() {
		return hasMove;
	}

	public String getSelectedName() {
		CircleImageView iv = (CircleImageView) getChildAt(selected);
		return iv.getName();
	}

	private float getParentHeight() {
		View view = (View) getParent();
		return (float) view.getHeight() + 100;
	}

	public void enlargeView() {
		CircleImageView circleImageView = (CircleImageView) getSelectedItem();
		circleImageView.enlargeView();
		for (int i = 0; i < getChildCount(); i++) {
			if (i != selected)
				((CircleImageView) getChildAt(i)).shrinkView();
		}
		float scale = getParentHeight() / (float) getWidth() * 1.1F;
		ViewHelper.setScaleX(this, scale);
		ViewHelper.setScaleY(this, scale);
	}

	public interface OnItemClickListener {
		void onItemClick(View view, String name);
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public interface OnItemSelectedListener {
		void onItemSelected(View view, String name);
	}

	public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
		this.onItemSelectedListener = onItemSelectedListener;
	}

	public interface OnCenterClickListener {
		void onCenterClick();
	}

	public void setOnCenterClickListener(
			OnCenterClickListener onCenterClickListener) {
		this.onCenterClickListener = onCenterClickListener;
	}

	public interface OnRotationFinishedListener {
		void onRotationFinished(View view, String name);
	}

	public interface OnMoveLeftListener {
		void onMove(String name);
	}

	public void setOnRotationFinishedListener(OnRotationFinishedListener onRotationFinishedListener) {
		this.onRotationFinishedListener = onRotationFinishedListener;
	}

	public OnMoveLeftListener getOnMoveLeftListener() {
		return onMoveLeftListener;
	}

	public void setOnMoveLeftListener(OnMoveLeftListener onMoveLeftListener) {
		this.onMoveLeftListener = onMoveLeftListener;
	}

}