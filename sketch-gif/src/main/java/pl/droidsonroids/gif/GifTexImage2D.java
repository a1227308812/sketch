package pl.droidsonroids.gif;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.nio.Buffer;

import pl.droidsonroids.gif.annotations.Beta;

/**
 * Provides support for animated GIFs in OpenGL.
 * There are 2 possible usages:
 * <ol>
 * <li>Rendering GIF automatically according to its timing to internal frame buffer in the background thread,
 * and requesting frame to be copied to 2D texture when needed. See {@link #glTexImage2D(int, int)} and {@link #glTexImage2D(int, int)}</li>
 * <li>Manual frame advancing. See {@link #seekToFrame(int)} (int)}</li>
 * </ol>
 */
@Beta
public class GifTexImage2D {
	private final GifInfoHandle mGifInfoHandle;

	/**
	 * Constructs new GifTexImage2D.
	 * Decoder thread is initially stopped, use {@link #startDecoderThread()} to start it.
	 *
	 * @param inputSource source
	 * @param options     null-ok; options controlling parameters like subsampling and opacity
	 * @throws IOException when creation fails
	 */
	public GifTexImage2D(final InputSource inputSource, @Nullable GifOptions options) throws IOException {
		if (options == null) {
			options = new GifOptions();
		}
		mGifInfoHandle = inputSource.open();
		mGifInfoHandle.setOptions(options.inSampleSize, options.inIsOpaque);
		mGifInfoHandle.initTexImageDescriptor();
	}

	/**
	 * See {@link GifDrawable#getFrameDuration(int)}
	 *
	 * @param index index of the frame
	 * @return duration of the given frame in milliseconds
	 * @throws IndexOutOfBoundsException if {@code index < 0 || index >= <number of frames>}
	 */
	public int getFrameDuration(@IntRange(from = 0) int index) {
		return mGifInfoHandle.getFrameDuration(index);
	}

	/**
	 * Seeks to given frame
	 *
	 * @param index index of the frame
	 * @throws IndexOutOfBoundsException if {@code index < 0 || index >= <number of frames>}
	 */
	public void seekToFrame(@IntRange(from = 0) int index) {
		mGifInfoHandle.seekToFrameGL(index);
	}

	/**
	 * @return number of frames in GIF, at least one
	 */
	public int getNumberOfFrames() {
		return mGifInfoHandle.getNumberOfFrames();
	}

	/**
	 * Equivalent of {@link android.opengl.GLES20#glTexImage2D(int, int, int, int, int, int, int, int, Buffer)}.
	 * Where <code>Buffer</code> contains pixels of the current frame.
	 *
	 * @param level  level-of-detail number
	 * @param target target texture
	 */
	public void glTexImage2D(int target, int level) {
		mGifInfoHandle.glTexImage2D(target, level);
	}

	/**
	 * Equivalent of {@link android.opengl.GLES20#glTexSubImage2D(int, int, int, int, int, int, int, int, Buffer)}.
	 * Where <code>Buffer</code> contains pixels of the current frame.
	 *
	 * @param level  level-of-detail number
	 * @param target target texture
	 */
	public void glTexSubImage2D(int target, int level) {
		mGifInfoHandle.glTexSubImage2D(target, level);
	}

	/**
	 * Creates frame buffer and starts decoding thread. Does nothing if already started.
	 */
	public void startDecoderThread() {
		mGifInfoHandle.startDecoderThread();
	}

	/**
	 * Stops decoder thread and releases frame buffer. Does nothing if already stopped.
	 */
	public void stopDecoderThread() {
		mGifInfoHandle.stopDecoderThread();
	}

	/**
	 * See {@link GifDrawable#recycle()}. Decoder thread is stopped automatically.
	 */
	public void recycle() {
		if (mGifInfoHandle != null) {
			mGifInfoHandle.recycle();
		}
	}

	/**
	 * @return width of the GIF canvas, 0 if recycled
	 */
	public int getWidth() {
		return mGifInfoHandle.getWidth();
	}

	/**
	 * @return height of the GIF canvas, 0 if recycled
	 */
	public int getHeight() {
		return mGifInfoHandle.getHeight();
	}

	/**
	 * See {@link GifDrawable#getDuration()}
	 *
	 * @return duration of of one loop the animation in milliseconds. Result is always multiple of 10.
	 */
	public int getDuration() {
		return mGifInfoHandle.getDuration();
	}

	@Override
	@SuppressWarnings("ThrowFromFinallyBlock")
	protected final void finalize() throws Throwable {
		try {
			recycle();
		} finally {
			super.finalize();
		}
	}
}
