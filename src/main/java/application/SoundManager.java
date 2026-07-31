package application;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class SoundManager {

    // CLIP HOLDERS
    public Clip clip;
    private final String[][] sounds = new String[9][];
    private final Map<Integer, String> songs = new HashMap<>();
    private final int[] loopStarts = {111, 7498, 182, 538, 32332, 7236, 4234};
    public int maxSongs = 0;

    /* VOLUME SLIDER */
    private FloatControl gainControl;
    public int volumeScale = 3;
    public float volume;
    private volatile boolean isLooping = false;

    /**
     * CONSTRUCTOR
     * Imports all sound files
     */
    public SoundManager() {

        sounds[0] = getSounds("00_music");
        maxSongs = sounds[0].length - 1;
        fillSongNames(sounds[0]);

        sounds[1] = getSounds("01_ui");
        sounds[2] = getSounds("02_actions");
        sounds[3] = getSounds("03_player");
        sounds[4] = getSounds("04_enemies");
        sounds[5] = getSounds("05_objects");
        sounds[6] = getSounds("06_collectables");
        sounds[7] = getSounds("07_items");
        sounds[8] = getSounds("08_projectiles");
    }

    private void fillSongNames(String[] sounds) {

        int index = 0;
        for (String song : sounds) {

            // Format song name, add to list of songs
            song = song.replace("/sound/00_music/", "")
                    .replaceFirst("^\\d+_", "")
                    .replace(".wav", "")
                    .replace("_", " ");

            songs.put(index, capitalizeWords(song));

            index++;
        }
    }
    private String capitalizeWords(String text) {
        StringBuilder result = new StringBuilder();

        for (String word : text.split(" ")) {
            result.append(word.substring(0, 1).toUpperCase())
                    .append(word.substring(1))
                    .append(" ");
        }

        return result.toString().trim();
    }

    private String[] getSounds(String library) {

        List<String> sounds = new ArrayList<>();

        try {
            boolean runningFromJar =
                    Driver.class.getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .getPath()
                            .endsWith(".jar");

            if (runningFromJar) {
                String jarPath = new File(
                        Driver.class.getProtectionDomain()
                                .getCodeSource()
                                .getLocation()
                                .toURI()
                ).getPath();

                JarFile jarFile = new JarFile(jarPath);
                Enumeration<JarEntry> entries = jarFile.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();

                    if (entry.getName().startsWith("sound/" + library + "/") && !entry.isDirectory()) {
                        sounds.add("/" + entry.getName());
                    }
                }

                jarFile.close();
            }
            else {
                File folder = new File(
                        Objects.requireNonNull(
                                getClass().getClassLoader()
                                        .getResource("sound/" + library)
                        ).toURI()
                );

                for (File f : Objects.requireNonNull(folder.listFiles())) {
                    sounds.add("/sound/" + library + "/" + f.getName().toLowerCase());
                }
            }

        }
        catch (Exception e) {
            System.out.println("Error playing sound: " + e.getMessage());
        }

        return sounds.toArray(new String[0]);
    }

    public String getSongName(Integer index) {
        if (index < 0 || songs.size() < index) return songs.get(0);
        return songs.get(index);
    }

    public void setFile(int category, int record) {

        try {
            String path = sounds[category][record].substring(1);

            InputStream is = getClass()
                    .getClassLoader()
                    .getResourceAsStream(path);

            if (is == null) {
                throw new RuntimeException("Sound not found: " + path);
            }

            BufferedInputStream bis = new BufferedInputStream(is);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bis);

            clip = AudioSystem.getClip();
            clip.open(ais);

            gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            checkVolume();
        }
        catch (Exception e) {
            System.out.println("Error loading sound: " + e.getMessage());
        }
    }

    public int getLoopStart(int record) {
        return record >= loopStarts.length ? 0 : loopStarts[record];
    }

    public void loop(int startTime) {

        if (clip == null) {
            return;
        }

        // Set looping flag to true
        isLooping = true;

        // Get audio format and calculate total frames
        AudioFormat format = clip.getFormat();
        float frameRate = format.getFrameRate();
        int totalFrames = clip.getFrameLength();

        // Convert start time to frames and ensure it's within bounds
        int startFrame = Math.max(0, (int) (startTime * frameRate / 1000));

        // Check for invalid start frame
        if (startFrame >= totalFrames || startTime <= 0) {
            clip.start();
            return;
        }

        // Set up a line listener to handle the looping
        clip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP) {
                synchronized (clip) {
                    // Only loop if still in looping mode and clip is open
                    if (isLooping && clip.isOpen()) {
                        clip.setFramePosition(startFrame);
                        clip.start();
                    }
                }
            }
        });

        // Start playing from the beginning
        clip.setFramePosition(0);
        clip.start();
    }

    public void play() {
        clip.start();
    }

    public void stop() {
        isLooping = false;

        if (clip != null) {
            clip.stop();
        }
    }

    public void checkVolume() {

        switch (volumeScale) {
            case 0:
                volume = -80f;
                break;
            case 1:
                volume = -20f;
                break;
            case 2:
                volume = -12f;
                break;
            case 3:
                volume = -5f;
                break;
            case 4:
                volume = 1f;
                break;
            case 5:
                volume = 6f;
                break;
        }

        setGain(volume);
    }

    private void setGain(float gain) {
        if (gainControl == null) return;
        gainControl.setValue(gain);
    }
}