package pl.szmaus.third.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szmaus.primary.entity.AdFirms;
import pl.szmaus.primary.repository.AdFirmsRepository;
import pl.szmaus.secondary.service.MailService;
import pl.szmaus.third.repository.Raise202203Repository;
import pl.szmaus.utility.MailDetails;
import pl.szmaus.utility.MailsUtility;

import javax.mail.Session;
import java.util.HashMap;


@Slf4j
@Service
@ConditionalOnProperty(value="scheduling.enabled", havingValue="true", matchIfMissing = true)
public class Raise202203SchedulerService {

    private static final String SIGNATURE ="<br/><br/><strong>Serdecznie pozdrawiamy<br/>" + "<strong>Zespół xxxx<br/></p>";
    private static final String IMAGES_RAISEVAT_JPG = "/images/RaiseVat.jpg";
    private static final String IMAGES_RAISEVAT_ID = "RaiseVat1";
    private static final String IMAGES_LOGO_JPG = "/images/Logo.jpg";
    private static final String IMAGES_LOGO_ID = "Logo1";
    private static final String TO_EMAIL_CLIENT = "TEST@gmail.com, TEST2@gmail.com";
    private static final String BCC_EMAIL_CLIENT = "test1biuro@biuroxxxx.pl, test2biuro@biuroxxxx.pl";
    private static final String MAIL_BODY_PART1 ="<table style=\"height: 118px; width: 100%; border-collapse: collapse;\" border=\"0\">\n" + "<tbody>\n" + "<tr style=\"height: 46px;\">\n" + "<td style=\"width: 50%; height: 46px;\" colspan=\"2\">\n" + "<p><strong><center>";
    private static final String MAIL_BODY_PART2 = "<H1></H1><center><img src=cid:" + IMAGES_RAISEVAT_ID +"></center></td>\n" +"<td style=\"width: 50%;\">&nbsp;</td>\n" +"</tr>\n" +"<tr style=\"height: 18px;\">\n" +"<td style=\"width: 50%; height: 18px;\" colspan=\"2\">\n";
    private static final String MAIL_BODY_PART3 = SIGNATURE+ "<p><img src=cid:" + IMAGES_LOGO_ID +" width=\"170\" height=\"50\"></p>\n" + "</td>\n" + "<td style=\"width: 50%;\">&nbsp;</td>\n" + "</tr>\n" + "</tbody>\n" + "</table>";
    private final AdFirmsRepository adFirmsRepository;
    private final MailService mailService;
    private final Raise202203Repository raise202203Repository;
    private final Raise202203Service raise202203Service;


    public Raise202203SchedulerService(AdFirmsRepository adFirmsRepository, MailService mailService, Raise202203Repository raise202203Repository, Raise202203Service raise202203Service) {
        this.adFirmsRepository = adFirmsRepository;
        this.mailService = mailService;
        this.raise202203Repository = raise202203Repository;
        this.raise202203Service = raise202203Service;
       }

    @Transactional
    @Scheduled(cron =  "0 15 8 31 3 2022") //at 8:15 am 1st day of each month
    public void trackRaiseForClient() {
        log.info("Beginning of scheduler");

        raise202203Service.raise202203List()
                .stream()
                .forEach(d -> {
                    Session session = mailService.confSmtpHostEmail();
                    HashMap<String,String> imagesMap = new HashMap<>();
                    MailDetails mailDetails = new MailDetails();
                    String toEmail = "";
                    String bccEmail = "";
                    byte[]data=null;
                    String attachmentPath=null;
                    AdFirms adFirms = adFirmsRepository.findByNumber(d.getNumber());
                    if (d.getRaiseInPln()!=0  ) {
                        String messageTitle = "AKTUALIZACJA CEN ZA ŚWIADCZONE USŁUGI";
                        String body = "<p><strong>Szanowni Państwo, </strong></p>\n" +
                                "<p>Na wstępie chcielibyśmy podziękować za dotychczasowe zaufanie oraz długotrwałą współpracę między Naszymi firmami.<br/></p>"+ "<p>Rok 2022 to prawdziwa rewolucja w podatkach, procedurach i rozliczeniach.<br/></p>"+
                               "<p><strong> Wzrost inflacji najwyższy od przeszło 20 lat, gruntowna zmiana przepisów podatkowych czy elektroniczny obieg faktur to tylko część rzeczy, które mają istotny wpływ na ceny zarówno towarów jak i usług, z którymi muszą się zmierzyć wszyscy przedsiębiorcy w 2022 r. </strong><br/></p>" +
                                "<p>Żyjemy w czasie wielu zmian i wyzwań, które również są sprawdzianem dla naszego Biura. Dla księgowych oznacza to jeszcze więcej pracy i jeszcze mniej czasu. Wspomniane powyżej okoliczności jak też zmiany wprowadzone przez „Polski Ład” znacząco zwiększyły zakres obowiązków dla księgowych.<br/></p>"+
                                "<p>Nasze Biuro, aby móc dalej służyć rzetelnym wsparciem oraz profesjonalizmem swoim Szacownym Klientom w prowadzeniu Ksiąg Rachunkowych oraz gąszczu zmieniających się przepisów podatkowych. Mimo podjętych prób przez ostatnie trzy miesiące utrzymania aktualnych cen jest zmuszone do aktualizacji cennika za wykonywane usługi.<br/></p>"+
                                "<p>Do każdej zmiany cennika usług  odnieśliśmy się w sposób racjonalny i rzetelny mając na względzie okazane przez Państwa zaufanie do świadczonych przez Nasze Biuro usług.<br/></p>"+
                                "<p>Aneks do umowy uzyska  więc następujące brzmienie w §1, pkt1. Zmiana cennika usług (zmiana cen za usługi) wejdzie w życie z dniem 01.04.2022 r.<br/></p>"+
                                "<p>Dziękujemy za zrozumienie  w związku na powyższe okoliczności.<br/></p>";
                                mailDetails = MailsUtility.mailsUtility(messageTitle,MAIL_BODY_PART1+messageTitle+MAIL_BODY_PART2 +body+MAIL_BODY_PART3);
                        d.setIdRaise202203Status(1);
                        raise202203Repository.save(d);
                        imagesMap.put("<"+ IMAGES_RAISEVAT_ID +">", IMAGES_RAISEVAT_JPG);
                       imagesMap.put("<" + IMAGES_LOGO_ID + ">", IMAGES_LOGO_JPG);
                        toEmail = TO_EMAIL_CLIENT;
                        bccEmail = BCC_EMAIL_CLIENT;
                        mailService.sendEmailWithImagesAndAttachments(session, toEmail, bccEmail, mailDetails.getMailBody(), mailDetails.getMailTitle(), imagesMap, data, attachmentPath);
                        log.info("mail sent to {} {}; title{}; content:{} ", toEmail, bccEmail, mailDetails.getMailTitle(), mailDetails.getMailBody());
                        d.setIdRaise202203Status(2);
                        raise202203Repository.save(d);
                    }
                });
    }
}