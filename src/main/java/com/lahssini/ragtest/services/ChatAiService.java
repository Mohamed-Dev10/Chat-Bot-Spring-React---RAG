package com.lahssini.ragtest.services;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@BrowserCallable
@AnonymousAllowed
public class ChatAiService {
    private ChatClient chatClient;
    @Value("classpath:prompts/prompt.st")
    private Resource promptResource;
    private VectorStore vectorStore;
    public ChatAiService(ChatClient.Builder builder, VectorStore vectorStore){
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
    }

    public  String ragChat(String question){
        PromptTemplate promptTemplate=new PromptTemplate(promptResource);
        List<Document> document=vectorStore.similaritySearch(question);
        List<String> context=document.stream().map(d->d.getContent()).toList();
        Prompt prompt=promptTemplate.create(Map.of("context",context,"question",question));
        return  chatClient.prompt(prompt).call().content();
    }
}

