package org.example;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class CalculatorTest {
    private AndroidDriver driver;
    @BeforeClass
    public void setUp() throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", "Android");
        caps.setCapability("platformVersion", "15.0");
        caps.setCapability("deviceName", "emulator-5554");
        caps.setCapability("appPackage", "com.google.android.calculator");
        caps.setCapability("appActivity", ".Calculator");
        caps.setCapability("automationName", "UiAutomator2");

        driver = new AndroidDriver(new URL("http://localhost:4723/wd/hub"), caps);
    }

    @Test
    public void testAddition() {
        driver.findElement(By.id("digit_1")).click();

        driver.findElement(By.id("op_add")).click();

        driver.findElement(By.id("digit_2")).click();

        driver.findElement(By.id("eq")).click();

        String result = driver.findElement(By.id("result_final")).getText();
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
