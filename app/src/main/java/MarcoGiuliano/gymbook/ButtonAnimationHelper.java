package MarcoGiuliano.gymbook;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;


public class ButtonAnimationHelper {
    private final RoutineActivityLogic routineActivityLogic;
    private AnimatorSet animatorSet;
    private final Handler handler;
    private long startTime;
    private float initialX;
    private boolean isMoveStarted;
    private boolean buttonEditionMode;
    private boolean buttonIsDeleted;
    private Button button;
    private static final float MIN_DISTANCE = 150;
    private static final float DURATION_TOUCH = 1000;
    private static final int DURATION_ANIMATION = 300;
    private static final int DURATION_SCALE_X_Y = 2000;

    public ButtonAnimationHelper(RoutineActivityLogic routineActivityLogic, Button button){
        this.routineActivityLogic = routineActivityLogic;
        this.button = button;
        isMoveStarted = false;
        buttonEditionMode = false;
        buttonIsDeleted = false;
        handler = new Handler();
        setClicks();
    }

    private final Runnable animationRunnable = () -> {
        if(buttonEditionMode)   isMoveStarted = true;
        buttonEditionMode = true;
        setupButtonAnimation(button);
    };

    @SuppressWarnings("ConstantConditions") @SuppressLint("ClickableViewAccessibility")
    public void setClicks(){
        button.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startTime = System.currentTimeMillis();
                    initialX = motionEvent.getX();
                    handler.postDelayed(animationRunnable, 1000);
                    break;

                case MotionEvent.ACTION_MOVE:
                    float deltaX = motionEvent.getX() - initialX;
                    if ((deltaX > MIN_DISTANCE || deltaX < -MIN_DISTANCE) && buttonEditionMode) {
                        animateButtonRemoval();
                        buttonEditionMode = false;
                        buttonIsDeleted = true;
                        stopAnimation();
                        isMoveStarted = false;
                        handler.removeCallbacks(animationRunnable);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    long duration = System.currentTimeMillis() - startTime;
                    if (!isMoveStarted && duration < DURATION_TOUCH) {
                        handler.removeCallbacks(animationRunnable);
                        if (buttonEditionMode) {
                            routineActivityLogic.editButtonName(button);
                            buttonEditionMode = false;
                            stopAnimation();
                        } else if (!buttonEditionMode && !buttonIsDeleted) {
                            routineActivityLogic.newExerciseActivity(button);
                        }
                    } else if (duration < DURATION_TOUCH) isMoveStarted = false;
                    break;
            }
            return false;
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    public void setupButtonAnimation(View view) {
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 1.02f, 0.99f, 1.02f);
        scaleXAnimator.setDuration(DURATION_SCALE_X_Y);
        scaleXAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        scaleXAnimator.setRepeatMode(ObjectAnimator.REVERSE);

        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.99f, 1.0f);
        scaleYAnimator.setDuration(DURATION_SCALE_X_Y);
        scaleYAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        scaleYAnimator.setRepeatMode(ObjectAnimator.REVERSE);

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
        animatorSet.start();
    }

    public void animateButtonRemoval() {
        Animation animation = new TranslateAnimation(0, button.getWidth(), 0, 0);
        animation.setDuration(DURATION_ANIMATION);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                routineActivityLogic.deleteButton(button);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        button.startAnimation(animation);
    }

    public void stopAnimation() {
        button.setScaleX(1.0f);
        button.setScaleY(1.0f);

        if(animatorSet != null)
            animatorSet.cancel();

        if(handler != null)
            handler.removeCallbacks(animationRunnable);
    }
}
