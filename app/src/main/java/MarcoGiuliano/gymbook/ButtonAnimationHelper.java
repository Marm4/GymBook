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
import android.widget.EditText;


public class ButtonAnimationHelper {
    private final RoutineActivityLogic routineActivityLogic;
    private final ExerciseActivityLogic exerciseActivityLogic;
    private AnimatorSet animatorSet;
    private final Handler handler;
    private long startTime;
    private float initialX;
    private boolean isMoveStarted;
    private boolean viewEditionMode;
    private boolean viewIsDeleted;
    private View view;
    private static final float MIN_DISTANCE = 150;
    private static final float DURATION_TOUCH = 1000;
    private static final int DURATION_ANIMATION = 300;
    private static final int DURATION_SCALE_X_Y = 2000;

    public ButtonAnimationHelper(RoutineActivityLogic routineActivityLogic, ExerciseActivityLogic exerciseActivityLogic, View view){
        this.routineActivityLogic = routineActivityLogic;
        this.exerciseActivityLogic = exerciseActivityLogic;
        this.view = view;
        isMoveStarted = false;
        viewEditionMode = false;
        viewIsDeleted = false;
        handler = new Handler();
        setClicks();
    }

    private final Runnable animationRunnable = () -> {
        if(viewEditionMode)   isMoveStarted = true;
        viewEditionMode = true;
        setupButtonAnimation(view);
    };

     @SuppressLint("ClickableViewAccessibility")
    public void setClicks(){
        view.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startTime = System.currentTimeMillis();
                    initialX = motionEvent.getX();
                    handler.postDelayed(animationRunnable, 1000);
                    break;

                case MotionEvent.ACTION_MOVE:
                    float deltaX = motionEvent.getX() - initialX;
                    if ((deltaX > MIN_DISTANCE || deltaX < -MIN_DISTANCE) && viewEditionMode) {
                        animateButtonRemoval();
                        viewEditionMode = false;
                        viewIsDeleted = true;
                        stopAnimation();
                        isMoveStarted = false;
                        handler.removeCallbacks(animationRunnable);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    long duration = System.currentTimeMillis() - startTime;
                    if(routineActivityLogic != null)    forRoutine(duration);
                    else if(exerciseActivityLogic != null)  forExercise(duration);
                    break;
            }
            return false;
        });
    }

    private void forExercise(long duration){
        if(viewEditionMode && duration < DURATION_TOUCH)    exerciseActivityLogic.editTextName((EditText) view);
    }

    @SuppressWarnings("ConstantConditions")
    private void forRoutine(long duration){
        if (!isMoveStarted && duration < DURATION_TOUCH) {
            handler.removeCallbacks(animationRunnable);
            if (viewEditionMode) {
                routineActivityLogic.editButtonName((Button) view);
                viewEditionMode = false;
                stopAnimation();
            } else if (!viewEditionMode && !viewIsDeleted) {
                routineActivityLogic.newExerciseActivity((Button) view);
            }
        } else if (duration < DURATION_TOUCH) isMoveStarted = false;
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
        Animation animation = new TranslateAnimation(0, view.getWidth(), 0, 0);
        animation.setDuration(DURATION_ANIMATION);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(routineActivityLogic != null)    routineActivityLogic.alertDelete((Button) view);
                else if(exerciseActivityLogic != null)  exerciseActivityLogic.alertDelete((EditText) view);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(animation);
    }



    public void stopAnimation() {
        view.setScaleX(1.0f);
        view.setScaleY(1.0f);
        viewEditionMode = false;

        if(animatorSet != null)
            animatorSet.cancel();

        if(handler != null)
            handler.removeCallbacks(animationRunnable);
    }
}
