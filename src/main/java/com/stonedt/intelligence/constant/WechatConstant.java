package com.stonedt.intelligence.constant;
/**
 * 微信相关
 * @author wangyi
 *
 */
public class WechatConstant {
	
	// appid
	public final static String AppID = "wx27ae60471bc5516f";
	
	//AppSecret
	public final static String AppSecret = "18f16a4529395014b66cb8394aeddaf9";
	
	//获取AccessToken接口
	public final static String api_wechat_AccessToken = "18f16a4529395014b66cb8394aeddaf9";
	
	//发送模板消息
	public final static String api_wechat_template = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx27ae60471bc5516f&secret=18f16a4529395014b66cb8394aeddaf9";
	
	//获取二维码ticket
	public final static String api_wechat_qrcode = "18f16a4529395014b66cb8394aeddaf9";
	
	//模板id
	public final static String api_wechat_template_id = "Yr-BmtZJctEJSgfq2GfqPnb44iLc6exhesUuUcrasdk";
	
	//模板消息推送
	public final static String api_wechat_templatepush = "https://api.weixin.qq.com/cgi-bin/message/template/send";
	
	
	//生成临时二维码
	public final static String api_wechat_temporaryqrcode = "https://api.weixin.qq.com/cgi-bin/qrcode/create";
	
	
	/**
     * 授权
     */
    public static final String AUTH_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?";
    
    
    //
    public static final String AUTH_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?";
	
    
	/**
	 * 
	 */
    public static final String AUTH_Basic_Info = "https://api.weixin.qq.com/cgi-bin/user/info?";
    
    
    
    public static final String api_wechat_user ="https://api.weixin.qq.com/cgi-bin/user/get?";
    
	
	
	
	
	

}
