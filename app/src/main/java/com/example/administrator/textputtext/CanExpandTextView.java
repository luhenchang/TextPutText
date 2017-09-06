package com.example.administrator.textputtext;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.xml.datatype.Duration;

/**
 * Created by Administrator on 2017/9/5/005.
 */

public class CanExpandTextView extends LinearLayout {
    //- 1：显示内容的控件。TextView visible_context_tv;
    private TextView visible_context_tv;
    //- 2：可点击控件。TextView button_expand_tv;
    private TextView click_expand_tv;
    // - 3 :展开后允许显示的最大行数：
    int maxExpandLines;
    //- 4 :动画的时间：
    int animal_duration;
    //文本内容真实高度
    int realHeight;
    //- 5 :来个标记记录文字是否发生了变动：
    boolean flag_isChang = false;
    //来记录是否是收缩状态
    boolean isClosed = true;
    //- 6 :没有展开时候的容器布局的高度：
    int shrinkageHeight = 0;
    //剩余点击控件的高度
    int lastHeight = 0;
    //- 7 :判断是否在执行动画：
    boolean isAnimate = false;
    //- 8：我们需要定义一个接口来监听我们的容器布局展开收缩的状态：
    ExpandStateListener expandStateListener;


    public CanExpandTextView(Context context) {
       super(context,null);
    }

    public CanExpandTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //初始化我们所需要的属性
        init(context, attrs);
    }

    /***
     * 这里我们来实现自定义的属性
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        //首先设置容器布局的方向：竖直的
        setOrientation(VERTICAL);
        //这里我们需要自定义属性：这里我我们都知道，刚接触android的可以去自己去看看自定义属性：
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandParames);
        //初始TextView显示字体行数:这里注意了默认的最大展开能允许显示的text行数可以这里设置也可以咋布局中设置的
        //我就在布局中设置，相当高大上
        maxExpandLines = typedArray.getInteger(R.styleable.ExpandParames_max_expend_lines, 2);
        //这里设置动画执行的时间长度，同样可在布局xml中折花枝也可以在这里设置。我就在xml中设置了
        animal_duration = typedArray.getInteger(R.styleable.ExpandParames_animal_duration, 0);
        //最后回收这个TypedArray:
        typedArray.recycle();
    }


    private interface ExpandStateListener {
        void ExpandStarteChangerListener(boolean isExpand);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //初始化容器内部的两个控件
        visible_context_tv = (TextView) findViewById(R.id.visible_context_tv);
        click_expand_tv = (TextView) findViewById(R.id.click_expand_tv);
        //给这个可点击控件设置点击时间。
        click_expand_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpandaAnimal expandaAnimal;
                isClosed = !isClosed;
                if (isClosed) {
                    //设置显示让用户去操作
                    click_expand_tv.setText("..展开");
                    if (expandStateListener != null) {
                        expandStateListener.ExpandStarteChangerListener(true);
                    }
                    //收缩所以我们开始高度getHeight(),结束变为0
                    expandaAnimal = new ExpandaAnimal(getHeight(), shrinkageHeight);
                } else {
                    click_expand_tv.setText("收起");
                    if (expandStateListener != null) {
                        expandStateListener.ExpandStarteChangerListener(false);
                    }
                    //展开：结束执行过程时候高度为内容控件的高度+点击控件的高度
                    expandaAnimal = new ExpandaAnimal(getHeight(), lastHeight + realHeight);
                }
                //让执行之后的动画保存当前状态。
                expandaAnimal.setFillAfter(true);
                expandaAnimal.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        isAnimate = true;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        clearAnimation();
                        isAnimate = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                clearAnimation();
                startAnimation(expandaAnimal);
            }
        });
    }

    //执行动画过程中不让其他时间影响。
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //如果正在执行动画，那么其他打断时间分发。
        return isAnimate;
    }
    public void setListener(ExpandStateListener listener) {
        this.expandStateListener = listener;
    }
    public class ExpandaAnimal extends Animation {
        int startHeight, endHeight;

        public ExpandaAnimal(int startHeight, int endHeight) {
            //这是执行的时间
            setDuration(animal_duration);
            this.startHeight = startHeight;
            this.endHeight = endHeight;
        }

        /***
         *
         * @param interpolatedTime:这个值用来设置时间的0-1变化范围
         * @param t 设置动画效果和状态
         */
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            //这里我们需要计算内容TextView的变化的动态高度height
            //我们将开始的高度+内容变化高度
            //内容变化高度=(endHeight-startHeight)*interpolatedTime
            int height = (int) (startHeight + (endHeight - startHeight) * interpolatedTime);
            //动态的设置内容TextView的高度
            visible_context_tv.setHeight(height - lastHeight);
            //从新摆放容器布局的子view
            CanExpandTextView.this.getLayoutParams().height = height;
            CanExpandTextView.this.requestLayout();

        }
        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

    public void setText(String text) {
        flag_isChang = true;
        visible_context_tv.setText(text);
    }

    public void setText(String text, boolean isClosed) {
        this.isClosed = isClosed;
        if (isClosed) {
            click_expand_tv.setText("..展开");
        } else {
            click_expand_tv.setText("\n收起");
        }
        clearAnimation();
        setText(text);
        getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //1.如果控件被设置Visible=gon或者内容控件TextView内容没有变化那么没必要测量
        if (getVisibility() == GONE || flag_isChang == false) {
            return;
        }
        flag_isChang = false;
        //初始化默认状态，显示文本就可以
        click_expand_tv.setVisibility(GONE);
        visible_context_tv.setMaxLines(Integer.MAX_VALUE);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //如果本身没有文字行数没有达到限制最少的行数那么就没必要展开或者
        if (visible_context_tv.getLineCount() <= maxExpandLines) {
            return;
        }
        //获取内容TexView的真实高度，后面我们需要用到
        realHeight = getRealHeightTextView(visible_context_tv);
        //如果处于收缩状态，则设置最多显示行数
        if (isClosed) {
            visible_context_tv.setLines(maxExpandLines);
        }
        click_expand_tv.setVisibility(VISIBLE);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //如果是收缩的状态那么需要去
        if (isClosed) {
            visible_context_tv.post(new Runnable() {
                @Override
                public void run() {
                    //剩余高度=当前收缩高度-内容控件的高度
                    lastHeight = getHeight() - visible_context_tv.getHeight();
                    //收缩时候的容器的高度
                    shrinkageHeight =getMeasuredHeight();
                }
            });
        }

    }

    private int getRealHeightTextView(TextView visible_context_tv) {
        //getLineTop返回值是一个根据行数而形成等差序列，如果参数为行数，则值即为文本的高度
        int textHeight = visible_context_tv.getLayout().getLineTop
                (visible_context_tv.getLineCount());
        return textHeight + visible_context_tv.getCompoundPaddingBottom()
                + visible_context_tv.getCompoundPaddingTop();
    }
}
