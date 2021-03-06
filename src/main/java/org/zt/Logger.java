package org.zt;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import org.apache.commons.io.FileUtils;

public class Logger {

	private static File logFile = new File("main.log");

	public static void log(String str) {
		if (Config.instance().getLogLevel() > 3) {
			return;
		}
		str = "[info]" + str;
		printLog(str);
	}

	private static void printLog(String str) {
		try {
			String content = "[" + (new Date()).toLocaleString() + "]" + str + "\n";
			System.out.print(content);
			FileUtils.writeStringToFile(logFile, content, "UTF-8", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void debug(String str) {
		if (Config.instance().getLogLevel() > 2) {
			return;
		}
		str = "[debug]" + str;
		printLog(str);
	}
}
