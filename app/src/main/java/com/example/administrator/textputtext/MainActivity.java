package com.example.administrator.textputtext;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private CanExpandTextView mTv_content;
    private String[] strings=new String[]{};
    private TextView tv;
    private Button bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTv_content= (CanExpandTextView) findViewById(R.id.expandable_text);
        strings=getResources().getStringArray(R.array.news);
        StringBuffer buffer=new StringBuffer();
        for (int i = 0; i <strings.length ; i++) {
            buffer.append(strings[i]);
        }
        mTv_content.setText("\n\b\b\b\b\b我们可以看到这个代理HeaderViewListAdapter类，它处理完头布局尾布局和列表item直接的关系，" +
                "然后调用 return mAdapter.getView(adjPosition, convertView, parent);来和我们自定义的adapter交互。" +
                "我们现在知道了listView的源码添加头和尾的原理：就是通过一个代理adapter来处理完头布局和尾布局，然后去和我" +
                "们自定义的MyAdapter交互。接下来我们来分装自己的RecylerView实现listView一样添加头布局和尾布局的方法。。");
    }
}
