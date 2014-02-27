/*
 * Copyright 2014 Peng fei Pan
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

package me.xiaopan.android.imageloader.task.http;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.android.imageloader.Configuration;
import me.xiaopan.android.imageloader.decode.ByteArrayInputStreamCreator;
import me.xiaopan.android.imageloader.decode.FileInputStreamCreator;
import me.xiaopan.android.imageloader.decode.InputStreamCreator;
import me.xiaopan.android.imageloader.download.ImageDownloader.DownloadListener;
import me.xiaopan.android.imageloader.task.BitmapLoadCallable;
import me.xiaopan.android.imageloader.task.Request;
import me.xiaopan.android.imageloader.util.ImageLoaderUtils;

public class HttpBitmapLoadCallable extends BitmapLoadCallable {
	private File cacheFile = null;
	private InputStreamCreator inputStreamCreator = null;
	
	public HttpBitmapLoadCallable(Request request, ReentrantLock reentrantLock, Configuration configuration) {
		super(request, reentrantLock, configuration);
	}

	@Override
	public InputStreamCreator getInputStreamCreator() {
		if(inputStreamCreator == null){
			if(request.getOptions().isEnableDiskCache()){
				cacheFile = configuration.getBitmapCacher().getDiskCacheFile(configuration.getContext(), ImageLoaderUtils.encodeUrl(request.getImageUri()));
				if(HttpBitmapLoadTask.isAvailableOfFile(cacheFile, request.getOptions().getDiskCachePeriodOfValidity(), configuration, request.getName())){
					inputStreamCreator = new FileInputStreamCreator(cacheFile);
				}else{
					inputStreamCreator = getNetInputStreamCreator(configuration, request, cacheFile);
				}
			}else{
				inputStreamCreator = getNetInputStreamCreator(configuration, request, null);
			}
		}
		return inputStreamCreator;
	}

	@Override
	public void onFailed() {
		if(inputStreamCreator instanceof FileInputStreamCreator && cacheFile != null && cacheFile.exists()){
			cacheFile.delete();
		}
	}
	
	/**
     * 获取网络输入流监听器
     * @param requestName
     * @param imageUrl
     * @param cacheFile
     * @param maxRetryCount
     * @param httpClient
     * @return
     */
    private InputStreamCreator getNetInputStreamCreator(Configuration configuration, Request request, File cacheFile){
    	final NetInputStreamCreatorHolder holder = new NetInputStreamCreatorHolder();
    	configuration.getImageDownloader().execute(request, cacheFile, configuration, new DownloadListener() {
			@Override
			public void onFailed() {}
			
			@Override
			public void onComplete(final byte[] data) {
				holder.inputStreamCreator = new ByteArrayInputStreamCreator(data);
			}
			
			@Override
			public void onComplete(final File cacheFile) {
				holder.inputStreamCreator = new FileInputStreamCreator(cacheFile);
			}
		});
    	return holder.inputStreamCreator;
    }
	
	private class NetInputStreamCreatorHolder{
		InputStreamCreator inputStreamCreator;
	}
}
