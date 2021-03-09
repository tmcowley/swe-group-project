package app.util;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class emailController {
   /**
    * this method allows an email to be sent to a user
    * @param to the email address that is going to recieve the email
    * @param subject the subject of the email
    * @param body the contents of the email
    */
   public void sendEmail(String to, String subject, String body) {
      String from = "resmodus.help@gmail.com";
      String host = "localhost"; // or IP address

      // Get the session object
      Properties properties = System.getProperties();
      properties.setProperty("mail.smtp.host", host);
      Session session = Session.getDefaultInstance(properties);

      try {
         // compose the message
         System.out.println("Notice: Composing email from " + from + ", to " + to);
         MimeMessage message = new MimeMessage(session);
         message.setFrom(new InternetAddress(from));
         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
         message.setSubject(subject);
         message.setText(body);

         // Send message
         // THIS IS THE BIT THAT'S BROKEN
         Transport.send(message);
         System.out.println("Notice: Message sent successfully ...");

      } catch (MessagingException mex) {
         mex.printStackTrace();
      }
   }
}