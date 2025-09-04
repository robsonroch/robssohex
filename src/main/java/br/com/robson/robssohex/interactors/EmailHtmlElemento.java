package br.com.robson.robssohex.interactors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailHtmlElemento {
    private String tag;
    private String conteudo;
}
