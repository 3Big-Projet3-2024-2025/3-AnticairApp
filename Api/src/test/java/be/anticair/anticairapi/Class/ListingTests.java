package be.anticair.anticairapi.Class;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test that verify the Listing function's
 * @Author Verly Noah
 */
@SpringBootTest
@TestPropertySource(properties = {
        "spring.main.allow-bean-definition-overriding=true"
})
public class ListingTests {

    /**
     * The antiquity that will be used for the test
     */
    private Listing listing;

    /**
     * SetUp to create a antiquity
     */
    @BeforeEach
    public void setUp(){
        listing = new Listing(1,100.0,"C'est Jésus","Statut","noahverly@hotmail.be",0,true,1);
    }

    /**
     * Test to check the application of the commission
     * @Author Noah Verly
     */
    @Test
    public void testApplyCommission(){
        //delta 0,01, is used as tolerance for the floating point
        assertEquals(100.0, listing.getPriceAntiquity());
        listing.applyCommission();
        assertEquals(120.0, listing.getPriceAntiquity(), 0.01);
        listing.applyCommission();
        assertEquals(144.0, listing.getPriceAntiquity(), 0.01);
        listing.applyCommission();
        assertEquals(172.8, listing.getPriceAntiquity(), 0.01);
        listing.applyCommission();
        assertEquals(207.36, listing.getPriceAntiquity(), 0.01);
        listing.applyCommission();
        assertEquals(248.83, listing.getPriceAntiquity(), 0.01);
    }
}