package top.ourfor.app.iplay.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import dalvik.system.DexFile;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnnotationUtil {
    private static final List<Class<?>> clazzs = new LinkedList<>();

    public static void load(String packageName, String dexPath) throws IOException {
        DexFile dexFile = new DexFile(dexPath);
        Enumeration<String> urls = dexFile.entries();
        while (urls.hasMoreElements()) {
            String url = urls.nextElement();
            if (url.contains(packageName)) {
                loadClassWith(url);
            }
        }
    }

    public static List<Class<?>> findDecorateWith(Class<? extends Annotation> annotation) {
        if (clazzs.isEmpty()) {
            return new LinkedList<>();
        }
        List<Class<?>> classes = new LinkedList<>();
        for (Class<?> clazz : clazzs) {
            if (clazz.isAnnotationPresent(annotation)) {
                classes.add(clazz);
            }
        }
        return classes;
    }

    private static void loadClassWith(String className) {
        if (className == null || className.contains("$") || className.endsWith(".R")) {
            return;
        }

        try {
            Class<?> clazz = Class.forName(className);
            clazzs.add(clazz);
        } catch (ClassNotFoundException e) {
            log.error("failed load class: {}", className);
            log.error("error: ", e);
        }
    }

    private static void loadClassWith(File file, String packageName) {
        if (file.isFile()) {
            String fileName = file.getName();
            if (fileName.endsWith(".class")) {
                String className = fileName.substring(0, fileName.length() - 6);
                try {
                    Class<?> clazz = Class.forName(packageName + "." + className);
                    clazzs.add(clazz);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else if (file.isDirectory()) {
            String[] children = file.list();
            assert children != null;
            String basePackageName = packageName + '.' + file.getName();
            for (String child : children) {
                loadClassWith(new File(file, child), basePackageName);
            }
        }
    }
}

