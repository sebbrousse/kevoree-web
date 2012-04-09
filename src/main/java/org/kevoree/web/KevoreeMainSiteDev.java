package org.kevoree.web;


import org.kevoree.annotation.ComponentType;
import org.kevoree.library.javase.webserver.FileServiceHelper;
import org.kevoree.library.javase.webserver.KevoreeHttpRequest;
import org.kevoree.library.javase.webserver.KevoreeHttpResponse;
import org.kevoree.library.javase.webserver.ParentAbstractPage;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: duke
 * Date: 27/03/12
 * Time: 11:12
 */

@ComponentType
public class KevoreeMainSiteDev extends ParentAbstractPage {

    protected String basePage = "overview.html";

    private HashMap<String, byte[]> contentRawCache = new HashMap<String, byte[]>();
    private HashMap<String, String> contentTypeCache = new HashMap<String, String>();

    private PageRenderer krenderer = new PageRenderer();

    @Override
    public void startPage() {
        super.startPage();
        contentRawCache.clear();
        contentTypeCache.clear();
    }

    @Override
    public void updatePage() {
        super.updatePage();
        contentRawCache.clear();
        contentTypeCache.clear();
    }

    @Override
    public void stopPage() {
        super.stopPage();
        contentRawCache.clear();
        contentTypeCache.clear();
    }

    @Override
    public KevoreeHttpResponse process(KevoreeHttpRequest request, KevoreeHttpResponse response) {

        if (contentTypeCache.containsKey(request.getUrl())) {
            response.setRawContent(contentRawCache.get(request.getUrl()));
            response.getHeaders().put("Content-Type", contentTypeCache.get(request.getUrl()));
            return response;
        }

        if (FileServiceHelper.checkStaticFile(basePage, this, request, response)) {
            if (request.getUrl().equals("/") || request.getUrl().endsWith(".html") || request.getUrl().endsWith(".css")) {
                String pattern = getDictionary().get("urlpattern").toString();
                if (pattern.endsWith("**")) {
                    pattern = pattern.replace("**", "");
                }
                if (!pattern.endsWith("/")) {
                    pattern = pattern + "/";
                }
                response.setContent(response.getContent().replace("{urlpattern}", pattern));
            }
            cacheResponse(request, response);
            return response;
        }
        if (krenderer.checkForTemplateRequest(basePage, this, request, response)) {
            cacheResponse(request, response);
            return response;
        }
        response.setContent("Bad request");
        return response;
    }

    public void cacheResponse(KevoreeHttpRequest request, KevoreeHttpResponse response) {
        if (response.getRawContent() != null) {
            contentRawCache.put(request.getUrl(), response.getRawContent());
        } else {
            contentRawCache.put(request.getUrl(), response.getContent().getBytes());
        }
        contentTypeCache.put(request.getUrl(), response.getHeaders().get("Content-Type"));
    }

}
