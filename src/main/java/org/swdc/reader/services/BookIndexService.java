package org.swdc.reader.services;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSLockFactory;
import org.apache.lucene.store.NIOFSDirectory;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.jpa.anno.Transactional;
import org.swdc.fx.services.Service;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookTag;
import org.swdc.reader.repository.BookRepository;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

import java.util.*;


public class BookIndexService extends Service {

    @Aware
    private BookRepository repository = null;

    private IndexReader reader = null;

    private IndexWriter writer = null;

    private FileSystem indexFs = null;

    @Override
    public void initialize() {
        try{
            logger.info("lucene initializing..");
            File fileFs = new File("data/index/indexed.zip");
            indexFs = FileSystems.newFileSystem(fileFs.toPath(),Map.of("create", "true"));
            if(!DirectoryReader.indexExists(getIndexDir())) {
                IndexWriter writer = getLuceneWriter();
                writer.commit();
                writer.flush();
            }
            logger.info("index loaded.");
        } catch (Exception e){
            logger.error("fail to init index folder",e);
        }
    }

    public synchronized void createBookIndex(Book book) {
        try {
            Document existed = getIndexByBookId(book.getId());
            if (existed != null) {
                this.updateBookIndex(book);
                return;
            }
            synchronized (BookIndexService.class) {
                existed = getIndexByBookId(book.getId());
                if (existed != null) {
                    return;
                }
                Set<BookTag> tags = book.getTags();
                String tagText = "";
                if (tags != null && tags.size() > 0) {
                    tagText = tags.stream().map(BookTag::getName)
                            .reduce((nameA, nameB)-> nameA + "," + nameB)
                            .orElse("");
                }

                IndexWriter writer = getLuceneWriter();

                Document doc = new Document();
                Field idField = new StringField("id",book.getId().toString(),Field.Store.YES);
                Field titleField = new TextField("title",book.getTitle(), Field.Store.YES);
                Field authorField = new StringField("author",book.getAuthor(),Field.Store.YES);
                Field publisherField = new StringField("publisher",book.getPublisher(),Field.Store.YES);
                Field tagsField = new StringField("tags",tagText,Field.Store.YES);

                doc.add(idField);
                doc.add(titleField);
                doc.add(authorField);
                doc.add(publisherField);
                doc.add(tagsField);

                writer.addDocument(doc);
                writer.commit();
                writer.flush();
            }
        } catch (Exception e) {
            logger.error("fail to create index",e);
        }
    }

    public synchronized void updateBookIndex(Book book) {
        try {
            IndexWriter writer = getLuceneWriter();
            Document doc = getIndexByBookId(book.getId());
            if (doc == null) {
                createBookIndex(book);
                return;
            }

            Set<BookTag> tags = book.getTags();
            String tagText = "";
            if (tags != null && tags.size() > 0) {
                tagText = tags.stream().map(BookTag::getName)
                        .reduce((nameA, nameB)-> nameA + "," + nameB)
                        .orElse("");
            }


            doc.removeField("title");
            doc.add(new TextField("title",book.getTitle(),Field.Store.YES));

            doc.removeField("author");
            doc.add(new StringField("author",book.getAuthor(),Field.Store.YES));

            doc.removeField("publisher");
            doc.add(new StringField("publisher",book.getPublisher(),Field.Store.YES));

            doc.removeField("tags");
            doc.add(new StringField("tags",tagText,Field.Store.YES));

            writer.updateDocument(new Term("id",book.getId().toString()),doc);

            writer.commit();
        } catch (Exception e){
            logger.error("fail to update index",e);
        }
    }

    public synchronized void cleanAllIndexes() throws IOException {
        IndexWriter writer = getLuceneWriter();
        writer.deleteAll();
        writer.forceMergeDeletes();
        writer.commit();
    }

    @Transactional
    public List<Book> searchByEquals(String field, String keyword) {
        try {
            IndexReader reader = getLuceneReader();
            IndexSearcher searcher = new IndexSearcher(reader);
            Query query = new TermQuery(new Term(field,keyword));
            TopDocs docs = searcher.search(query,Integer.MAX_VALUE);
            if(docs.totalHits.value == 0) {
                return Collections.emptyList();
            }

            List<Book> result = new ArrayList<>();
            for (ScoreDoc doc: docs.scoreDocs) {
                Document document = searcher.doc(doc.doc);
                IndexableField target = document.getField(field);
                if (keyword.equalsIgnoreCase(target.stringValue())) {
                    Long id = Long.valueOf(document.getField("id").stringValue());
                    Book book = repository.getOne(id);
                    if (book != null) {
                        result.add(book);
                    }
                }
            }
            return result;
        } catch (Exception e) {
            logger.error("fail to fetch document",e);
            return Collections.emptyList();
        }
    }

    public List<Book> searchByWildcard(String field, String keyword) {
        try {
            IndexReader reader = getLuceneReader();
            IndexSearcher searcher = new IndexSearcher(reader);
            Query query = new WildcardQuery(new Term(field,"*" + keyword.toLowerCase() + "*"));
            TopDocs docs = searcher.search(query,Integer.MAX_VALUE);
            if(docs.totalHits.value == 0) {
                return Collections.emptyList();
            }

            List<Book> result = new ArrayList<>();
            for (ScoreDoc doc: docs.scoreDocs) {
                Document document = searcher.doc(doc.doc);
                IndexableField target = document.getField(field);
                if (target.stringValue().contains(keyword.toLowerCase())) {
                    Long id = Long.valueOf(document.getField("id").stringValue());
                    Book book = repository.getOne(id);
                    result.add(book);
                }
            }
            return result;
        } catch (Exception e) {
            logger.error("fail to fetch document",e);
            return Collections.emptyList();
        }
    }

    public synchronized void removeBookIndex(Book book) {
        try {
            IndexReader reader = getLuceneReader();
            IndexSearcher searcher = new IndexSearcher(reader);
            Query query = new TermQuery(new Term("id",book.getId().toString()));
            TopDocs docs = searcher.search(query,1);
            if(docs.totalHits.value == 0) {
                return;
            }
            IndexWriter writer = getLuceneWriter();
            writer.deleteDocuments(query);
            writer.commit();
            writer.flush();
        } catch (Exception e) {
            logger.error("fail to remove index",e);
        }
    }

    private IndexWriter getLuceneWriter() throws IOException {
        if (writer != null) {
            return writer;
        } else {
            Directory directory = getIndexDir();
            Analyzer analyzer = new IKAnalyzer(true);
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            config.setCommitOnClose(true);
            this.writer = new IndexWriter(directory,config);
            return writer;
        }
    }

    private Directory getIndexDir() throws IOException {
        //return FSDirectory.open(Paths.get("data/index"));
        return new NIOFSDirectory(indexFs.getPath("lucene_index"), FSLockFactory.getDefault());
    }

    private IndexReader getLuceneReader() throws IOException {
        if (reader != null) {
            IndexReader reader = DirectoryReader.openIfChanged((DirectoryReader)this.reader);
            if (reader != null) {
                this.reader = reader;
            }
            return this.reader;
        } else {
            Directory directory = getIndexDir();
            reader = DirectoryReader.open(directory);
            return reader;
        }
    }

    private Document getIndexByBookId(Long id) throws IOException {
        IndexReader reader = getLuceneReader();
        IndexSearcher searcher = new IndexSearcher(reader);
        Query query = new TermQuery(new Term("id",id.toString()));
        TopDocs docs = searcher.search(query,1);
        if(docs.totalHits.value == 0) {
            return null;
        }
        return searcher.doc(docs.scoreDocs[0].doc);
    }

    @Override
    public void destroy() {
        try {
            logger.info("saving index..");
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (indexFs != null) {
                indexFs.close();
            }
            logger.info("lucene has shutdown.");
        } catch (Exception e) {
            logger.error("fail to close index", e);
        }
    }
}
