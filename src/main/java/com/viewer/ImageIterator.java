package com.viewer;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ImageIterator {
    private List<File> images;
    private int index = 0;

    // Разрешённые расширения
    private static final String[] ALL_EXTS =
            {".jpg",".jpeg",".png",".gif",".bmp",".webp"};

    public ImageIterator(File folder, String filter) {
        if (folder == null || !folder.exists()) {
            images = List.of();
            return;
        }
        File[] files = folder.listFiles(f -> {
            String name = f.getName().toLowerCase();
            if (filter.equals("Все")) {
                return Arrays.stream(ALL_EXTS).anyMatch(name::endsWith);
            }
            return name.endsWith(filter.toLowerCase());
        });
        images = files == null ? List.of()
                : Arrays.stream(files)
                .sorted()
                .collect(Collectors.toList());
    }

    public boolean hasNext() { return !images.isEmpty(); }

    // Следующее (с переходом к началу)
    public File next() {
        if (images.isEmpty()) return null;
        index = (index + 1) % images.size();
        return current();
    }

    // Предыдущее (с переходом к концу)
    public File previous() {
        if (images.isEmpty()) return null;
        index = (index - 1 + images.size()) % images.size();
        return current();
    }

    public File first() {
        index = 0;
        return current();
    }

    public File last() {
        index = images.isEmpty() ? 0 : images.size() - 1;
        return current();
    }

    public File current() {
        return images.isEmpty() ? null : images.get(index);
    }

    public int getIndex()  { return index + 1; }
    public int getTotal()  { return images.size(); }
}