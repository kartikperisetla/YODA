package edu.cmu.sv.spoken_language_understanding.nested_chunking_understander;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by David Cohen on 1/6/15
 *
 * Manage launching and communication between SLU child processes
 */
public class ExternalModelManager {

    static Process theanoSubProcess;
    static InputStreamReader stdoutInputStreamReader;
    static BufferedReader stdoutBufferedReader;
    public static boolean running = false;

    public static void startTheano(){
        running = true;
        ProcessBuilder processBuilder =
                new ProcessBuilder("../slu_tools/run_model_server.py", "-k", Chunker.chunkerModelFile, "-c", MultiClassifier.classifierModelFile);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

        try {
            System.out.println("launching theano...");
            theanoSubProcess = processBuilder.start();
            stdoutInputStreamReader = new InputStreamReader(theanoSubProcess.getInputStream());
            stdoutBufferedReader = new BufferedReader(stdoutInputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(stdoutBufferedReader.readLine());
            System.out.println("theano message:" + stringBuilder.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String runModel(String input) {
        try {
            theanoSubProcess.getOutputStream().write(input.getBytes());
            theanoSubProcess.getOutputStream().flush();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(stdoutBufferedReader.readLine());
            System.out.println("string returned from subprocess:" + stringBuilder.toString());
            return stringBuilder.toString();
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
