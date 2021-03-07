package app.util;

import java.util.*;  
import javax.mail.*;  
import javax.mail.internet.*;  
import javax.activation.*;  

  
public class SendEmail  
{  
 public static void sendEmail(String to, String subject, String body){  
      String from = "resmodus.help@gmail.com"; 
      String host = "localhost";//or IP address  
  
     //Get the session object  
      Properties properties = System.getProperties();  
      properties.setProperty("mail.smtp.host", host);  
      Session session = Session.getDefaultInstance(properties);  
  
     //compose the message  
      try{  
         MimeMessage message = new MimeMessage(session);  
         message.setFrom(new InternetAddress(from));  
         message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));  
         message.setSubject(subject);  
         message.setText(body);  
  
         // Send message  
         Transport.send(message);
         System.out.println("message sent successfully....");  
  
      }catch (MessagingException mex) {mex.printStackTrace();}  
   }  
}  