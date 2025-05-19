package test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import base.BaseClass;
import base.ExcelAppender;
import base.WPHomePageElements;
import io.github.bonigarcia.wdm.WebDriverManager;

public class ReceivedMessages {

	WebDriver driver;
	BaseClass baseClass;
	ExcelAppender excelAppender;
	WPHomePageElements wPHomePage;
	SoftAssert softAssert = new SoftAssert();
	static final String COOKIES_FILE = "whatsapp_cookies.ser";
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(ReceivedMessages.class);

	@Test
	public void loginToWhatsApp() throws ClassNotFoundException, IOException, InterruptedException {

		WebDriverManager.chromedriver().setup();
		ChromeOptions options = new ChromeOptions();
		options.addArguments(
				"user-data-dir=/home/mahamadgaus/Desktop/WPAutomation/WhatsAppAutomation/.whatsapp-profile");
		options.addArguments("--profile-directory=Default");
		driver = new ChromeDriver(options);
		driver.manage().window().maximize();
		logger.info("Launching Chrome browser for the test execution...");
		driver.get("https://web.whatsapp.com");

		wPHomePage = new WPHomePageElements(driver);

		// Load cookies if available
		File cookieFile = new File(COOKIES_FILE);
		if (cookieFile.exists()) {
			BaseClass.loadCookies(driver, cookieFile);
			driver.navigate().refresh();
		}

		// Wait for WhatsApp Home (after login)
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[role='textbox']")));
			logger.info("Logged in!");
		} catch (Exception e) {
			logger.info("Please scan the QR code manually within 60 seconds...");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[role='textbox']")));
			BaseClass.saveCookies(driver, cookieFile);
			logger.info("Login successful. Cookies saved for next time.");
		}

		try {
			wait = new WebDriverWait(driver, Duration.ofSeconds(5));
			WebElement continueBtn = wait
					.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()='Continue']")));
			if (continueBtn.isDisplayed()) {
				logger.info("'Continue' popup appeared. Clicking it...");
				continueBtn.click();
			}
		} catch (Exception f) {
			logger.info("Proceeding...");
		}

		Thread.sleep(1000);
		
		WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(600000));
		WebElement unreadMessage = wait1.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@aria-label, 'unread message')]")));
		
		
		if(unreadMessage.isDisplayed() && driver.findElement(By.xpath("//span[@title='Aasim Mulla']")).isDisplayed()) {
			driver.findElement(By.xpath("//span[@title='Aasim Mulla']")).click();
			
			String recText = driver.findElement(By.xpath("(//span[text()='Mumbai'])[last()]")).getText();
			Thread.sleep(1000);
			wPHomePage.clickOnCommonGroup();
			logger.info("User clicked on Common Group");
			Thread.sleep(1000);
			
			// Type a message to send in Common group
			wPHomePage.typeMessage(recText);
			Thread.sleep(1000);
			wPHomePage.clickOnSendButton();
			Thread.sleep(1000);
			
			if(wPHomePage.messageToSend.equals("Ahmedabad")) {
				wPHomePage.clickOnAhmedabadGroup();
				Thread.sleep(1000);
				wPHomePage.typeMessage("Ahmedabad1");
				Thread.sleep(1000);
				wPHomePage.clickOnSendButton();
				ExcelAppender.appendToExcel("Ahmedabad1");
			}
			else if(wPHomePage.messageToSend.equals("Mumbai")) {
				wPHomePage.clickOnMumbaiGroup();
				Thread.sleep(1000);
				wPHomePage.typeMessage("Mumbai1");
				Thread.sleep(1000);
				wPHomePage.clickOnSendButton();
				ExcelAppender.appendToExcel("Mumbai1");
			}
			
		}
		
		
		
		
		
		
		
		
		
		
		
		

		// driver.quit();
		softAssert = null;
		driver = null;
	}

}
