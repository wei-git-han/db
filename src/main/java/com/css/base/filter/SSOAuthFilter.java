package com.css.base.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.css.addbase.apporgan.entity.BaseAppUser;
import com.css.addbase.apporgan.service.BaseAppUserService;
import com.css.app.db.config.controller.SsoAdmin;
import com.netflix.discovery.converters.Auto;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alibaba.fastjson.JSONObject;
import com.css.base.entity.SSORole;
import com.css.base.entity.SSOUser;
import com.css.base.utils.StringUtils;

/*
 * 该组件需要配合Spring MVC 禁止使用统配后缀名
 * 后缀名为css/js/font且为get请求忽略认证
 */
@Component
public class SSOAuthFilter extends OncePerRequestFilter{
	
//	/*
//	 * 单点服务器主机
//	 */
//	@Value("${csse.sso.server.url}")
//	private String sso_server_url;
//	@Value("${csse.sso.server.api.checkToken}")
//	private  String apiCheckToken;
//	@Value("${csse.sso.server.api.userinfo}")
//	private  String apiUserinfo;
//	
//	/**
//	 * 微服务配置
//	 */
//	@Value("${csse.mircoservice.oauth2.client.accessTokenUri}")
//	private String accessTokenUri;
//	//@Value("${csse.mircoservice.oauth2.client.clientId}")
//	//private  String clientId;
//	@Value("${csse.mircoservice.oauth2.client.clientSecret}")
//	private  String clientSecret;
//	@Value("${csse.mircoservice.app}")
//	private  String apiAppinfo;
//	/**
//	 * 不进行权限验证的后缀名文件
//	 */
//	@Value("${csse.sso.server.no-auth-suffixs}")
//	private String no_auth_suffixs;
	
	private static Set<String> suffixs;
	private static  String ssoUserInfoURL;
	private static  String apiAppinfoURL;
	private static  String ssoCheckTokenURL;
	private static  String appSecretconfig;
	private static  String accessTokenUrl;
	public static final ThreadLocal<String> tokenThreadLocal=new ThreadLocal<String>();
	private static final ThreadLocal<HttpServletRequest> requestThreadLocal=new ThreadLocal<HttpServletRequest>();
	@Autowired
	public BaseAppUserService baseAppUserService;
//	@Autowired
//	public void  SSOAuthFilter(BaseAppUserService baseAppUserService){
//		 this.baseAppUserService  = baseAppUserService;
//	}
	private static SSOAuthFilter ssoAuthFilter;

	public void SSOAuthFilter(){
		ssoAuthFilter = this;
		ssoAuthFilter.baseAppUserService = this.baseAppUserService;
	}

	@PostConstruct
	public void init(){
		ssoAuthFilter = this;
		ssoAuthFilter.baseAppUserService = this.baseAppUserService;
	}

	/*
	 * 单点获取用户信息接口地址
	 */
	public void initFilterBean(Environment env){
		/*
		 * 单点服务器主机
		 */
		String sso_server_url = env.getProperty("csse.sso.server.url");
		String apiCheckToken = env.getProperty("csse.sso.server.api.checkToken");
		String apiUserinfo = env.getProperty("csse.sso.server.api.userinfo");
		
		/**
		 * 微服务配置
		 */
		String accessTokenUri = env.getProperty("");
		String clientSecret = env.getProperty("");
		String apiAppinfo = env.getProperty("");
		/**
		 * 不进行权限验证的后缀名文件
		 */
		String no_auth_suffixs = env.getProperty("csse.sso.server.no-auth-suffixs");
		
		String baseUrl = sso_server_url+(sso_server_url.endsWith("/")?"":"/");
		ssoUserInfoURL = baseUrl + apiUserinfo;
		ssoCheckTokenURL = baseUrl + apiCheckToken;
		String grant_type = "client_credentials";
		accessTokenUrl = accessTokenUri+"?grant_type="+grant_type;
		apiAppinfoURL = sso_server_url + apiAppinfo;
		appSecretconfig = clientSecret;
		suffixs=new HashSet<String>();
		for(String suffix:no_auth_suffixs.split(",")){
			suffixs.add(suffix);
		}
	}
	
	/*
	 * 后缀名长度为2-4的get请求忽略认证
	 */
	public boolean noAuth(HttpServletRequest request){
		if(request.getMethod().equals("get")||request.getMethod().toLowerCase().equals("get")){
			String url = request.getRequestURI();
			int index = url.lastIndexOf(".");
			if(url.lastIndexOf(".")>-1){
				String suffix = url.substring(index+1).toLowerCase();
				if(suffixs.contains(suffix)){
					return true;
				}
			}
		}
		return false ;
	}
	private boolean checkToken(String token){
//		try {
//			HttpURLConnection con = (HttpURLConnection)(new URL(ssoCheckTokenURL+token)).openConnection();
//			con.setDoInput(true);
//			con.setRequestMethod("POST");
//			con.setRequestProperty("content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
//			InputStream is = con.getInputStream();
//			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//			String line  = reader.readLine();
//			reader.close();
//			JSONObject jsonResult = JSONObject.parseObject(line);
//			String  result = jsonResult.getString("result");
//			String reslut = jsonResult.getString("reslut");
//			if("success".equals(result) || "success".equals(reslut)){
//				return true;
//			}
//			throw new Exception("token 校验异常"+token);
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return false ;
		BaseAppUser baseAppUser = ssoAuthFilter.baseAppUserService.queryToken(token);
		if(baseAppUser != null){
			return  true;
		}
		return false;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String reqSuffix = request.getRequestURI();
		String name = request.getParameter("username");
		if(!noAuth(request) && !"/api/sso/authen".equals(reqSuffix)){
			String access_token=request.getParameter("access_token");//从URL参数中获取token
			String access_token_c = null;                                  //从cookie中获取token
			Cookie[] cookies = request.getCookies();
			if(cookies!=null){
				for(Cookie cookie:cookies){
				  if("access_token".equals(cookie.getName())){
				    	access_token_c=cookie.getValue();
				   }
			  }}	
			if(access_token != null){
					//校验token  
					if(!checkToken(access_token)){ //如果token校验未通过,抛异常
						try {
							throw new Exception("校验异常"+request.getRequestURI());
						} catch (Exception e) {
							e.printStackTrace();
						}
						return ;
					  }else{			
							if(! access_token.equals(access_token_c)){//如果access_token有更新，则更新cookie
								Cookie cookie_new  = new Cookie("access_token", access_token);
								cookie_new.setPath("/");
								response.addCookie(cookie_new);
							}  
							requestThreadLocal.set(request);
							tokenThreadLocal.set(access_token);
						}
				}else{ //如果access_token为空，则校验access_token_c
					if(!checkToken(access_token_c)){ //如果token校验未通过,抛异常
						try {
							throw new Exception("校验异常"+request.getRequestURI());
						} catch (Exception e) {
							e.printStackTrace();
						}
						return ;
				     }
					requestThreadLocal.set(request);
					tokenThreadLocal.set(access_token_c);
			    }  
		}
		if(reqSuffix.endsWith(".html") || reqSuffix.endsWith(".htm")) {
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "must-revalidate");
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Cache-Control", "no-store");
			response.setDateHeader("Expires", 0);
		}

			filterChain.doFilter(request, response);

	}
	
	@Override
	public void destroy(){
		  
	}
	/*
	 * 获取当前登录人token
	 */
	public static String getToken(){
		return tokenThreadLocal.get();
	}
	
	
	/**
	 * 获取为服务token
	 * @return
	 */
	public static String getAccessToken(String appID,String appSecret) {
		if (StringUtils.isBlank(appSecret)) {
			appSecret = appSecretconfig;
		}
		try {
			JSONObject access_token = new RestTemplate().postForObject(accessTokenUrl+"&client_id="+appID+"&client_secret="+appSecret,null,JSONObject.class);
			System.out.println(appID + ":" +appSecret+":获取token成功-管理员");
			return access_token.getString("access_token");
		} catch (HttpClientErrorException e) {
			System.out.println(appID + ":" +appSecret+":获取token失败-管理员");
			System.err.println(e);
			return "";
		}
		
	}

	/*
	 * 获取单点当前登录人信息
	 */
	public static SSOUser getSUser(){
		HttpSession session=requestThreadLocal.get().getSession(true);
		session.setMaxInactiveInterval(5*60*60);//单位为 秒
		SSOUser user=(SSOUser) session.getAttribute(getToken());
		SSOUser ssoUser = new SSOUser();
		if(user != null && user.getUseruuid()!=null){
			return user;
		}
		try {
//			HttpURLConnection con = (HttpURLConnection) new URL(ssoUserInfoURL).openConnection();
//			con.setDoInput(true);
//			con.setDoOutput(true);
//			con.setRequestMethod("POST");
//			con.setRequestProperty("content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
//			OutputStream os =  con.getOutputStream();
//			String body ="access_token="+ getToken();
//			os.write(body.getBytes());
//			os.flush();
//			InputStream is = con.getInputStream();
//			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//			String line  = reader.readLine();
//			reader.close();
//			user=JSONObject.parseObject(line,SSOUser.class);
			//SsoAdmin ssoAdmin = new SsoAdmin();
			//user = ssoAdmin.getToken(getToken());

			BaseAppUser baseAppUser = ssoAuthFilter.baseAppUserService.queryToken(getToken());
			ssoUser.setUserId(baseAppUser.getUserId());
			ssoUser.setTokenId(baseAppUser.getToken());
			ssoUser.setFullname(baseAppUser.getTruename());
			ssoUser.setOrganId(baseAppUser.getOrganid());
			ssoUser.setSex(baseAppUser.getSex());
			ssoUser.setAccount(baseAppUser.getAccount());
			ssoUser.setUseruuid(baseAppUser.getUserId());
			requestThreadLocal.get().getSession(true).setAttribute(getToken(), ssoUser);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ssoUser ;
	}
	
	/*
	 * 获取当前登录人员角色信息
	 */
	public static SSORole getRole(String appID,String appSecret){
		try {
			String account = SSOAuthFilter.getSUser().getAccount();
			SSORole role= (SSORole) new RestTemplate().getForObject(apiAppinfoURL+"?account="+account+"&access_token=" +getAccessToken(appID,appSecret),SSORole.class, new Object[0]);
			return role;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}



	
	
}
