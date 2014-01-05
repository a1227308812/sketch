/*
 * Copyright 2013 Peng fei Pan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xiaoapn.easy.imageloader.execute.task;

import me.xiaoapn.easy.imageloader.ImageLoader;
import me.xiaoapn.easy.imageloader.display.BitmapType;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

public class DisplayBitmapTask implements Runnable {
	private Request request;
	private boolean isFromMemoryCache;
	private ImageView imageView;
	private BitmapType bitmapType;
	private ImageLoader imageLoader;
	private BitmapDrawable bitmapDrawable;

	public DisplayBitmapTask(ImageLoader imageLoader, ImageView imageView, BitmapDrawable bitmapDrawable, BitmapType bitmapType, boolean isFromMemoryCache, Request request) {
		this.request = request;
		this.imageView = imageView;
		this.bitmapType = bitmapType;
		this.imageLoader = imageLoader;
		this.bitmapDrawable = bitmapDrawable;
		this.isFromMemoryCache = isFromMemoryCache;
	}

	@Override
	public void run() {
		request.getOptions().getBitmapDisplayer().display(imageView, bitmapDrawable, bitmapType, isFromMemoryCache, imageLoader, request);
	}
}
