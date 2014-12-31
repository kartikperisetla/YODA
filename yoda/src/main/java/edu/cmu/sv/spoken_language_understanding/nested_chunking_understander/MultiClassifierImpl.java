package edu.cmu.sv.spoken_language_understanding.nested_chunking_understander;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 12/30/14.
 */
public class MultiClassifierImpl implements MultiClassifier {
    public static final String classifierModelFile = "src/resources/models_and_serialized_objects/classifier.model";
    public static final String classifierTrainingFile = "src/resources/corpora/classifier_training_file.txt";
    public static final String serializedClassifierFile = "src/resources/models_and_serialized_objects/serialized_classifier.srl";

    public static class VariableNotClassified {
        @Override
        public String toString() {
            return "0NotClassified";
        }
    }
    public static final VariableNotClassified NOT_CLASSIFIED = new VariableNotClassified();

    Map<String, Integer> featurePositionMap = new HashMap<>();
    Map<String, List<Object>> outputInterpretation;
    Process theanoSubProcess;
    InputStreamReader stdoutInputStreamReader;
    BufferedReader stdoutBufferedReader;
    InputStreamReader stderrInputStreamReader;
    BufferedReader stderrBufferedReader;

    public MultiClassifierImpl(Map<String, Integer> featurePositionMap, Map<String, List<Object>> outputInterpretation, boolean launchClassifier) {
        this.featurePositionMap = featurePositionMap;
        this.outputInterpretation = outputInterpretation;

        if (launchClassifier) {
            ProcessBuilder processBuilder =
                    new ProcessBuilder("../slu_tools/run_classifier.py", "-m" , classifierModelFile);
            try {

                System.out.println("launching theano...");
                theanoSubProcess = processBuilder.start();
                stdoutInputStreamReader = new InputStreamReader(theanoSubProcess.getInputStream());
                stdoutBufferedReader = new BufferedReader(stdoutInputStreamReader);
                stderrInputStreamReader = new InputStreamReader(theanoSubProcess.getErrorStream());
                stderrBufferedReader= new BufferedReader(stderrInputStreamReader);

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(stdoutBufferedReader.readLine());
                System.out.println("theano message:" + stringBuilder.toString());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void trainTheanoModel(){
        ProcessBuilder processBuilder =
                new ProcessBuilder("../slu_tools/train_classifier.py", "-t", classifierTrainingFile, "-m", classifierModelFile);
        try {
            Process p = processBuilder.start();
            p.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public MultiClassifierImpl() {
    }

    @Override
    public void classify(NodeMultiClassificationProblem classificationProblem) {
        String theanoString = packTestSample(classificationProblem)+"\n";
        try {
            theanoSubProcess.getOutputStream().write(theanoString.getBytes());
            theanoSubProcess.getOutputStream().flush();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(stdoutBufferedReader.readLine());
            System.out.println("string returned from subprocess:" + stringBuilder.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        //todo: run model given input

        //todo: read generated result

    }

    @Override
    public String packTestSample(NodeMultiClassificationProblem classificationProblem){
        List<Double> features = featureVector(extractFeatures(classificationProblem));
        return "[" + String.join(", ", features.stream().map(Object::toString).collect(Collectors.toList())) + "]";
    }

    @Override
    public String packTrainingSample(NodeMultiClassificationProblem classificationProblem){
        String ans = packTestSample(classificationProblem);
        List<Integer> output = new LinkedList<>();
        for (String variable : outputInterpretation.keySet().stream().sorted().collect(Collectors.toList())){
            Object valueForVariable = classificationProblem.outputRolesAndFillers.get("ground_truth").get(variable);
            if (valueForVariable==null)
                valueForVariable = NOT_CLASSIFIED;
            output.add(outputInterpretation.get(variable).indexOf(valueForVariable));
        }
        ans += " -> ";
        ans += "[" + String.join(", ", output.stream().map(Object::toString).collect(Collectors.toList())) + "]";
        return ans;
    }

    @Override
    public Set<String> extractFeatures(NodeMultiClassificationProblem classificationProblem) {
        Set<String> featuresPresent = new HashSet<>();

        //// collect features present
        String[] tmp = classificationProblem.stringForAnalysis.split(" ");
        List<String> tokens = new LinkedList<>(Arrays.asList(tmp));

        // collect unigram features
        for (int i = 0; i < tokens.size(); i++) {
            featuresPresent.add("Unigram: " + tokens.get(i));
        }

        tokens.add(0, "<S>");
        tokens.add("</S>");

        // collect bigram features
        for (int i = 0; i < tokens.size() - 1; i++) {
            featuresPresent.add("Bigram: " + tokens.get(i) + ", " + tokens.get(i + 1));
        }

        // collect context features
        String[] fillerPath = classificationProblem.contextPathInStructure.split("\\.");
        for (int i = 0; i < fillerPath.length; i++) {
            featuresPresent.add("NodeContext: " + (fillerPath.length - i) + ", " + fillerPath[i]);
        }
        return featuresPresent;
    }

    @Override
    public List<Double> featureVector(Set<String> featuresPresent) {
            //// assemble feature vector
        List<Double> features = new LinkedList<>();
        Set<Integer> featuresOn = new HashSet<>();
        for (String presentFeature : featuresPresent) {
            if (featurePositionMap.containsKey(presentFeature))
                featuresOn.add(featurePositionMap.get(presentFeature));
            else {
//                System.out.println("WARNING: present feature not in model:" + presentFeature);
            }
        }

        for (int i = 0; i < featurePositionMap.size(); i++) {
            if (featuresOn.contains(i))
                features.add(1.0);
            else
                features.add(0.0);
        }
        return features;
    }



}
