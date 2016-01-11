package puzzle.collector;

import java.util.Date;

import org.sikuli.script.Sikulix;
import org.sikuli.script.Screen;
import org.sikuli.script.Pattern;
import puzzle.collector.data.ProcessingRegion;

import static puzzle.collector.data.Utils.getRuntime;
import static puzzle.collector.data.Collector.collectPuzzle;
import static puzzle.collector.data.Settings.getPath;
import static puzzle.collector.data.Settings.setDefaultSettings;
import static puzzle.collector.data.ProcessingImages.splitSampleImageIntoElements;
import static puzzle.collector.data.Utils.puzzlePopup;

/**
 * Программа тестирования сбора пазлов в игре sky2fly (http://sky2fly.ru/).
 * Для корректной работы требуются библиотеки SikuliX (https://github.com/RaiMan/SikuliX-2014.git).
 *
 * Created by OlegusTesterovichus on 01.01.2016.
 */
public class PuzzleCollector {

    public static void main(String[] args) throws Exception {
        new Screen().wait(new Pattern(getPath() + "init.png").similar(0.95F), 9999999);
        Sikulix.popup("Нажмите кнопку \"OK\" для начала сбора.", "Puzzle puzzleCollector");
        long total = new Date().getTime();
        setDefaultSettings();
        ProcessingRegion.findWorkplaceRegion();
        ProcessingRegion.findWorkplacePuzzleRegion();
        ProcessingRegion.discardWorkplacePuzzleRegion();
        splitSampleImageIntoElements();
        collectPuzzle();
        System.out.println(puzzlePopup("\nОбщее время сбора пазла: " + getRuntime(total)));
    }

}
