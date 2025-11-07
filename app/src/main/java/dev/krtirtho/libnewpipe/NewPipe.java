package dev.krtirtho.libnewpipe;

import org.jetbrains.annotations.Nullable;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.search.SearchInfo;
import org.schabi.newpipe.extractor.services.youtube.YoutubeService;
import org.schabi.newpipe.extractor.stream.StreamInfo;

import com.grack.nanojson.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NewPipe {

    public static void main(String[] args) {
        Map<String, List<String>> parsed = parseArgs(args);

        String streams = getSingle(parsed, "--streams");
        String search = getSingle(parsed, "--search");
        List<String> contentFilters = parsed.getOrDefault("--content-filters", List.of());
        String sortFilter = getSingle(parsed, "--sort-filter");

        if (streams != null) {
            getVideoInfo(streams);
        } else if (search != null) {
            search(search, contentFilters, sortFilter);
        } else {
            System.out.println("Usage:");
            System.out.println("  --streams <url_or_id>");
            System.out.println("  --search <query> [--content-filters f1 f2 ...] [--sort-filter sort]");
        }
    }

    private static Map<String, List<String>> parseArgs(String[] args) {
        Map<String, List<String>> map = new HashMap<>();
        String currentKey = null;

        for (String arg : args) {
            if (arg.startsWith("--")) {
                if (arg.contains("=")) {
                    String[] parts = arg.split("=", 2);
                    String key = parts[0];
                    String value = parts[1];
                    map.put(key, new ArrayList<>(splitValues(value)));
                    currentKey = null;
                } else {
                    currentKey = arg;
                    map.putIfAbsent(currentKey, new ArrayList<>());
                }
            } else if (currentKey != null) {
                map.get(currentKey).add(arg);
            }
        }
        return map;
    }

    // Split by comma for inline args like --filters=a,b,c
    private static List<String> splitValues(String value) {
        String[] parts = value.split(",");
        List<String> list = new ArrayList<>();
        for (String p : parts) {
            if (!p.isBlank()) list.add(p.trim());
        }
        return list;
    }

    private static String getSingle(Map<String, List<String>> map, String key) {
        List<String> vals = map.get(key);
        return (vals != null && !vals.isEmpty()) ? vals.get(0) : null;
    }
    public static YoutubeService getService() {
        org.schabi.newpipe.extractor.NewPipe.init(new Downloader());
        try {
            return (YoutubeService) org.schabi.newpipe.extractor.NewPipe.getService(0);
        } catch (ExtractionException e) {
            throw new RuntimeException(e);
        }
    }

    public static void getVideoInfo(String videoId) {
        try {
            YoutubeService service = getService();
            var info = StreamInfo.getInfo(service.getStreamExtractor(service.getStreamLHFactory().fromId(videoId)));

            Map<String, Object> resultMap = Convert.streamInfoMap(info);
            System.out.println(JsonWriter.string(resultMap));
        } catch (ExtractionException | IOException e) {
            System.err.println(JsonWriter.string(Map.of("error", e.getMessage())));
        }
    }

    public static void search(String query, @Nullable List<String> contentFilters, @Nullable String sortFilter) {
        try {
            YoutubeService service = getService();
            var searchResults = SearchInfo.getInfo(service, service.getSearchQHFactory().fromQuery(query, contentFilters, sortFilter));

            List<Map<String, Object>> resultList = searchResults.getRelatedItems().stream().map(Convert::infoItemMap).toList();

            System.out.println(JsonWriter.string(resultList));
        } catch (ExtractionException | IOException e) {
            System.err.println(JsonWriter.string(Map.of("error", e.getMessage())));
        }
    }
}
