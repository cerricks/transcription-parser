/*
 * Copyright 2018 Clifford Errickson.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package util.transcription;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.transcription.data.Alternative;
import util.transcription.data.Result;
import util.transcription.data.SpeakerLabel;
import util.transcription.data.Timestamp;
import util.transcription.data.Transcription;

/**
 * A utility for parsing a Transcription result file into multiple outputs.
 */
public class TranscriptionResultFileParser {

  private final static Logger logger = Logger.getLogger(TranscriptionResultFileParser.class.getName());

  private final static String OUTPUT1_FILENAME = "continuous_transcript.txt";
  private final static String OUTPUT2_FILENAME = "speaker_duration.txt";
  private final static String OUTPUT3_FILENAME = "keyword_count.txt";

  public static void main(String[] args) {
    if (args.length != 1) {
      throw new IllegalArgumentException("path to input file is required");
    }

    TranscriptionResultFileParser parser = new TranscriptionResultFileParser();

    logger.log(Level.INFO, "Parsing file: {0}", args[0]);

    try {
      parser.parse(args[0]);
    } catch (IOException ex) {
      logger.log(Level.SEVERE, null, ex);
      System.exit(1);
    }

    logger.log(Level.INFO, "Processing complete");
  }

  public void parse(String input) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();

    Transcription transcription = objectMapper.readValue(new File(input), Transcription.class);

    // Continuous transcript
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT1_FILENAME))) {
      for (Result result : transcription.getResults()) {
        writer.write(result.getAlternatives().get(0).getTranscript());
      }
    }

    // speaker duration
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT2_FILENAME))) {
      SpeakerLabel startSpeakerLabel = null;
      SpeakerLabel previousSpeakerLabel = null;

      for (SpeakerLabel speakerLabel : transcription.getSpeakerLabels()) {
        if (startSpeakerLabel == null) {
          startSpeakerLabel = speakerLabel;
          previousSpeakerLabel = speakerLabel;
          continue;
        }

        if (speakerLabel.getSpeaker() != previousSpeakerLabel.getSpeaker()) {
          writer.write("Speaker " + startSpeakerLabel.getSpeaker() + " (" + startSpeakerLabel.getFrom() + "-" + previousSpeakerLabel.getTo() + "):");
          writer.newLine();
          writer.write(transcription.getTranscript(startSpeakerLabel.getFrom(), previousSpeakerLabel.getTo()));
          writer.newLine();

          startSpeakerLabel = speakerLabel;
        }

        previousSpeakerLabel = speakerLabel;
      }

      writer.write("Speaker " + startSpeakerLabel.getSpeaker() + " (" + startSpeakerLabel.getFrom() + "-" + previousSpeakerLabel.getTo() + "):");
      writer.newLine();
      writer.write(transcription.getTranscript(startSpeakerLabel.getFrom(), previousSpeakerLabel.getTo()));
      writer.newLine();
    }

    // keywords
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT3_FILENAME))) {
      Map<String, Map<Integer, Integer>> keywordSpeakerCounts = new HashMap<>();

      for (Result result : transcription.getResults()) {
        for (Alternative alternative : result.getAlternatives()) {
          for (Timestamp timestamp : alternative.getTimestamps()) {
            if (timestamp.getWord().matches("[%].*")) {
              String keyword = timestamp.getWord().substring(1);

              int speakerIndex = transcription.getSpeakerIndex(timestamp);

              if (keywordSpeakerCounts.containsKey(keyword)) {
                if (keywordSpeakerCounts.get(keyword).containsKey(speakerIndex)) {
                  keywordSpeakerCounts.get(keyword).put(speakerIndex, keywordSpeakerCounts.get(keyword).get(speakerIndex) + 1);
                } else {
                  keywordSpeakerCounts.get(keyword).put(speakerIndex, 1);
                }
              } else {
                Map<Integer, Integer> speakerCounts = new HashMap();
                speakerCounts.put(speakerIndex, 1);

                keywordSpeakerCounts.put(keyword, speakerCounts);
              }
            }
          }
        }
      }

      for (String keyword : keywordSpeakerCounts.keySet()) {
        for (Integer speakerIndex : keywordSpeakerCounts.get(keyword).keySet()) {
          writer.write(keyword + ": Speaker " + speakerIndex + ": " + keywordSpeakerCounts.get(keyword).get(speakerIndex) + " time(s)");
          writer.newLine();
        }
      }
    }
  }
}
