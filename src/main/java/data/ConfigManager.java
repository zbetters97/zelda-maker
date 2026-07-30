package data;

import application.GamePanel;

import java.io.*;

public class ConfigManager {

    private final GamePanel gp;
    private final File saveDir = new File(System.getProperty("user.home") + "/zelda-maker/");

    public ConfigManager(GamePanel gp) {
        this.gp = gp;
    }

    public void saveConfig() {

        try {
            // IMPORT FILE
            if (!saveDir.exists()) saveDir.mkdirs();
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(saveDir, "config.txt")));

            // FULLSCREEN
            bw.write("FULLSCREEN\n" + gp.fullScreenOn);
            bw.newLine();

            // MUSIC VOLUME
            bw.write("MUSIC VOLUME\n" + gp.music.volumeScale);
            bw.newLine();

            // SOUND EFFECTS VOLUME
            bw.write("SE VOLUME\n" + gp.se.volumeScale);
            bw.newLine();

            // CONTROLS
            bw.write("BTN_A\n" + gp.keyH.btn_A);
            bw.newLine();
            bw.write("BTN_B\n" + gp.keyH.btn_B);
            bw.newLine();
            bw.write("BTN_X\n" + gp.keyH.btn_X);
            bw.newLine();
            bw.write("BTN_Y\n" + gp.keyH.btn_Y);
            bw.newLine();
            bw.write("BTN_L\n" + gp.keyH.btn_L);
            bw.newLine();
            bw.write("BTN_R\n" + gp.keyH.btn_R);
            bw.newLine();

            // CLOSE FILE
            bw.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void loadConfig() {

        try {
            // IMPORT FILE
            BufferedReader br = new BufferedReader(new FileReader(new File(saveDir,"config.txt")));

            br.readLine();

            // FULL SCREEN
            String s = br.readLine();
            gp.fullScreenOn = Boolean.parseBoolean(s);
            br.readLine();

            // MUSIC VOLUME
            s = br.readLine();
            gp.music.volumeScale = Integer.parseInt(s);
            br.readLine();

            // SOUND EFFECTS VOLUME
            s = br.readLine();
            gp.se.volumeScale = Integer.parseInt(s);
            br.readLine();

            // CONTROLS
            s = br.readLine();
            gp.keyH.btn_A = Integer.parseInt(s);
            br.readLine();
            s = br.readLine();
            gp.keyH.btn_B = Integer.parseInt(s);
            br.readLine();
            s = br.readLine();
            gp.keyH.btn_X = Integer.parseInt(s);
            br.readLine();
            s = br.readLine();
            gp.keyH.btn_Y = Integer.parseInt(s);
            br.readLine();
            s = br.readLine();
            gp.keyH.btn_L = Integer.parseInt(s);
            br.readLine();
            s = br.readLine();
            gp.keyH.btn_R = Integer.parseInt(s);
            br.readLine();

            br.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());

            // FULL SCREEN
            gp.fullScreenOn = false;

            // MUSIC VOLUME
            gp.music.volumeScale = 3;

            // SOUND EFFECTS VOLUME
            gp.se.volumeScale = 3;
        }
    }
}