package pl.szmaus.secondary.service;


import lombok.extern.slf4j.Slf4j;
import pl.szmaus.configuration.MailConfiguration;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.io.InputStream;

@Slf4j
@Service
public class MailService {

    private final MailConfiguration mailConfiguration;

    public MailService(MailConfiguration mailConfiguration) {
        this.mailConfiguration = mailConfiguration;
    }

    private MimeMessage configurationEmail(Session session, String toEmail,String bccEmail, String subject){
            MimeMessage msg = new MimeMessage(session);
        try
        {
            msg.addHeader("Content-type", "text/HTML; charset=" + mailConfiguration.getCodingSystem());
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setHeader("Content-Type", "text/html; charset=" + mailConfiguration.getCodingSystem());
            msg.setHeader("Content-Language", "pl");
            StringTokenizer stringTokenizer= new StringTokenizer(mailConfiguration.getFromEmail(),"@");
            String personalInternetAddress = stringTokenizer.nextToken();

            msg.setFrom(new InternetAddress(mailConfiguration.getFromEmail(),personalInternetAddress));
            msg.setReplyTo(InternetAddress.parse(mailConfiguration.getFromEmail(), false));
            msg.setSubject(subject,mailConfiguration.getCodingSystem());
            msg.setSentDate(new Date());

            InternetAddress[] myToList = InternetAddress.parse(toEmail);
            InternetAddress[] myBccList = InternetAddress.parse(bccEmail);
            msg.setRecipients(Message.RecipientType.TO,myToList);
            msg.addRecipients(Message.RecipientType.BCC,myBccList);

        }catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }

    public void sendEmailWithImagesAndAttachments(Session session,String toEmail, String bccEmail, String htmlText, String subject, HashMap<String,String> imagesMap, byte[] data, String attachmentPath ){
        try
        {
            MimeMessage msg = configurationEmail(session,toEmail,bccEmail,subject);
            MimeMultipart multipart = new MimeMultipart("related");
            multipart=attachedHtmlTextInEmail(htmlText,multipart);
            multipart=attachedImagesInEmail(imagesMap, multipart);
            if(data!=null && attachmentPath!=null) {
                multipart = attachedAttachmentInEmail(multipart, data, attachmentPath);
            }
            setContentSendEmail(msg,multipart);
        }catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setContentSendEmail( MimeMessage msg,MimeMultipart multipart ) throws MessagingException {
            msg.setContent(multipart);
            log.info("Message is ready");
            System.out.println("body");
            Transport.send(msg);
            log.info("EMail Sent Successfully!!");
    }

    private MimeMultipart attachedHtmlTextInEmail(String htmlText,MimeMultipart multipart) throws MessagingException {
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(htmlText, "text/html; charset=" + mailConfiguration.getCodingSystem());
        multipart.addBodyPart(messageBodyPart);
        return multipart;
    }

    private MimeMultipart attachedImagesInEmail(HashMap<String, String> parametersMap,  MimeMultipart multipart) throws MessagingException, IOException {
      for (Map.Entry<String, String> entry : parametersMap.entrySet()) {
          BodyPart imgPart = new MimeBodyPart();
          InputStream inputStream = this.getClass().getResourceAsStream(entry.getValue());
          ByteArrayDataSource ds1 = new ByteArrayDataSource(inputStream, "image/jpg");
          imgPart.setDataHandler(new DataHandler(ds1));
          imgPart.setHeader("Content-ID", entry.getKey());
          imgPart.setDisposition(MimeBodyPart.INLINE);
          multipart.addBodyPart(imgPart);
      }
        return multipart;
    }

    private MimeMultipart attachedAttachmentInEmail( MimeMultipart multipart,  byte[] data, String path) throws MessagingException, IOException {
        MimeBodyPart attachmentPart = new MimeBodyPart();
        InputStream inputStreamByte = new ByteArrayInputStream(data);
        File filepdf = new File(path);
        Files.copy(inputStreamByte, filepdf.toPath(), StandardCopyOption.REPLACE_EXISTING);
        attachmentPart.attachFile(filepdf);
        multipart.addBodyPart(attachmentPart);
        return multipart;
    }

    @Transactional
    public Session confSmtpHostEmail( ) {
        Properties props = new Properties();
        props.put("mail.smtp.host", mailConfiguration.getMailSmtpHost()); //SMTP Host
        props.put("mail.smtp.port", mailConfiguration.getMailSmtpPort()); //TLS Port
        props.put("mail.smtp.auth", mailConfiguration.getMailSmtpAuth()); //enable authentication
        props.put("mail.smtp.starttls.enable", mailConfiguration.getMailSmtpStarttlsEnable()); //enable STARTTLS
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailConfiguration.getFromEmail(), mailConfiguration.getPassword());
            }
        };
        Session session = Session.getInstance(props, auth);
     //   session.setDebug(true);
        return session;
    }
}
