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
public class Result {

  private List<Alternative> alternatives;

  public Result() {
  }

  public List<Alternative> getAlternatives() {
    return alternatives;
  }

  public void setAlternatives(List<Alternative> alternatives) {
    this.alternatives = alternatives;
  }

}
