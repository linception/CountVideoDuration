import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.MultimediaInfo;

public class Progress {

	private static Encoder encoder = new Encoder();
	public static Set<String> videoSuffixes;

	public static void main(String[] args) throws EncoderException, IOException {
		File file = null;
		if (args.length > 0) {
			file = new File(args[0]);
		}

		try (Scanner scanner = new Scanner(System.in);) {
			while (true) {
				if (file == null) {
					System.out.println("Please enter file or dictionary path:");
				} else if (!file.exists()) {
					System.out.println("The path \"" + file.getCanonicalPath()
							+ "\" is not exists, Please enter file or dictionary path again:");
				} else {
					break;
				}
				file = new File(scanner.next());
			}
		}

		videoSuffixes = new HashSet<>(Arrays.asList(encoder.getSupportedDecodingFormats()));
		int duration = getDurations(file);
		System.out.println(formatDuration(duration));
	}

	private static int getDurations(File file) {
		int time = 0;
		if (file == null) {
			return 0;
		}
		if (file.isFile()) {
			time += getDuration(file);
		} else if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				time += getDurations(f);
			}
		}
		return time;
	}

	private static long getDuration(File file) {
		if (file == null) {
			return 0;
		}
		boolean isVideo = false;
		String fileName = file.getName();
		int lastIndexOf = fileName.lastIndexOf(".");
		if (lastIndexOf != -1) {
			if (videoSuffixes.contains(fileName.substring(lastIndexOf + 1, fileName.length()))) {
				isVideo = true;
			}
		}
		if (!isVideo) {
			return 0;
		}
		try {
			MultimediaInfo info = encoder.getInfo(file);
			System.out.println(formatDuration(info.getDuration()) + "\t" + file.getAbsolutePath());
			return info.getDuration();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static String formatDuration(long duration) {
		duration /= 1000;
		long s = duration % 60;
		long m = (duration / 60) % 60;
		long h = duration / 3600;
		return h + ":" + (m > 9 ? "" : 0) + m + ":" + (s > 9 ? "" : "0") + s;
	}
}
