package base;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.Registry;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import static org.monte.media.AudioFormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

public class My_Screen_Recorder extends ScreenRecorder {

    private String name;

    // ThreadLocal to make it parallel-safe
    private static ThreadLocal<ScreenRecorder> recorder = new ThreadLocal<>();

//-----------------------------------------------------------------------------------------------------------------------//

    public My_Screen_Recorder(GraphicsConfiguration cfg, Rectangle captureArea, Format fileFormat, Format screenFormat,
                              Format mouseFormat, Format audioFormat, File movieFolder, String name)
            throws IOException, AWTException {
        super(cfg, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat, movieFolder);
        this.name = name;
    }

//-----------------------------------------------------------------------------------------------------------------------//

    @Override
    protected File createMovieFile(Format fileFormat) throws IOException {
        if (!movieFolder.exists()) {
            movieFolder.mkdirs();
        } else if (!movieFolder.isDirectory()) {
            throw new IOException("\"" + movieFolder + "\" is not a directory.");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_hh-mm-ss a");
        return new File(movieFolder,
                name + "-" + dateFormat.format(new Date()) + "." + Registry.getInstance().getExtension(fileFormat));
    }

//-----------------------------------------------------------------------------------------------------------------------//
    // This method will start recording
    public static void Start_Recording(String methodName) throws Exception {
        File file = new File("./Recordings/");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle captureSize = new Rectangle(0, 0, screenSize.width, screenSize.height);

        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();

        My_Screen_Recorder screenRecorder = new My_Screen_Recorder(gc, captureSize,
                new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                        CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                        DepthKey, 24, FrameRateKey, Rational.valueOf(15),
                        QualityKey, 1.0f, KeyFrameIntervalKey, 15 * 60),
                new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(30)),
                null, file, methodName);

        recorder.set(screenRecorder); // assign to ThreadLocal
        screenRecorder.start();
    }

//-----------------------------------------------------------------------------------------------------------------------//
    // This method will stop recording
    public static void Stop_Recording() throws Exception {
        ScreenRecorder sr = recorder.get();
        if (sr != null) {
            sr.stop();
            recorder.remove(); // clean up after stopping
        }
    }
}