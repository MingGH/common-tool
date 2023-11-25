package run.runnable.commontool.util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import lombok.SneakyThrows;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Properties;

/**
 * @author Asher
 * on 2023/11/25
 */
public interface EmailUtil {

    @SneakyThrows
    static void send(String smtpUrl, String port, String userName, String password,
                     String fromEmail,
                     String toEmail,
                     String subject,
                     String content,
                     String attachFilePath){
        Properties prop = new Properties();
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.ssl.trust", smtpUrl);
        prop.put("mail.smtp.host", smtpUrl);
        prop.put("mail.smtp.port", port);
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.ssl.enable", "true");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(content, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        if (StringUtils.hasLength(attachFilePath)) {
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            attachmentBodyPart.attachFile(new File(attachFilePath));
            multipart.addBodyPart(attachmentBodyPart);
        }

        message.setContent(multipart);
        Transport.send(message);
    }

}
