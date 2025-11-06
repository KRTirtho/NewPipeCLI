package org.example;

import org.example.Downloader;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.ServiceList;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.stream.StreamInfo;
import org.schabi.newpipe.extractor.stream.StreamType;
import org.schabi.newpipe.extractor.stream.VideoStream;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class App{
    public static void main(String[] args) {
        if (args.length == 0 || args[0] == null || args[0].trim().isEmpty()) {
            System.err.println("Usage: NewPipeCLI <videoUrl>");
            System.err.println("Please provide a video URL as an argument.");
            return;
        }
        String videoUrl = args[0];
        try {
            Downloader downloader = new Downloader();
            NewPipe.init(downloader);
            StreamInfo info = StreamInfo.getInfo(videoUrl);

            System.out.println("Title: " + info.getName());
            System.out.println("Uploader: " + info.getUploaderName());
            System.out.println("Description: " + info.getDescription());

            System.out.println("\nAudio Streams:");
            info.getAudioStreams().forEach(stream -> {
                System.out.println("- " + Objects.requireNonNull(stream.getFormat()).getName() + " (" + stream.getAverageBitrate() + "kbps): " + stream.getUrl());
            });

            System.out.println("\nVideo Streams:");
            List<VideoStream> videoStreams = info.getVideoStreams();
            videoStreams.addAll(info.getVideoOnlyStreams());

            videoStreams.forEach(stream -> {
                System.out.println("- " + stream.getResolution() + " (" + Objects.requireNonNull(stream.getFormat()).getName() + "): " + stream.getUrl());
            });
        } catch (IOException | ExtractionException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
