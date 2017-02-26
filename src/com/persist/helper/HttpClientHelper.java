package com.persist.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpClientHelper {
	public static HttpClient checkNetwork(String url) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(request);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return httpClient;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 作用：实现网络访问文件，将获取到数据储存在文件流中
	 * 
	 * @param url
	 *            ：访问网络的url地址
	 * @return inputstream
	 */
	public static InputStream loadFileFromURL(String url) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet requestGet = new HttpGet(url);
		HttpResponse httpResponse;
		try {
			httpResponse = httpClient.execute(requestGet);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = httpResponse.getEntity();
				return entity.getContent();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 作用：实现网络访问文件，将获取到的数据存在字节数组中
	 * 
	 * @param url
	 *            ：访问网络的url地址
	 * @return byte[]
	 */
	public static byte[] loadByteFromURL(String url) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet requestGet = new HttpGet(url);
		try {
			HttpResponse httpResponse = httpClient.execute(requestGet);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = httpResponse.getEntity();
				return EntityUtils.toByteArray(httpEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("====>" + e.toString());
		}
		return null;
	}

	/**
	 * 作用：实现网络访问文件，返回字符串
	 * 
	 * @param url
	 *            ：访问网络的url地址
	 * @return String
	 */
	public static String loadTextFromURL(String url) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet requestGet = new HttpGet(url);
		try {
			HttpResponse httpResponse = httpClient.execute(requestGet);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = httpResponse.getEntity();
				return EntityUtils.toString(httpEntity, "utf-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 作用：实现网络访问文件，先给服务器通过“GET”方式提交数据，再返回相应的数据
	 * 
	 * @param url
	 *            ：访问网络的url地址
	 * @param params
	 *            String url：访问url时，需要传递给服务器的参数。
	 *            第二个参数格式为：username=wangxiangjun&password=123456
	 * @return byte[]
	 */
	public static byte[] doGetSubmit(String url, String params) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet requestGet = new HttpGet(url + "?" + params);
		try {
			HttpResponse httpResponse = httpClient.execute(requestGet);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = httpResponse.getEntity();
				return EntityUtils.toByteArray(httpEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 作用：实现网络访问文件，先给服务器通过“POST”方式提交数据，再返回相应的数据
	 * 
	 * @param url
	 *            ：访问网络的url地址
	 * @param params
	 *            String url：访问url时，需要传递给服务器的参数。 第二个参数为：List<NameValuePair>
	 * @return byte[]
	 */
	public static byte[] doPostSubmit(String url, List<NameValuePair> params) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost requestPost = new HttpPost(url);
		try {
			requestPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
			HttpResponse httpResponse = httpClient.execute(requestPost);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = httpResponse.getEntity();
				return EntityUtils.toByteArray(httpEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 作用：实现网络访问文件，先给服务器通过“POST”方式提交数据，再返回相应的数据
	 * 
	 * @param url
	 *            ：访问网络的url地址
	 * @param params
	 *            String url：访问url时，需要传递给服务器的参数。 Map<String , Object>
	 * @return byte[]
	 */
	public static byte[] doPostSubmit(String url, Map<String, Object> params) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost requestPost = new HttpPost(url);

		List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
		try {
			if (params != null) {
				for (Map.Entry<String, Object> entry : params.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue().toString();
					BasicNameValuePair nameValuePair = new BasicNameValuePair(
							key, value);
					parameters.add(nameValuePair);
				}
			}
			requestPost
					.setEntity(new UrlEncodedFormEntity(parameters, "utf-8"));
			HttpResponse httpResponse = httpClient.execute(requestPost);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = httpResponse.getEntity();
				return EntityUtils.toByteArray(httpEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// public static byte[] doPostSubmitBody(String url, Map<String, String>
	// map,
	// String filePath, byte[] body_data, String charset) {
	// HttpClient httpClient = new DefaultHttpClient();
	// HttpPost httpPost = new HttpPost(url);
	// HttpResponse response = null;
	// // 实现文件上传：form表单 mutilpart/form-data,封装表单的类
	// MultipartEntity entity = new MultipartEntity();
	// // FileBody存客户端上的数据
	// FileBody body = new FileBody(data);// 文件
	// // 指的是input标签，第一个参数自定义，第二个body指第二个input标签，即文件
	// FormBodyPart bodyPart = new FormBodyPart("form", body);
	// if (params != null && !params.isEmpty()) {
	// for (Map.Entry<String, String> entry : params.entrySet()) {
	// /*
	// * ############ admin ############ 123
	// */
	// // StringBody读取type=“text”的标签，本例读用户名和密码
	// try {
	// entity.addPart(entry.getKey(),
	// new StringBody(entry.getValue()));
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// entity.addPart(bodyPart);
	// httpPost.setEntity(entity);
	// }
	// // 完成所有表单的操作
	// try {
	// response = httpClient.execute(httpPost);
	// if (response.getStatusLine().getStatusCode() == 200) {
	// // 取回服务器端的响应结果
	// byte[] result = EntityUtils.toByteArray(response.getEntity());
	// return result;
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// if (httpClient != null) {
	// httpClient.getConnectionManager().shutdown();
	// }
	// }
	// }
}
