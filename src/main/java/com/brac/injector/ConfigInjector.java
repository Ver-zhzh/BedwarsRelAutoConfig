package com.brac.injector;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.lang.reflect.Field;


public abstract class ConfigInjector {
    
   
    protected boolean injectField(Class<?> targetClass, String fieldName, Object config) {
        try {
            Field field = targetClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, config);
            return true;
        } catch (NoSuchFieldException e) {
            System.err.println("[BRAC] Field not found: " + fieldName + " in " + targetClass.getName());
            e.printStackTrace();
            return false;
        } catch (IllegalAccessException e) {
            System.err.println("[BRAC] Cannot access field: " + fieldName);
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("[BRAC] Error injecting field: " + fieldName);
            e.printStackTrace();
            return false;
        }
    }
   
    protected Object getField(Class<?> targetClass, String fieldName) {
        try {
            Field field = targetClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(null);
        } catch (NoSuchFieldException e) {
            System.err.println("[BRAC] Field not found: " + fieldName + " in " + targetClass.getName());
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            System.err.println("[BRAC] Cannot access field: " + fieldName);
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("[BRAC] Error getting field: " + fieldName);
            e.printStackTrace();
            return null;
        }
    }
    
   
    protected boolean injectInstanceField(Object instance, String fieldName, Object config) {
        Class<?> currentClass = instance.getClass();

        // Try to find the field in the class hierarchy
        while (currentClass != null) {
            try {
                Field field = currentClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(instance, config);
                System.out.println("[BRAC] Successfully injected field: " + fieldName + " in " + currentClass.getName());
                return true;
            } catch (NoSuchFieldException e) {
              
                currentClass = currentClass.getSuperclass();
            } catch (IllegalAccessException e) {
                System.err.println("[BRAC] Cannot access field: " + fieldName);
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                System.err.println("[BRAC] Error injecting instance field: " + fieldName);
                e.printStackTrace();
                return false;
            }
        }

    
        System.err.println("[BRAC] Field not found: " + fieldName + " in " + instance.getClass().getName() + " or its parent classes");
        return false;
    }
    
   
    protected Object getInstanceField(Object instance, String fieldName) {
        Class<?> currentClass = instance.getClass();

        // Try to find the field in the class hierarchy
        while (currentClass != null) {
            try {
                Field field = currentClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(instance);
            } catch (NoSuchFieldException e) {
                // Field not found in this class, try parent class
                currentClass = currentClass.getSuperclass();
            } catch (IllegalAccessException e) {
                System.err.println("[BRAC] Cannot access field: " + fieldName);
                e.printStackTrace();
                return null;
            } catch (Exception e) {
                System.err.println("[BRAC] Error getting instance field: " + fieldName);
                e.printStackTrace();
                return null;
            }
        }

        
        System.err.println("[BRAC] Field not found: " + fieldName + " in " + instance.getClass().getName() + " or its parent classes");
        return null;
    }
    
  
    protected FileConfiguration backupConfig(Class<?> targetClass, String fieldName) {
        Object original = getField(targetClass, fieldName);
        if (original instanceof FileConfiguration) {
            // Create a copy of the configuration
            FileConfiguration backup = new YamlConfiguration();
            FileConfiguration originalConfig = (FileConfiguration) original;
            
            // Copy all values
            for (String key : originalConfig.getKeys(true)) {
                backup.set(key, originalConfig.get(key));
            }
            
            return backup;
        }
        return null;
    }
    
    
    protected FileConfiguration backupInstanceConfig(Object instance, String fieldName) {
        Object original = getInstanceField(instance, fieldName);
        if (original instanceof FileConfiguration) {
            // Create a copy of the configuration
            FileConfiguration backup = new YamlConfiguration();
            FileConfiguration originalConfig = (FileConfiguration) original;
            
           
            for (String key : originalConfig.getKeys(true)) {
                backup.set(key, originalConfig.get(key));
            }
            
            return backup;
        }
        return null;
    }
}

