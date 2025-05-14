package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import io.github.bonigarcia.wdm.WebDriverManager;

public class WhatsAppLogin {
	WebDriver driver;
	SoftAssert softAssert = new SoftAssert();
	static final String COOKIES_FILE = "whatsapp_cookies.ser";
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(IssueOfSSLConnection.class);

	private static void saveCookies(WebDriver driver, File file) throws IOException {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
			oos.writeObject(driver.manage().getCookies());
		}
	}

	@SuppressWarnings("unchecked")
	private static void loadCookies(WebDriver driver, File file) throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
			Set<Cookie> cookies = (Set<Cookie>) ois.readObject();
			for (Cookie cookie : cookies) {
				driver.manage().addCookie(cookie);
			}
		}
	}
	
	@Test
	public void loginToWhatsApp() throws ClassNotFoundException, IOException, InterruptedException {
		WebDriverManager.chromedriver().setup();
//		ChromeOptions options = new ChromeOptions();
//		options.addArguments("--headless");
//		options.addArguments("--no-sandbox");
//		options.addArguments("--disable-dev-shm-usage");
//	    driver = new ChromeDriver(options);
		driver = new ChromeDriver();

		driver.manage().window().maximize();
		logger.info("Launching Chrome browser for the test execution...");
		driver.get("https://web.whatsapp.com");
		// Load cookies if available
		File cookieFile = new File(COOKIES_FILE);
		if (cookieFile.exists()) {
			loadCookies(driver, cookieFile);
			driver.navigate().refresh();
		}

		// Wait for WhatsApp Home (after login)
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[role='textbox']")));
			System.out.println("Logged in!");
		} catch (TimeoutException e) {
			System.out.println("Please scan the QR code manually within 60 seconds...");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[role='textbox']")));
			saveCookies(driver, cookieFile);
			System.out.println("Login successful. Cookies saved for next time.");
		}

		// Do any automation here...

		// Keep session alive
		Thread.sleep(10000);
		driver.quit();
	}

	

}
