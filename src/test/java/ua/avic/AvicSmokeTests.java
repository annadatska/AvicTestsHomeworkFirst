package ua.avic;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.By.id;
import static org.openqa.selenium.By.xpath;
import static org.testng.Assert.assertTrue;

public class AvicSmokeTests {

    private WebDriver driver;

    @BeforeTest
    public void profileSetUp() {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
    }

    @BeforeMethod
    public void testsSetUp(){
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.get("https://www.avic.ua/");
    }

    @Test(priority = 1)
    public void checkThatURLContainsNewRAMAmount() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        driver.findElement(xpath("//input[@id='input_search']")).sendKeys("iphone 12");
        driver.findElement(xpath("//button[@class='button-reset search-btn']")).click();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.findElement(xpath("//img[contains(@title,'iPhone 12 64GB Purple')]")).click();
        wait.until(webDriver -> (
                (JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
        driver.findElement(xpath("//a[contains(@title,'128GB Purple (MJNP3)')]")).click();
        assertTrue(driver.getCurrentUrl().contains("128gb"));

    }

    @Test(priority = 2)
    public void checkAddXiaomiPhoneToCart() {
        Actions action = new Actions(driver);
        WebDriverWait wait = new WebDriverWait(driver, 30);
        driver.findElement(xpath("//span[@class='sidebar-item']")).click();
        WebElement phonesAndAccessorizes = wait.until(ExpectedConditions.presenceOfElementLocated(
                xpath("//a[@class='sidebar-item'][contains(@href,'telefonyi-i-aksessuaryi')]")));
        action.moveToElement(phonesAndAccessorizes).build().perform();
        WebElement smartphones = wait.until(ExpectedConditions.presenceOfElementLocated(
                xpath("//a[@class='sidebar-item'][contains(@href,'smartfonyi')]")));
        action.moveToElement(smartphones).build().perform();
        wait.until(ExpectedConditions.presenceOfElementLocated(
                xpath("//a[@class='single-hover__link'][contains(@href,'smartfonyi/proizvoditel--xiaomi')]")));
        driver.findElement(xpath("//a[@class='single-hover__link'][contains(@href,'smartfonyi/proizvoditel--xiaomi')]")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(xpath("//a[contains(@data-ecomm-cart,'Redmi Note 9 Pro 6\\/64GB Green')]")));
        driver.findElement(xpath("//a[contains(@data-ecomm-cart,'Redmi Note 9 Pro 6\\/64GB Green')]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(xpath("//div[@id='js_cart']")));
        WebElement amount = wait.until(ExpectedConditions.visibilityOfElementLocated(
                xpath("//input[@value='1'][contains(@class,'changeAmount')]")));
        assertTrue(amount.isDisplayed());
    }

    @Test(priority = 3)
    public void checkThatPopupEmptyCartAppearsWhenDeleteAddedProduct() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        driver.findElement(xpath("//input[@id='input_search']")).sendKeys("iphone 12");
        driver.findElement(xpath("//button[@class='button-reset search-btn']")).click();
        wait.until(webDriver -> (
                (JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
        driver.findElement(xpath("//a[contains(@data-ecomm-cart,'64GB Purple (MJNM3)')]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(xpath("//div[@id='js_cart']")));
        driver.findElement(xpath("//a[contains(@href,'checkout')]")).click();
        wait.until(webDriver -> (
                (JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
        driver.findElement(xpath("//div[contains(@data-ecomm-cart,'MJNM3')]//i[contains(@class,'icon-close')]")).click();
        WebElement emptyCartAlert = wait.until(ExpectedConditions.presenceOfElementLocated(id("modalAlert")));
        assertTrue(emptyCartAlert.isDisplayed());
    }

    @Test(priority = 4)
    public void checkThatSearchResultsContainsSearchWord() {
        driver.findElement(xpath("//input[@id='input_search']")).sendKeys("Xiaomi");
        driver.findElement(xpath("//button[@class='button-reset search-btn']")).click();
        List<WebElement> elementList = driver.findElements(xpath("//div[contains(@class,'js_more_content')]"));
        for (WebElement webElement : elementList) {
            assertTrue(webElement.getText().contains("Xiaomi"));
        }
    }

    @AfterMethod
    public void tearDown() {
        driver.close();
    }
}
