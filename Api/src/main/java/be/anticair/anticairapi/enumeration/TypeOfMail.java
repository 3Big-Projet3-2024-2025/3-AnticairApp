package be.anticair.anticairapi.enumeration;

import lombok.Getter;

@Getter
/**
 * Enumaration to simplify the sending of mail
 * @Author Verly Noah
 */
public enum TypeOfMail {
    VALIDATIONOFANANTIQUITY (1, "ValdiationOfAnAntiquity.html", "Validation of your antiquity"),
    CONFIRMATIONOFAPPLICATIONCOMMISSION (2, "ConfirmationOfApplicationCommission.html", "Confirmation of the commission application"),
    REJECTIONOFANTIQUITY (3, "RejectionOfAntiquity.html", "Refusal to validate your antique"),
    REDISTRIBUTEANTIQUITYINITANTIQUARIAN (4, "redistributeAntiquityInitAntiquarian.html", "Redistribution of your antiquity"),
    REDISTRIBUTEANTIQUITYNEWANTIQUARIAN (5, "redistributeAntiquityNewAntiquarian.html", "A new to antiquity to be checked");

    /**
     * The type of mail to the mailService
     */
    private int typeOfMail;
    /**
     * Which template to use
     */
    private String templateHTMLName1;

    /**
     * Which template to use
     */
    private String subject;

    /**
     * Constructor
     * @param typeOfMail The type of mail to the mailService
     * @param templateHTMLName Which template to use
     * @Author Verly Noah
     */
    TypeOfMail(int typeOfMail, String templateHTMLName, String subject) {
        this.typeOfMail = typeOfMail;
        this.templateHTMLName1 = templateHTMLName;
        this.subject = subject;
    }
}