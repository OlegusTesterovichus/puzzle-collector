package puzzle.collector.data;

import org.sikuli.script.Location;
import org.sikuli.script.Pattern;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;

import static puzzle.collector.data.Settings.getDefaultSimilarity;
import static puzzle.collector.data.Settings.*;
import static puzzle.collector.data.Utils.*;

/**
 * Вспомогательный класс для хранения изображений и информации к ним.
 *
 * Created by OlegusTesterovichus on 04.01.2016.
 */
public class Image {

    /** хранит информацию по изображению */
    private HashMap<Object, Object> imageMap;

    public Image() {
        this.imageMap = new HashMap<Object, Object>();
    }

    public Image(Integer w, Integer h) {
        this.imageMap = new HashMap<Object, Object>();
        this.imageMap.put(IOpt.WIDTH, w);
        this.imageMap.put(IOpt.HEIGHT, h);
    }

    public enum IOpt {
        WIDTH,
        HEIGHT
    }

    /**
     * Добавляет в imageMap {@link HashMap} изображение {@link BufferedImage},
     * локацию {@link Location} и флаг собираемости.
     *
     * @param image буферизированное изображение.
     * @param imageLocation Место нахождения изображения.
     * @param isCollected Флаг необходимости собирать элемент.
     */
    public void setImage(BufferedImage image, Location imageLocation, boolean isCollected)
            throws Exception {
        this.imageMap.put("imagePattern", new Pattern().setBImage(image)
                .similar(getDefaultSimilarity()));
        this.imageMap.put("imageLocation", imageLocation);
        this.imageMap.put("isCollected", isCollected);
        if (isDebug()) {
            writeImage(image, getDebugPath());
        }
        InputStream qwe = getClass().getResourceAsStream("");
    }

    public Integer getSize(IOpt key) {
        return (Integer) this.imageMap.get(key);
    }

    public void setSize(IOpt opt, Integer size) {
        this.imageMap.put(opt, size);
    }

    public void collected() {
        this.imageMap.put("isCollected", true);
    }

    public boolean isCollected() {
        return (Boolean) imageMap.get("isCollected");
    }

    public Pattern getPattern() {
        return (Pattern) imageMap.get("imagePattern");
    }

    public Location getLocation() {
        return (Location) imageMap.get("imageLocation");
    }

    @Override
    public String toString() {
        return this.imageMap.toString();
    }

}
