package pl.szmaus.utility;

import org.springframework.stereotype.Component;

@Component
public class MailsUtility {

    public static MailDetails mailsUtility(String mailTitle, String mailBody){
        MailDetails mailDetails = new MailDetails();
        mailDetails.setMailBody(mailBody);
        mailDetails.setMailTitle(mailTitle);
        return mailDetails;
    }
}
