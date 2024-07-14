package com.lahssini.ragtest.config;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Component
public class DataLoader {
    @Value("classpath:/pdfs/cv.pdf")
    private Resource pdfFile;
    @Value("enset-vs1.json")
    private String storeFile;
    private JdbcClient jdbcClient;
    private VectorStore vectorStore;
    private static Logger log= LoggerFactory.getLogger(DataLoader.class);

    public DataLoader(JdbcClient jdbcClient, VectorStore vectorStore) {
        this.jdbcClient = jdbcClient;
        this.vectorStore = vectorStore;
    }

    //@Bean
    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel){
      SimpleVectorStore  simpleVectorStore=new SimpleVectorStore(embeddingModel);
        String path=Path.of("src","main","resources","store").toFile().getAbsolutePath()+"/"+storeFile;
        File fileStore=new File(path);
        if(fileStore.exists()){
            log.info("Vector store Exist =>"+path);
            simpleVectorStore.load(fileStore);
        }
        else {
            PagePdfDocumentReader pagePdfDocumentReader=new PagePdfDocumentReader(pdfFile);
            List<Document> documents=pagePdfDocumentReader.get();
            TextSplitter textSplitter=new TokenTextSplitter();
            List<Document> chunks=textSplitter.split(documents);
            simpleVectorStore.add(chunks);
            simpleVectorStore.save(fileStore);
        }
        return simpleVectorStore;
    }
    @PostConstruct
    public void initStore(){
        Integer count=jdbcClient.sql("select count(*) from vector_store")
                .query(Integer.class).single();
        if(count==0){
            PagePdfDocumentReader pagePdfDocumentReader=new PagePdfDocumentReader(pdfFile);
            List<Document> documents=pagePdfDocumentReader.get();
            TextSplitter textSplitter=new TokenTextSplitter();
            List<Document> chunks=textSplitter.split(documents);
            vectorStore.add(chunks);
        }
    }
}
