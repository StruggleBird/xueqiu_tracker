package org.zt;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mailer {
  public static void send(String content,String url) {
    try {
      Properties props = new Properties();//key value:配置参数。真正发送邮件时再配置
      props.setProperty("mail.transport.protocol", "smtp");//指定邮件发送的协议，参数是规范规定的
      props.setProperty("mail.host", "smtp.qq.com");//指定发件服务器的地址，参数是规范规定的
      props.setProperty("mail.smtp.auth", "true");//请求服务器进行身份认证。参数与具体的JavaMail实现有关

      Session session = Session.getInstance(props);//发送邮件时使用的环境配置
      session.setDebug(false);
      MimeMessage message = new MimeMessage(session);

      //设置邮件的头
      message.setFrom(new InternetAddress(Config.instance().getMailAddr()));
      message.setRecipients(Message.RecipientType.TO, Config.instance().getMailAddr());
      message.setSubject("[雪球变更提醒]" +content);
      //设置正文
      message.setContent("<a href='" + url + "'>" + content + "</a>", "text/html;charset=utf-8");

      message.saveChanges();

      //发送邮件
      Transport ts = session.getTransport();
      ts.connect(Config.instance().getMailAddr(), Config.instance().getMailToken()); // 密码为授权码不是邮箱的登录密码
      ts.sendMessage(message, message.getAllRecipients());//对象，用实例方法}
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public static void main(String[] args) {
    send("大富科技", "https://xueqiu.com/P/ZH2099343");
  }
}
