package cn.izern.commons.util;

import java.io.IOException;  
import java.net.URL;  
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;  
import java.util.Map;  
  
import java.util.Set;

import org.apache.http.HttpEntity;  
import org.apache.http.NameValuePair;  
import org.apache.http.client.config.RequestConfig;  
import org.apache.http.client.entity.UrlEncodedFormEntity;  
import org.apache.http.client.methods.CloseableHttpResponse;  
import org.apache.http.client.methods.HttpGet;  
import org.apache.http.client.methods.HttpPost;  
import org.apache.http.conn.ssl.DefaultHostnameVerifier;  
import org.apache.http.conn.util.PublicSuffixMatcher;  
import org.apache.http.conn.util.PublicSuffixMatcherLoader;  
import org.apache.http.entity.StringEntity;  
import org.apache.http.impl.client.CloseableHttpClient;  
import org.apache.http.impl.client.HttpClients;  
import org.apache.http.message.BasicNameValuePair;  
import org.apache.http.util.EntityUtils;  

/**
 * httpclient工具类
 * @author zern
 *
 * 2016年9月23日上午11:17:59
 */
public class HttpClientUtils {
	
    private static RequestConfig requestConfig = RequestConfig.custom()  
            .setSocketTimeout(30000)  
            .setConnectTimeout(30000)  
            .setConnectionRequestTimeout(30000)  
            .build();  
      
    /** 
     * 发送 post请求 
     * @param httpUrl 地址 
     */  
    public static String sendHttpPost(String httpUrl) {  
        HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost    
        return sendHttpPost(httpPost);  
    }  
      
    /**
     * 发送 post请求 
     * @param httpUrl 地址 
     * @param params 参数(格式:key1=value1&key2=value2) 
     * @return
     */
    public static String sendHttpPost(String httpUrl, String params) {  
        HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost    
        try {  
            //设置参数  
            StringEntity stringEntity = new StringEntity(params, "UTF-8");  
            httpPost.setEntity(stringEntity);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return sendHttpPost(httpPost);  
    }  
      
    /** 
     * 发送 post请求 
     * @param httpUrl 地址 
     * @param maps 参数 
     */  
    public static String sendHttpPost(String httpUrl, Map<String, String> maps) {  
        HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost    
        // 创建参数队列    
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();  
        for (String key : maps.keySet()) {  
            nameValuePairs.add(new BasicNameValuePair(key, maps.get(key)));  
        }  
        try {  
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return sendHttpPost(httpPost);  
    }  
      
      
    /** 
     * 发送Post请求 
     * @param httpPost 
     * @return 
     */  
    private static String sendHttpPost(HttpPost httpPost) {  
        CloseableHttpClient httpClient = null;  
        CloseableHttpResponse response = null;  
        HttpEntity entity = null;  
        String responseContent = null;  
        try {  
            // 创建默认的httpClient实例.  
            httpClient = HttpClients.createDefault();  
            httpPost.setConfig(requestConfig);  
            // 执行请求  
            response = httpClient.execute(httpPost);  
            entity = response.getEntity();  
            responseContent = EntityUtils.toString(entity, "UTF-8");  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                // 关闭连接,释放资源  
                if (response != null) {  
                    response.close();  
                }  
                if (httpClient != null) {  
                    httpClient.close();  
                }  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return responseContent;  
    }  
  
    /** 
     * 发送 get请求 
     * @param httpUrl 
     */  
    public static String sendHttpGet(String httpUrl) {  
        HttpGet httpGet = new HttpGet(httpUrl);// 创建get请求
        return sendHttpGet(httpGet);  
    }
    
   /**
    * 发生 get请求
    * @param httpUrl
    * @param header 头信息
    * @return
    */
    public static String sendHttpGet(String httpUrl, Map<String, String> header) {  
        HttpGet httpGet = new HttpGet(httpUrl);// 创建get请求
        Set<String> keys = header.keySet();
        for (String key : keys) {
			httpGet.setHeader(key, header.get(key));
		}
        return sendHttpGet(httpGet);  
    }  
      
    /** 
     * 发送 get请求Https 
     * @param httpUrl 
     */  
    public static String sendHttpsGet(String httpUrl) {  
        HttpGet httpGet = new HttpGet(httpUrl);// 创建get请求  
        return sendHttpsGet(httpGet);  
    }  
    
    /** 
     * 发送 get请求Https 
     * @param httpUrl 
     */  
    public static String sendHttpsGet(String httpUrl, Map<String, String> header) {  
        HttpGet httpGet = new HttpGet(httpUrl);// 创建get请求  
        Set<String> keys = header.keySet();
        for (String key : keys) {
			httpGet.setHeader(key, header.get(key));
		}
        return sendHttpsGet(httpGet);  
    }
      
    /** 
     * 发送Get请求 
     * @param httpPost 
     * @return 
     */  
    private static String sendHttpGet(HttpGet httpGet) {  
        CloseableHttpClient httpClient = null;  
        CloseableHttpResponse response = null;  
        HttpEntity entity = null;  
        String responseContent = null;  
        try {  
            // 创建默认的httpClient实例.  
            httpClient = HttpClients.createDefault();  
            httpGet.setConfig(requestConfig);  
            // 执行请求  
            response = httpClient.execute(httpGet);  
            entity = response.getEntity();  
            responseContent = EntityUtils.toString(entity, "UTF-8");  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                // 关闭连接,释放资源  
                if (response != null) {  
                    response.close();  
                }  
                if (httpClient != null) {  
                    httpClient.close();  
                }  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return responseContent;  
    }  
      
    /** 
     * 发送Get请求Https 
     * @param httpPost 
     * @return 
     */  
    private static String sendHttpsGet(HttpGet httpGet) {  
        CloseableHttpClient httpClient = null;  
        CloseableHttpResponse response = null;  
        HttpEntity entity = null;  
        String responseContent = null;  
        try {  
            // 创建默认的httpClient实例.  
            PublicSuffixMatcher publicSuffixMatcher = PublicSuffixMatcherLoader.load(new URL(httpGet.getURI().toString()));  
            DefaultHostnameVerifier hostnameVerifier = new DefaultHostnameVerifier(publicSuffixMatcher);  
            httpClient = HttpClients.custom().setSSLHostnameVerifier(hostnameVerifier).build();  
            httpGet.setConfig(requestConfig);  
            // 执行请求  
            response = httpClient.execute(httpGet);  
            entity = response.getEntity();  
            responseContent = EntityUtils.toString(entity, "UTF-8");  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                // 关闭连接,释放资源  
                if (response != null) {  
                    response.close();  
                }  
                if (httpClient != null) {  
                    httpClient.close();  
                }  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return responseContent;  
    }
}
