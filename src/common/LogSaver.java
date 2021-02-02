package common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public class LogSaver {
	public static void saveData(Map<String, Object> dt) {

		DateFormat df = new SimpleDateFormat("yyMMddhhmmss");
		File fout = new File("out/" + df.format(new Date()) + ".txt");
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(fout);
			pw.print(JSON.toJSONString(dt));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.flush();
				pw.close();
			}
		}

	}
}
