package pl.szmaus.secondary.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szmaus.Enum.InvoiceStatus;
import pl.szmaus.configuration.MailConfiguration;
import pl.szmaus.configuration.ScheduleConfiguration;
import pl.szmaus.primary.entity.AdFirms;
import pl.szmaus.primary.repository.AdFirmsRepository;
import pl.szmaus.primary.service.AdFirmsService;
import pl.szmaus.secondary.entity.GmFs;
import pl.szmaus.secondary.repository.GmFsRepository;;
import pl.szmaus.secondaryz.repository.R3DocumentFilesRepository;
import pl.szmaus.utility.MailDetails;
import pl.szmaus.utility.MailsUtility;

import javax.mail.Session;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;

import static java.time.LocalDate.now;

@Slf4j
@Service
@ConditionalOnProperty(value="scheduling.enabled", havingValue="true", matchIfMissing = true)
public class OutstandingInvoicesSchedulerService {

    private static final String SIGNATURE ="<br/><br/><strong>Serdecznie pozdrawiamy<br/>" + "<strong>Zespół xxxx<br/></p>" ;
    private static final String IMAGES_LOGO_JPG = "/images/Logo.jpg";
    private static final String IMAGES_LOGO_ID = "Logo1";
    private static final String IMAGES_REMINDER_JPG = "/images/Reminder.jpg";
    private static final String IMAGES_REMINDER_ID = "Reminder1";
    private static final String IMAGES_DEBTS_JPG = "/images/OutstandingDebts.jpg";
    private static final String IMAGES_DEBTS_ID = "OutstandingDebts1";
    private static final String TO_EMAIL_CLIENT = "TEST@gmail.com";
    private static final String BCC_EMAIL_CLIENT = "test1biuro@biuroxxxx.pl";
    private static final String MAIL_BODY_PART1 = "<table style=\"height: 118px; width: 100%; border-collapse: collapse;\" border=\"0\">\n" + "<tbody>\n" + "<tr style=\"height: 46px;\">\n" + "<td style=\"width: 100%; height: 46px;\" colspan=\"2\">\n" + "<p><strong><center>";
    private static final String MAIL_BODY_PART2 =  "</center></strong></p>\n" + "</td>\n" + "<td style=\"width: 100%;\">\n" + "<p><strong>&nbsp;</strong></p>\n" + "</td>\n" + "</tr>\n" + "<tr style=\"height: 18px;\">\n" + "<td style=\"width: 100%; height: 18px;\" colspan=\"2\"><H1></H1><center><img src=cid:";
    private static final String MAIL_BODY_PART3= " ></center></td>\n" + "<td style=\"width: 100%;\">&nbsp;</td>\n" + "</tr>\n" + "<tr style=\"height: 18px;\">\n" + "<td style=\"width: 100%; height: 18px;\" colspan=\"2\">\n";
    private static final String MAIL_BODY_PART4 = SIGNATURE + "<p><img src=cid:" + IMAGES_LOGO_ID +" width=\"170\" height=\"50\"></p>\n" + "</td>\n" + "<td style=\"width: 100%;\">&nbsp;</td>\n" + "</tr>\n" + "</tbody>\n" + "</table>";

    private final ScheduleConfiguration scheduleConfiguration;
    private final MailService mailService;
    private final MailConfiguration mailConfiguration;
    private final GmFsRepository gmFsRepository;
    private final GmFsService gmFsService;
    private final AdFirmsRepository adFirmsRepository;
    private final AdFirmsService adFirmsService;
    private final R3DocumentFilesRepository r3DocumentFilesRepository;

    public OutstandingInvoicesSchedulerService(ScheduleConfiguration scheduleConfiguration, MailService mailService, MailConfiguration mailConfiguration, GmFsRepository gmFsRepository, GmFsService gmFsService, AdFirmsRepository adFirmsRepository, AdFirmsService adFirmsService, R3DocumentFilesRepository r3DocumentFilesRepository) {
        this.scheduleConfiguration = scheduleConfiguration;
        this.mailService = mailService;
        this.mailConfiguration = mailConfiguration;
        this.gmFsRepository = gmFsRepository;
        this.gmFsService = gmFsService;
        this.adFirmsRepository = adFirmsRepository;
        this.adFirmsService = adFirmsService;
        this.r3DocumentFilesRepository = r3DocumentFilesRepository;
    }

    @Transactional
    @Scheduled(cron =  "0 15 8 5,11 * ?") //at 8:15 am 5th an 11th day of each month
    public void trackReminderUnpaidSalesInvoices() {
        Session session = mailService.confSmtpHostEmail();
        gmFsService.currentMonthGmFsList()
                .stream()
                .filter(p->r3DocumentFilesRepository.findByGuid(p.getGuid())!=null)
                .forEach(d -> {
                    String payment = "";
                    HashMap<String,String> imagesMap = new HashMap<>();
                    MailDetails mailDetails = new MailDetails();
                    String toEmail = "";
                    String bccEmail = "";
                    byte[]data=null;
                    String attachmentPath=null;
                    adFirmsService.verificationIfTaxIdIsValid();
                    AdFirms adFirms = adFirmsRepository.findByTaxId(d.getTaxIdReceiver().replaceAll("\\D", ""));
                    payment=gmFsService.paymentForInvoices(d.getNameOfPayment());
                    if(ifNotPaidInvoiceBeforeDeadline(d) == true) {
                        String messageTitle = "Upływający termin płatności";
                        String body = "<p><strong>Dzień dobry,</strong></p>\n" +
                                "<p>"+ d.getIssueInvoiceDate().plusDays(mailConfiguration.getPaymentDate()) +  " upływa termin płatności faktury nr " +  d.getNumber() + " za usługi opieki księgowej.</p>\n" +
                                "<p>Kwota do zapłaty<strong>:  " + d.getGrossAmountInPln().setScale(2, RoundingMode.CEILING).toString().replace(".", ",") + " zł " + "</strong></p>\n" +
                                "<p>Nazwa klienta <strong>:  "+  adFirms.getFullname() + "</strong></p>" +
                                "<p>"+  payment + "</p>\n" +
                                "<p>Jeżeli płatność została zrealizowana, dziękujemy.</p>\n" +
                                "<p>Dzięki terminowej wpłacie odsetki nie zostaną naliczone.</p>\n";
                        mailDetails =MailsUtility.mailsUtility(messageTitle,MAIL_BODY_PART1+messageTitle+MAIL_BODY_PART2 + IMAGES_REMINDER_ID +MAIL_BODY_PART3+body+MAIL_BODY_PART4);
                        d.setStatus(InvoiceStatus.START_SENDING_REMAINDER1.label);
                        gmFsRepository.save(d);
                        imagesMap.put("<"+IMAGES_REMINDER_ID+">",IMAGES_REMINDER_JPG);
                    } else if( ifNotPaidInvoiceAfterDeadline(d) == true) {
                        String messageTitle = "Informujemy o braku wpłaty za wystawioną Fakturę za usługi Księgowe";
                        String body = "<p><strong>Dzień dobry,</strong></p>\n" +
                                "<p> Uprzejmie informuję, że na dzień <strong>" + now() + "</strong> Państwa zadłużenie wobec xxxx Sp. z o.o. z tytułu wystawionych faktur o numerze "+ d.getNumber()+ "<br/>"+
                                "<p>z datą płatności<strong> <font color=red>"+ d.getIssueInvoiceDate().plusDays(mailConfiguration.getPaymentDate()) + "</font></strong>"+ " wynosi <strong>" +d.getGrossAmountInPln().setScale(2, RoundingMode.CEILING).toString().replace(".", ",") + " zł</strong> brutto<br/>"+
                                "<p>Prosimy o uregulowanie ww. zadłużenia w terminie do dnia " + now().plusDays(3)+"</p>\n" +
                                "<p>(liczy się data wpływu środków na konto Biura).</p>\n" +
                                "<p>W razie braku wpłaty w wyżej wskazanym terminie Biuro wyśle do Państwa wezwanie do zapłaty, za\n" +
                                "\n" +"które zostanie naliczona opłata.</p>\n" +
                                "<p>Jeżeli dokonali Państwo spłaty ww. kwoty w dniu dzisiejszym prosimy uznać wiadomość za niebyłą.</p>\n";
                        mailDetails =MailsUtility.mailsUtility("Brak Płatności",MAIL_BODY_PART1+messageTitle+MAIL_BODY_PART2 + IMAGES_DEBTS_ID +MAIL_BODY_PART3+body+MAIL_BODY_PART4);
                        d.setStatus(InvoiceStatus.START_SENDING_REMAINDER2.label);
                        gmFsRepository.save(d);
                        imagesMap.put("<"+IMAGES_DEBTS_ID+">", IMAGES_DEBTS_JPG);
                    }
                    if(ifNotPaidInvoiceBeforeDeadline(d) == true || ifNotPaidInvoiceAfterDeadline(d) == true) {
                        imagesMap.put("<"+IMAGES_LOGO_ID+">", IMAGES_LOGO_JPG);
                        toEmail= TO_EMAIL_CLIENT;
                        bccEmail = BCC_EMAIL_CLIENT;
                        mailService.sendEmailWithImagesAndAttachments(session, toEmail, bccEmail, mailDetails.getMailBody(), mailDetails.getMailTitle(), imagesMap, data, attachmentPath);
                        log.info("mail sent to {}{}; title{}; content:{} ",toEmail, bccEmail, mailDetails.getMailTitle(), mailDetails.getMailBody());
                        if(ifNotPaidInvoiceBeforeDeadline(d) == true) {
                            d.setStatus(InvoiceStatus.REMAINDER1.label);
                            gmFsRepository.save(d);
                        } else if(ifNotPaidInvoiceAfterDeadline(d) == true){
                            d.setStatus(InvoiceStatus.REMAINDER2.label);
                            gmFsRepository.save(d);
                        }
                    }
                });
    }

    private Boolean ifNotPaidInvoiceAfterDeadline(GmFs gmFs) {
        if(gmFs.getStatus().equals(InvoiceStatus.START_SENDING_REMAINDER2.label) || gmFs.getStatus().equals(InvoiceStatus.REMAINDER1.label))
            return true;
        else
            return false;
    }

    private Boolean ifNotPaidInvoiceBeforeDeadline(GmFs gmFs) {
        if(gmFs.getStatus().equals(InvoiceStatus.SENDING_INVOICE.label) || gmFs.getStatus().equals(InvoiceStatus.START_SENDING_REMAINDER1.label))
            return true;
        else
            return false;
    }
}