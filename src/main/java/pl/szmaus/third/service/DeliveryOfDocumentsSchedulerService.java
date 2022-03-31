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
import pl.szmaus.primary.entity.AdFirms;
import pl.szmaus.secondary.entity.AdditionlFilesReceivedDocuments;
import pl.szmaus.primary.repository.AdFirmsRepository;
import pl.szmaus.primary.service.AdFirmsService;
import pl.szmaus.secondary.entity.GmFs;
import pl.szmaus.secondary.repository.AdditionlFilesReceivedDocumentsRepository;
import pl.szmaus.secondary.repository.GmFsRepository;
import pl.szmaus.secondary.service.AdditionlFilesReceivedDocumentsService;
import pl.szmaus.secondary.service.GmFsService;
import pl.szmaus.secondary.service.MailService;
import pl.szmaus.secondaryz.repository.R3DocumentFilesRepository;
import pl.szmaus.third.entity.EKsiegowy;
import pl.szmaus.third.entity.ReceivedDocumentsFromClients;
import pl.szmaus.third.repository.ReceivedDocumentsFromClientsRepository;
import pl.szmaus.utility.MailDetails;
import pl.szmaus.utility.MailsUtility;

import javax.mail.Session;
import java.util.HashMap;
import java.util.List;
import static java.time.LocalDate.now;

@Slf4j
@Service
@ConditionalOnProperty(value="scheduling.enabled", havingValue="true", matchIfMissing = true)
public class DeliveryOfDocumentsSchedulerService {

    private static final String SIGNATURE = "<br/><br/><strong>Serdecznie pozdrawiamy<br/>" + "<strong>Zespół xxxx<br/></p>";
    private static final String IMAGES_LOGO_JPG = "/images/Logo.jpg";
    private static final String IMAGES_LOGO_ID = "Logo1";
    private static final String IMAGES_RECEIPT_JPG = "/images/Receipt.jpg";
    private static final String IMAGES_RECEIPT_ID = "Receipt1";
    private static final String IMAGES_DEADLINE_JPG = "/images/deadline.jpg";
    private static final String IMAGES_DEADLINE_ID = "deadline1";
    private static final String TO_EMAIL_CLIENT = "TEST@gmail.com";
    private static final String BCC_EMAIL_CLIENT = "test1biuro@biuroxxxx.pl";

    private final ScheduleConfiguration scheduleConfiguration;
    private final MailService mailService;
    private final MailConfiguration mailConfiguration;
    private final GmFsRepository gmFsRepository;
    private final GmFsService gmFsService;
    private final AdFirmsRepository adFirmsRepository;
    private final AdFirmsService adFirmsService;
    private final R3DocumentFilesRepository r3DocumentFilesRepository;
    private final ReceivedDocumentsFromClientsRepository receivedDocumentsFromClientsRepository;
    private final AdditionlFilesReceivedDocumentsService additionlFilesReceivedDocumentsService;
    private final AdditionlFilesReceivedDocumentsRepository additionlFilesReceivedDocumentsRepository;
    private final ReceivedDocumentsFromClientsService receivedDocumentsFromClientsService;

    public DeliveryOfDocumentsSchedulerService(ScheduleConfiguration scheduleConfiguration, MailService mailService, MailConfiguration mailConfiguration, GmFsRepository gmFsRepository, GmFsService gmFsService, AdFirmsRepository adFirmsRepository, AdFirmsService adFirmsService, R3DocumentFilesRepository r3DocumentFilesRepository, ReceivedDocumentsFromClientsRepository receivedDocumentsFromClientsRepository, AdditionlFilesReceivedDocumentsService additionlFilesReceivedDocumentsService, AdditionlFilesReceivedDocumentsRepository additionlFilesReceivedDocumentsRepository, ReceivedDocumentsFromClientsService receivedDocumentsFromClientsService) {
        this.scheduleConfiguration = scheduleConfiguration;
        this.mailService = mailService;
        this.mailConfiguration = mailConfiguration;
        this.gmFsRepository = gmFsRepository;
        this.gmFsService = gmFsService;
        this.adFirmsRepository = adFirmsRepository;
        this.adFirmsService = adFirmsService;
        this.r3DocumentFilesRepository = r3DocumentFilesRepository;
        this.receivedDocumentsFromClientsRepository = receivedDocumentsFromClientsRepository;
        this.additionlFilesReceivedDocumentsService = additionlFilesReceivedDocumentsService;
        this.additionlFilesReceivedDocumentsRepository = additionlFilesReceivedDocumentsRepository;
        this.receivedDocumentsFromClientsService = receivedDocumentsFromClientsService;
    }

    @Transactional
    @Scheduled(cron = "0 15 8 1,6,11 * ?") //at 8:15 am 1st, 6th, 11th day of each month
    public void trackDeliveredDocuments() {
        gmFsService.currentMonthGmFsList()
                .stream()
                .forEach(d -> {
                    Session session = mailService.confSmtpHostEmail();
                    HashMap<String, String> imagesMap = new HashMap<>();
                    MailDetails mailDetails = new MailDetails();
                    String toEmail = "";
                    String bccEmail = "";
                    byte[]data=null;
                    String attachmentPath=null;
                    adFirmsService.verificationIfTaxIdIsValid();
                    List<AdFirms> adFirmsList = adFirmsRepository.findAllByTaxId(d.getTaxIdReceiver().replaceAll("\\D", ""));
                    AdFirms adFirms = adFirmsRepository.findByTaxId(d.getTaxIdReceiver().replaceAll("\\D", ""));
                    AdditionlFilesReceivedDocuments additionlFilesReceivedDocuments = additionlFilesReceivedDocumentsRepository.findByNumber(adFirmsList.get(0).getNumber().toString());
                    ReceivedDocumentsFromClients receivedDocumentsFromClients = receivedDocumentsFromClientsRepository.findByIdFirm(adFirmsList.get(0).getId());
                    if (adFirms == null) {
                        mailDetails = MailsUtility.mailsUtility("Firma" + d.getFullNameReceiver() + "o NIP-ie " + d.getTaxIdReceiver() + " nie jest wprowadzona w module administracyjnym w Raks", "<br> Proszę srawdź czy firma " + d.getFullNameReceiver() + " jest wprowadzona w module administracyjnym w Raks. Faktura nie została wysłana do klienta ponieważ nie znaleziono tej firmy module administracyjnym w RAKS <br/>" + "Zespół xxxx<br/><br/></p>" + "<img src=cid:" + IMAGES_LOGO_ID + " width=\"170\" height=\"50\"/>");
                        toEmail = mailConfiguration.getToEmailIt();
                        bccEmail = mailConfiguration.getBccEmailIt();
                    } else if (adFirmsService.ifSizeOfAdFirmsListIsMoreThenOne(adFirmsList) == true) {
                        mailDetails = MailsUtility.mailsUtility("Jest więcej niż jedna firma o tym samym nipie", "<br> Mail nie został wysłany do klienta. <br/> Znalazłem więcej niż jedną firmę w module administracyjnym Raks" + " o NIPie" + d.getTaxIdReceiver() + ", nazwa firmy: " + adFirmsList.get(0) + adFirmsList.get(1) + " <br/> Należy się zastanowić na który adres mailowy wysyłać informację. Należy sprawdzić czy maile są kompletne. <br/>" + "Zespół xxxx<br/><br/></p>" + "<img src=cid:" + IMAGES_LOGO_ID + " width=\"170\" height=\"50\"/>");
                        toEmail = mailConfiguration.getToEmailIt();
                        bccEmail = mailConfiguration.getBccEmailIt();
                    } else if (adFirmsService.ifEmailAdressExists(adFirms) == false) {
                        mailDetails = MailsUtility.mailsUtility("Firma: " + d.getFullNameReceiver() + " nie ma adresu mailowego w module adinistracyjnym Raks", "<br> Proszę srawdź dane firmy " + d.getFullNameReceiver() + ", ponieważ brakuje adresu mailowego w module administracyjnym w Raks <br/>" + "Zespół xxxx<br/><br/></p>" + "<img src=cid:" + IMAGES_LOGO_ID + " width=\"170\" height=\"50\"/>");
                        toEmail = mailConfiguration.getToEmailIt();
                        bccEmail = mailConfiguration.getBccEmailIt();
                    } else if (ifReceivedDocument(additionlFilesReceivedDocuments, adFirms.getId()) == true) {
                        mailDetails = MailsUtility.mailsUtility("Dokumenty firmy: " + d.getFullNameReceiver() + " zostały dostarczone", "<img src=cid:" + IMAGES_RECEIPT_ID + " width=\"280\" height=\"200\"/>" + "<br><br>Dokumenty firmy: " + d.getFullNameReceiver() + " zostały dostarczone, dlatego mail do klienta nie został wysłany. <br/><br/>" + "Zespół xxxx<br/><br/></p>" + "<img src=cid:" + IMAGES_LOGO_ID + " width=\"170\" height=\"50\"/>");
                        imagesMap.put("<" + IMAGES_RECEIPT_ID + ">", IMAGES_RECEIPT_JPG);
                        toEmail = mailConfiguration.getToEmailOwner();
                        bccEmail = mailConfiguration.getBccEmailOwner();
                    } else if (ifNotReceivedDocumentFirstInfo(receivedDocumentsFromClients) == true) {
                        mailDetails = MailsUtility.mailsUtility(" Informacja o terminie przekazania dokumentów księgowych dla firmy " + adFirmsList.get(0).getFullname(),
                                "<p><strong>Informacja o terminie przekazania dokumentów księgowych dla firmy " + adFirmsList.get(0).getFullname() + "</strong></p>\n" +
                                        "<p><strong><img src=cid:" + IMAGES_RECEIPT_ID + " width=\"280\" height=\"200\"/></strong></p>\n" +
                                        "<p></i><br>" +
                                        "<font size=\"3\"> Uprzejmie prosimy o dostarczenie do nas dokumentów księgowych do końca 5-ego dnia miesiąca. Umożliwi to nam terminowe rozliczenie Twojego podatku. Zachęcamy do tego, aby przekazywane dokumenty były w formie elektronicznej JPK lub PDF, co znacząco wpłynie na szybkość i jakość naszych usług.<br/>" +
                                        SIGNATURE +
                                        "<img src=cid:" + IMAGES_LOGO_ID + " width=\"170\" height=\"50\">");
                        imagesMap.put("<" + IMAGES_RECEIPT_ID + ">", IMAGES_RECEIPT_JPG);
                        toEmail = TO_EMAIL_CLIENT;
                        bccEmail = BCC_EMAIL_CLIENT;
                    } else if (ifNotReceivedDocumentFirstReminder(receivedDocumentsFromClients) == true) {
                        mailDetails = MailsUtility.mailsUtility("Miną termin przekazywania dokumentów księgowych firmy: " + adFirmsList.get(0).getFullname(), "<H1>Miną termin przekazywania dokumentów księgowych firmy: " + adFirmsList.get(0).getFullname() + "</H1><img src=cid:" + IMAGES_DEADLINE_ID + " width=\"280\" height=\"200\"/>" +
                                "<br/><br/><font size=\"3\"> Do tej pory nie odnotowaliśmy wpływu Twoich dokumentów do nas. Dlatego chcemy zwrócić Twoją uwagę, że nasze umowy nie gwarantują terminowego rozliczenia podatkowego w przypadku dokumentacji dostarczonej po 5-tym dniu miesiąca. Dokładamy wszelkich starań, aby utrzymać najwyższą jakość naszych usług. Co za tym idzie m.in. terminowość w rozliczeniu Twoich spraw z urzędem. Nasz \"miesiąc rozliczeniowy\" trwa 10-15 dni, dla tego terminowe dostarczanie dokumentów ma dla nas kluczowe znaczenie.<br/><br/>" +
                                "Jeśli Państwo dostarczyli dokumenty, prosze traktować wiadomość za niebyłą." +
                                SIGNATURE +
                                "<img src=cid:" + IMAGES_LOGO_ID + " width=\"170\" height=\"50\">");
                        imagesMap.put("<" + IMAGES_DEADLINE_ID + ">", IMAGES_DEADLINE_JPG);
                        toEmail = TO_EMAIL_CLIENT;
                        bccEmail = BCC_EMAIL_CLIENT;
                    } else if (ifNotReceivedDocumentSecondReminder(receivedDocumentsFromClients) == true) {
                        mailDetails = MailsUtility.mailsUtility("Do firmy " + d.getFullNameReceiver() + " wysłano już dwa przypomnienia dotyczace dostarczenia dokumentów", "<H1>Do firmy " + d.getFullNameReceiver() + " wysłano już dwa przypomnienia dotyczace dostarczenia dokumentów.</H1><img src=cid:" +IMAGES_DEADLINE_ID + " width=\"280\" height=\"200\"/>" + "<br> <br>Do tej pory nie wpłyneły dokumenty do księgowości. Zalecam kontak telefoniczny z klientem. <br><br>" + "Zespół xxxx<br/></p>" + "<img src=cid:" + IMAGES_LOGO_ID + " width=\"170\" height=\"50\"/>");
                        imagesMap.put("<"+IMAGES_DEADLINE_ID+">",IMAGES_DEADLINE_JPG);
                        toEmail = mailConfiguration.getToEmailOwner();
                        bccEmail = mailConfiguration.getBccEmailOwner();
                    }
                    if (ifEmailShouldBeSent(d,additionlFilesReceivedDocuments, receivedDocumentsFromClients) == true) {
                        imagesMap.put("<"+IMAGES_LOGO_ID+">",IMAGES_LOGO_JPG);
                        mailService.sendEmailWithImagesAndAttachments(session, toEmail, bccEmail, mailDetails.getMailBody(), mailDetails.getMailTitle(), imagesMap, data, attachmentPath);
                        log.info("mail sent to {} {}; title{}; content:{} ", toEmail, bccEmail, mailDetails.getMailTitle(), mailDetails.getMailBody());
                        if (ifReceivedDocument(additionlFilesReceivedDocuments, adFirms.getId()) == true) {
                            saveStatusForReceivedDocuments(receivedDocumentsFromClients, adFirms.getId());
                        } else if (ifNotReceivedDocumentFirstInfo(receivedDocumentsFromClients) == true) {
                            saveStatusForFirstInfo(receivedDocumentsFromClients, adFirms.getId());
                        } else if (ifNotReceivedDocumentFirstReminder(receivedDocumentsFromClients) == true) {
                            receivedDocumentsFromClientsService.editReceivedDocumentsFromClients(receivedDocumentsFromClients, 2);
                        } else if (ifNotReceivedDocumentSecondReminder(receivedDocumentsFromClients) == true) {
                            receivedDocumentsFromClientsService.editReceivedDocumentsFromClients(receivedDocumentsFromClients, 4);
                        }
                    }
                });
    }

    private Boolean ifReceivedDocument(AdditionlFilesReceivedDocuments additionlFilesReceivedDocuments, Integer idFirms) {
        if (additionlFilesReceivedDocuments != null && additionlFilesReceivedDocumentsService.checkIfRecivedDocumentFromFirebird(idFirms) == true)
            return true;
        else
            return false;
    }

    private Boolean ifNotReceivedDocumentFirstInfo(ReceivedDocumentsFromClients receivedDocumentsFromClients) {
        if (receivedDocumentsFromClients == null || (receivedDocumentsFromClients.getIdReceivedDocumentsFromClientsStatus() == 1 && !receivedDocumentsFromClients.getData().equals(now().minusMonths(1).toString().substring(0, 7))) || (receivedDocumentsFromClients.getIdReceivedDocumentsFromClientsStatus() == 2 && !receivedDocumentsFromClients.getData().equals(now().minusMonths(1).toString().substring(0, 7))) || (receivedDocumentsFromClients.getIdReceivedDocumentsFromClientsStatus() == 3 && !receivedDocumentsFromClients.getData().equals(now().minusMonths(1).toString().substring(0, 7))))
            return true;
        else
            return false;
    }

    private Boolean ifNotReceivedDocumentFirstReminder(ReceivedDocumentsFromClients receivedDocumentsFromClients) {
        if (receivedDocumentsFromClients.getIdReceivedDocumentsFromClientsStatus() == 1 && receivedDocumentsFromClients.getData().equals(now().minusMonths(1).toString().substring(0, 7)))
            return true;
        else
            return false;
    }

    private Boolean ifNotReceivedDocumentSecondReminder(ReceivedDocumentsFromClients receivedDocumentsFromClients) {
        if (receivedDocumentsFromClients.getIdReceivedDocumentsFromClientsStatus() == 2 && receivedDocumentsFromClients.getData().equals(now().minusMonths(1).toString().substring(0, 7)))
            return true;
        else
            return false;
    }

    private Boolean ifEmailShouldBeSent(GmFs gmFs, AdditionlFilesReceivedDocuments additionlFilesReceivedDocuments, ReceivedDocumentsFromClients receivedDocumentsFromClients) {
        List<AdFirms> adFirmsList = adFirmsRepository.findAllByTaxId(gmFs.getTaxIdReceiver().replaceAll("\\D", ""));
        AdFirms adFirms = adFirmsRepository.findByTaxId(gmFs.getTaxIdReceiver().replaceAll("\\D", ""));
        if ( adFirms == null || adFirmsService.ifSizeOfAdFirmsListIsMoreThenOne(adFirmsList) == true || adFirmsService.ifEmailAdressExists(adFirms) == false || ifReceivedDocument(additionlFilesReceivedDocuments, adFirmsList.get(0).getId()) == true || (ifNotReceivedDocumentFirstInfo(receivedDocumentsFromClients) == true) || ifNotReceivedDocumentFirstReminder(receivedDocumentsFromClients) == true || ifNotReceivedDocumentSecondReminder(receivedDocumentsFromClients) == true)
            return true;
        else
            return false;
    }

    private void saveStatusForFirstInfo(ReceivedDocumentsFromClients receivedDocumentsFromClients, Integer idFirm) {
        if (receivedDocumentsFromClients == null) {
            ReceivedDocumentsFromClients receivedDocumentsFromClients1 = new ReceivedDocumentsFromClients();
            receivedDocumentsFromClientsService.saveReceivedDocumentsFromClients(receivedDocumentsFromClients1, idFirm, 1);
        } else {
            receivedDocumentsFromClientsService.editReceivedDocumentsFromClients(receivedDocumentsFromClients, 1);
        }
    }

    private void saveStatusForReceivedDocuments(ReceivedDocumentsFromClients receivedDocumentsFromClients, Integer idFirm) {
        if (receivedDocumentsFromClients == null) {
            ReceivedDocumentsFromClients receivedDocumentsFromClients1 = new ReceivedDocumentsFromClients();
            receivedDocumentsFromClientsService.saveReceivedDocumentsFromClients(receivedDocumentsFromClients1, idFirm, 3);
        } else {
            receivedDocumentsFromClientsService.editReceivedDocumentsFromClients(receivedDocumentsFromClients, 3);
        }
    }
}