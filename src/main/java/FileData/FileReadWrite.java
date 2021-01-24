package FileData;

import java.io.*;

public class FileReadWrite {
    public static final int NUMBER_LINES = 100;

    public void doUserListWriter(File file, String message) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.newLine();
            bw.write(message);
            bw.flush();
        } catch (Exception e) {
            throw new RuntimeException("SWW", e);
        }
    }

    public void doChatWriter(File file, String message) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.newLine();
            bw.write(message);
            bw.flush();
        } catch (Exception e) {
            throw new RuntimeException("SWW", e);
        }
    }

    public String[] readFileTxt(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String[] lines = new String[lineCount(file)];
        if (lineCount(file) < NUMBER_LINES) {
            for (int i = 0; i < lineCount(file); i++) {
                lines[i] = br.readLine();
            }
        } else {
            for (int i = 0; i < NUMBER_LINES; i++) {
                lines[i] = br.readLine();
            }
        }
        br.close();
        return lines;
    }


    public int lineCount(File file) {
        int line = 0;
        try (LineNumberReader lr = new LineNumberReader(new FileReader(file))) {
            while (lr.readLine() != null) {
                line++;
            }
            return line;
        } catch (Exception e) {
            throw new RuntimeException("SWW", e);
        }
    }

    public File chatUserMessage(String nickname){
        File file = new File("D:/ru.geekbrains/history_" + nickname + ".txt");
        return file;
    }
    public File allChatMessage(){
        File file = new File("D:/ru.geekbrains/history_SpaceChat.txt");
        return file;
    }
}





