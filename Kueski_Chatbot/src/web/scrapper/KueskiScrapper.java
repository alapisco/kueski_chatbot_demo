package web.scrapper;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KueskiScrapper {

    private static final String baseUrl = "https://kueski.zendesk.com";
    private static final String categoriesRelativeUrl = "/hc/es/categories/203136108-Preguntas-Frecuentes";

    /*
    This method gets all questions and answers from the Kueski helpdesk page
    */
    public static ArrayList<String[]> getAllQuestions(){

        ArrayList<String[]> questions = new ArrayList<>();
        String[] categoriesUrls = getCategoriesUrls();

        for (int i = 0; i < categoriesUrls.length ; i++) {
            String[] questionsUrls = getQuestionsUrls(categoriesUrls[i]);
            for (int j = 0; j < questionsUrls.length; j++) {
                String[] qa = getQuestionAndAnswerFromUrl(questionsUrls[j]);
                questions.add(qa);
            }
        }

        return questions;
    }

    /*
    This method gets all the questions categories urls
    */
    private static String[] getCategoriesUrls(){

        String FAQpage = baseUrl+categoriesRelativeUrl;

        WebClient client = getWebClient();

        String[] sectionsUrls  = null;

        try {
            String searchUrl = FAQpage;
            HtmlPage page = client.getPage(searchUrl);


            List<HtmlElement> sectionTree = page.getByXPath("//div[@class='section-tree']") ;

            List<HtmlElement> sections = sectionTree.get(0).getByXPath("//section[@class='section']");

            sectionsUrls  = new String[sections.size()];

            int index = 0;
            for(HtmlElement section : sections){
                HtmlAnchor itemAnchor = section.getFirstByXPath(".//h3[@class='section-tree-title']/a");
                sectionsUrls[index++] = itemAnchor.getHrefAttribute();
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return sectionsUrls;
    }

    /*
    This method gets all the questions urls from a category
    */
    private static String[] getQuestionsUrls(String categoryUrl){

        WebClient client = getWebClient();

        String[] questionsUrls  = null;

        try {
            String categoryPage = baseUrl + categoryUrl;
            HtmlPage page = client.getPage(categoryPage);


            List<HtmlElement> articleList = page.getByXPath("//ul[@class='article-list']") ;

            List<HtmlElement> articles = articleList.get(0).getByXPath("//li[@class='article-list-item ']");

            List<HtmlAnchor> anchors = articles.get(0).getByXPath("//a[@class='article-list-link']");

            questionsUrls = new String[anchors.size()];

            for (int i = 0; i < anchors.size(); i++) {
                questionsUrls[i] = anchors.get(i).getHrefAttribute();
            }


        }catch(Exception e){
            e.printStackTrace();
        }

        return questionsUrls;
    }

    /*
    This method gets all the question and its answer from a question url
    */
    private static String[] getQuestionAndAnswerFromUrl(String questionrUrl){

        WebClient client = getWebClient();

        String[] qa  = new String[2];

        try {
            String categoryPage = baseUrl + questionrUrl;
            HtmlPage page = client.getPage(categoryPage);

            HtmlElement article = page.getFirstByXPath("//article[@class='article']") ;

            HtmlElement title = article.getFirstByXPath("//h1[@class='article-title']") ;

            String question = title.asText();
            qa[0] = question;

            String answerRawText = article.asText();

            String[] tokens = answerRawText.split("Seguir");

            String answer = tokens[1].split("Facebook")[0].trim();

            // reemplazar caracter raro de salto de linea, y dobles comillas por sencillas
            char c = (char)10;
            qa[1] = answer.replace(c+"","\\n").replace('\"','\'' );


        }catch(Exception e){
            e.printStackTrace();
        }

        return qa;
    }

    private static WebClient getWebClient(){
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        return client;
    }

    public static void main(String[] args) {
        String [] q = getQuestionAndAnswerFromUrl("/hc/es/articles/360008048413--Qu%C3%A9-pasa-si-no-puedo-hacer-un-pago-");
        System.out.println(Arrays.toString(q));
    }
}
