package org.example;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class CalculatorTest {

    private AndroidDriver driver;

    @BeforeClass
    public void setUp() throws MalformedURLException {
        UiAutomator2Options options = new UiAutomator2Options();

        options.setCapability("browserstack.user", "bsuser_PZcs6M");
        options.setCapability("browserstack.key", "RszxAFsyyrrmAHcV639L");
        options.setCapability("app", "bs://2ffeeb8c67c2e4857054ea125a5b09fc6b2e6f58");

        options.setDeviceName("OnePlus 7");
        options.setPlatformVersion("9.0");
        options.setAutomationName("UiAutomator2");

        driver = new AndroidDriver(new URL("http://hub.browserstack.com/wd/hub"), options);
    }

    @Test
    public void testAddition() {
        driver.findElement(AppiumBy.id("digit_1")).click();
        driver.findElement(AppiumBy.id("op_add")).click();
        driver.findElement(AppiumBy.id("digit_2")).click();
        driver.findElement(AppiumBy.id("eq")).click();

        String result = driver.findElement(AppiumBy.id("result_final")).getText();
        System.out.println("Result: " + result);

        assert result.equals("3");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
