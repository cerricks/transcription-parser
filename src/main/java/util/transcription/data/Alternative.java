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
package util.transcription.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Represents a JSON object.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Alternative {

  private String transcript;
  private double confidence;
  private List<Timestamp> timestamps;

  public Alternative() {
  }

  public List<Timestamp> getTimestamps() {
    return timestamps;
  }

  public void setTimestamps(List<Timestamp> timestamps) {
    this.timestamps = timestamps;
  }

  public String getTranscript() {
    return transcript;
  }

  public void setTranscript(String transcript) {
    this.transcript = transcript;
  }

  public double getConfidence() {
    return confidence;
  }

  public void setConfidence(double confidence) {
    this.confidence = confidence;
  }

  public double getStartTime() {
    return timestamps.get(0).getFrom();
  }

  public double getEndTime() {
    return timestamps.get(timestamps.size() - 1).getTo();
  }

}
