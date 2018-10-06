package co.edu.intecap.boundservice.model;

public class Song {
    String fileName;
    String absolutePath;

    public Song(String fileName,String absolutePath) {
        this.fileName = fileName;
        this.absolutePath=absolutePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }
}
