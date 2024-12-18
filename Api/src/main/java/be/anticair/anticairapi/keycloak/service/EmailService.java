package be.anticair.anticairapi.keycloak.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Service
public class EmailService {
    private static final String URL_TEMPLATE_HTML = "/TemplateHTML/";
    @Autowired
    @Lazy
    private UserService userService;
    @Autowired
    private JavaMailSender mailSender;

    public void sendHtmlEmail(String receiver, String sender, String subject, int typeOfMail, Map<String,String> otherInformation) throws MessagingException, IOException {
       try {
           //Allow to create email
           MimeMessage message= this.setInformationMail(sender,receiver,subject);

           String htmlTemplate = "";
           switch (typeOfMail) {
               case 1: //Valdiation of an antiquity, so notify the owner
                  htmlTemplate=this.loadFilePath("ValdiationOfAnAntiquity.html");
                  htmlTemplate = this.replaceAntiquityInformation(htmlTemplate,otherInformation);
                   break;
               case 2: //Application of the commission, so notify the antiquarian
                   htmlTemplate=this.loadFilePath("ConfirmationOfApplicationCommission.html");

                   htmlTemplate = this.replaceAntiquityInformation(htmlTemplate,otherInformation);
                   double priceWithCommission = Double.parseDouble(otherInformation.get("price"));
                   double commissionDouble = priceWithCommission/1.20;
                   priceWithCommission -= commissionDouble;
                   String commissionString = Double.toString(priceWithCommission);
                   htmlTemplate = htmlTemplate.replace("${commission}", commissionString);
                   break;
               case 3: //Valdiation of an antiquity, so notify the owner
                   htmlTemplate=this.loadFilePath("RejectionOfAntiquity.html");
                   htmlTemplate = this.replaceAntiquityInformation(htmlTemplate,otherInformation);
                   htmlTemplate = htmlTemplate.replace("${note_title}", otherInformation.get("note_title"));
                   htmlTemplate = htmlTemplate.replace("${note_description}", otherInformation.get("note_description"));
                   htmlTemplate = htmlTemplate.replace("${note_price}", otherInformation.get("note_price"));
                   break;


               default: // Default mail
                   htmlTemplate = "";
                   break;

           }
           //Shared information
           htmlTemplate = this.replaceSharedInformation(htmlTemplate,receiver);


           message.setContent(htmlTemplate, "text/html; charset=utf-8");
           mailSender.send(message);
       }catch (MessagingException e){
           throw new MessagingException("Error while sending email : " + e.getMessage());
       }

    }

    /**
     * Allow to load the base information of the mail
     * @param sender Email of the sender
     * @param receiver Email of the receiver
     * @param subject Subject of the email
     * @return return the based email
     * @throws MessagingException
     * @Author Verly Noah
     */
    private MimeMessage setInformationMail(String sender, String receiver, String subject) throws MessagingException {

        try{
            //Allow to create email
            MimeMessage message = mailSender.createMimeMessage();
            //To set from who
            message.setFrom(new InternetAddress(sender));
            //To set the receiver
            message.setRecipients(MimeMessage.RecipientType.TO, receiver);
            //To the subject
            message.setSubject(subject);
            return message;
        }catch (MessagingException e){
            throw new MessagingException("Error while setting email's information : " + e.getMessage());
        }
    }

    /**
     * Allow to load the base information of the mail
     * @param fileName name of the file with the template
     * @return return the based email
     * @throws MessagingException
     * @Author Verly Noah
     */
    private String loadFilePath(String fileName) throws IOException {
        try{
            // Constructing the absolute path to the HTML template
            String filePath = System.getProperty("user.dir") + URL_TEMPLATE_HTML + fileName;

            // Lire le fichier HTML
            return new String(Files.readAllBytes(Paths.get(filePath)));
        }catch (IOException e){
            throw new IOException(e.getMessage());
        }

    }

    /**
     * Allow put the shared information to each mail
     * @param htmlTemplate name of the file with the template
     * @param receiver Email of the receiver
     * @return return the based email
     * @throws MessagingException
     * @Author Verly Noah
     */
    private String replaceSharedInformation(String htmlTemplate, String receiver){
        //Get information about the receiver
        List<UserRepresentation> users = userService.getUsersByEmail(receiver);
        htmlTemplate = htmlTemplate.replace("${receiver_name}", users.getFirst().getLastName()+" "+users.getFirst().getFirstName());

        String currentYear = String.valueOf(LocalDate.now().getYear());
        htmlTemplate = htmlTemplate.replace("${current_year}", currentYear);
        return htmlTemplate;
    }

    private String replaceAntiquityInformation(String htmlTemplate, Map<String,String> otherInformation){
        htmlTemplate = htmlTemplate.replace("${title}", otherInformation.get("title"));
        htmlTemplate = htmlTemplate.replace("${description}", otherInformation.get("description"));
        htmlTemplate = htmlTemplate.replace("${price}", otherInformation.get("price"));
        return htmlTemplate;
    }

}