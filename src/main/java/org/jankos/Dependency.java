package org.jankos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Dependency {
    private String group;
    private String name;
    private String version;
}
