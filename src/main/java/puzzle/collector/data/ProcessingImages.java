package puzzle.collector.data;

import org.sikuli.script.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;

import static puzzle.collector.data.ProcessingRegion.findSampleRegion;
import static puzzle.collector.data.ProcessingRegion.getRegionOutOfCollection;

/**
 * Утилитный класс для работы с изображениями.
 *
 * Created by OlegusTesterovichus on 02.01.2016.
 */
public class ProcessingImages {

    private ProcessingImages() { }

    /** хранит буферизированное изображение примера пазла */
    private static BufferedImage simpleImage;

    /** Хранит элементы для поиска */
    private static ArrayList<Image> imagesCollection;

    /**
     * Получает из изображения примера элементы для поиска частей пазла
     * с последующим добавлением в imagesCollection.
     */
    public static void splitSampleImageIntoElements()
            throws Exception {
        long startTime = new Date().getTime();
        resizeSampleImage();
        Double imageWidth = (double) simpleImage.getWidth();
        Double imageHeight = (double) simpleImage.getHeight();
        int countWidthSquares = new BigDecimal(imageWidth / Settings.getPuzzleElementWidth())
                .setScale(0, RoundingMode.FLOOR).intValue();
        int countHeightSquares = new BigDecimal(imageHeight / Settings.getPuzzleElementHeight())
                .setScale(0, RoundingMode.FLOOR).intValue();
        Double realPuzzleElementWidth = new BigDecimal(imageWidth /
                (double)countWidthSquares).doubleValue();
        Double realPuzzleElementHeight = new BigDecimal(imageHeight /
                (double)countHeightSquares).doubleValue();
        Double y = (double) simpleImage.getMinY() + realPuzzleElementHeight / (double) 2;
        ArrayList<Image> imagesStorage = new ArrayList<Image>();
        for (int hPoint = 0; hPoint != countHeightSquares; hPoint++) {
            Double x = (double) simpleImage.getMinX() + realPuzzleElementWidth / 2;
            for (int wPoint = 0; wPoint != countWidthSquares; wPoint++) {
                Image image = new Image();
                if ((wPoint == 0 && hPoint == 0) || (wPoint == 0 && hPoint == countHeightSquares -1)) {
                    image.setImage(getCropElement(simpleImage, getIntRoundFloor(x), getIntRoundFloor(y)),
                            getElementLocation(getIntRoundFloor(x), getIntRoundFloor(y)), true);
                    x = x + realPuzzleElementWidth;
                } else if ((wPoint != countWidthSquares - 1 || hPoint != 0) &&
                            (wPoint != countWidthSquares - 1 || hPoint != countHeightSquares - 1)) {
                    image.setImage(getCropElement(simpleImage, getIntRoundFloor(x), getIntRoundFloor(y)),
                            getElementLocation(getIntRoundFloor(x), getIntRoundFloor(y)), false);
                    x = x + realPuzzleElementWidth;
                } else {
                    image.setImage(getCropElement(simpleImage, getIntRoundFloor(x), getIntRoundFloor(y)),
                            getElementLocation(getIntRoundFloor(x), getIntRoundFloor(y)), true);
                }
                imagesStorage.add(image);
            }
            y = y + realPuzzleElementHeight;
        }
        imagesCollection = imagesStorage;
        Utils.debug("Хранилище элементов для поиска сформировано за " + Utils.getRuntime(startTime));
    }

    /**
     * Изменение размера изображения примера до размеров рабочей области пазла.
     */
    private static void resizeSampleImage() throws Exception {
        Region puzzleRegion = getRegionOutOfCollection(ProcessingRegion.Workplace.PUZZLE);
        BufferedImage originalImage = findOriginalSampleImage();
        int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
        BufferedImage resizedImage = new BufferedImage(puzzleRegion.getW(), puzzleRegion.getH(), type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, puzzleRegion.getW(), puzzleRegion.getH(), null);
        g.dispose();
        if (Settings.isDebug()) {
            Utils.writeImage(resizedImage, Settings.getDebugPath());
        }
        simpleImage = resizedImage;
    }

    /**
     * �?щет оригинальное изображение примера в директории для поиска или по скриншоту.
     */
    private static BufferedImage findOriginalSampleImage()
            throws Exception {
        File dir = new File(Settings.getCropImgPath());
        if (dir.exists() && dir.isDirectory() && dir.list().length != 0) {
            return getOriginalSampleImageInDirectory(dir);
        } else {
            return getOriginalSampleImageInRegion();
        }
    }

    /**
     * Получение изображения примера пазла из хранилища примеров.
     *
     * @param directory путь к директории с примерами пазлов.
     * @return Буферизированное изображение примера {@link BufferedImage}.
     */
    private static BufferedImage getOriginalSampleImageInDirectory(File directory)
            throws Exception {
        Region mainSampleRegion = getRegionOutOfCollection(ProcessingRegion.Workplace.MAIN_SAMPLE);
        Pattern temp = Utils.getPatternByRegoin(mainSampleRegion);
        mainSampleRegion.click(Settings.getPath() + "sample.png");
        mainSampleRegion.waitVanish(temp);
        Region puzzleRegion = getRegionOutOfCollection(ProcessingRegion.Workplace.PUZZLE);
        Image image = getSizeImg(Settings.getCropImgPath());
        int x = (int) (puzzleRegion.getX() + puzzleRegion.getW() / 2 -
                (image.getSize(Image.IOpt.WIDTH) / 2) * 1.5);
        int y = (int) (puzzleRegion.getY() + puzzleRegion.getH() / 2 -
                (image.getSize(Image.IOpt.HEIGHT) / 2) * 1.5);
        int w = (int) (image.getSize(Image.IOpt.WIDTH) * 1.5);
        int h = (int) (image.getSize(Image.IOpt.HEIGHT) * 1.5);
        Region region = new Region(x, y, w, h);
        File[] files = directory.listFiles();
        long initTimer = new Date().getTime();
        if (files != null) {
            for (File file : files) {
                BufferedImage BImage = ImageIO.read(file);
                Pattern pattern = new Pattern().setBImage(BImage).similar(Settings.getDefaultSimilarity());
                try {
                    region.find(pattern);
                    Utils.debug("Найдено совпадение с эталоном [" + file.getName() + "] за " +
                            Utils.getRuntime(initTimer));
                    temp = Utils.getPatternByRegoin(mainSampleRegion);
                    mainSampleRegion.click(Settings.getPath() + "sample.png");
                    mainSampleRegion.waitVanish(temp);
                    return ImageIO.read(new File(Settings.getPuzzlesImgPath() + file.getName()));
                } catch (Exception ignored) { }
            }
        }
        throw new Exception("�?зображение для сравнения не найдено.");
    }

    /**
     * Получение изображения примера пазла из скриншота по граничным точкам поиска.
     *
     * @return Буферизированное изображение {@link BufferedImage} примера.
     */
    private static BufferedImage getOriginalSampleImageInRegion() throws Exception {
        Region mainSampleRegion = getRegionOutOfCollection(ProcessingRegion.Workplace.MAIN_SAMPLE);
        Pattern tempToOpen = Utils.getPatternByRegoin(mainSampleRegion);
        mainSampleRegion.click(Settings.getPath() + "sample.png");
        mainSampleRegion.waitVanish(tempToOpen);
        findSampleRegion();
        Region sample = getRegionOutOfCollection(ProcessingRegion.Workplace.SAMPLE);
        BufferedImage sampleImage = sample.getScreen().capture(sample).getImage();
        Pattern tempToClose = Utils.getPatternByRegoin(mainSampleRegion);
        mainSampleRegion.click(Settings.getPath() + "sample.png");
        mainSampleRegion.waitVanish(tempToClose);
        return sampleImage;
    }

    /**
     * Поиск изображений в заданном каталоге с определением максимального размера сторон.
     * Необходим для корректного расчета области поиска.
     *
     * @param directory Путь к директории хранения изображений.
     * @return {@link Image} Объект с максимальными размерами сторон абстрактного изображения.
     * */
    public static Image getSizeImg(String directory) throws Exception {
        long initTimer = new Date().getTime();
        File dir = new File(directory);
        if (!dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            Image abstractImage = new Image(0, 0);
            if (files != null) {
                for (File file : files) {
                    try {
                        BufferedImage BImage = ImageIO.read(file);
                        if (BImage.getWidth() > abstractImage.getSize(Image.IOpt.WIDTH)) {
                            abstractImage.setSize(Image.IOpt.WIDTH, BImage.getWidth());
                        }
                        if (BImage.getHeight() > abstractImage.getSize(Image.IOpt.HEIGHT)) {
                            abstractImage.setSize(Image.IOpt.HEIGHT, BImage.getHeight());
                        }
                    } catch (Exception ignore) {}
                }
                if (abstractImage.getSize(Image.IOpt.WIDTH) > 0 &&
                        abstractImage.getSize(Image.IOpt.HEIGHT) > 0) {
                    Utils.debug("Определен максимальный размер эталонного изображения: " + abstractImage +
                            " за " + Utils.getRuntime(initTimer) + " ms.");
                    return abstractImage;
                }
            }
        }
        throw new Exception(Utils.puzzlePopup("Невозможно получить размеры эталонных " +
                "изображений\nНе найдены файлы изображений в директории:\n" + dir.getAbsolutePath()));
    }

    /** Математическое округление */
    private static int getIntRoundFloor(Double a) {
        return new BigDecimal(a).setScale(0, RoundingMode.FLOOR).intValue();
    }

    /** Возвращает локацию элемента пазла. */
    private static Location getElementLocation(int offsetX, int offsetY) {
        return new Location(simpleImage.getMinX() + offsetX - Settings.getCropElementW() / 2,
                simpleImage.getMinY() + offsetY - Settings.getCropElementH() / 2);
    }

    /** Возвращает изображение центральной части элемента пазла заданной ширины/высоты.*/
    private static BufferedImage getCropElement(BufferedImage image, int x, int y) {
        int cropX = x - Settings.getCropElementW() / 2;
        int cropY = y - Settings.getCropElementH() / 2;
        return image.getSubimage(cropX, cropY, Settings.getCropElementW(), Settings.getCropElementH());
    }

    public static ArrayList<Image> getImagesCollection() {
        return imagesCollection;
    }
}
