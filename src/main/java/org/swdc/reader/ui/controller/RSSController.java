package org.swdc.reader.ui.controller;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.swdc.fx.FXController;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.Listener;
import org.swdc.fx.resource.icons.FontawsomeService;
import org.swdc.reader.core.ext.AbstractResolver;
import org.swdc.reader.core.locators.TextLocator;
import org.swdc.reader.entity.RSSSource;
import org.swdc.reader.services.CommonComponents;
import org.swdc.reader.services.RSSService;
import org.swdc.reader.ui.events.RSSRefreshEvent;
import org.swdc.reader.ui.view.RSSView;
import org.swdc.reader.ui.view.cells.RSSCellView;
import org.swdc.reader.ui.view.cells.RSSListCell;
import org.swdc.reader.ui.view.dialogs.RSSAddDialog;
import org.swdc.reader.ui.view.dialogs.RSSEditDialog;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class RSSController extends FXController {

    @Aware
    private RSSAddDialog addDialog = null;

    @Aware
    private RSSEditDialog editDialog = null;

    @Aware
    private RSSService service = null;

    @FXML
    private ComboBox<RSSSource> sources;

    private ObservableList<RSSSource> rssList = FXCollections.observableArrayList();

    @FXML
    private ListView<RSSData> feedList;

    private ObservableList<RSSData> feeds = FXCollections.observableArrayList();

    @FXML
    private WebView contentView;

    @FXML
    private HBox tools;

    @Aware
    private CommonComponents components = null;

    private SimpleBooleanProperty disable = new SimpleBooleanProperty(false);

    private String loadingPage = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            InputStream loading = this.getClass().getModule().getResourceAsStream("views/RSSLoading.html");
            loadingPage = new String(loading.readAllBytes(), StandardCharsets.UTF_8);

            sources.setItems(rssList);
            sources.getSelectionModel().selectedItemProperty().addListener(this::onSourceChange);
            feedList.setItems(feeds);
            feedList.setCellFactory((lv) -> new RSSListCell(findView(RSSCellView.class)));

            sources.disableProperty().bind(disable);
            feedList.disableProperty().bind(disable);
            contentView.disableProperty().bind(disable);
            tools.disableProperty().bind(disable);

            feedList.setOnMouseClicked(e -> {
                RSSData data = feedList.getSelectionModel().getSelectedItem();
                if (data == null) {
                    return;
                }
                disable.setValue(true);
                contentView.getEngine().loadContent(loadingPage);
                Runnable task = () -> {
                    try {
                        Document doc = Jsoup.connect(data.getUrl()).get();
                        Element result = getContent(doc.body());
                        this.resolveImages(result, doc.baseUri());
                        StringBuilder sb = new StringBuilder("<html><head><style>");
                        List<AbstractResolver> resolvers = getScoped(AbstractResolver.class);
                        for (AbstractResolver resolver : resolvers) {
                            if (resolver.support(TextLocator.class)) {
                                resolver.renderStyle(sb);
                            }
                        }
                        sb.append("</style></head><body><div>")
                                .append(result.html())
                                .append("</div></body></html>");
                        result = Jsoup.parse(sb.toString());
                        for (AbstractResolver resolver : resolvers) {
                            if (resolver.support(TextLocator.class)) {
                                resolver.renderContent(result);
                            }
                        }
                        result.getElementsByTag("a").remove();
                        String contentText = result.html();
                        Platform.runLater(() -> {
                            disable.setValue(false);
                            contentView.getEngine().loadContent(contentText);
                        });
                    } catch (Exception ex) {
                        logger.error("fail to load rss content", ex);
                    }
                };
                components.submitTask(task);
            });


        } catch (Exception e) {
            logger.error("unknown error",e);
        }
    }

    private void onSourceChange(Observable observable, RSSSource old, RSSSource select) {
        if (select == null) {
            return;
        }
        this.onRefresh();
    }

    @Override
    public void initialize() {
        rssList.clear();
        rssList.addAll(service.getAllRss());
    }

    @Listener(RSSRefreshEvent.class)
    public void onRssRefresh(RSSRefreshEvent event) {
        rssList.clear();
        rssList.addAll(service.getAllRss());
    }

    @FXML
    public void onAddRss() {
        addDialog.reset();
        addDialog.getStage().show();
    }

    @FXML
    public void onRssEdit() {
        RSSSource source = sources.getSelectionModel().getSelectedItem();
        if (source == null) {
            return;
        }
        editDialog.setSource(source);
        editDialog.show();
    }

    @FXML
    public void onRefresh() {
        RSSSource select = sources.getSelectionModel().getSelectedItem();
        if (select == null) {
            return;
        }
        disable.setValue(true);
        Runnable task = () -> {
            try {
                List<RSSData> feeds = new ArrayList<>();
                XmlReader reader = new XmlReader(new URL(select.getUrl()));
                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(reader);
                SimpleDateFormat format = new SimpleDateFormat("MM-dd");
                List<SyndEntry> entries = feed.getEntries();
                for (SyndEntry entry: entries) {
                    RSSData data = new RSSData();
                    data.setTitle(entry.getTitle());
                    data.setDesc(Jsoup.parse(entry.getDescription().getValue()).text());
                    data.setDate(format.format(entry.getPublishedDate()));
                    data.setUrl(entry.getLink());
                    feeds.add(data);
                }
                Platform.runLater(() -> {
                    this.feeds.clear();
                    this.feeds.addAll(feeds);
                    disable.setValue(false);
                });
            } catch (Exception ex) {
                logger.error("fail to load feeds.");
            }
        };
        components.submitTask(task);
    }

    @FXML
    public void onDeleteSource() {
        RSSSource select = sources.getSelectionModel().getSelectedItem();
        if (select == null) {
            return;
        }
        RSSView view = getView();
        view.showAlertDialog(i18n("lang@dialog-warn"),
                i18n("lang@dialog-delete") + select.getName() + "?"
                , Alert.AlertType.CONFIRMATION)
                .ifPresent(buttonType -> {
                    if (buttonType == ButtonType.OK) {
                        service.deleteRssSource(select);
                        sources.getSelectionModel().clearSelection();
                        feeds.clear();
                    }
                });
    }
    /**
     * 获取正文
     * @param element 一般是Body元素，其他的也可以
     * @return 通常是含有大段文本的Div
     */
    private Element getContent(Element element) {

        List<String> tags = Arrays.asList("p","br");
        Element next = null;
        int maxTag = 0;
        String tagName = "";

        for (String tag: tags) {
            Element tagElem = findTag(element,tag);
            if (tagElem != null && tagElem.getAllElements().size() > maxTag) {
                maxTag = tagElem.getAllElements().size();
                tagName = tag;
                next = tagElem;
            }
        }

        if (next == null) {
            return null;
        }

        Element iter = next;
        while (iter != null) {
            iter = findTag(next,tagName);
            if (iter != null) {
                next = iter;
            }
        }
        return next;
    }

    /**
     * 内联图片
     * @param element HTML的元素
     * @param base BaseURI，域名或者说基地址
     * @return 处理后的element
     * @throws Exception 出现了问题，但是应该出现不了，除非Img标签的Src不正确。
     */
    private Element resolveImages(Element element, String base) throws Exception {
        URI uri = new URI(base);
        Elements images = element.getElementsByTag("img");
        // 不含图片，无需内联
        if (images == null || images.size() == 0) {
            return element;
        }
        // 使用http协议开启Connection
        for (Element imageItem: images) {
            String relative = imageItem.attr("src");
            if (imageItem.attr("data-original") != null && !imageItem.attr("data-original").isBlank()) {
                relative = imageItem.attr("data-original");
            }
            if (imageItem.attr("data-src") != null && !imageItem.attr("data-src").isBlank()) {
                relative = imageItem.attr("data-src");
            }
            if (relative.startsWith("data:")) {
                continue;
            }
            URL imageTarget;
            if (!relative.startsWith("http")) {
                imageTarget = uri.resolve(relative).toURL();
            } else {
                imageTarget = new URL(relative);
            }
            // 开启Connection
            HttpURLConnection connection = (HttpURLConnection) imageTarget.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);
            // 发起Http请求
            connection.connect();

            // 请求失败了的情况
            if (connection.getResponseCode() != 200) {
                imageItem.remove();
                continue;
            }
            // 开始读取数据
            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            inputStream.transferTo(bout);
            inputStream.close();
            connection.disconnect();
            // 内联图片到src中
            String imageData = Base64.getEncoder().encodeToString(bout.toByteArray());
            imageItem.attr("src","data:image/png;base64," + imageData);
        }
        return element;
    }

    /**
     * 寻找Element中，含有最多的指定Tag的那个子元素
     * @param element 在哪里找
     * @param tag 找那个Tag
     * @return 找到的含有指定类型Tag组多的子元素
     */
    private Element findTag(Element element, String tag) {
        List<Node> nodes = element.childNodes();
        int maxP = 0;
        Element max = null;
        for (Node node: nodes) {
            if (!(node instanceof Element)) {
                continue;
            }
            Element elementNode = (Element)node;
            Elements p = elementNode.getElementsByTag(tag);
            if (p == null || p.size() <= 1) {
                continue;
            }
            if (p.size() > maxP) {
                maxP = p.size();
                max = elementNode;
            }
        }
        return max;
    }

}
