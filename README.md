# Gesture - Android #
<a target="_blank" href="LICENSE"><img src="https://img.shields.io/badge/licence-MIT-brightgreen.svg" alt="license : MIT"></a>
<a target="_blank" href="https://www.cmarix.com/android-application-development-services.html"><img src="https://img.shields.io/badge/platform-android-blue.svg" alt="license : MIT"></a>

## Core Features ##

 - Pinch to zoom with two fingers.
 - Rotate the image using two fingers.

## How it works ##

 - User can take picture from camera or select image from Gallery.
 - Set the selected image.
 - Rotate the image inside the defined UIView with two fingers.
 - Zoom in or Zoom out the image inside the same view.

## Purpose of this code ##

 - Many developers are facing a issue to move the image inside UIView without scrollview. To overcome this scenario, we have prepared this code for the Android developer to make their life easy.
 - This code allow users to zoom in/out the image, move or rotate the image using two fingers without scroll view.


## Requirements ##

 - Android 4.1+
 - Android Studio 3.2.1

## When you can use this code ##

 - Whenever you are having a requirement of erasing on image, this code will be help you to zoom in/out the image using two fingers where it should not affect the erase operation.

## Code Snippet ##

**Step 1**: Image touch listener

	setOnTouchListener(new OnTouchListener() {

	@Override
	public boolean onTouch(View v, MotionEvent event) {
       mScaleDetector.onTouchEvent(event);
       PointF curr = new PointF(event.getX(), event.getY());

       switch (event.getAction()) {
           case MotionEvent.ACTION_DOWN:
               last.set(curr);
               start.set(last);
               mode = DRAG;
               break;

           case MotionEvent.ACTION_MOVE:
               if (mode == DRAG) {
                   float deltaX = curr.x - last.x;
                   float deltaY = curr.y - last.y;
                   float fixTransX = getFixDragTrans(deltaX, viewWidth,
                           origWidth * saveScale);
                   float fixTransY = getFixDragTrans(deltaY, viewHeight,
                           origHeight * saveScale);
                   matrix.postTranslate(fixTransX, fixTransY);
                   fixTrans();
                   last.set(curr.x, curr.y);
               }

               break;

           case MotionEvent.ACTION_UP:
               mode = NONE;
               int xDiff = (int) Math.abs(curr.x - start.x);
               int yDiff = (int) Math.abs(curr.y - start.y);
               if (xDiff < CLICK && yDiff < CLICK)
                   performClick();
               break;

           case MotionEvent.ACTION_POINTER_UP:
               mode = NONE;
               break;
       }
       setImageMatrix(matrix);
       invalidate();
       return true; // indicate event was handled
	}

	});

**Step 2**: Image Scale listener onScale() is used for Zoom In and Zoom out.

    @Override
	public boolean onScale(ScaleGestureDetector detector) {
	float mScaleFactor = detector.getScaleFactor();
	float origScale = saveScale;
	saveScale *= mScaleFactor;
	if (saveScale > maxScale) {
       saveScale = maxScale;
       mScaleFactor = maxScale / origScale;
	} else if (saveScale < minScale) {
       saveScale = minScale;
       mScaleFactor = minScale / origScale;
	}

	if (origWidth * saveScale <= viewWidth
           || origHeight * saveScale <= viewHeight)
       matrix.postScale(mScaleFactor, mScaleFactor, viewWidth / 2,
               viewHeight / 2);
	else
       matrix.postScale(mScaleFactor, mScaleFactor,
               detector.getFocusX(), detector.getFocusY());

	fixTrans();
	return true;
	}

      
 
**Step 3**:  onMeasure() method is used to rescale image on rotation.
  
	@Override	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	  super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	  viewWidth = MeasureSpec.getSize(widthMeasureSpec);
	  viewHeight = MeasureSpec.getSize(heightMeasureSpec);

	//
	// Rescales image on rotation
	//
	if (oldMeasuredHeight == viewWidth && oldMeasuredHeight == viewHeight
           || viewWidth == 0 || viewHeight == 0)
       return;
	oldMeasuredHeight = viewHeight;
	oldMeasuredWidth = viewWidth;

	if (saveScale == 1) {
       // Fit to screen.
       float scale;

       Drawable drawable = getDrawable();
       if (drawable == null || drawable.getIntrinsicWidth() == 0
               || drawable.getIntrinsicHeight() == 0)
           return;
       int bmWidth = drawable.getIntrinsicWidth();
       int bmHeight = drawable.getIntrinsicHeight();

       Log.d("bmSize", "bmWidth: " + bmWidth + " bmHeight : " + bmHeight);

       float scaleX = (float) viewWidth / (float) bmWidth;
       float scaleY = (float) viewHeight / (float) bmHeight;
       scale = Math.min(scaleX, scaleY);
       matrix.setScale(scale, scale);

       // Center the image
       float redundantYSpace = (float) viewHeight - (scale * (float) bmHeight);
       float redundantXSpace = (float) viewWidth - (scale * (float) bmWidth);
       redundantYSpace /= (float) 2;
       redundantXSpace /= (float) 2;

       matrix.postTranslate(redundantXSpace, redundantYSpace);

       origWidth = viewWidth - 2 * redundantXSpace;
       origHeight = viewHeight - 2 * redundantYSpace;
       setImageMatrix(matrix);
	}
	fixTrans();
	}

**Step 4**: For fix Transition on image use getFixTrans()

    float getFixTrans(float trans, float viewSize, float contentSize) {
	float minTrans, maxTrans;

	if (contentSize <= viewSize) {
       minTrans = 0;
       maxTrans = viewSize - contentSize;
	   } else {
       minTrans = viewSize - contentSize;
       maxTrans = 0;
  	 }

	if (trans < minTrans)
       return -trans + minTrans;
	if (trans > maxTrans)
       return -trans + maxTrans;
	return 0;
	}


## Let us know! ##
We’d be really happy if you sent us links to your projects where you use our component. Just send an email to [biz@cmarix.com](mailto:biz@cmarix.com "biz@cmarix.com") and do let us know if you have any questions or suggestion regarding Gestures.

P.S. We’re going to publish more awesomeness examples on third party libraries, coding standards, plugins etc, in all the technology. Stay tuned!

## Stay Socially Connected ##

Get more familiar with our work by visiting few of our portfolio links.

[Portfolio](https://www.cmarix.com/portfolio.html) | [Facebook](https://www.facebook.com/CMARIXTechnoLabs/) | [Twitter](https://twitter.com/CMARIXTechLabs) | [Linkedin](https://www.linkedin.com/company/cmarix-technolabs-pvt-ltd-) | [Behance](https://www.behance.net/CMARIXTechnoLabs/) | [Instagram](https://instagram.com/cmarixtechnolabs/) | [Dribbble](https://dribbble.com/CMARIXTechnoLabs) | [Uplabs](https://www.uplabs.com/cmarixtechnolabs)

Please don’t forget to follow them.

## License ##

	MIT License
	
	Copyright © 2019 CMARIX TechnoLabs
	
	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:
	
	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.
	
	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.

