package org.zt;

import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mailer {
	public static void send(Map<String, Object> oriData, Map<String, Object> data, String url) {
		try {
			Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
			final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
			Properties props = new Properties();// key value:配置参数。真正发送邮件时再配置
			props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
			props.setProperty("mail.transport.protocol", "smtp");// 指定邮件发送的协议，参数是规范规定的
			props.setProperty("mail.host", "smtp.qq.com");// 指定发件服务器的地址，参数是规范规定的
			props.setProperty("mail.smtp.port", "465");
            props.setProperty("mail.smtp.socketFactory.port", "465");
			props.setProperty("mail.smtp.auth", "true");// 请求服务器进行身份认证。参数与具体的JavaMail实现有关
			props.put("mail.smtp.timeout", 5000);
			props.put("mail.smtp.connectiontimeout", 5000);
			props.put("mail.smtp.writetimeout", 5000);

			Session session = Session.getInstance(props);// 发送邮件时使用的环境配置
			session.setDebug(false);
			MimeMessage message = new MimeMessage(session);

			// 设置邮件的头
			message.setFrom(new InternetAddress(Config.instance().getMailAddr()));
			message.setRecipients(Message.RecipientType.TO, Config.instance().getMailAddr());
			message.setSubject("[雪球-"+extractCubeName(data)+"]" + extractStockName(data));
			// 设置正文
			String content = "<a href='" + url + "'>" + url + "</a><br>";
			content += "原有持仓：" + extractStockName(oriData) + "<br>"; 
			content += "最新持仓：" + extractStockName(data) + "<br>"; 
			message.setContent(content, "text/html;charset=utf-8");

			message.saveChanges();

			// 发送邮件
			Transport ts = session.getTransport();
			ts.connect(Config.instance().getMailAddr(), Config.instance().getMailToken()); // 密码为授权码不是邮箱的登录密码
			ts.sendMessage(message, message.getAllRecipients());// 对象，用实例方法}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String extractCubeName(Map<String, Object> data) {
		return data.get("name").toString();
	}

	private static List<String> extractStockName(Map<String, Object> map) {
		if (map.containsKey("name")) {
			map = (Map<String, Object>) map.get("data");
		}
		List<String> result = new ArrayList<>();
		Map<String, Float> stockInfo = StockUtils.extractStockInfo(map);

		stockInfo.forEach((key, weight) -> {
			String stockName = key + "(" + weight + "%)";
			result.add(stockName);
		});

		return result;
	}

}
