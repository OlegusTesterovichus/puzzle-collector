package puzzle.collector.data;

import org.sikuli.basics.Settings;
import org.sikuli.script.Region;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Match;
import org.sikuli.script.Location;
import org.sikuli.script.Button;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;

import static puzzle.collector.data.ProcessingImages.getImagesCollection;
import static puzzle.collector.data.ProcessingRegion.getRegionOutOfCollection;
import static puzzle.collector.data.Settings.isDebug;

/**
 * Утилитный класс для сбора пазла в рабочей области.
 *
 * Created by OlegusTesterovichus on 03.01.2016.
 */
public class Collector {

    private Collector() {
        super();
    }

    /** Область рабочего окна пазла. */
    private static Region workplaceRegion;

    /** Собираемая область пазла. */
    private static Region puzzleRegion;

    /** Коллекция собираемых элементов пазла */
    private static ArrayList<Image> imagesCollection;

    /**
     * Осуществляет поиск элементов содержащихся в imagesCollection в рабочей области
     * с последующим перемещением в хранимые локации.
     */
    public static void collectPuzzle() throws Exception {
        imagesCollection = getImagesCollection();
        long startTime = new Date().getTime();
        workplaceRegion = getRegionOutOfCollection(ProcessingRegion.Workplace.MAIN);
        puzzleRegion = getRegionOutOfCollection(ProcessingRegion.Workplace.PUZZLE);
        Settings.AutoWaitTimeout = (float) 0.15;
        for (float i = (float) 0.87; i > 0.60; i = (float) (i - 0.04)) {
            collectWithDefaultSimilar(i);
        }
        for (Object element : imagesCollection) {
            Image image = (Image) element;
            if (!image.isCollected()) {
                System.out.println("Не найден элемент " + image.getPattern().getBImage().toString());
            }
        }
        Utils.debug("Пазл собран за " + Utils.getRuntime(startTime));
    }

    /**
     * Метод осуществляет поиск переданного изображения {@link Image} в рабочей области "workplaceRegion" по
     * флагу "image.isCollected()" и со схожестью 85%.
     * Если элемент найден -> Происходит перемещение элемента в соответствующие координаты области puzzleRegion.
     * Если элемент не найден -> Проставляется флаг "image.notFound()".
     * Если найдено несколько элементов -> Проставляется флаг "image.foundFew()".
     */
    private static void collectWithDefaultSimilar(float sim) throws FindFailed, AWTException {
        int changes = -1;
        if (isDebug()) {
            imagesCollection.get(1).collected();
        }
        while (changes != 0) {
            changes = 0;
            for (Object element : imagesCollection) {
                Image image = (Image) element;
                if (!image.isCollected() && workplaceRegion.exists(image.getPattern()
                        .similar(sim), 0.2) != null) {
                    Match match = workplaceRegion.find(image.getPattern().similar(sim));
                    Location target = image.getLocation();
                    Utils.dragDrop(new Location(match.getX() + 16, match.getY() + 16),
                            new Location(puzzleRegion.getX() + target.getX() + 16,
                                    puzzleRegion.getY() + target.getY() + 16), 50, Button.LEFT);
                    image.collected();
                    changes++;
                }
            }
        }
    }

}
