package com.github.lit.code.util;

import java.io.*;
import java.util.logging.Logger;

/**
 * User : liulu
 * Date : 2018/2/6 15:39
 * version $Id: ResourceUtils.java, v 0.1 Exp $
 */
public class FileUtils {

    private static final Logger LOGGER = Logger.getLogger(FileUtils.class.getName());

    public static String readToString (String filePath) {
        if (!filePath.startsWith(File.separator)) {
            filePath = File.separator + filePath;
        }
        InputStream inputStream = FileUtils.class.getResourceAsStream(filePath);
        if (inputStream == null) {
            return "";
        }
        return readToString(inputStream);
    }

    public static String readToString(InputStream inputStream) {

        byte[] b = new byte[1024 * 4];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            for (int len; ; ) {
                len = inputStream.read(b, 0, b.length);
                if (len == -1) {
                    break;
                }
                outputStream.write(b, 0, len);
            }
            return outputStream.toString("UTF-8");
        } catch (IOException e) {
            //
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                //
            }
        }
        return "";
    }

    public static void writeToFile (String text, String filePath, boolean overwrite) {

        try {

            File file = new File(filePath);
            if (file.exists() && !overwrite) {
                LOGGER.info(String.format("file %s is exist, do nothing", filePath));
                return;
            }
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(text);
            fileWriter.flush();
            fileWriter.close();
            LOGGER.info(String.format("generate file: %s ", filePath));
        } catch (IOException e) {
            //
        }

    }


}
