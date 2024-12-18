package be.anticair.anticairapi.enumeration;

import lombok.Getter;

@Getter
/**
 * Enumeration to know the antiquity state
 * @Author Verly Noah
 */
public enum AntiquityState {
    /**
     * an antiquity has been rejected and the owner need to change some information
     */
    REJECTED (-1),
    /**
     * an antiquity have needed to be check
     */
    NEED_TO_BE_CHECK(0),
    /**
     * An antiquity has been accepted and the commission has been applied
     */
    ACCEPTED(1),
    /**
     * An antiquity has been accepted and the commission has been applied before to be modified, the antiquity need to be chech again
     */
    ACCEPTED_BUT_MODIFIED(2),
    /**
     * An antiquity has been sold
     */
    SOLD(3);

    /**
     * the state of the antiquity
     */
    private final int state;

    /**
     * the constructor
     * @param state the state of the antiquity
     * @Author Verly Noah
     */
    AntiquityState(final int state) {
        this.state = state;
    }
}
