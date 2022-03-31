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
import pl.szmaus.primary.entity.AdFirms;
import pl.szmaus.primary.repository.AdFirmsRepository;
import pl.szmaus.primary.service.AdFirmsService;
import pl.szmaus.secondary.entity.GmFs;
import pl.szmaus.secondary.repository.GmFsRepository;
import pl.szmaus.secondaryz.repository.R3DocumentFilesRepository;
import pl.szmaus.utility.MailDetails;
import pl.szmaus.utility.MailsUtility;

import javax.mail.Session;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@ConditionalOnProperty(value="scheduling.enabled", havingValue="true", matchIfMissing = true)
public class IssuedSalesInvoicesSchedulerService {

    private static final String SIGNATURE ="</i><br/><br/><strong>Serdecznie pozdrawiamy<br/>" + "<strong>Zespół xxxx<br/></p>";
    private static final String IMAGES_LOGO_JPG = "/images/Logo.jpg";
    private static final String IMAGES_LOGO_ID = "Logo1";
    private static final String TO_EMAIL_CLIENT = "TEST@gmail.com";
    private static final String BCC_EMAIL_CLIENT = "test1biuro@biuroxxxx.pl";

    private final MailService mailService;
    private final MailConfiguration mailConfiguration;
    private final GmFsRepository gmFsRepository;
    private final GmFsService gmFsService;
    private final AdFirmsRepository adFirmsRepository;
    private final AdFirmsService adFirmsService;
    private final R3DocumentFilesRepository r3DocumentFilesRepository;

    public IssuedSalesInvoicesSchedulerService(MailService mailService, MailConfiguration mailConfiguration, GmFsRepository gmFsRepository, GmFsService gmFsService, AdFirmsRepository adFirmsRepository, AdFirmsService adFirmsService, R3DocumentFilesRepository r3DocumentFilesRepository) {
        this.mailService = mailService;
        this.mailConfiguration = mailConfiguration;
        this.gmFsRepository = gmFsRepository;
        this.gmFsService = gmFsService;
        this.adFirmsRepository = adFirmsRepository;
        this.adFirmsService = adFirmsService;
        this.r3DocumentFilesRepository = r3DocumentFilesRepository;
    }
    @Transactional
    @Scheduled(cron =  "0 15 8 1 * ?") //at 8:15 am 1st day of each month
        public void trackIssuedSalesInvoices() {
        log.info("Beginning of scheduler");
        Session session = mailService.confSmtpHostEmail();
        gmFsService.currentMonthGmFsList()
                .stream()
                .filter(p->r3DocumentFilesRepository.findByGuid(p.getGuid())!=null)
                .forEach(d -> {
                    MailDetails mailDetails = new MailDetails();
                    HashMap<String,String> imagesMap = new HashMap<>();
                    String toEmail = "";
                    String bccEmail = "";
                    byte[] data = r3DocumentFilesRepository.findByGuid(d.getGuid()).getData();
                    String payment = "";
                    String tempStatus = d.getStatus();
                    adFirmsService.verificationIfTaxIdIsValid();
                    List<AdFirms> adFirmsList =adFirmsRepository.findAllByTaxId(d.getTaxIdReceiver().replaceAll("\\D", ""));
                    AdFirms adFirms = adFirmsRepository.findByTaxId(d.getTaxIdReceiver().replaceAll("\\D", ""));
                    if( adFirms==null){
                        mailDetails = MailsUtility.mailsUtility("Firma" + d.getFullNameReceiver()+ "o NIP-ie " +d.getTaxIdReceiver()+ " nie jest wprowadzona w module administracyjnym w Raks","<br> Proszę srawdź czy firma " + d.getFullNameReceiver() + " jest wprowadzona w module administracyjnym w Raks. Faktura nie została wysłana do klienta ponieważ nie znaleziono tej firmy module administracyjnym w RAKS <br/>" + SIGNATURE +"<img src=cid:"+IMAGES_LOGO_ID+">");
                        toEmail=mailConfiguration.getToEmailIt();
                        bccEmail= mailConfiguration.getBccEmailIt();
                    } else if (adFirmsService.ifSizeOfAdFirmsListIsMoreThenOne( adFirmsList)==true){
                        mailDetails = MailsUtility.mailsUtility("Jest więcej niż jedna firma o tym samym nipie", "<br> Mail nie został wysłany do klienta. <br/> Znalazłem więcej niż jedną firmę w module administracyjnym Raks" + " o NIPie" + d.getTaxIdReceiver() + ", nazwa firmy: "+ adFirmsList.get(0) +adFirmsList.get(1) +" <br/> Należy się zastanowić na który adres mailowy wysyłać informację. Należy sprawdzić czy maile są kompletne. <br/>" + SIGNATURE +"<img src=cid:"+IMAGES_LOGO_ID+">");
                        toEmail=mailConfiguration.getToEmailIt();
                        bccEmail= mailConfiguration.getBccEmailIt();
                    } else if (adFirmsService.ifEmailAdressExists(adFirms)==false){
                        mailDetails = MailsUtility.mailsUtility( "Firma: " + d.getFullNameReceiver() + " nie ma adresu mailowego w module adinistracyjnym Raks","<br> Proszę srawdź dane firmy " + d.getFullNameReceiver() + ", ponieważ brakuje adresu mailowego w module administracyjnym w Raks <br/>"+ SIGNATURE +"<img src=cid:"+IMAGES_LOGO_ID+">");
                        toEmail=mailConfiguration.getToEmailIt();
                        bccEmail= mailConfiguration.getBccEmailIt();
                    } else if(ifGeneratedPdfNotSend(d) == true) {
                        payment=gmFsService.paymentForInvoices(d.getNameOfPayment());
                        mailDetails = MailsUtility.mailsUtility("xxxx e-faktura nr " + d.getNumber(), "<font size=\"3\">Dzień dobry,<br/> W załączeniu przesyłamy e-fakturę wystawioną dla firmy <strong> " + adFirms.getFullname() + ":</strong><br/>" +
                                " nr faktury: <strong>" + d.getNumber() + "<br/> </strong>data wystawienia: <strong>" + d.getIssueInvoiceDate() + "<br/> </strong>kwota do zapłaty: <strong>"
                                + d.getGrossAmountInPln().setScale(2, RoundingMode.CEILING).toString().replace(".", ",") + " zł" +
                                "<br/></strong>termin płatności: <strong>" + d.getIssueInvoiceDate().plusDays(mailConfiguration.getPaymentDate()) + payment +
                                " </font></strong><br/> <br />Informujemy, że załączone dokumenty w formacie PDF są fakturami w rozumieniu Rozporządzenia Ministra Finans&oacute; w z dnia 20 grudnia 2012 r. w sprawie przesyłania faktur w formie elektronicznej, zasad ich przechowywania oraz trybu udostępniania organowi podatkowemu lub organowi kontroli skarbowej. Można je przechowywać w formie elektronicznej lub papierowej po wydrukowaniu. <br/><br/>Treść tej wiadomości zawiera informacje przeznaczone wyłącznie dla jej zamierzonego adresata. Jeżeli nie jesteście Państwo jej adresatem, bądź otrzymaliście ją przez pomyłkę, prosimy o powiadomienie o tym nadawcy oraz trwałe jej usunięcie. " +
                                SIGNATURE+
                                "<img src=cid:"+IMAGES_LOGO_ID+">");
                        d.setStatus(InvoiceStatus.START_SENDING_INV.label);
                        gmFsRepository.save(d);
                        toEmail= TO_EMAIL_CLIENT;
                        bccEmail = BCC_EMAIL_CLIENT;
                    }
                    if( ifEmailShouldBeSent(d) == true ) {
                        imagesMap.put("<"+IMAGES_LOGO_ID+">", IMAGES_LOGO_JPG);
                        mailService.sendEmailWithImagesAndAttachments(session, toEmail,bccEmail, mailDetails.getMailBody(), mailDetails.getMailTitle(), imagesMap, data, System.getProperty("user.dir") +"\\src\\main\\resources\\pdf\\FV.pdf");bccEmail= mailConfiguration.getBccEmailIt();
                        log.info("mail sent to {} {}; title{}; content:{} ", toEmail, bccEmail, mailDetails.getMailTitle(), mailDetails.getMailBody());
                        if(ifGeneratedPdfNotSend(d) == true)
                            saveStatusForInvoices(tempStatus,d);
                    }
                });
    }

    private Boolean ifGeneratedPdfNotSend(GmFs gmFs) {
        if( gmFs.getStatus()==null ||  gmFs.getStatus().equals(InvoiceStatus.START_SENDING_INV.label) ||  gmFs.getStatus().equals(InvoiceStatus.PAID_TO_SEND.label))
            return true;
        else
            return false;
    }

    private Boolean ifEmailShouldBeSent(GmFs gmFs) {
        List<AdFirms> adFirmsList =adFirmsRepository.findAllByTaxId(gmFs.getTaxIdReceiver().replaceAll("\\D", ""));
        AdFirms adFirms = adFirmsRepository.findByTaxId(gmFs.getTaxIdReceiver().replaceAll("\\D", ""));
        if( adFirms == null || adFirmsService.ifSizeOfAdFirmsListIsMoreThenOne( adFirmsList)==true || adFirmsService.ifEmailAdressExists(adFirms)==false || ifGeneratedPdfNotSend(gmFs) == true )
            return true;
        else
            return false;
    }

    private void saveStatusForInvoices(String tempStatus, GmFs gmFs){
        if (tempStatus==null) {
            gmFs.setStatus(InvoiceStatus.SENDING_INVOICE.label);
        } else if (tempStatus.equals(InvoiceStatus.PAID_TO_SEND.label)) {
            gmFs.setStatus(InvoiceStatus.PAID.label);
        }
        gmFsRepository.save(gmFs);
    }

}