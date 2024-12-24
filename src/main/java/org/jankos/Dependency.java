package org.jankos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Dependency {
  private String group;
  private String name;
  private String version;

  public String notation() {
    return group + ":" + name + ":" + version;
  }
}
