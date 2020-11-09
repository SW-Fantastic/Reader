package org.swdc.reader.services.index;

public class IndexTreeNode {

    private String displayName;

    private boolean keyword = false;

    private boolean indexer = false;

    private Class indexerClass;

    private String keywordName;

    public IndexTreeNode(AbstractIndexer indexer) {
        this.indexer = true;
        this.indexerClass = indexer.getClass();
        this.displayName = indexer.name();
    }

    public IndexTreeNode(String keyword) {
        this.displayName = keyword;
        this.keywordName = keyword;
    }

    public boolean isIndexer() {
        return indexer;
    }

    public boolean isKeyword() {
        return keyword;
    }

    public void setIndexer(boolean indexer) {
        this.indexer = indexer;
    }

    public void setIndexerClass(Class indexerClass) {
        this.indexerClass = indexerClass;
    }

    public void setKeyword(boolean keyword) {
        this.keyword = keyword;
    }

    public void setKeywordName(String keywordName) {
        this.keywordName = keywordName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Class getIndexerClass() {
        return indexerClass;
    }

    public String getKeywordName() {
        return keywordName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
