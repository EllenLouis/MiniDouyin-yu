package com.example.minidouyin.model;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

import java.util.jar.Attributes;

public class SlideMenu extends FrameLayout {
    private View menuView,mainView;
    private int menuWidth;
    private Scroller scroller;

    public SlideMenu(Context context) {
        super(context);
        init();//什么这里是
    }
    public SlideMenu(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }
    private void init(){
        scroller = new Scroller(getContext());
    }

    protected void onFinishInflate(){
        super.onFinishInflate();
        menuView = getChildAt(0);
        mainView = getChildAt(1);
        menuWidth = menuView.getLayoutParams().width;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN: downX = (int) ev.getX();
            break;
            case MotionEvent.ACTION_MOVE: int deltaX = (int) (ev.getX()-downX);
            if(Math.abs(deltaX)>200){
                return true;
            }
            break;
        }
        return super.onInterceptTouchEvent(ev);
    }
    @Override
    protected void onLayout(boolean changed, int l,int t,int r,int b){
        menuView.layout(-menuWidth,0,0,b);
        mainView.layout(0,0,r,b);
    }
    private int downX;
    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN: downX = (int)event.getX();break;
            case MotionEvent.ACTION_MOVE: int moveX = (int)event.getX();
                int deltaX = moveX-downX;
                int newScrollX = getScrollX()-deltaX;
                if(newScrollX < -menuWidth) newScrollX = -menuWidth;
                if(newScrollX> 0) newScrollX = 0;
                scrollTo(newScrollX,0);
                downX = moveX;
                break;
            case MotionEvent.ACTION_UP:
                if(getScrollX()> -menuWidth/2){
                    closeMenu();
                }else{
                    openMenu();
                }break;
        }
        return true;
    }
    private void closeMenu(){
        scroller.startScroll(getScrollX(),0,0-getScrollX(),0,400);
        invalidate();
    }
    private void openMenu(){
        scroller.startScroll(getScrollX(),0,-menuWidth-getScrollX(),0,400);
        invalidate();
    }
    public void computeScroll(){
        super.computeScroll();
        if(scroller.computeScrollOffset()){
            scrollTo(scroller.getCurrX(),0);
            invalidate();
        }
    }//动画未结束
    public void switchMenu(){
        if(getScrollX()==0){
            openMenu();
        }else{
            closeMenu();
        }
    }
}
