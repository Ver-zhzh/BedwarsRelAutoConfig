package com.brac.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


public class FileUtil {
    

    public static boolean copyFile(File source, File destination) {
        try {
            if (!source.exists()) {
                return false;
            }
            
            // Create parent directories if they don't exist
            File parentDir = destination.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
   
    public static boolean deleteRecursively(File file) {
        if (!file.exists()) {
            return false;
        }
        
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    deleteRecursively(child);
                }
            }
        }
        
        return file.delete();
    }
    
   
    public static boolean createDirectory(File directory) {
        if (directory.exists()) {
            return directory.isDirectory();
        }
        return directory.mkdirs();
    }
    
    
    public static boolean isDirectoryEmpty(File directory) {
        if (!directory.exists() || !directory.isDirectory()) {
            return true;
        }
        
        File[] files = directory.listFiles();
        return files == null || files.length == 0;
    }

   
    public static String readFileContent(File file) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
 
    public static boolean writeFileContent(File file, String content) {
        try {
            // Create parent directories if they don't exist
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                writer.write(content);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    

    public static String getReadableFileSize(File file) {
        if (!file.exists()) {
            return "0 B";
        }
        
        long size = file.length();
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }
}

