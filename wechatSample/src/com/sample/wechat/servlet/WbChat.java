package com.sample.wechat.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.sample.wechat.message.Articles;
import com.sample.wechat.message.Item;
import com.sample.wechat.message.Music;
import com.sample.wechat.message.ReplyMusicMessage;
import com.sample.wechat.message.ReplyTextMessage;
import com.sample.wechat.message.ReplyTuwenMessage;
import com.sample.wechat.message.RequestTextMessage;
import com.sample.wechat.util.Final;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class WbChat extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public WbChat() {
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
        //����û�жԷ�����Ϣ�߽�����֤��ֱ�ӷ����ˣ�Ҫ����֤�Ļ��Լ�ȥ�ٶȰ�
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter pw = response.getWriter();
        String echo = request.getParameter("echostr");
        echo = new String(echo.getBytes("ISO-8859-1"),"UTF-8");
        pw.println(echo);
        //�����ύ��Ϊ�����ߵ�URL��Tokenʱ��΢�ŷ�����������GET������д��URL�ϣ�
        //ֻ���㷵�ز���echostrʱ�����Ż���Ϊ������ӿ���ͨ�ģ��Ż��ύ�ɹ�
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
		// ������΢�Ÿ�ƽ̨������Ϣʱ�ͻᵽ����
		// �ظ����ֺ�ͼ����Ϣ���Ҷ�д���ˣ��Լ����Ը����Լ�����Ҫ����Ӧ�Ĵ���
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter pw = response.getWriter();
		String wxMsgXml = IOUtils.toString(request.getInputStream(), "utf-8");
		RequestTextMessage textMsg = null;
		try {
			textMsg = getRequestTextMessage(wxMsgXml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		StringBuffer replyMsg = new StringBuffer();
		String receive = textMsg.getContent();
		String messageType = textMsg.getMessageType();
		String event = textMsg.getEvent();
		String returnXml = null;
		
		switch (messageType) {
		case Final.TEXT:
			returnXml = dealReceiveTextMsg(textMsg, replyMsg, receive);
			break;
			
		case Final.EVENT:
			String cont = "";
			if(Final.EVENT_SUBSCRIBE.equals(event)){
				cont = "��ӭ�㶩�������";
			}else if(Final.EVENT_UNSUBSCRIBE.equals(event)){
				cont = "������´��ٻ�ӭ��";
			}
			returnXml = dealSubOrUnSubEvent(cont,textMsg);
			break;
		}
		
		pw.println(returnXml);
	}

	/**
	 * ������/ȡ�����ĵĻ�ӭ��Ϣ
	 * @param textMsg
	 * @return
	 */
	private String dealSubOrUnSubEvent(String content ,RequestTextMessage textMsg) {
		return getReplyTextMessage(content,
				textMsg.getFromUserName(), textMsg.getToUserName());
	}
	
	/**
	 * ������յ��ı���Ϣ
	 * @param textMsg
	 * @param replyMsg
	 * @param receive
	 * @return
	 */
	private String dealReceiveTextMsg(RequestTextMessage textMsg,
			StringBuffer replyMsg, String receive) {
		String returnXml;
		if (textMsg != null && !receive.equals("")) {
			if (receive.equals("��") || receive.equals("?")) {
				replyMsg.append("��ӭʹ��΢��ƽ̨��");
				replyMsg.append("\r\n1����ǰʱ��");
				replyMsg.append("\r\n2��������");
				replyMsg.append("\r\n3����ͼ��");
				replyMsg.append("\r\n��������������ֱ������������Ϣ");

				returnXml = getReplyTextMessage(replyMsg.toString(),
						textMsg.getFromUserName(), textMsg.getToUserName());

			} else if (receive.equals("2")) {

				// �ظ�������Ϣ
				returnXml = getReplyMusicMessage(textMsg.getFromUserName(),
						textMsg.getToUserName());

			} else if (receive.equals("3")) {

				// �ظ�ͼ��
				returnXml = getReplyTuwenMessage(textMsg.getFromUserName(),
						textMsg.getToUserName());

			} else if (receive.equals("1")) {
				// �ظ�ʱ��
				SimpleDateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");

				replyMsg.append("��ǰʱ��\r\n" + df.format(new Date()));
				returnXml = getReplyTextMessage(replyMsg.toString(),
						textMsg.getFromUserName(), textMsg.getToUserName());

			} else {
				replyMsg.append("�յ��� " + textMsg.getContent());
				returnXml = getReplyTextMessage(replyMsg.toString(),
						textMsg.getFromUserName(), textMsg.getToUserName());

			}
		} else {

			replyMsg.append("�����ˣ���˭���ö�����");
			returnXml = getReplyTextMessage(replyMsg.toString(),
					textMsg.getFromUserName(), textMsg.getToUserName());

		}
		return returnXml;
	}
	
	 //         ��ȡ�����ı���Ϣ
    private RequestTextMessage getRequestTextMessage(String xml){
            XStream xstream = new XStream(new DomDriver());
            
            xstream.alias("xml", RequestTextMessage.class);
            xstream.aliasField("ToUserName", RequestTextMessage.class, "toUserName");
            xstream.aliasField("FromUserName", RequestTextMessage.class, "fromUserName");
            xstream.aliasField("CreateTime", RequestTextMessage.class, "createTime");
            xstream.aliasField("MsgType", RequestTextMessage.class, "messageType");
            xstream.aliasField("Content", RequestTextMessage.class, "content");
            xstream.aliasField("MsgId", RequestTextMessage.class, "msgId");
            xstream.aliasField("Event", RequestTextMessage.class, "event");//�¼����֣�����/ȡ������
            xstream.aliasField("EventKey", RequestTextMessage.class, "eventKey");//�¼����֣�����/ȡ������
            xstream.aliasField("Ticket", RequestTextMessage.class, "ticket");//�¼����֣�����/ȡ������
           
            RequestTextMessage requestTextMessage = (RequestTextMessage)xstream.fromXML(xml); 
            return requestTextMessage;
    }
//    �ظ��ı���Ϣ
    private String getReplyTextMessage(String content, String fromUserName,String toUserName){
        
        ReplyTextMessage we = new ReplyTextMessage();
        we.setMessageType("text");
        we.setFuncFlag("0");
        we.setCreateTime(new Long(new Date().getTime()).toString());
        we.setContent(content);
        we.setToUserName(fromUserName);  
        we.setFromUserName(toUserName);
        XStream xstream = new XStream(new DomDriver()); 
        xstream.alias("xml", ReplyTextMessage.class);
        xstream.aliasField("ToUserName", ReplyTextMessage.class, "toUserName");
        xstream.aliasField("FromUserName", ReplyTextMessage.class, "fromUserName");
        xstream.aliasField("CreateTime", ReplyTextMessage.class, "createTime");
        xstream.aliasField("MsgType", ReplyTextMessage.class, "messageType");
        xstream.aliasField("Content", ReplyTextMessage.class, "content");
        xstream.aliasField("FuncFlag", ReplyTextMessage.class, "funcFlag");
        String xml =xstream.toXML(we);
        return xml;
    }
//    �ظ�������Ϣ
    private String getReplyMusicMessage(String fromUserName,String toUserName){
           
            ReplyMusicMessage we = new ReplyMusicMessage();
            Music music = new Music();
            
            we.setMessageType("music");
            we.setCreateTime(new Long(new Date().getTime()).toString());
            we.setToUserName(fromUserName);  
            we.setFromUserName(toUserName);
            we.setFuncFlag("0");
            
            music.setTitle("�ؼ�|X-man");
            music.setDescription("����˹ �ؼ�  ��������ҵĳ�˼...");
            
            String url = "http://raul.pagekite.me/wechatSample/111.mp3";
            String url2 = "http://raul.pagekite.me/wechatSample/111.mp3";
            music.setMusicUrl(url);
            music.setHqMusicUrl(url2);
            
            we.setMusic(music);
            
            XStream xstream = new XStream(new DomDriver()); 
            xstream.alias("xml", ReplyMusicMessage.class);
            xstream.aliasField("ToUserName", ReplyMusicMessage.class, "toUserName");
            xstream.aliasField("FromUserName", ReplyMusicMessage.class, "fromUserName");
            xstream.aliasField("CreateTime", ReplyMusicMessage.class, "createTime");
            xstream.aliasField("MsgType", ReplyMusicMessage.class, "messageType");
            xstream.aliasField("FuncFlag", ReplyMusicMessage.class, "funcFlag");
            xstream.aliasField("Music", ReplyMusicMessage.class, "Music");
            
            xstream.aliasField("Title", Music.class, "title");
            xstream.aliasField("Description", Music.class, "description");
            xstream.aliasField("MusicUrl", Music.class, "musicUrl");
            xstream.aliasField("HQMusicUrl", Music.class, "hqMusicUrl");
           
            String xml =xstream.toXML(we);
            return xml;
    }
    
//    �ظ�ͼ����Ϣ
    private String getReplyTuwenMessage(String fromUserName,String toUserName){
        ReplyTuwenMessage we = new ReplyTuwenMessage();
        Articles articles = new Articles();
        Item item = new Item();
        
        we.setMessageType("news");
        we.setCreateTime(new Long(new Date().getTime()).toString());
        we.setToUserName(fromUserName);  
        we.setFromUserName(toUserName);
        we.setFuncFlag("0");
        we.setArticleCount(1);
         
        item.setTitle("����");
        item.setDescription("���飨SHUNSUKE����Twitter�����������е�ż��Ȯ���ǹ�����ϵ����Ȯ�����׳�Ӣϵ����������Ϊ���������ȶ��ߺ����硣");
        item.setPicUrl("http://bcs.duapp.com/yishi-music/111.jpg?sign=MBO:97068c69ccb2ab230a497c59d528dcce:hmzcBYxgI4yUaTd9GvahO1GvE%2BA%3D");
        item.setUrl("http://baike.baidu.com/view/6300265.htm");        
       
        articles.setItem(item);
        we.setArticles(articles);
         
        XStream xstream = new XStream(new DomDriver()); 
        xstream.alias("xml", ReplyTuwenMessage.class);
        xstream.aliasField("ToUserName", ReplyTuwenMessage.class, "toUserName");
        xstream.aliasField("FromUserName", ReplyTuwenMessage.class, "fromUserName");
        xstream.aliasField("CreateTime", ReplyTuwenMessage.class, "createTime");
        xstream.aliasField("MsgType", ReplyTuwenMessage.class, "messageType");
        xstream.aliasField("Articles", ReplyTuwenMessage.class, "Articles");
        
        xstream.aliasField("ArticleCount", ReplyTuwenMessage.class, "articleCount");
        xstream.aliasField("FuncFlag", ReplyTuwenMessage.class, "funcFlag");
        
        xstream.aliasField("item", Articles.class, "item");
        
        xstream.aliasField("Title", Item.class, "title");
        xstream.aliasField("Description", Item.class, "description");
        xstream.aliasField("PicUrl", Item.class, "picUrl");
        xstream.aliasField("Url", Item.class, "url");
       
        String xml =xstream.toXML(we);
        return xml;
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
