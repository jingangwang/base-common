/**
 * 
 */
package com.wjg.base.common.utils.httpclient;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ghost
 * @version 创建时间：2016年11月17日 下午3:04:26 类说明 此工厂类实现了httpclient的连接池
 */
public class HttpClientFactory {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(HttpClientFactory.class);
	// 连接超时时间
	private static final int SOCKET_TIMEOUT = 10 * 1000;
	// 传输超时时间
	private static final int CONNECT_TIMEOUT = 30 * 1000;
	// 最大连接数
	private static final int MAX_TOTAL = 200;
	// 设置最大路由
	private static final int MAX_PER_ROUTE = 20;
	// 请求连接池管理器
	private static PoolingHttpClientConnectionManager poolConnManager;
	// httpclient对象
	private static CloseableHttpClient httpClient;
	// 请求器的配置
	private static RequestConfig requestConfig;

	static {
		SSLContextBuilder builder = new SSLContextBuilder();
		try {
			builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					builder.build());
			// 配置同时支付http和https
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
					.<ConnectionSocketFactory> create()
					.register("http",
							PlainConnectionSocketFactory.getSocketFactory())
					.register("https", sslsf).build();
			// 初始化池管理器
			poolConnManager = new PoolingHttpClientConnectionManager(
					socketFactoryRegistry);
			// 最大连接数
			poolConnManager.setMaxTotal(MAX_TOTAL);
			// 设置最大路由数
			poolConnManager.setDefaultMaxPerRoute(MAX_PER_ROUTE);

			requestConfig = RequestConfig.custom()
					.setConnectionRequestTimeout(SOCKET_TIMEOUT)
					.setSocketTimeout(SOCKET_TIMEOUT)
					.setConnectTimeout(CONNECT_TIMEOUT).build();
			// 初始化httpclient
			httpClient = getHttpClient();
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error(e.toString(), e);
		} catch (KeyStoreException e) {
			LOGGER.error(e.toString(), e);
		} catch (KeyManagementException e) {
			LOGGER.error(e.toString(), e);
		}
	}

	/**
	 * 获取httpclient对象
	 */
	private static CloseableHttpClient getHttpClient() {
		CloseableHttpClient httpClient = HttpClients.custom()
				.setConnectionManager(poolConnManager)
				.setDefaultRequestConfig(requestConfig)
				.setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
				.build();
		return httpClient;
	}
	/**
	 * 获取httpclient实例
	 * @return
	 */
	public static CloseableHttpClient getInstance(){
		LOGGER.info("now client pool:"+poolConnManager.getTotalStats().toString());
		return httpClient;
	}
}
