package puzzle.collector.data;

import java.util.Date;

/**
 * Класс хранит все настройки необходимые для работы сборщика.
 *
 * Created by OlegusTesterovichus on 04.01.2016.
 */
public class Settings {

    /** Флаг режима отладки */
    private final static boolean debug = true;

    /** Путь к временной директории хранилища изображений для отладки */
    private final static String DEBUG_IMG_PATH = "src\\main\\resources\\imgs\\temp\\";

    /** Путь к директории хранилища изображений */
    private final static String IMG_PATH = "src\\main\\resources\\imgs\\";

    /** Путь к директории хранилища эталонных изображений собираемых пазлов */
    private final static String PUZZLES_IMG_PATH = "src\\main\\resources\\imgs\\puzzles\\fullSize\\";

    /** Путь к директории хранилища порезанных изображений собираемых пазлов */
    private final static String CROP_IMG_PATH = "src\\main\\resources\\imgs\\puzzles\\Cropping\\";

    /** Точность совпадения по умолчанию (85%)*/
    private final static float defaultSimilarity = 0.85F;

    /** Ширина элемента собираемого пазла (px) */
    private final static double puzzleElementWidth = 77;

    /** Высота элемента собираемого пазла (px) */
    private final static double puzzleElementHeight = 80;

    /** ??скомая ширина элемента пазла (px) */
    private static final int cropElementW = 28;

    /** ??скомая высота элемента пазла (px) */
    private static final int cropElementH = 28;

    /**
     * Установка системных настроек.
     * MoveMouseDelay - скорость передвижения курсора.
     * AutoWaitTimeout - максимальное время ожидания (поиск, присутствие и т.д.)
     * ActionLogs - флаг ведения лога действий (отключено за неинформативностью).
     */
    public static void setDefaultSettings() {
        long startTime = new Date().getTime();
        Utils.deleteDirectory(getDebugPath());
        org.sikuli.basics.Settings.MoveMouseDelay = (float) 0.015;
        org.sikuli.basics.Settings.AutoWaitTimeout = (float) 0.3;
        org.sikuli.basics.Settings.ActionLogs = false;
        Utils.debug("Настройки установлены за " + Utils.getRuntime(startTime));
    }

    public static double getPuzzleElementHeight() {
        return puzzleElementHeight;
    }

    public static double getPuzzleElementWidth() {
        return puzzleElementWidth;
    }


    public static int getCropElementW() {
        return cropElementW;
    }

    public static int getCropElementH() {
        return cropElementH;
    }

    public static String getPath() {
        return IMG_PATH;
    }

    public static String getPuzzlesImgPath() {
        return PUZZLES_IMG_PATH;
    }

    public static String getDebugPath() {
        return DEBUG_IMG_PATH;
    }

    public static float getDefaultSimilarity() {
        return defaultSimilarity;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static String getCropImgPath() {
        return CROP_IMG_PATH;
    }

    /** Утилитный класс. Запрещаем инстанцирование. */
    private Settings() { }
}
