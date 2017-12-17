package com.example.e450c.yjh_lagoulogindemo;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnLayoutChangeListener{

    private RelativeLayout relativeLayout;

    private ScrollView scrollView;

    private Button btn_login;

    private int screenHeight = 0;//屏幕高度
    private int keyHeight = 0; //软件盘弹起后所占高度

    private View service,content;
    private float scale = 0.0f; //logo缩放比例


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        // 隐藏标题栏
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏

        setContentView(R.layout.activity_main);

        btn_login = (Button) findViewById(R.id.btn_login);
        relativeLayout = (RelativeLayout) findViewById(R.id.logo_content);
        scrollView = (ScrollView) findViewById(R.id.main_content);
        content = findViewById(R.id.content);
        screenHeight = this.getResources().getDisplayMetrics().heightPixels; //获取屏幕高度
        keyHeight = screenHeight / 3;
        scrollView.addOnLayoutChangeListener(this);
        if(isFullScreen(this)){
            AndroidBug5497Workaround.assistActivity(this);
        }

    }

    public boolean isFullScreen(Activity activity) {
        return (activity.getWindow().getAttributes().flags &
                WindowManager.LayoutParams.FLAG_FULLSCREEN)==WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }


        public void zoomOut(final View view, float dist) {
            view.setPivotY(view.getHeight());
            view.setPivotX(view.getWidth() / 2);
            AnimatorSet mAnimatorSet = new AnimatorSet();

            ObjectAnimator mAnimatorScaleX = ObjectAnimator.ofFloat(view, "scaleX", scale, 1.0f);
            ObjectAnimator mAnimatorScaleY = ObjectAnimator.ofFloat(view, "scaleY", scale, 1.0f);
            ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(view, "translationY", view.getTranslationY(), 0);

            mAnimatorSet.play(mAnimatorTranslateY).with(mAnimatorScaleX);
            mAnimatorSet.play(mAnimatorScaleX).with(mAnimatorScaleY);
            mAnimatorSet.setDuration(300);
            mAnimatorSet.start();
        }


        public void zoomIn(final View view, float dist) {
            view.setPivotY(view.getHeight());
            view.setPivotX(view.getWidth() / 2);
            AnimatorSet mAnimatorSet = new AnimatorSet();
            ObjectAnimator mAnimatorScaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, scale);
            ObjectAnimator mAnimatorScaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, scale);
            ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(view, "translationY", 0.0f, -dist);
            mAnimatorSet.play(mAnimatorTranslateY).with(mAnimatorScaleX);
            mAnimatorSet.play(mAnimatorScaleX).with(mAnimatorScaleY);
            mAnimatorSet.setDuration(300);
            mAnimatorSet.start();
        }

        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            Log.e("****bottom****",bottom+"");
            Log.e("****oldBottom****",oldBottom+"");

            if(oldBottom != 0 && bottom != 0 &&(oldBottom - bottom > keyHeight)){
                if ((btn_login.getTop()-(oldBottom - bottom))>0){
                    int dist = btn_login.getTop()-(oldBottom - bottom);
                    ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(content, "translationY", 0.0f, -dist);
                    mAnimatorTranslateY.setDuration(300);
                    mAnimatorTranslateY.setInterpolator(new LinearInterpolator());
                    mAnimatorTranslateY.start();
//                service.setVisibility(View.INVISIBLE);
                }
                zoomIn(relativeLayout,(oldBottom - bottom) - keyHeight);
                Toast.makeText(MainActivity.this, "监听到软键盘弹起...", Toast.LENGTH_SHORT).show();

            }else if(oldBottom != 0 && bottom != 0 &&(bottom - oldBottom > keyHeight)){
                ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(content, "translationY", content.getTranslationY(), 0);
                mAnimatorTranslateY.setDuration(300);
                mAnimatorTranslateY.setInterpolator(new LinearInterpolator());
                mAnimatorTranslateY.start();
                zoomOut(relativeLayout, (bottom - oldBottom) - keyHeight);
                Toast.makeText(MainActivity.this, "监听到软件盘关闭...", Toast.LENGTH_SHORT).show();

            }
        }
}


