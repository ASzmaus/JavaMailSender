package pl.szmaus.third.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szmaus.configuration.MailConfiguration;
import pl.szmaus.configuration.ScheduleConfiguration;
import pl.szmaus.primary.repository.AdFirmsRepository;
import pl.szmaus.primary.service.AdFirmsService;
import pl.szmaus.secondary.service.MailService;
import pl.szmaus.third.entity.EKsiegowy;
import pl.szmaus.third.repository.EKsiegowyUserRepository;
import pl.szmaus.utility.MailDetails;
import pl.szmaus.utility.MailsUtility;

import javax.mail.Session;
import java.util.HashMap;


@Slf4j
@Service
@ConditionalOnProperty(value="scheduling.enabled", havingValue="true", matchIfMissing = true)
public class EKsiegowySchedulerService {

    private static final String SIGNATURE ="<br/><br/><strong>Serdecznie pozdrawiamy<br/>" + "<strong>Zespół xxxx<br/></p>";
    private static final String IMAGES_LOGO_JPG = "/images/Logo.jpg";
    private static final String IMAGES_LOGO_ID = "Logo1";
    private static final String IMAGES_EACCOUNTANT_JPG = "/images/eKsiegowy.jpg";
    private static final String IMAGES_EACCOUNTANT_ID = "eKsiegowy1";
    private static final String TO_EMAIL_CLIENT = "TEST@gmail.com";
    private static final String BCC_EMAIL_CLIENT = "test1biuro@biuroxxxx.pl";
    private static final String MAIL_BODY_PART1 ="<table style=\"height: 118px; width: 100%; border-collapse: collapse;\" border=\"0\">\n" + "<tbody>\n" + "<tr style=\"height: 46px;\">\n" + "<td style=\"width: 50%; height: 46px;\" colspan=\"2\">\n" + "<p><strong><img src=cid:" + IMAGES_LOGO_ID + " width=\"170\" height=\"50\"></strong></p>\n" + "<p><strong><center>";
    private static final String MAIL_BODY_PART2 = "</center></strong></p>\n" +"</td>\n" +"<td style=\"width: 50%;\">\n" +"<p><strong>&nbsp;</strong></p>\n" +"</td>\n" +"</tr>\n" +"<tr style=\"height: 18px;\">\n" +"<td style=\"width: 50%; height: 18px;\" colspan=\"2\"><H1></H1><center><img src=cid:" + IMAGES_EACCOUNTANT_ID + " width=\"210\" height=\"50\"/></center></td>\n" +"<td style=\"width: 50%;\">&nbsp;</td>\n" +"</tr>\n" +"<tr style=\"height: 18px;\">\n" +"<td style=\"width: 50%; height: 18px;\" colspan=\"2\">\n";
    private static final String MAIL_BODY_PART3 = SIGNATURE+ "<p><img src=cid:" + IMAGES_LOGO_ID +" width=\"170\" height=\"50\"></p>\n" + "</td>\n" + "<td style=\"width: 50%;\">&nbsp;</td>\n" + "</tr>\n" + "</tbody>\n" + "</table>";

    private final MailService mailService;
    private final MailConfiguration mailConfiguration;
    private final EKsiegowyUserRepository eKsiegowyUserRepository;
    private final EKsiegowyService eKsiegowyService;


    public EKsiegowySchedulerService(ScheduleConfiguration scheduleConfiguration, MailService mailService, MailConfiguration mailConfiguration, AdFirmsRepository adFirmsRepository, AdFirmsService adFirmsService, EKsiegowyUserRepository eKsiegowyUserRepository, EKsiegowyService eKsiegowyService) {
        this.mailService = mailService;
        this.mailConfiguration = mailConfiguration;
        this.eKsiegowyUserRepository = eKsiegowyUserRepository;
        this.eKsiegowyService = eKsiegowyService;
     }

    @Transactional
    @Scheduled(cron =  "0 15 8 1 * ?") //at 8:15 am 1st day of each month
    public void trackIssuedSalesInvoices() {
        log.info("Beginning of scheduler");

        eKsiegowyService.eKsiegowyList()
                .stream()
                .forEach(d -> {
                    Session session = mailService.confSmtpHostEmail();
                    HashMap<String,String> imagesMap = new HashMap<>();
                    MailDetails mailDetails = new MailDetails();
                    String toEmail = "";
                    String bccEmail = "";
                    byte[]data=null;
                    String attachmentPath=null;
                    if (d.getPassword() == null|| d.getEmail() == null ) {
                       mailDetails = MailsUtility.mailsUtility("<br> Proszę srawdź dane firmy " + d.getName() + d.getSurname() + ", ponieważ brakuje adresu mailowego  loginu lub hasła w Bazie MsSQL <br/>","Niekompletne dane dla  " + d.getName() + d.getSurname() + " w bazie MSSQL" + "<br/><br/>Zmieniamy się dla Ciebie :) <br/>" + SIGNATURE+ "<img src=cid:" + IMAGES_LOGO_ID +" width=\"170\" height=\"50\">");
                    } else  if(ifInfoShouldBeSend(d)==true) {
                       mailDetails = MailsUtility.mailsUtility("Jesteśmy gotowi. Wprowadzamy nowe rozwiązanie eKsiegowy dla firmy " + d.getCompanyName()+ "!","<p><H1>Jesteśmy gotowi. Wprowadzamy nowe rozwiązanie eKsiegowy dla firmy " + d.getCompanyName()+ "!</H1><center><img src=cid:" + IMAGES_EACCOUNTANT_ID + " width=\"210\" height=\"50\"/></center></p>\n" +
                                     "<p><br><font size=\"3\">Dzień dobry,<br/> Wprowadziliśmy to usprawnienie, aby zwiększyć jakość naszych usług jakie dla Ciebie świadczymy." +
                                            "eKsiegowy to nowoczesna webowa aplikacja uruchamiana w przeglądarce internetowej. Dzięki niej będziesz mógł m.in. przekazać nam swoje dokumenty księgowe oraz sprawdzić ich statusu." +
                                            "To nie wszystko - jest też aplikacja mobilna, dzięki której tuż po służbowych zakupach w intuicyjny i wygodny sposób zeskanujesz i prześlesz do nas dokument zakupu. To oznacza, że będziesz miał swoje dokumenty księgowe zawsze przy sobie.\n" +
                                            "Już niedługo prześlemy szczegóły konta, które założymy dla Ciebie. " +
                                            "<br/><br/>Zmieniamy się dla Ciebie :) <br/>" +
                                            SIGNATURE+
                                            "<img src=cid:" + IMAGES_LOGO_ID +" width=\"170\" height=\"50\">");
                        d.setIdEKsiegowyStatus(1);
                        eKsiegowyUserRepository.save(d);

                    } else if(ifLoginPasswordShouldBeSend(d)==true){
                        String messageTitle = "Szczegóły logowania do eKsiegowy";
                        String body = "<p>Dzień dobry,</p>\n" +
                                "<p>Właśnie utworzyliśmy dla firmy " + d.getCompanyName()  + " konto w eKsiegowym. Pamiętaj, aby nikomu nie podawać Twojego hasła. Jest ono przeznaczone tylko dla Ciebie. Tuż po pierwszym zalogowaniu się zmień hasło na takie jakie zapamiętasz. Jest to bardzo ważne.</p>\n" +
                                "<p><strong>Login</strong>:  " +  d.getEmail() + "</p>\n" +
                                "<p><strong>Hasło</strong>:  " + d.getPassword() + "</p>\n" +
                                "<p><strong>Strona</strong>: <a href=\"https://biuroxxxx.eksiegowy.eu/\">https://biuroxxxx.eksiegowy.eu/</a></p>\n" +
                                "<p><strong>Link do pomocy eKsięgowy: </strong> <a href=\"http://pomoc.helpoffice.pl/eksiegowy.html\">http://pomoc.helpoffice.pl/eksiegowy.html</a></p>\n" +
                                "<p><strong>Link do aplikacji mobilnej:</strong></p>\n" +
                                "</td>\n" + "<td style=\"width: 50%;\">\n" + "<p>&nbsp;</p>\n" + "</td>\n" + "</tr>\n" + "<tr style=\"height: 18px;\">\n" + "<td style=\"width: 50%; height: 18px;\"><center><a href=\"https://play.google.com/store/apps/details?id=pl.helpoffice.epanelksiegowego\"><img src=cid:GooglePlay2 /></a></center></td>\n" + "<td style=\"width: 50%;\"><center><a href=\"https://www.appstoremagazine.com/economia/eksiegowy/\"><img src=cid:AppStore1 /></a></center></td>\n" + "</tr>\n" + "<tr style=\"height: 18px;\">\n" + "<td style=\"width: 50%; height: 18px;\" colspan=\"2\">\n";
                        mailDetails = MailsUtility.mailsUtility("Logowanie do eKsiegowego dla firmy " + d.getCompanyName()+ "!",MAIL_BODY_PART1+messageTitle+MAIL_BODY_PART2 +body+MAIL_BODY_PART3);
                        d.setIdEKsiegowyStatus(3);
                        eKsiegowyUserRepository.save(d);
                        imagesMap.put("<GooglePlay2>", "/images/GooglePlay.jpg");
                        imagesMap.put("<AppStore1>", "/images/AppStore.jpg");
                        }

                    if (ifEmailShouldBeSent(d)==true) {
                        imagesMap.put("<" + IMAGES_EACCOUNTANT_ID + ">", IMAGES_EACCOUNTANT_JPG);
                        imagesMap.put("<" + IMAGES_LOGO_ID + ">", IMAGES_LOGO_JPG);
                        toEmail = TO_EMAIL_CLIENT;
                        bccEmail = BCC_EMAIL_CLIENT;
                        mailService.sendEmailWithImagesAndAttachments(session, toEmail, bccEmail, mailDetails.getMailBody(), mailDetails.getMailTitle(), imagesMap, data, attachmentPath);
                        log.info("mail sent to {} {}; title{}; content:{} ", toEmail, bccEmail, mailDetails.getMailTitle(), mailDetails.getMailBody());
                        if (ifInfoShouldBeSend(d) == true) {
                            d.setIdEKsiegowyStatus(2);
                            eKsiegowyUserRepository.save(d);
                        } else if (ifLoginPasswordShouldBeSend(d) == true) {
                            d.setIdEKsiegowyStatus(4);
                            eKsiegowyUserRepository.save(d);
                        }
                    }
                });
    }

    private Boolean ifLoginPasswordIsNull(EKsiegowy eKsiegowy) {
        if(eKsiegowy.getPassword() == null|| eKsiegowy.getEmail() == null )
            return true;
        else
            return false;
    }

    private Boolean ifInfoShouldBeSend(EKsiegowy eKsiegowy) {
        if(eKsiegowy.getIdEKsiegowyStatus()==null || eKsiegowy.getIdEKsiegowyStatus()==1 )
            return true;
        else
            return false;
    }

    private Boolean ifLoginPasswordShouldBeSend(EKsiegowy eKsiegowy) {
        if(eKsiegowy.getIdEKsiegowyStatus()==2 || eKsiegowy.getIdEKsiegowyStatus()==3)
            return true;
        else
            return false;
    }

    private Boolean ifEmailShouldBeSent(EKsiegowy eKsiegowy) {
        if( ifLoginPasswordIsNull(eKsiegowy)==true || ifInfoShouldBeSend(eKsiegowy)==true || ifLoginPasswordShouldBeSend(eKsiegowy)==true)
            return true;
        else
            return false;
    }

}