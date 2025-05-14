package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import base.BaseClass;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.cdimascio.dotenv.Dotenv;

public class IssueOfSSLConnection extends BaseClass {

	WebDriver driver;
	SoftAssert softAssert = new SoftAssert();
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(IssueOfSSLConnection.class);

	@Test
	public void checkIssueOfSSLConnection() throws InterruptedException {

		try {		
			WebDriverManager.chromedriver().setup();
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--headless");
			options.addArguments("--no-sandbox");
			options.addArguments("--disable-dev-shm-usage");
		    driver = new ChromeDriver(options);
			driver.manage().window().maximize();
			logger.info("Launching Chrome browser for the test execution...");
			driver.get(FelixWebConsoleURL.toString());
			logger.info("Navigating to the target webpage: " + FelixWebConsoleURL);
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
			Thread.sleep(2000);
			driver.findElement(By.xpath("//button[@id='details-button']")).click();
			Thread.sleep(2000);
			driver.findElement(By.xpath("//a[@id='proceed-link']")).click();
			Thread.sleep(5000);
			WebElement searchPlugin = wait.until(ExpectedConditions
					.elementToBeClickable(By.xpath("(//input[@class='filter ui-state-default ui-corner-all'])[1]")));
			searchPlugin.sendKeys("com.logilite.search.solr", Keys.ENTER);
			WebElement NameOfSearchedPlugin = driver.findElement(By.xpath("//span[text()='com.logilite.search.solr']"));
			softAssert.assertEquals(NameOfSearchedPlugin.getText(), "com.logilite.search.solr");
			logger.info("User Searched for plugin " + NameOfSearchedPlugin.getText());
			driver.findElement(By.xpath("//span[@class='ui-icon ui-icon-refresh']")).click();
			logger.info("User Refreshed the plugin " + NameOfSearchedPlugin.getText());
			Thread.sleep(5000);
			softAssert.assertEquals(
					driver.findElement(By.xpath("//div[text()='Operation completed succesfully.']")).getText(),
					"Operation completed succesfully.");
			logger.info("Operation completed succesfully.");
			driver.findElement(By.xpath("(//button[@title='Close'])[2]")).click();
			Thread.sleep(2000);

			// Restart the server
			logger.info("...Server Restarting...");
			try {
				ProcessBuilder builder = new ProcessBuilder(CommandToRestart);

				builder.directory(new File(LocationOfSHfile));
				builder.redirectErrorStream(true); // Merge stderr with stdout
				Process process = builder.start();

				// Read the command output
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null) {
					System.out.println(line); // Print each line of output
					logger.info(line);
				}

				// Wait for the command to finish and get exit code
				int exitCode = process.waitFor();
				System.out.println("Exit code: " + exitCode);
				logger.info(exitCode);
				if(exitCode!=0) {
					Assert.fail ("Error occured while restarting the server...");
					logger.error("Error occured while restarting the server...");
				}

			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			Thread.sleep(10000);

			driver.switchTo().newWindow(WindowType.TAB);
			Set<String> handles = driver.getWindowHandles();
			List<String> tabs = new ArrayList<String>(handles);
			driver.get(WebUiURL);
			logger.info("Opened webui on new tab : " + WebUiURL);
			Thread.sleep(2000);
			driver.findElement(By.xpath("//div[@id='main-login']")).click();
			Thread.sleep(3000);
			driver.findElement(By.xpath("//input[@autocomplete='username']")).sendKeys(BaseClass.webUiUsername);
			logger.info("User Entered valid username : " + webUiUsername);
			driver.findElement(By.xpath("//input[@autocomplete='current-password']")).sendKeys(webUiPassword);
			logger.info("User Enetered valid Password : " + webUiPassword);
			driver.findElement(By.xpath("(//input[@type='checkbox'])[1]")).click();
			driver.findElement(By.xpath("(//button[@class='login-btn z-button'])[1]")).click();
			logger.info("User clicked on Login button");

			Thread.sleep(3000);
			driver.findElement(By.xpath("(//input[@class='z-combobox-input'])[1]")).clear();
			driver.findElement(By.xpath("(//input[@class='z-combobox-input'])[1]")).sendKeys("PharmaVerge ERP",
					Keys.ENTER);
			logger.info("User selects the Client");
			Thread.sleep(2000);
			driver.findElement(By.xpath("(//input[@class='z-combobox-input'])[2]")).clear();
			driver.findElement(By.xpath("(//input[@class='z-combobox-input'])[2]")).sendKeys("Admin", Keys.ENTER);
			logger.info("User selects the Role : Admin");
			driver.findElement(By.xpath("(//button[@class='login-btn z-button'])[1]")).click();
			Thread.sleep(2000);
			WebElement userNameRoleOnHomepage1 = wait.until(ExpectedConditions
					.elementToBeClickable(By.xpath("//span[text()='SuperUser@PharmaVerge ERP.*/Admin']")));
			softAssert.assertEquals(userNameRoleOnHomepage1.getText(), "SuperUser@PharmaVerge ERP.*/Admin");
			logger.info("User login to webui successfully");

			WebElement searchButton = wait
					.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@class='z-bandbox-input']")));

			searchButton.sendKeys("Document Explorer");
			Thread.sleep(2000);
			driver.findElement(By.xpath("//div[text()=' Document Explorer']")).click();
			logger.info("User searched for Document Explorer window");
			Thread.sleep(2000);

			if (driver
					.findElement(By.xpath("//span[text()='Render component problem: Storage provider is not found.']"))
					.isDisplayed()) {
				driver.findElement(By.xpath("//button[text()='OK']")).click();
				logger.error("User gets an error Popup");
				driver.switchTo().window(tabs.get(0));
				logger.info("User Navigates back to the : " + FelixWebConsoleURL);
				Thread.sleep(1500);
				// Refresh the page
				driver.navigate().refresh();
				logger.info("User refreshes the FelixWebConsoleURL Successfully...");
				Thread.sleep(3000);
				driver.findElement(By.xpath("(//input[@class='filter ui-state-default ui-corner-all'])[1]"))
						.sendKeys("org.idempiere.dms", Keys.ENTER);

				// searchPlugin.sendKeys("org.idempiere.dms", Keys.ENTER);
				logger.info("User searched for plugin org.idempiere.dms");
				WebElement NameOfSearchedPlugin1 = driver.findElement(By.xpath("//span[text()='org.idempiere.dms']"));
				softAssert.assertEquals(NameOfSearchedPlugin1.getText(), "org.idempiere.dms");
				driver.findElement(By.xpath("//span[@class='ui-icon ui-icon-refresh']")).click();
				logger.info("User Refreshes the plugin org.idempiere.dms");
				Thread.sleep(5000);
				driver.findElement(By.xpath("(//button[@title='Close'])[2]")).click();

				Thread.sleep(2000);
				driver.findElement(By.xpath("(//input[@class='filter ui-state-default ui-corner-all'])[1]")).clear();
				driver.findElement(By.xpath("(//input[@class='filter ui-state-default ui-corner-all'])[1]"))
						.sendKeys("com.logilite.search", Keys.ENTER);
				logger.info("User searched for plugin com.logilite.search");
				WebElement NameOfSearchedPlugin2 = driver
						.findElement(By.xpath("(//span[text()='com.logilite.search'])[1]"));
				softAssert.assertEquals(NameOfSearchedPlugin2.getText(), "com.logilite.search");
				driver.findElement(By.xpath("(//span[@class='ui-icon ui-icon-refresh'])[1]")).click();
				logger.info("User Refreshes the plugin com.logilite.search");
				Thread.sleep(5000);
				driver.findElement(By.xpath("(//button[@title='Close'])[2]")).click();

				Thread.sleep(2000);
				driver.findElement(By.xpath("(//input[@class='filter ui-state-default ui-corner-all'])[1]")).clear();
				driver.findElement(By.xpath("(//input[@class='filter ui-state-default ui-corner-all'])[1]"))
						.sendKeys("com.logilite.search.solr", Keys.ENTER);
				logger.info("User searched for plugin com.logilite.search.solr");
				WebElement NameOfSearchedPlugin3 = driver
						.findElement(By.xpath("//span[text()='com.logilite.search.solr']"));
				softAssert.assertEquals(NameOfSearchedPlugin3.getText(), "com.logilite.search.solr");
				driver.findElement(By.xpath("//span[@class='ui-icon ui-icon-refresh']")).click();
				logger.info("User Refreshes the plugin com.logilite.search.solr");
				Thread.sleep(5000);
				driver.findElement(By.xpath("(//button[@title='Close'])[2]")).click();

				Thread.sleep(2000);
				driver.findElement(By.xpath("(//input[@class='filter ui-state-default ui-corner-all'])[1]")).clear();
				driver.findElement(By.xpath("(//input[@class='filter ui-state-default ui-corner-all'])[1]"))
						.sendKeys("com.logilite.dmstheme", Keys.ENTER);
				logger.info("User searched for plugin com.logilite.dmstheme");
				WebElement NameOfSearchedPlugin4 = driver
						.findElement(By.xpath("//span[text()='com.logilite.dmstheme']"));
				softAssert.assertEquals(NameOfSearchedPlugin4.getText(), "com.logilite.dmstheme");
				driver.findElement(By.xpath("//span[@class='ui-icon ui-icon-refresh']")).click();
				logger.info("User Refreshes the plugin com.logilite.dmstheme");
				Thread.sleep(5000);
				driver.findElement(By.xpath("(//button[@title='Close'])[2]")).click();

				Thread.sleep(2000);
				driver.findElement(By.xpath("(//input[@class='filter ui-state-default ui-corner-all'])[1]")).clear();
				driver.findElement(By.xpath("(//input[@class='filter ui-state-default ui-corner-all'])[1]"))
						.sendKeys("com.logilite.dms.storageprovider", Keys.ENTER);
				logger.info("User searched for plugin com.logilite.dms.storageprovider");
				WebElement NameOfSearchedPlugin5 = driver
						.findElement(By.xpath("//span[text()='com.logilite.dms.storageprovider']"));
				softAssert.assertEquals(NameOfSearchedPlugin5.getText(), "com.logilite.dms.storageprovider");
				driver.findElement(By.xpath("//span[@class='ui-icon ui-icon-refresh']")).click();
				logger.info("User Refreshes the plugin com.logilite.dms.storageprovider");
				Thread.sleep(5000);
				driver.findElement(By.xpath("(//button[@title='Close'])[2]")).click();

				// Restart the Server
				logger.info("...Server Restarting...");
				try {
					ProcessBuilder builder = new ProcessBuilder(CommandToRestart);

					builder.directory(new File(LocationOfSHfile));
					builder.redirectErrorStream(true); // Merge stderr with stdout
					Process process = builder.start();

					// Read the command output
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String line;
					while ((line = reader.readLine()) != null) {
						System.out.println(line); // Print each line of output
						logger.info(line);
					}

					// Wait for the command to finish and get exit code
					int exitCode = process.waitFor();
					System.out.println("Exit code: " + exitCode);
					logger.info(exitCode);
					if(exitCode!=0) {
						Assert.fail ("Error occured while restarting the server...");
						logger.error("Error occured while restarting the server...");
					}

				} catch (Exception g) {
					logger.error(g.getMessage());
				}
				Thread.sleep(10000);

				driver.switchTo().newWindow(WindowType.TAB);
				driver.get(WebUiURL);
				logger.info("User again Opened webui on new tab : " + WebUiURL);
				Thread.sleep(2000);
				driver.findElement(By.xpath("//div[@id='main-login']")).click();
				Thread.sleep(3000);
				driver.findElement(By.xpath("//input[@autocomplete='username']")).sendKeys(webUiUsername);
				logger.info("Entered valid username : " + webUiUsername);
				Thread.sleep(500);
				driver.findElement(By.xpath("//input[@autocomplete='current-password']")).sendKeys(webUiPassword);
				logger.info("Entered valid Password : " + webUiPassword);
				Thread.sleep(500);
				driver.findElement(By.xpath("(//input[@type='checkbox'])[1]")).click();
				driver.findElement(By.xpath("(//button[@class='login-btn z-button'])[1]")).click();
				logger.info("User clicked on Login button");

				Thread.sleep(3000);
				driver.findElement(By.xpath("(//input[@class='z-combobox-input'])[1]")).clear();
				driver.findElement(By.xpath("(//input[@class='z-combobox-input'])[1]")).sendKeys("PharmaVerge ERP",
						Keys.ENTER);
				logger.info("User selects the Client");
				Thread.sleep(2000);
				driver.findElement(By.xpath("(//input[@class='z-combobox-input'])[2]")).clear();
				driver.findElement(By.xpath("(//input[@class='z-combobox-input'])[2]")).sendKeys("Admin", Keys.ENTER);
				logger.info("User selects the Role : Admin");
				driver.findElement(By.xpath("(//button[@class='login-btn z-button'])[1]")).click();
				Thread.sleep(2000);
				WebElement userNameRoleOnHomepage2 = wait.until(ExpectedConditions
						.elementToBeClickable(By.xpath("//span[text()='SuperUser@PharmaVerge ERP.*/Admin']")));
				softAssert.assertEquals(userNameRoleOnHomepage2.getText(), "SuperUser@PharmaVerge ERP.*/Admin");
				logger.info("User login to webui successfully");
				WebElement searchButton1 = wait
						.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@class='z-bandbox-input']")));

				searchButton1.sendKeys("Document Explorer");
				Thread.sleep(2000);
				driver.findElement(By.xpath("//div[text()=' Document Explorer']")).click();
				logger.info("User searched for Document Explorer window");
				Thread.sleep(2000);

				if (driver
						.findElement(
								By.xpath("//span[text()='Render component problem: Storage provider is not found.']"))
						.isDisplayed()) {
					logger.error("Again User gets an error Popup");
					// driver.findElement(By.xpath("//button[text()='OK']")).click();
					logger.error("Error still not resolved");
					softAssert.fail("Error still not resolved");
				} else {
					logger.info("Document explorer window Opened successfully");
				}

			} else {
				logger.info("Document explorer window Opened successfully");
			}
		} catch (Exception f) {
			softAssert.fail("Error occured while running the script");
			logger.error(f.getMessage());
		}

		softAssert.assertAll();
		softAssert = null;
		driver = null;
	}

}
