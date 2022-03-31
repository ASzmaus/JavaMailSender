package pl.szmaus.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "mailing")
public class MailConfiguration {

    private String fromEmail;
    private String toEmailOwner;
    private String bccEmailOwner;
    private String toEmailIt;
    private String bccEmailIt;
    private String password;
    private String mailSmtpHost;
    private String mailSmtpPort;
    private String mailSmtpAuth;
    private String mailSmtpStarttlsEnable;
    private String codingSystem;
    private Integer paymentDate;

}
