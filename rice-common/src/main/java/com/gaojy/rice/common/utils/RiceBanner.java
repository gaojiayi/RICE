package com.gaojy.rice.common.utils;

import com.gaojy.rice.common.constants.LoggerName;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

import java.io.InputStream;
import java.io.InputStreamReader;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName RiceBanner.java
 * @Description 打印banner
 * @createTime 2022/01/05 00:19:00
 */
public class RiceBanner {
    private static Logger log = LoggerFactory.getLogger(LoggerName.COMMON_LOGGER_NAME);

    public static void show(String path, int color_index) {
        Ansi.Color color;
        switch (color_index) {
            case 0:
                color = BLACK;
                break;
            case 1:
                color = RED;
                break;
            case 2:
                color = GREEN;
                break;
            case 3:
                color = YELLOW;
                break;
            case 4:
                color = BLUE;
                break;
            case 5:
                color = MAGENTA;
                break;
            case 6:
                color = CYAN;
                break;
            case 7:
                color = WHITE;
                break;
            default:
                color = DEFAULT;
        }
        BufferedReader in = null;
        AnsiConsole.systemInstall();
        try {
            InputStream stream = RiceBanner.class.getClassLoader().getResourceAsStream("rice.banner");
            in = new BufferedReader(new InputStreamReader(stream));
            String str = "";
            while ((str = in.readLine()) != null) {
                //log.info(ansi().fg(color).a(str).reset().toString());
               String  a = ansi().fg(color).a(str).reset().toString();
                System.out.println(ansi().fg(color).a(str).reset());
            }
        } catch (Exception e) {
            log.error("Print RICE banner info exception:" + e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            AnsiConsole.systemUninstall();
        }
    }

    public static void main(String[] args) {
        show("./rice.banner",2);
    }
}
