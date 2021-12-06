package ru.bcs.creditmarkt.strapi.dto.phrasy;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Phrasy {
    private String orig;
    private String ablt;
    private String gent;
    private String loct;
    private String datv;
    private String accs;
}

