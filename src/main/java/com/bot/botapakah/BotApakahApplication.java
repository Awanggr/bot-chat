package com.bot.botapakah;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse.MessageContentResponseBuilder;
import com.linecorp.bot.model.Multicast;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.w3c.dom.Document;
import org.json.*;

import java.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.*;



@SpringBootApplication
@LineMessageHandler
public class BotApakahApplication extends SpringBootServletInitializer {
    String id="";

    @Autowired
    private LineMessagingClient lineMessagingClient;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(BotApakahApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(BotApakahApplication.class, args);
    }

    @EventMapping
    //Handling penerima text dari user
    public void handleTextEvent(MessageEvent<TextMessageContent> messageEvent){
        String pesan = messageEvent.getMessage().getText().toLowerCase();//Merubah text yang dikirim user menjadi lowercase
        id = messageEvent.getSource().getUserId();//Mengambil id user pengirim
        String replyToken = messageEvent.getReplyToken();//Mengambil token

        Bot bot = new Bot("simplebot","libs/");//memanggil bot
        Chat chatSession = new Chat(bot);//membuat session untuk bot
        
        String response = chatSession.multisentenceRespond(pesan);//mengirimkan pesan dari LINE untuk diproses bot
        String responsenospace = response.replaceAll("\\s\\s\\s\\s\\s\\s\\s\\s\\s\\s", "\n");//merubah hasiljawaban dari bot untuk dirapikan
        String responseenter = responsenospace.replaceAll("%", "\n");//merubah % menjadi /n utnuk memberikan space enter
        String[] arrOfStr = responseenter.split("#", 2);//melakukan pemisahan kata dengan # untuk link gambar

        if(arrOfStr.length==2){//pengecekan apakah terdapat 2 kata
            if(!arrOfStr[1].equals(null)){//jika kata ke 2 tidak kosong maka kata tersebut merupakan link gambar
                balasChatGambar(replyToken, arrOfStr[0], arrOfStr[1]);//mengirim balasan dengan isian kata dan link gambar
            }
        }else{
            balasChat(replyToken, responseenter);//jikahanya ada 1 kata maka akan dikirim sebagai balasan biasa
        }
        
    }

    private void balasChat(String replyToken, String jawaban){//fungsi untuk mengirim balsan biasa
        TextMessage jawabanDalamBentukTextMessage = new TextMessage(jawaban);
        try {
            lineMessagingClient
                .replyMessage(new ReplyMessage(replyToken, jawabanDalamBentukTextMessage))
                .get();
        } catch (InterruptedException|ExecutionException e) {
            System.out.println("Ada error saat ingin membalas chat biasa");
        }
    }

    private void balasChatGambar(String replyToken, String jawaban, String gambar){//fungsi untuk mengirim balasan gambar
        TextMessage jawabanDalamBentukTextMessage = new TextMessage(jawaban);
        ImageMessage jawabanDenganGambar = new ImageMessage(gambar, gambar);
        List<Message> multipesan=new ArrayList<>();
            Set<String> userid = new HashSet<>();
    
            userid.add(id);
            multipesan.add(jawabanDalamBentukTextMessage);
            multipesan.add(jawabanDenganGambar);
    
            Multicast multi = new Multicast(userid,multipesan);
            try {
                lineMessagingClient
                        .multicast(multi)
                        .get();
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Ada error saat ingin membalas chat gambar");
            }
    }
    //\n         

}
