package base;

import java.io.File;

import io.github.cdimascio.dotenv.Dotenv;

public class BaseClass {

	static Dotenv dotenv = Dotenv.load();

	protected static String keyFile 							= dotenv.get("KEY_PAIR_PATH");
	protected static String user 								= dotenv.get("SSH_USERNAME");
	protected static String ssh_IP_ADDRESS					    = dotenv.get("SSH_IP_ADDRESS");
	protected static String port 								= dotenv.get("PORT_NO");
	protected static String BASE_AUTH_USERNAME					= dotenv.get("BASE_AUTH_USERNAME");
	protected static String BASE_AUTH_PASSWORD					= dotenv.get("BASE_AUTH_PASSWORD");
	protected static String webUiUsername						= dotenv.get("WEBUI_USERNAME");
	protected static String webUiPassword						= dotenv.get("WEBUI_PASSWORD");

	public static String FelixWebConsoleURL = "https://" + BASE_AUTH_USERNAME + ":" + BASE_AUTH_PASSWORD
            + "@" + ssh_IP_ADDRESS + ":" + port + "/osgi/system/console/";
	
	public static String WebUiURL = "https://" + ssh_IP_ADDRESS + ":"+ port + "/";
	
	static String projectDir = System.getProperty("user.dir");
    static File shFileDir = new File(projectDir + "/src/main/resources");
    protected static String LocationOfSHfile = shFileDir.getAbsolutePath();
	
	
	protected static String[] CommandToRestart = { "bash", "-c",
			"sudo ./restart_idempiere.sh " + keyFile + " " + user + " " + ssh_IP_ADDRESS };
}
