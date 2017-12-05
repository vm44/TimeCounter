package com.vzl;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.LinearLayout;

public class DrawView extends View{
	
	final int SHADOW_SHIFT=10;
	final int SHADOW_SHIFT_HRS=5;
	final int SHADOW_SHIFT_MIN=7;
	
	int vOffset, vCenter, vShift;
	LinearLayout ll;
	
	float aRot;
	
	Paint paint = new Paint();
	Paint textPaint = new Paint();
	int color=Color.WHITE;

	public DrawView(Context context,LinearLayout ll) {
		super(context);
		// TODO Auto-generated constructor stub
		this.ll=ll;
		setFocusable(true);
        setFocusableInTouchMode(true);      
               
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);		
	};
	
	public void setShift(int shift)
	{
		vShift=shift;
	}

	public void drawWatch(Canvas canvas)
	{
		int px = getMeasuredWidth() / 2;
		int py = px;//getMeasuredHeight() /2 ;
				
		
		paint.setColor(Color.WHITE);
		textPaint.setColor(Color.WHITE);
		
		for(int i=0;i<60;i++)
		{
			//canvas.drawCircle(240, vOffset+5, 5, paint);	
			if((i % 5) == 0)
			{
			    int textWidth = (int)textPaint.measureText(String.valueOf(i));
			    int textHeight = (int)textPaint.measureText(String.valueOf(i));
			    int cardinalX = px-textWidth/2;
			    int cardinalY = vOffset+15;//+textHeight;
			    paint.setStrokeWidth(5);
			    //canvas.se
				canvas.drawText(String.valueOf(i/5), cardinalX, cardinalY, textPaint);

			    canvas.drawLine(px, vOffset+20, px, vOffset+30, paint);
			}
			else
			{
				paint.setStrokeWidth(1);			
				canvas.drawLine(px, vOffset+5, px, vOffset+10, paint);
			}
			
			canvas.rotate(6, px, vCenter);//vOffset+py);			
		}
		
	}
	
	@Override
	public void onDraw(Canvas canvas)
	{
		vOffset=ll.getTop();
		int bottom=ll.getBottom()-vShift;
		
		Calendar cldr=Calendar.getInstance();
		int sec=cldr.get(Calendar.SECOND);
		float min=cldr.get(Calendar.MINUTE);
		float hrs=cldr.get(Calendar.HOUR);
		
		int px = getMeasuredWidth() / 2;
		int py = px;//getMeasuredHeight() /2 ;
		vCenter=vOffset+(bottom-vOffset)/2;
		
		paint.setColor(Color.WHITE);
		
		drawWatch(canvas);
		
		/*
		for(int i=0;i<60;i++)
		{
			//canvas.drawCircle(240, vOffset+5, 5, paint);	
			if((i % 5) == 0)
				canvas.drawLine(240, vOffset+5, 240, vOffset+25, paint);
			else
				canvas.drawLine(240, vOffset+5, 240, vOffset+10, paint);
			canvas.rotate(6, px, vOffset+py);			
		}
		*/
		//paint.setColor(color);
		//color+=0x0f0f0f0f;
		aRot=aRot=(hrs*60+min)/2;;
		canvas.rotate(aRot, px+SHADOW_SHIFT_HRS,  vCenter+SHADOW_SHIFT_HRS);//vOffset+py+5);
		//canvas.drawCircle(240, vOffset+5, 15, paint);
		paint.setColor(Color.GRAY);
		paint.setAlpha(179);
		paint.setStrokeWidth(12);
		canvas.drawLine(px+SHADOW_SHIFT_HRS, vCenter+SHADOW_SHIFT_HRS, 
				px+SHADOW_SHIFT_HRS, vOffset+120+SHADOW_SHIFT_HRS, paint);
		canvas.rotate(-aRot, px+SHADOW_SHIFT_HRS,  vCenter+SHADOW_SHIFT_HRS);//vOffset+py+5);
				
//		aRot=360/(60*12)*(hrs*60+min);
		aRot=(hrs*60+min)/2;
		//aRot=hrs*(360/12)+min/5;
		canvas.rotate(aRot, px, vCenter);//vOffset+py);
		//canvas.drawCircle(240, vOffset+5, 15, paint);
		paint.setStrokeWidth(12);
		paint.setColor(Color.WHITE);
		paint.setAlpha(255);
		canvas.drawLine(px, vCenter, px, vOffset+120, paint);
		canvas.rotate(-aRot, px, vCenter);// vOffset+py);

		
		aRot=(min*60+sec)/10;
		canvas.rotate(aRot, px+SHADOW_SHIFT_MIN,  vCenter+SHADOW_SHIFT_MIN);//vOffset+py+5);
		//canvas.drawCircle(240, vOffset+5, 15, paint);
		paint.setColor(Color.GRAY);
		paint.setAlpha(179);
		paint.setStrokeWidth(7);
		canvas.drawLine(px+SHADOW_SHIFT_MIN, vCenter+SHADOW_SHIFT_MIN, 
				px+SHADOW_SHIFT_MIN, vOffset+50+SHADOW_SHIFT_MIN, paint);
		canvas.rotate(-aRot, px+SHADOW_SHIFT_MIN,  vCenter+SHADOW_SHIFT_MIN);//vOffset+py+5);
		
		aRot=(min*60+sec)/10;
		canvas.rotate(aRot, px, vCenter);// vOffset+py);
		//canvas.drawCircle(240, vOffset+5, 15, paint);
		paint.setStrokeWidth(7);
		paint.setColor(Color.WHITE);
		paint.setAlpha(255);
		canvas.drawLine(px, vCenter, px, vOffset+50, paint);
		canvas.rotate(-aRot, px, vCenter);// vOffset+py);

		aRot=sec*6;
		canvas.rotate(aRot, px+SHADOW_SHIFT,  vCenter+SHADOW_SHIFT);//vOffset+py+5);
		//canvas.drawCircle(240, vOffset+5, 15, paint);
		paint.setColor(Color.GRAY);
		paint.setAlpha(179);
		paint.setStrokeWidth(3);
		canvas.drawLine(px+SHADOW_SHIFT, vCenter+SHADOW_SHIFT, 
				px+SHADOW_SHIFT, vOffset+SHADOW_SHIFT, paint);
		canvas.rotate(-aRot, px+SHADOW_SHIFT,  vCenter+SHADOW_SHIFT);//vOffset+py+5);

		aRot=sec*6;
		canvas.rotate(aRot, px,  vCenter);//vOffset+py);
		//canvas.drawCircle(240, vOffset+5, 15, paint);
		paint.setStrokeWidth(3);
		paint.setColor(Color.WHITE);
		paint.setAlpha(255);
		canvas.drawLine(px, vCenter, px, vOffset, paint);
		paint.setStrokeWidth(1);
		paint.setColor(Color.RED);
		canvas.drawLine(px, vCenter, px, vOffset, paint);
		canvas.rotate(-aRot, px,  vCenter);//vOffset+py);
		//aRot+=6;
		//if(aRot>360)aRot=0;
	}
	
}
