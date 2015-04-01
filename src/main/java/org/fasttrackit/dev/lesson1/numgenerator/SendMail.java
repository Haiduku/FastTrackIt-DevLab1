package org.fasttrackit.dev.lesson1.numgenerator;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by Admin on 28.03.2015.
 */
public class SendMail implements Runnable{

    public void run(){

        final String username = "pcursuri@gmail.com";
        final String password = "cursfast";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("pcursuri@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("mihai.traian.daniel@gmail.com"));
            message.setSubject("Num-guess");
            message.setText("Congratulation! You have won!");

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
