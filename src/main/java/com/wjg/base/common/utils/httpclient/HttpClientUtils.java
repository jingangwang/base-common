/**
 * 
 */
package com.wjg.base.common.utils.httpclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * @author ghost
 * @version 创建时间：2016年11月17日 下午3:54:30 类说明 httpclient 工具类 （带连接池）
 */
public class HttpClientUtils {
	
	private static HttpClientUtils instance = null;
	
	private HttpClientUtils(){
	}
	
	public static HttpClientUtils getInstance(){
		if(instance == null){
			instance = new HttpClientUtils();
		}
		return instance;
	}
	/**
	 * 发送post请求无参数
	 * @param url	目标地址
	 * @return		执行结果
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String sendHttpPost(String url) throws ClientProtocolException, IOException{
		HttpPost httpPost = new HttpPost(url);
		return sendHttpRequest(httpPost);
	}
	/**
	 * 发送post请求带参数 json  xml  string
	 * @param url	目标地址
	 * @param params	
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String sendHttpPost(String url,String params,String contentType) throws ClientProtocolException, IOException{
		HttpPost httpPost = new HttpPost(url);
		StringEntity stringEntity = new StringEntity(params, "UTF-8");
		httpPost.addHeader("Content-Type", contentType);
		httpPost.setEntity(stringEntity);
		return sendHttpRequest(httpPost);
	}
	/**
	 * 发送post请求带参数
	 * @param url		目标地址
	 * @param params	参数map
	 * @return			执行结果
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String sendHttpPost(String url,Map<String,String> params) throws ClientProtocolException, IOException{
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		for (String key : params.keySet()) {
			pairs.add(new BasicNameValuePair(key, params.get(key)));
		}
		httpPost.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
		return sendHttpRequest(httpPost);
	}
	/**
	 * 发送get请求
	 * @param url	目标地址
	 * @return		返回结果
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String sendHttpGet(String url) throws ClientProtocolException, IOException{
		HttpGet httpGet = new HttpGet(url);
		return sendHttpRequest(httpGet);
	}
	/**
	 * 发送get请求带参数
	 * @param url		目标地址
	 * @param params	请求参数map
	 * @return			执行结果
	 * @throws ParseException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public String sendHttpGet(String url,Map<String,String> params) throws ParseException, UnsupportedEncodingException, IOException, URISyntaxException{
		HttpGet httpGet = new HttpGet(url);
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		for (String key : params.keySet()) {
			pairs.add(new BasicNameValuePair(key, params.get(key)));
		}
		String reqParams = EntityUtils.toString(new UrlEncodedFormEntity(pairs));
		httpGet.setURI(new URI(httpGet.getURI().toString()+"?"+reqParams));
		return sendHttpRequest(httpGet);
	}
	/**
	 * 此方法执行get或者post请求
	 * @param request
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private String sendHttpRequest(HttpUriRequest request) throws ClientProtocolException, IOException{
		String respContent = null;
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient = null;
		try {
			httpClient = HttpClientFactory.getInstance();
			response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			respContent = EntityUtils.toString(entity, "UTF-8");
		} finally {
			if (response != null) {
				response.close();
			}
		}
		return respContent;
	}
}
