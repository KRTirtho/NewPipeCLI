package org.example;

import org.schabi.newpipe.extractor.downloader.Request;
import org.schabi.newpipe.extractor.downloader.Response;
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.Request.Builder;
import okhttp3.ResponseBody;

/**
 * A simple implementation of the NewPipeExtractor Downloader interface using OkHttp.
 */
public class Downloader extends org.schabi.newpipe.extractor.downloader.Downloader {

    private final OkHttpClient client;

    public Downloader() {
        this(new OkHttpClient.Builder().followRedirects(true).build());
    }

    public Downloader(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public Response execute(Request request) throws IOException, ReCaptchaException {
        Builder builder = new Builder()
                .url(request.url())
                .method(request.httpMethod(), createRequestBody(request));

        // Add headers
        for (Map.Entry<String, List<String>> entry : request.headers().entrySet()) {
            for (String value : entry.getValue()) {
                builder.addHeader(entry.getKey(), value);
            }
        }

        okhttp3.Request okRequest = builder.build();

        try (okhttp3.Response okResponse = client.newCall(okRequest).execute()) {
            int code = okResponse.code();
            String body = null;

            ResponseBody responseBody = okResponse.body();
            if (responseBody != null) {
                body = responseBody.string();
            }

            // Handle Recaptcha (NewPipe uses this for YouTube sometimes)
            if (code == 429 && body != null && body.contains("https://www.google.com/recaptcha")) {
                throw new ReCaptchaException("ReCaptcha challenge detected", request.url());
            }

            return new Response(code, okResponse.message(), okResponse.headers().toMultimap(), body, request.url());
        }
    }

    private RequestBody createRequestBody(Request request) {
        if (request.dataToSend() == null || Objects.requireNonNull(request.dataToSend()).length == 0) {
            return null;
        }
        assert request.dataToSend() != null;
        String contentTypeHeader = request.headers().get("Content-Type").get(0);
        return RequestBody.create(
                Objects.requireNonNull(request.dataToSend()),
                MediaType.parse(contentTypeHeader != null ? contentTypeHeader : "application/x-www-form-urlencoded")
        );
    }
}