package com.persist.day25_thumbnail;

import com.persist.helper.HttpURLConnHelper;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
/**
 * @ClassName:  MainActivity   
 * @Description:Bitmap二次采样，根据图片字节数组，对图片进行二次采样，不致于加载过大图片出现内存溢出 
 * @date:   2016-3-21 下午4:06:34   
 *
 */

//第一步：在加载网络、文件的图片时，进行图片的解码BitmapFactory.decode(),只进行图片的尺寸获取，不进行实际的Bitmap创建
//因此，第一次解码，不会占用太多的内存，可以获取图片的宽和高；
//第二步：根据图片真实的宽高，以及客户端希望图片加载后的尺寸，进行一个计算，形成一种图片解码时缩小采样的一个数值，这个数值
//可以直接运用在BitmapFactory上，加载到内存的Bitmap,就是变小的，内存是小图片的内容，不是原始图片的内存
public class MainActivity extends Activity {
	private ImageView imageView_thumbnail;
	private ImageView imageView_original;
	private final static String URL = "http://e.hiphotos.baidu.com/baike/c0%3Dbaike116%2C5%2C5%2C116%2C38/sign=226923588594a4c21e2eef796f9d70b0/c995d143ad4bd1138711756558afa40f4afb058d.jpg";
	private Handler handler = new Handler();
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initView();
        
        loadNetworkData();
    }

    private void loadNetworkData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				byte[] data = HttpURLConnHelper.loadByteFromURL(URL);
				final Bitmap bm_orinal = BitmapFactory.decodeByteArray(data, 0, data.length);//原图
				// 注意：Android手机默认图片格式采用ARGB_8888一个像素占用4个字节
				// alpha(A)值，Red（R）值，Green(G)值，Blue（B）值各占8个bites ， 共32bites , 即4个字节。
				// 这个色彩模式色彩最细腻，显示质量最高。但同样的，占用的内存也最大。
				// 这是一种高质量的图片格式，电脑上普变采用的格式。它也是Android手机上一个Bitmap的默认格式。
				int bm_orinal_size = bm_orinal.getRowBytes() * bm_orinal.getHeight();// 原图大小(600 * 4 * 900 = 2160000 2M)
				
				final Bitmap bm_thumbnail = createThumbnail(data, 0, 100);//缩略图
				int bm_thumbnail_size = bm_thumbnail.getRowBytes() * bm_thumbnail.getHeight();// 缩略图大小
				Log.i("TAG", "原图大小："+bm_orinal_size+"，缩略图大小："+bm_thumbnail_size);
				
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						imageView_original.setImageBitmap(bm_orinal);
						imageView_thumbnail.setImageBitmap(bm_thumbnail);
					}
				});
			}
		}).start();
	}

	private void initView() {
    	imageView_original = (ImageView) findViewById(R.id.imageView_original);
		imageView_thumbnail = (ImageView) findViewById(R.id.imageView_thumbnail);
	}

	private Bitmap createThumbnail(byte[] data, int newWidth, int newHeight){
		// 第一次采样：目的只获取图片的宽度和高度，并不希望获取图片像素点的数据
		BitmapFactory.Options options = new BitmapFactory.Options();
		// 第一次采样，仅仅解码边缘区域(数据边界)
		options.inJustDecodeBounds = true;
		// 开始解码执行第一次采样(如果指定了inJustDecodeBounds，decodeByteArray将返回为空，也就是并不分配空间，不占用内存，但可计算出原始图片的长度和宽度)
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		// 获取原图片的宽度和高度
		int oldWidth = options.outWidth;
		int oldHeight = options.outHeight;
		Log.i("TAG", "原图宽度是：" + oldWidth+"，原图高度是：" + oldHeight);
		
		// ratio:比 计算缩放比例
		int ratioWidth = 0;
		int ratioHeight = 0;
		if (newWidth == 0 && newHeight != 0) {
			ratioHeight = oldHeight / newHeight;
			// 属性值inSampleSize为采样比率(图片缩放的倍数。如果设为4，则宽和高都为原来的1/4，则图是原来的1/16)：表示缩略图大小为原始图片宽高的几分之一，最好是2的幂，因为这种C语言解码效率最高
			options.inSampleSize = ratioHeight;
		}else if (newWidth != 0 && newHeight == 0) {
			ratioWidth = oldWidth / newWidth;
			options.inSampleSize = ratioWidth;
		}else if (newWidth ==0 && newHeight == 0) {
			options.inSampleSize = 1;
		}else {
			ratioHeight = oldHeight / newHeight;
			ratioWidth = oldWidth / newWidth;
			// 注意：如果inSampleSize最终的值不是2的整数幂，取其最接近的2的整数幂（7倍取4 15取8依次类推往前取）
			//特别注意：如果inSampleSize的值在16倍~32倍之间（即2的3次幂~2的四次幂之间），取特殊倍数24
			options.inSampleSize = ratioHeight > ratioWidth ? ratioHeight : ratioWidth;
		}
		// 将图片的质量设置为Alpha_8。目的是又不影响视觉效果，又大大节省了图片的占用空间
		// 此处设置Alpha_8由于某种原因无效，为什么？？？？？所以还是要设置Config.RGB_565
//		options.inPreferredConfig = Config.ALPHA_8;
		options.inPreferredConfig = Config.RGB_565;
		// 图片进行第二次采样，既要采集图片的边界信息，又需要采集图片像素点的数据
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
		Log.i("TAG", "缩略图宽度是:"+bitmap.getWidth()+",缩略图高度是:"+bitmap.getHeight());
		return bitmap;
	}
	
	// 对于SD卡中的图片生成缩略图
		private Bitmap createThumbnail(String pathName, int newWidht, int newHeight) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(pathName, options);
			int oldWidth = options.outWidth;
			int oldHeight = options.outHeight;

			int widthRaito = 0, heightRatio = 0;
			if (newWidht == 0 && newHeight != 0) {
				heightRatio = oldHeight / newHeight;
				options.inSampleSize = heightRatio;
			} else if (newWidht != 0 && newHeight == 0) {
				widthRaito = oldWidth / newWidht;
				options.inSampleSize = widthRaito;
			} else {
				widthRaito = oldWidth / newWidht;
				heightRatio = oldHeight / newHeight;
				options.inSampleSize = widthRaito > heightRatio ? widthRaito : heightRatio;
			}
			options.inPreferredConfig = Config.ALPHA_8;
			options.inJustDecodeBounds = false;
			
			Bitmap bm = BitmapFactory.decodeFile(pathName, options);
			return bm;
		}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
