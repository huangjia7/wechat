package com.sample.wechat.servlet;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.sample.wechat.util.JsonUtil;


public class TokenServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public TokenServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		printMsg();
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		printMsg();
	}
	
	private void printMsg(){
        System.out.println("=========1获取token=========");         
        String accessToken = getToken(GET_TOKEN_URL, AppId, AppSecret);
        // 获取token         
        if (accessToken != null){
        	System.out.println(accessToken); 		
        	System.out.println("=========2获取ip列表=========");   
        	//System.out.println(getWXURLs(accessToken)); 	
        	
        	System.out.println("=========3长地址改短地址=========");   
        	System.out.println(longUrlToShort(accessToken)); 
        }
	}
	
	private static final String AppId = "wx0300c4e94da727ae";
	private static final String AppSecret = "7738839d3deccdf37a1644b7f158314b";
	public static final String GET_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";// 获取acces
	/**
	 * 得到token
	 * @param apiurl
	 * @param appid
	 * @param secret
	 * @return
	 */
	private String getToken(String apiurl, String appid, String secret) {

		String turl = String.format(
				"%s?grant_type=client_credential&appid=%s&secret=%s", apiurl, appid, secret);

		CloseableHttpClient httpclient = HttpClients.createDefault();
		String result = null;
		try {
			HttpResponse res = httpclient.execute(new HttpGet(turl));
			String responseContent = null; // 响应内容
			HttpEntity entity = res.getEntity();
			responseContent = EntityUtils.toString(entity, "UTF-8");
			JsonNode json = JsonUtil.JSON2Object(responseContent);
			// 将json字符串转换为json对象
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				if (json.get("errcode") != null) {// 错误时微信会返回错误码等信息，{"errcode":40013,"errmsg":"invalid appid"}
				} else {// 正常情况下{"access_token":"ACCESS_TOKEN","expires_in":7200}
					result = json.get("access_token").textValue();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭连接 ,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}
	}
	/**
	 * 得到微信的IP地址列表
	 * @param Tokens
	 * @return
	 */
	private String getWXURLs(String Tokens){
		String url = "https://api.weixin.qq.com/cgi-bin/getcallbackip?access_token={0}";
		String wxUrl = MessageFormat.format(url, Tokens);
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		StringBuffer sb = new StringBuffer("");
		try {
			HttpResponse res = httpclient.execute(new HttpGet(wxUrl));
			String responseContent = null; // 响应内容
			HttpEntity entity = res.getEntity();
			responseContent = EntityUtils.toString(entity, "UTF-8");
			JsonNode json = JsonUtil.JSON2Object(responseContent);
			// 将json字符串转换为json对象
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				if (json.get("errcode") != null) {// 错误时微信会返回错误码等信息，{"errcode":40013,"errmsg":"invalid appid"}
				} else {// 正常情况下{"access_token":"ACCESS_TOKEN","expires_in":7200}
					JsonNode ips = json.get("ip_list");
				      // 遍历 info 内的 array  
				      if (ips.isArray()){  
				        for (JsonNode objNode : ips) {  
				          sb.append(objNode.toString()).append("\n\r");
				        }  
				      } 					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭连接 ,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return sb.toString();
		}
	}
	
	/**
	 * 长链接 转 短链接
	 * @param Tokens
	 * @return
	 */
	private String longUrlToShort(String Tokens){
		String url = "https://api.weixin.qq.com/cgi-bin/shorturl?access_token={0}";
		String wxUrl = MessageFormat.format(url, Tokens);
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		StringBuffer sb = new StringBuffer("");
		try {
			HttpPost post = new HttpPost(wxUrl);
		       // 设置2个post参数，一个是scope、一个是q
	        List<NameValuePair> parameters = new ArrayList<NameValuePair>(0);
	        parameters.add(new BasicNameValuePair("action", "long2short"));
	        parameters.add(new BasicNameValuePair("long_url", "http://www.oschina.net/code/snippet_2499632_51935"));
	        // 构造一个form表单式的实体
	        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters);
	        // 将请求实体设置到httpPost对象中
	        post.setEntity(formEntity);
			
			HttpResponse res = httpclient.execute(post);
			String responseContent = null; // 响应内容
			HttpEntity entity = res.getEntity();
			responseContent = EntityUtils.toString(entity, "UTF-8");
			JsonNode json = JsonUtil.JSON2Object(responseContent);
			// 将json字符串转换为json对象
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				if (json.get("errcode") != null) {// 错误时微信会返回错误码等信息，{"errcode":40013,"errmsg":"invalid appid"}
				} else {// 正常情况下{"access_token":"ACCESS_TOKEN","expires_in":7200}
					JsonNode ips = json.get("short_url");
					System.out.println("转换后的短链接："+ips.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭连接 ,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return sb.toString();
		}
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
