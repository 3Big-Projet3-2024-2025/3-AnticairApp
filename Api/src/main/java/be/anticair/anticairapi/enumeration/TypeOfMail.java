package be.anticair.anticairapi.enumeration;

import lombok.Getter;

@Getter
/**
 * Enumaration to simplify the sending of mail
 * @Author Verly Noah
 */
public enum TypeOfMail {
    VALIDATIONOFANANTIQUITY (1, "ValdiationOfAnAntiquity.html"),
    CONFIRMATIONOFAPPLICATIONCOMMISSION (2, "ConfirmationOfApplicationCommission.html"),
    REJECTIONOFANTIQUITY (3, "RejectionOfAntiquity.html"),
    REDISTRIBUTEANTIQUITYINITANTIQUARIAN (4, "redistributeAntiquityInitAntiquarian.html"),
    REDISTRIBUTEANTIQUITYNEWANTIQUARIAN (5, "redistributeAntiquityNewAntiquarian.html");

    /**
     * The type of mail to the mailService
     */
    private int typeOfMail;
    /**
     * Which template to use
     */
    private String templateHTMLName1;

    /**
     * Constructor
     * @param typeOfMail The type of mail to the mailService
     * @param templateHTMLName Which template to use
     * @Author Verly Noah
     */
    TypeOfMail(int typeOfMail, String templateHTMLName) {
        this.typeOfMail = typeOfMail;
        this.templateHTMLName1 = templateHTMLName;
    }
}
