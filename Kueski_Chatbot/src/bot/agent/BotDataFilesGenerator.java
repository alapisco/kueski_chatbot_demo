package bot.agent;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import web.scrapper.KueskiScrapper;

public class BotDataFilesGenerator {

    public static void main(String[] args) throws Exception {

        String intentsDirectory = "/home/james/intents";

        System.out.println("Getting questions");
        ArrayList<String[]> questions = KueskiScrapper.getAllQuestions();

        System.out.println("Generating files");
        for (String[] question : questions) {
            generateIntentFileFromQuestion(question,intentsDirectory);
        }

        System.out.println("Done. Files generated at " + intentsDirectory);

    }

    public static void generateIntentFileFromQuestion(String question[], String outputDir){

        String q = question[0];
        String answer = question[1];

        UUID id = UUID.randomUUID();

        // removing question marks
        String intentName = q.replace('?',' ').replace('¿',' ').trim();

        // truncate intent name if question is too large
        if(intentName.length()>65){
            intentName = intentName.substring(0,65);
        }

        String intentAnswer = null;
        String intentQuestion = null;

        try {
            intentAnswer = getTemplate("intent_answer_template.json");
            intentQuestion = getTemplate("intent_question_template.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // replace placeholders for actual values

        intentAnswer = intentAnswer.replace("{UUID_PLACEHOLDER}",id.toString())
                .replace("{INTENT_NAME_PLACEHOLDER}",intentName)
                .replace("{ANSWER_PLACEHOLDER}", answer);

        id = UUID.randomUUID();
        intentQuestion = intentQuestion.replace("{UUID_PLACEHOLDER}",id.toString())
                .replace("{QUESTION_PLACEHOLDER}", q);

        String intentFileName = intentName+".json";
        String userSaysFileName = intentName+"_usersays_es.json";

        try {
            File intentFile = new File(outputDir + "/" + intentFileName);
            File userSaysFile = new File(outputDir + "/" + userSaysFileName);

            if( !intentFile.exists() && !userSaysFile.exists() ) {
                writeStringToFile(intentFile, intentAnswer);
                writeStringToFile(userSaysFile, intentQuestion);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void writeStringToFile(File file, String str) throws IOException{
        FileUtils.writeStringToFile(file, str+"\n", StandardCharsets.UTF_8,true);
    }

    private static String getTemplate(String templateFile) throws IOException {
        String content;
        content = IOUtils.toString(BotDataFilesGenerator.class.getResourceAsStream("/"+templateFile), StandardCharsets.UTF_8);
        return content;
    }

}
