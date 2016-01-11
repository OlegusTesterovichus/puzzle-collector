package puzzle.collector.data;

import org.sikuli.script.Button;
import org.sikuli.script.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.HashMap;

/**
 * Утилитный класс для работы с регионами.
 * Регион - ограниченная область экрана в которой осуществляется та или иная активность.
 *
 * Created by OlegusTesterovichus on 01.01.2016.
 */
public class ProcessingRegion {

    /** Коллекция регионов для сбора пазла */
    private static HashMap<Workplace, Region> regionCollection = new HashMap<Workplace, Region>();

    /**
     * {@link #MAIN} Регион рабочего окна пазла.
     * {@link #MAIN_SAMPLE} Регион нахождения кнопки вызова окна с примером пазла.
     * {@link #PUZZLE} Собираемая область пазла.
     * {@link #SAMPLE} Область нахождения примера пазла.
     */
    public enum Workplace {
        MAIN,
        MAIN_SAMPLE,
        PUZZLE,
        SAMPLE
    }

    /**
     * Метод осуществляет поиск по маркерам региона рабочего окна пазла.
     */
    public static void findWorkplaceRegion() throws FindFailed {
        long startTime = new Date().getTime();
        Screen screen = new Screen();
        Match LTPattern = screen.find(new Pattern(Settings.getPath() + "ltr.png")
                .similar(Settings.getDefaultSimilarity()));
        Match RBPattern = screen.find(new Pattern(Settings.getPath() + "rbr.png")
                .similar(Settings.getDefaultSimilarity()));
        Match HLBPattern = screen.find(new Pattern(Settings.getPath() + "hlbr.png")
                .similar(Settings.getDefaultSimilarity()));
        Integer MRegX = LTPattern.getX() + LTPattern.getW();
        Integer MRegY = LTPattern.getY() + LTPattern.getH();
        Integer MRegW = RBPattern.getX() - MRegX;
        Integer MRegH = RBPattern.getY() - MRegY; // + RBPattern.getH() + LTPattern.getH()
        regionCollection.put(Workplace.MAIN, new Region(MRegX, MRegY, MRegW, MRegH));
        Integer HRegY = MRegY + MRegH;
        Integer HRegW = MRegW / 2;
        Integer HRegH = HLBPattern.getY() + HLBPattern.getH();
        regionCollection.put(Workplace.MAIN_SAMPLE, new Region(MRegX, HRegY, HRegW, HRegH));
        Utils.debug("Рабочая область найдена за " + Utils.getRuntime(startTime));
    }

    /**
     * Метод осуществляет поиск собираемой области пазла по контрольным (граничным) точкам.
     */
    public static void findWorkplacePuzzleRegion() throws Exception {
        long startTime = new Date().getTime();
        Region reg = regionCollection.get(Workplace.MAIN);
        Region leftRegion = new Region(reg.getX(), reg.getY(), reg.getW() / 2, reg.getH());
        Match leftBorder = findMatch(leftRegion, Settings.getPath() + "vertical_border2.png");
        Region topRegion = new Region(reg.getX(), reg.getY(), reg.getW(), reg.getH() / 2);
        Match topBorder = findMatch(topRegion, Settings.getPath() + "horizontal_border2.png");
        Region bottomRegion = new Region(reg.getX(), reg.getY() + reg.getH() / 2, reg.getW(),
                reg.getH() / 2);
        Match bottomBorder = findMatch(bottomRegion, Settings.getPath() + "horizontal_border2.png");
        Region rightRegion = new Region(reg.getX() + reg.getW() / 2, reg.getY(), reg.getW() / 2,
                reg.getH());
        Match rightBorder = findMatch(rightRegion, Settings.getPath() + "vertical_border2.png");
        int puzzleRegionX = leftBorder.getX() + leftBorder.getW() / 2 - 1;
        int puzzleRegionY = topBorder.getY() + topBorder.getH() / 2 - 1;
        int puzzleRegionW = rightBorder.getX() + rightBorder.getW() / 2 - puzzleRegionX + 1;
        int puzzleRegionH = bottomBorder.getY() + bottomBorder.getH() / 2 - puzzleRegionY + 1;
        if (Settings.isDebug()) {
            Region model = new Region(puzzleRegionX, puzzleRegionY, puzzleRegionW, puzzleRegionH);
            BufferedImage screen = model.getScreen()
                    .capture(puzzleRegionX, puzzleRegionY, puzzleRegionW, puzzleRegionH).getImage();
            Utils.writeImage(screen, Settings.getDebugPath());
        }
        regionCollection.put(Workplace.PUZZLE,
                new Region(puzzleRegionX, puzzleRegionY, puzzleRegionW, puzzleRegionH));
        Utils.debug("Рабочая область пазла найдена за " + Utils.getRuntime(startTime));
    }

    /**
     * Метод осуществляет поиск области c примером пазла по контрольным (граничным) точкам.
     */
    public static void findSampleRegion() throws Exception {
        Region reg = regionCollection.get(Workplace.MAIN);
        Region leftRegion = new Region(reg.getX(), reg.getY(), reg.getW() / 2, reg.getH());
        Match leftBorder = findMatch(leftRegion, Settings.getPath() + "leftSampleBorder.png");
        Region topRegion = new Region(reg.getX(), reg.getY(), reg.getW(), reg.getH() / 2);
        Match topBorder = findMatch(topRegion, Settings.getPath() + "topSampleBorder.png");
        Region rightRegion = new Region(reg.getX() + reg.getW() / 2, reg.getY(), reg.getW() / 2,
                reg.getH());
        Match rightBorder = findMatch(rightRegion, Settings.getPath() + "rightSampleBorder.png");
        Region bottomRegion = new Region(reg.getX(), reg.getY() + reg.getH() / 2, reg.getW(),
                reg.getH() / 2);
        Match bottomBorder = findMatch(bottomRegion, Settings.getPath() + "bottomSampleBorder.png");
        int sampleRegionX = leftBorder.getX() + leftBorder.getW() - 1;
        int sampleRegionY = topBorder.getY() + topBorder.getH() - 1;
        int sampleRegionW = rightBorder.getX() - sampleRegionX + 1;
        int sampleRegionH = bottomBorder.getY() - sampleRegionY + 1;
        if (Settings.isDebug()) {
            Region model = new Region(sampleRegionX, sampleRegionY, sampleRegionW, sampleRegionH);
            BufferedImage screen = model.getScreen()
                    .capture(sampleRegionX, sampleRegionY, sampleRegionW, sampleRegionH).getImage();
            Utils.writeImage(screen, Settings.getDebugPath());
        }
        regionCollection.put(Workplace.SAMPLE,
                new Region(sampleRegionX, sampleRegionY, sampleRegionW, sampleRegionH));
    }

    /**
     * Осуществляет поиск изображения в переданном регионе.
     *
     * @param reg Область экрана для поиска.
     * @param imagePath Путь к искомому изображению.
     * @return объект совпадения {@link Match}
     */
    private static Match findMatch(Region reg, String imagePath) throws Exception {
        Pattern pattern = new Pattern(imagePath).similar(Settings.getDefaultSimilarity());
        try {
            return reg.find(pattern);
        } catch (FindFailed ignore) {
            throw new Exception(Utils.puzzlePopup("Не найден шаблон\n" + pattern.toString() +
                    "\nв регионе " + reg.toString()));
        }
    }

    /**
     * Метод осуществляет очистку рабочей области пазла в границах рабочего окна.
     */
    public static void discardWorkplacePuzzleRegion() throws AWTException {
        long startTime = new Date().getTime();
        Region puzzleRegion = regionCollection.get(Workplace.PUZZLE);
        int hClearPoints = (puzzleRegion.getH() / (int) Settings.getPuzzleElementHeight()) * 2;
        int wClearPoints = (puzzleRegion.getW() / (int) Settings.getPuzzleElementWidth()) * 2;
        float hStep = puzzleRegion.getH() / hClearPoints;
        float wStep = puzzleRegion.getW() / wClearPoints;
        int y = (int) (puzzleRegion.getY() + (hStep / 2));
        for (int hPoint = 0; hPoint < hClearPoints; hPoint++) {
            int x = (int) (puzzleRegion.getX() + (wStep / 2));
            for (int wPoint = 0; wPoint < wClearPoints; wPoint++) {
                if (hPoint < hClearPoints / 2) {
                    Utils.dragDrop(new Location(x, y), new Location(x, puzzleRegion.getY() -
                            Settings.getPuzzleElementHeight()), 50, Button.LEFT);
                    x = (int) (x + (wStep));
                } else {
                    Utils.dragDrop(new Location(x, y), new Location(x, puzzleRegion.getY() +
                            puzzleRegion.getH() + Settings.getPuzzleElementHeight()), 50, Button.LEFT);
                    x = (int) (x + (wStep));
                }
            }
            y = (int) (y + (hStep));
        }
        Utils.debug("Рабочая область пазла очищена за " + Utils.getRuntime(startTime));
    }

    /**
     * Возвращает область {@link Region} из коллекции regionCollection по индексу {@link Workplace}
     *
     * @param i экземпляр перечисления
     * @return область экрана.
     */
    public static Region getRegionOutOfCollection(Workplace i) throws Exception {
        if (regionCollection.get(i) != null) {
            return regionCollection.get(i);
        }
        throw new Exception("Не найдена область с индексом \"" + i + "\"");
    }

    /** Утилитный класс. Запрещаем инстанцирование. */
    public ProcessingRegion() { }

}
