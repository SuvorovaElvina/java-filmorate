package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.throwable.NotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SlopeOne {

    private static Map<Film, Map<Film, Double>> diff = new HashMap<>();
    private static Map<Film, Map<Film, Integer>> freq = new HashMap<>();
    private static Map<User, HashMap<Film, Double>> outputData = new HashMap<>();

    public static Map<User, HashMap<Film, Double>> slopeOne(Map<User, HashMap<Film, Double>> inputData, List<Film> films) {
        buildDifferencesMatrix(inputData);
        predict(inputData, films);
        return outputData;
    }

    private static void buildDifferencesMatrix(Map<User, HashMap<Film, Double>> data) {
        for (HashMap<Film, Double> user : data.values()) {
            for (Entry<Film, Double> e : user.entrySet()) {
                if (!diff.containsKey(e.getKey())) {
                    diff.put(e.getKey(), new HashMap<Film, Double>());
                    freq.put(e.getKey(), new HashMap<Film, Integer>());
                }
                for (Entry<Film, Double> e2 : user.entrySet()) {
                    int oldCount = 0;
                    if (freq.get(e.getKey()).containsKey(e2.getKey())) {
                        oldCount = freq.get(e.getKey()).get(e2.getKey()).intValue();
                    }
                    double oldDiff = 0.0;
                    if (diff.get(e.getKey()).containsKey(e2.getKey())) {
                        oldDiff = diff.get(e.getKey()).get(e2.getKey()).doubleValue();
                    }
                    double observedDiff = e.getValue() - e2.getValue();
                    freq.get(e.getKey()).put(e2.getKey(), oldCount + 1);
                    diff.get(e.getKey()).put(e2.getKey(), oldDiff + observedDiff);
                }
            }
        }
        for (Film j : diff.keySet()) {
            for (Film i : diff.get(j).keySet()) {
                double oldValue = diff.get(j).get(i).doubleValue();
                int count = freq.get(j).get(i).intValue();
                diff.get(j).put(i, oldValue / count);
            }
        }
    }

    private static void predict(Map<User, HashMap<Film, Double>> data, List<Film> films) {
        HashMap<Film, Double> uPred = new HashMap<Film, Double>();
        HashMap<Film, Integer> uFreq = new HashMap<Film, Integer>();
        for (Film j : diff.keySet()) {
            uFreq.put(j, 0);
            uPred.put(j, 0.0);
        }
        for (Entry<User, HashMap<Film, Double>> e : data.entrySet()) {
            for (Film j : e.getValue().keySet()) {
                for (Film k : diff.keySet()) {
                    try {
                        double predictedValue = diff.get(k).get(j).doubleValue() + e.getValue().get(j).doubleValue();
                        double finalValue = predictedValue * freq.get(k).get(j).intValue();
                        uPred.put(k, uPred.get(k) + finalValue);
                        uFreq.put(k, uFreq.get(k) + freq.get(k).get(j).intValue());
                    } catch (NullPointerException e1) {
                        System.out.println(e);
                    }
                }
            }
            HashMap<Film, Double> clean = new HashMap<Film, Double>();
            for (Film j : uPred.keySet()) {
                if (uFreq.get(j) > 0) {
                    clean.put(j, uPred.get(j).doubleValue() / uFreq.get(j).intValue());
                }
            }
            for (Film j : films) {
                if (e.getValue().containsKey(j)) {
                    clean.put(j, e.getValue().get(j));
                } else if (!clean.containsKey(j)) {
                    clean.put(j, -1.0);
                }
            }
            outputData.put(e.getKey(), clean);
        }
    }
}

