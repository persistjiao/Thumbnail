package com.persist.helper;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
/**
 * @ClassName:  BitmapThumbnailHelper   
 * @Description:图片二次采样工具类(包含获取视频典型帧，音频封面) 
 * @author: Liu YanChao  
 * @date:   2016-5-27 上午10:49:44   
 *
 */
public class BitmapThumbnailHelper {

	/**
	 * 对图片进行二次采样，生成缩略图。放置加载过大图片出现内存溢出
	 */
	public static Bitmap createThumbnail(byte[] data, int newWidth,
			int newHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		int oldWidth = options.outWidth;
		int oldHeight = options.outHeight;

		// Log.i("Helper", "--->oldWidth:" + oldWidth);
		// Log.i("Helper", "--->oldHeight:" + oldHeight);

		int ratioWidth = 0;
		int ratioHeight = 0;

		if (newWidth != 0 && newHeight == 0) {
			ratioWidth = oldWidth / newWidth;
			options.inSampleSize = ratioWidth;
			// Log.i("Helper", "--->ratioWidth:" + ratioWidth);

		} else if (newWidth == 0 && newHeight != 0) {
			ratioHeight = oldHeight / newHeight;
			options.inSampleSize = ratioHeight;
		
		} else if (newWidth == 0 && newHeight == 0) {
			options.inSampleSize = 1;
		
		} else {
			ratioHeight = oldHeight / newHeight;
			ratioWidth = oldWidth / newWidth;
			options.inSampleSize = ratioHeight > ratioWidth ? ratioHeight
					: ratioWidth;
		}
		options.inPreferredConfig = Config.RGB_565;
		options.inJustDecodeBounds = false;
		Bitmap bm = BitmapFactory
				.decodeByteArray(data, 0, data.length, options);
		return bm;
	}

	public static Bitmap createThumbnail(String pathName, int newWidth,
			int newHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);
		int oldWidth = options.outWidth;
		int oldHeight = options.outHeight;

		int ratioWidth = 0;
		int ratioHeight = 0;

		if (newWidth != 0 && newHeight == 0) {
			ratioWidth = oldWidth / newWidth;
			options.inSampleSize = ratioWidth;
		} else if (newWidth == 0 && newHeight != 0) {
			ratioHeight = oldHeight / newHeight;
			options.inSampleSize = ratioHeight;
		
		} else if (newWidth == 0 && newHeight == 0) {
			options.inSampleSize = 1;
		
		} else {
			ratioHeight = oldHeight / newHeight;
			ratioWidth = oldWidth / newWidth;
			options.inSampleSize = ratioHeight > ratioWidth ? ratioHeight
					: ratioWidth;
		}
		options.inPreferredConfig = Config.RGB_565;
		options.inJustDecodeBounds = false;
		Bitmap bm = BitmapFactory.decodeFile(pathName, options);
		return bm;
	}

	// 获取视频文件的典型帧作为封面
	@SuppressLint("NewApi")
	public static Bitmap createVideoThumbnail(String filePath) {
		Bitmap bitmap = null;
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		try {
			retriever.setDataSource(filePath);
			bitmap = retriever.getFrameAtTime();
		} catch (Exception ex) {
		} finally {
			try {
				retriever.release();
			} catch (RuntimeException ex) {
			}
		}
		return bitmap;
	}

	// 获取音乐文件中内置的专辑图片
	@SuppressLint("NewApi")
	public static Bitmap createAlbumThumbnail(String filePath) {
		Bitmap bitmap = null;
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		try {
			retriever.setDataSource(filePath);
			byte[] art = retriever.getEmbeddedPicture();
			bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
		} catch (Exception ex) {
		} finally {
			try {
				retriever.release();
			} catch (RuntimeException ex) {
			}
		}
		return bitmap;
	}
	
		// 获取视频文件的典型帧作为封面自定义图片大小
		@SuppressLint("NewApi")
		public static Bitmap createVideoThumbnail(String filePath,int maxWith,int maxHeight) {
			Bitmap bitmap = null;
			MediaMetadataRetriever retriever = new MediaMetadataRetriever();
			try {
				retriever.setDataSource(filePath);
				bitmap = retriever.getFrameAtTime();
			} catch (Exception ex) {
			} finally {
				try {
					retriever.release();
				} catch (RuntimeException ex) {
				}
			}
			bitmap = Bitmap.createScaledBitmap(bitmap, maxWith, maxHeight, true);
			return bitmap;
		}

		// 获取音乐文件中内置的专辑图片自定义图片大小
		@SuppressLint("NewApi")
		public static Bitmap createAlbumThumbnail(String filePath,int maxWith,int maxHeight) {
			Bitmap bitmap = null;
			MediaMetadataRetriever retriever = new MediaMetadataRetriever();
			try {
				retriever.setDataSource(filePath);
				byte[] art = retriever.getEmbeddedPicture();
				bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
			} catch (Exception ex) {
			} finally {
				try {
					retriever.release();
				} catch (RuntimeException ex) {
				}
			}
			/**
			 * 从当前存在的位图，按一定期望的比例(大小)创建一个新的位图。
			 * src:用来构建子集的源位图
			 * dstWidth:新位图期望的宽度
			 * dstHeight:新位图期望的高度
			 * 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响
			 * filter参数为true可以进行滤波处理，有助于改善新图像质量;flase时，计算机不做过滤处理。
			 */
			bitmap = Bitmap.createScaledBitmap(bitmap, maxWith, maxHeight, true);
			return bitmap;
		}
}
