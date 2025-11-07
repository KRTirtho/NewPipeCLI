package dev.krtirtho.libnewpipe;

import org.schabi.newpipe.extractor.Image;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.channel.ChannelInfoItem;
import org.schabi.newpipe.extractor.localization.DateWrapper;
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItem;
import org.schabi.newpipe.extractor.services.youtube.ItagItem;
import org.schabi.newpipe.extractor.stream.AudioStream;
import org.schabi.newpipe.extractor.stream.Description;
import org.schabi.newpipe.extractor.stream.Stream;
import org.schabi.newpipe.extractor.stream.StreamInfo;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.VideoStream;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class to convert NewPipe Extractor objects into standard Java Maps.
 * This is the Java equivalent of the Kotlin utility class, using static methods
 * to mimic the companion object functionality.
 */
public final class Convert {

    // Prevent instantiation for a utility class
    private Convert() {
    }

    /**
     * Converts a list of Image objects into a list of generic Maps.
     *
     * @param thumbnails The list of NewPipe Image objects.
     * @return A List of Maps containing image URL, width, and height.
     */
    public static List<Map<String, Object>> thumbnailMap(List<Image> thumbnails) {
        if (thumbnails == null) {
            return new ArrayList<>();
        }
        // Using Java Streams to map the list functionally
        return thumbnails.stream().map(it -> {
            Map<String, Object> map = new HashMap<>();
            map.put("url", it.getUrl());
            map.put("width", it.getWidth());
            map.put("height", it.getHeight());
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * Converts a DateWrapper object into a generic Map.
     *
     * @param date The DateWrapper object.
     * @return A Map containing epoch seconds and approximation status.
     */
    public static Map<String, Object> dateWrapperMap(DateWrapper date) {
        Map<String, Object> map = new HashMap<>();
        // Note: Kotlin's toEpochSecond is used here. We handle potential null OffsetDateTime.
        map.put("offsetDateTime",
                date.offsetDateTime().truncatedTo(ChronoUnit.SECONDS).toEpochSecond()
        );
        map.put("isApproximation", date.isApproximation());
        return map;
    }

    /**
     * Converts a Description object into a generic Map.
     *
     * @param d The Description object.
     * @return A Map containing content and type.
     */
    public static Map<String, Object> descriptionMap(Description d) {
        Map<String, Object> map = new HashMap<>();
        map.put("content", d.getContent());
        map.put("type", d.getType());
        return map;
    }

    /**
     * Converts an ItagItem object into a generic Map.
     *
     * @param v The ItagItem object.
     * @return A Map containing various stream properties.
     */
    public static Map<String, Object> itagItemMap(ItagItem v) {
        Map<String, Object> map = new HashMap<>();
        map.put("mediaFormat", v.getMediaFormat() != null ? v.getMediaFormat().name() : null);
        map.put("id", v.id);
        map.put("itagType", v.itagType != null ? v.itagType.name() : null);
        map.put("avgBitrate", v.getAverageBitrate());
        map.put("sampleRate", v.getSampleRate());
        map.put("audioChannels", v.getAudioChannels());
        map.put("resolutionString", v.getResolutionString());
        map.put("fps", v.getFps());
        map.put("bitrate", v.getBitrate());
        map.put("width", v.getWidth());
        map.put("height", v.getHeight());
        map.put("initStart", v.getInitStart());
        map.put("initEnd", v.getInitEnd());
        map.put("indexStart", v.getIndexStart());
        map.put("indexEnd", v.getIndexEnd());
        map.put("quality", v.getQuality());
        map.put("codec", v.getCodec());
        map.put("targetDurationSec", v.getTargetDurationSec());
        map.put("approxDurationMs", v.getApproxDurationMs());
        map.put("contentLength", v.getContentLength());
        map.put("audioTrackId", v.getAudioTrackId());
        map.put("audioTrackName", v.getAudioTrackName());
        map.put("audioTrackType", v.getAudioTrackType() != null ? v.getAudioTrackType().name() : null);
        if (v.getAudioLocale() != null) {
            map.put("audioLocale", v.getAudioLocale().getDisplayName());
        }
        return map;
    }

    /**
     * Converts a Stream object (base class) into a generic Map.
     *
     * @param v The Stream object.
     * @return A Map containing base stream properties.
     */
    public static Map<String, Object> streamMap(Stream v) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", v.getId());
        // Accessing nullable fields requires explicit check
        map.put("mediaFormat", v.getFormat() != null ? v.getFormat().name() : null);
        map.put("content", v.getContent());
        map.put("isUrl", v.isUrl());
        map.put("deliveryMethod", v.getDeliveryMethod().name());
        map.put("manifestUrl", v.getManifestUrl());
        return map;
    }

    /**
     * Converts a VideoStream object into a generic Map, merging with base Stream map.
     *
     * @param v The VideoStream object.
     * @return A Map containing video stream properties.
     */
    public static Map<String, Object> videoStreamMap(VideoStream v) {
        // Kotlin's map + map is done via putAll in Java
        Map<String, Object> map = streamMap(v);

        map.put("resolution", v.getResolution());
        map.put("isVideoOnly", v.isVideoOnly());
        map.put("itag", v.getItag());
        map.put("bitrate", v.getBitrate());
        map.put("initStart", v.getInitStart());
        map.put("initEnd", v.getInitEnd());
        map.put("indexStart", v.getIndexStart());
        map.put("indexEnd", v.getIndexEnd());
        map.put("width", v.getWidth());
        map.put("height", v.getHeight());
        map.put("fps", v.getFps());
        map.put("quality", v.getQuality());
        map.put("codec", v.getCodec());

        // Explicit null check and conversion for nested object
        map.put("itagItem", v.getItagItem() != null
                ? itagItemMap(v.getItagItem())
                : null);

        return map;
    }

    /**
     * Converts an AudioStream object into a generic Map, merging with base Stream map.
     *
     * @param v The AudioStream object.
     * @return A Map containing audio stream properties.
     */
    public static Map<String, Object> audioStreamMap(AudioStream v) {
        // Kotlin's map + map is done via putAll in Java
        Map<String, Object> map = streamMap(v);

        map.put("itag", v.getItag());
        map.put("bitrate", v.getBitrate());
        map.put("initStart", v.getInitStart());
        map.put("initEnd", v.getInitEnd());
        map.put("indexStart", v.getIndexStart());
        map.put("indexEnd", v.getIndexEnd());
        map.put("quality", v.getQuality());
        map.put("codec", v.getCodec());
        map.put("audioTrackId", v.getAudioTrackId());
        map.put("audioTrackName", v.getAudioTrackName());
        if (v.getAudioLocale() != null) {
            map.put("audioLocale", v.getAudioLocale().getDisplayName());
        }
        map.put("audioTrackType", v.getAudioTrackType() != null ? v.getAudioTrackType().name() : null);

        // Explicit null check and conversion for nested object
        map.put("itagItem", v.getItagItem() != null
                ? itagItemMap(v.getItagItem())
                : null);

        return map;
    }

    /**
     * Converts a StreamInfo object into a generic Map.
     *
     * @param streamInfo The StreamInfo object.
     * @return A Map containing stream information and nested streams.
     */
    public static Map<String, Object> streamInfoMap(StreamInfo streamInfo) {
        Map<String, Object> map = new HashMap<>();

        map.put("id", streamInfo.getId());
        map.put("url", streamInfo.getUrl());
        map.put("originalUrl", streamInfo.getOriginalUrl());
        map.put("name", streamInfo.getName());
        map.put("streamType", streamInfo.getStreamType() != null ? streamInfo.getStreamType().name() : null);
        map.put("thumbnails", thumbnailMap(streamInfo.getThumbnails()));
        map.put("textualUploadDate", streamInfo.getTextualUploadDate());

        // Null check for uploadDate
        map.put("uploadDate", streamInfo.getUploadDate() != null ? dateWrapperMap(streamInfo.getUploadDate()) : null);

        map.put("duration", streamInfo.getDuration());
        map.put("ageLimit", streamInfo.getAgeLimit());

        // Null check for description
        map.put("description", streamInfo.getDescription() != null ? descriptionMap(streamInfo.getDescription()) : null);

        map.put("viewCount", streamInfo.getViewCount());
        map.put("likeCount", streamInfo.getLikeCount());
        map.put("dislikeCount", streamInfo.getDislikeCount());
        map.put("uploaderName", streamInfo.getUploaderName());
        map.put("uploaderUrl", streamInfo.getUploaderUrl());
        map.put("uploaderAvatars", thumbnailMap(streamInfo.getUploaderAvatars()));
        map.put("uploaderVerified", streamInfo.isUploaderVerified());
        map.put("uploaderSubscriberCount", streamInfo.getUploaderSubscriberCount());
        map.put("subChannelName", streamInfo.getSubChannelName());
        map.put("subChannelUrl", streamInfo.getSubChannelUrl());
        map.put("subChannelAvatars", streamInfo.getSubChannelAvatars());

        // Stream mapping using Java Streams
        map.put("videoStreams", streamInfo.getVideoStreams().stream().map(Convert::videoStreamMap).collect(Collectors.toList()));
        map.put("audioStreams", streamInfo.getAudioStreams().stream().map(Convert::audioStreamMap).collect(Collectors.toList()));
        map.put("videoOnlyStreams", streamInfo.getVideoOnlyStreams().stream().map(Convert::videoStreamMap).collect(Collectors.toList()));

        map.put("dashMpdUrl", streamInfo.getDashMpdUrl());
        map.put("hlsUrl", streamInfo.getHlsUrl());
        map.put("relatedItems", streamInfo.getRelatedItems().stream().map(Convert::infoItemInternalMap).toList());
        map.put("startPosition", streamInfo.getStartPosition());
//         map.put("subtitles", streamInfo.getSubtitles().stream().map(Convert::).toList());); // Commented out as in Kotlin source
        map.put("host", streamInfo.getHost());
        // map.put("privacy", streamInfo.getPrivacy()); // Commented out as in Kotlin source
        map.put("category", streamInfo.getCategory());
        map.put("licence", streamInfo.getLicence());
        map.put("supportInfo", streamInfo.getSupportInfo());
         map.put("language", streamInfo.getLanguageInfo().getDisplayName());
        map.put("tags", streamInfo.getTags());
//         map.put("streamSegments", streamInfo.getStreamSegments().stream().map(Convert::strea));
        // map.put("metaInfo", streamInfo.getMetaInfo()); // Commented out as in Kotlin source
        map.put("shortFormContent", streamInfo.isShortFormContent());
        // map.put("previewFrames", streamInfo.getPreviewFrames()); // Commented out as in Kotlin source

        return map;
    }

    /**
     * Converts the base InfoItem properties into a generic Map.
     * This is a private helper method, similar to the private Kotlin function.
     *
     * @param item The InfoItem object.
     * @return A Map containing base info item properties.
     */
    private static Map<String, Object> infoItemInternalMap(InfoItem item) {
        Map<String, Object> map = new HashMap<>();

        map.put("infoType", item.getInfoType() != null ? item.getInfoType().name() : null);
        // map.put("serviceId", item.getServiceId()); // Commented out as in Kotlin source
        map.put("url", item.getUrl());
        map.put("name", item.getName());
        map.put("thumbnails", thumbnailMap(item.getThumbnails()));

        return map;
    }

    /**
     * Converts a PlaylistInfoItem object into a generic Map.
     *
     * @param info The PlaylistInfoItem object.
     * @return A Map containing playlist info item properties.
     */
    public static Map<String, Object> playlistInfoItemMap(PlaylistInfoItem info) {
        Map<String, Object> map = infoItemInternalMap(info); // Start with base properties

        map.put("uploaderName", info.getUploaderName());
        map.put("uploaderUrl", info.getUploaderUrl());
        map.put("uploaderVerified", info.isUploaderVerified());
        map.put("streamCount", info.getStreamCount());

        // Null check for description
        map.put("description", info.getDescription() != null ? descriptionMap(info.getDescription()) : null);

        map.put("playlistType", info.getPlaylistType() != null ? info.getPlaylistType().name() : null);

        return map;
    }

    /**
     * Converts a ChannelInfoItem object into a generic Map.
     *
     * @param info The ChannelInfoItem object.
     * @return A Map containing channel info item properties.
     */
    public static Map<String, Object> channelInfoItemMap(ChannelInfoItem info) {
        Map<String, Object> map = infoItemInternalMap(info); // Start with base properties

        map.put("description", info.getDescription());
        map.put("subscriberCount", info.getSubscriberCount());
        map.put("streamCount", info.getStreamCount());
        map.put("verified", info.isVerified());

        return map;
    }

    /**
     * Converts a StreamInfoItem object into a generic Map.
     *
     * @param info The StreamInfoItem object.
     * @return A Map containing stream info item properties.
     */
    public static Map<String, Object> streamInfoItemMap(StreamInfoItem info) {
        Map<String, Object> map = infoItemInternalMap(info); // Start with base properties

        map.put("streamType", info.getStreamType() != null ? info.getStreamType().name() : null);
        map.put("uploaderName", info.getUploaderName());
        map.put("shortDescription", info.getShortDescription());
        map.put("textualUploadDate", info.getTextualUploadDate());

        // Explicit cast and null check for uploadDate
        map.put("uploadDate", info.getUploadDate() != null
                ? dateWrapperMap(info.getUploadDate())
                : null);

        map.put("viewCount", info.getViewCount());
        map.put("duration", info.getDuration());
        map.put("uploaderUrl", info.getUploaderUrl());
        map.put("uploaderAvatars", thumbnailMap(info.getUploaderAvatars()));
        map.put("uploaderVerified", info.isUploaderVerified());
        map.put("shortFormContent", info.isShortFormContent());

        return map;
    }

    /**
     * Converts an InfoItem object to the correct specific Map type using instanceof checks.
     * This is the Java equivalent of Kotlin's 'when' expression with smart casting.
     *
     * @param info The generic InfoItem object.
     * @return A Map containing the specific info item properties.
     */
    public static Map<String, Object> infoItemMap(InfoItem info) {
        if (info instanceof PlaylistInfoItem) {
            return playlistInfoItemMap((PlaylistInfoItem) info);
        } else if (info instanceof StreamInfoItem) {
            return streamInfoItemMap((StreamInfoItem) info);
        } else if (info instanceof ChannelInfoItem) {
            return channelInfoItemMap((ChannelInfoItem) info);
        } else {
            return infoItemInternalMap(info);
        }
    }
}