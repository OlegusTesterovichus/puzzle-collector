package puzzle.collector.data;

import org.sikuli.script.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import static org.sikuli.script.Sikulix.popup;
import static puzzle.collector.data.Settings.isDebug;

/**
 *
 * Created by OlegusTesterovichus on 01.01.2016.
 */
public class Utils {

    /**
     * Метод осуществляет перенос элементов пазла по координатам экрана.
     *
     * @param start координаты захвата элемента пазла.
     * @param end целевые координаты.
     * @param ms скорость совершения переноса.
     * @param buttons кнопка которой следует осуществить нажатие.
     */
    public static void dragDrop(Location start, Location end, long ms, int buttons)
            throws AWTException {
        RobotDesktop robot = new RobotDesktop();
        robot.mouseMove(start.x, start.y);
        robot.mousePress(buttons);
        robot.waitForIdle();
        robot.smoothMove(start, end, ms);
        robot.mouseRelease(buttons);
        robot.waitForIdle();
    }

    /**
     * Метод возвращает шаблон {@link Pattern} полученный из переданной области {@link Region}
     *
     * @param region область из которой требуется получить шаблон.
     * @return шаблон с изображением из области region.
     */
    public static Pattern getPatternByRegoin(Region region) {
        return new Pattern(region.getScreen().capture(region).getImage());
    }

    /**
     * Метод выводит диалоговое окно с текстом переданно в параметре msg
     *
     * @param msg Текстовка сообщения.
     * @return Текстовка сообщения (для исключений).
     */
    public static String puzzlePopup(String msg) {
        popup(msg, "Puzzle puzzleCollector");
        return msg;
    }

    /**
     * Вывод в консоль дополнительной информации для отладки.
     *
     * @param msg Текстовка сообщения.
     */
    public static void debug(String msg) {
        if (isDebug()) {
            System.out.println(msg);
        }
    }

    /**
     * Получает на вход время запуска "таймера" и возвращает разницу от времени вызова метода.
     *
     * @param startTime время запуска "таймера".
     * @return время выполнения в ms.
     */
    public static String getRuntime(long startTime) {
        return (new Date().getTime() - startTime) + " ms.";
    }

    /**
     * Метод осуществляет запись буферизированного изображения {@link BufferedImage} на диск.
     *
     * @param image изображение для записи на диск.
     * @param path путь к директории которую производится запись.
     */
    public static String writeImage(BufferedImage image, String path) throws Exception {
        File file = new File(path);
        String fullLink = path + new Date().getTime() + ".png";
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new IOException("Не удалось создать директорию\n" + file.getAbsolutePath());
            }
        }
        try {
            ImageIO.write(image, "png", new File(fullLink));
            return fullLink;
        } catch (IOException e) {
            throw new IOException(puzzlePopup("Ошибка при сохранении изображения на диск.\n" +
                    e.getMessage()));
        }
    }

    /**
     * Удаляет директорию со всеми вложениями.
     *
     * @param path путь к директории которую необходимо удалить.
     */
    public static void deleteDirectory(String path) {
        File file = new File(path);
        if (!file.exists())
            return;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for(File f : files)
                    deleteDirectory(f.getAbsolutePath());
            }
        }
        if (!file.delete()) {
            System.out.println("Не удалось удалить временную директорию\n " + file.getAbsolutePath());
        }
    }

    /**
     * Метод вырезает центральную часть изображения по размеру переданного параметра side.
     *
     * @param fullSizeDirectory директория с полноразмерными изображениями.
     * @param cropDirectory директория для записи обрезанных изображений.
     * @param side размер сторон (px)
     * TODO прикрутить к поиску изображения.
     */
    public static void copImage(String fullSizeDirectory, String cropDirectory, int side)
            throws Exception {
        File full = new File(fullSizeDirectory);
        File crop = new File(cropDirectory);
        if (!crop.exists()) {
            if (!crop.mkdirs()) {
                throw new IOException("Не удалось создать директорию\n" + crop.getAbsolutePath());
            }
        }
        if (!full.exists() && full.isDirectory()) {
            throw new Exception("Не найдена директория с примерами пазлов\n" +
                    full.getAbsolutePath());
        }
        File[] files = full.listFiles();
        if (files != null) {
            for (File file : files) {
                BufferedImage originalImage = ImageIO.read(file);
                BufferedImage cropImage = originalImage.getSubimage(originalImage
                                .getWidth() / 2 - side / 2, originalImage
                        .getHeight() / 2 - side / 2, side, side);
                writeImage(cropImage, cropDirectory + file.getName());
            }
        }
    }

    /** Утилитный класс. Запрещаем инстанцирование. */
    private Utils() { }

}
