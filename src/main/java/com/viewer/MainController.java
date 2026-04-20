package com.viewer;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.util.Duration;
import java.io.File;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainController {
    @FXML ImageView imageView;
    @FXML Label counterLabel, infoLabel;
    @FXML ComboBox<String> filterBox, transitionBox;

    private ImageIterator iterator;
    private Timeline autoSlide;
    private File imageFolder;

    @FXML
    public void initialize() {
        filterBox.getItems().addAll("Все", "jpg", "png", "gif", "bmp");
        filterBox.setValue("Все");

        transitionBox.getItems().addAll("Исчезание", "Сдвиг", "Масштаб");
        transitionBox.setValue("Исчезание");

        imageFolder = new File("src/main/resources/images");
        loadImages();
    }

    private void loadImages() {
        iterator = new ImageIterator(imageFolder, filterBox.getValue());
        if (iterator.hasNext()) showImage(iterator.current());
        else { imageView.setImage(null); counterLabel.setText("Пусто"); }
        updateCounter();
    }

    private void showImage(File file) {
        if (file == null) return;
        String effect = transitionBox.getValue();
        switch (effect) {
            case "Сдвиг":
                animateSlide(file);
                break;
            case "Масштаб":
                animateScale(file);
                break;
            default:
                animateFade(file);
                break;
        }
        showFileInfo(file);
    }

    // ── Анимация 1: Исчезание ──
    private void animateFade(File file) {
        FadeTransition ft = new FadeTransition(Duration.millis(400), imageView);
        ft.setFromValue(1); ft.setToValue(0);
        ft.setOnFinished(e -> {
            imageView.setImage(new Image(file.toURI().toString()));
            FadeTransition fi = new FadeTransition(Duration.millis(400), imageView);
            fi.setFromValue(0); fi.setToValue(1);
            fi.play();
        });
        ft.play();
    }

    // ── Анимация 2: Сдвиг ──
    private void animateSlide(File file) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(250), imageView);
        tt.setToX(-700);
        tt.setOnFinished(e -> {
            imageView.setImage(new Image(file.toURI().toString()));
            imageView.setTranslateX(700);
            TranslateTransition ti = new TranslateTransition(Duration.millis(250), imageView);
            ti.setToX(0); ti.play();
        });
        tt.play();
    }

    // ── Анимация 3: Масштаб ──
    private void animateScale(File file) {
        ScaleTransition st = new ScaleTransition(Duration.millis(250), imageView);
        st.setToX(0); st.setToY(0);
        st.setOnFinished(e -> {
            imageView.setImage(new Image(file.toURI().toString()));
            ScaleTransition si = new ScaleTransition(Duration.millis(250), imageView);
            si.setFromX(0); si.setFromY(0);
            si.setToX(1);   si.setToY(1);
            si.play();
        });
        st.play();
    }

    // ── Навигация ──
    @FXML void onNext()  { showImage(iterator.next());     updateCounter(); }
    @FXML void onPrev()  { showImage(iterator.previous()); updateCounter(); }
    @FXML void onFirst() { showImage(iterator.first());    updateCounter(); }
    @FXML void onLast()  { showImage(iterator.last());     updateCounter(); }

    @FXML void onFilterChanged() { loadImages(); }

    @FXML void onAuto() {
        if (autoSlide != null && autoSlide.getStatus() == Animation.Status.RUNNING) {
            autoSlide.stop(); return;
        }
        autoSlide = new Timeline(new KeyFrame(Duration.seconds(3), e -> onNext()));
        autoSlide.setCycleCount(Timeline.INDEFINITE);
        autoSlide.play();
    }

    private void updateCounter() {
        counterLabel.setText(iterator.getIndex() + " из " + iterator.getTotal());
    }

    // ── Информация о файле ──
    private void showFileInfo(File f) {
        try {
            BasicFileAttributes attr = Files.readAttributes(
                    f.toPath(), BasicFileAttributes.class);
            long kb = f.length() / 1024;
            String date = new SimpleDateFormat("dd.MM.yyyy HH:mm")
                    .format(new Date(attr.creationTime().toMillis()));
            infoLabel.setText(
                    "Файл: " + f.getName() +
                            " | Размер: " + kb + " КБ" +
                            " | Создан: " + date);
        } catch (Exception ex) {
            infoLabel.setText("Файл: " + f.getName());
        }
    }
}