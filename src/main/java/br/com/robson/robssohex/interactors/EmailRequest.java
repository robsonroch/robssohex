package br.com.robson.robssohex.interactors;

import br.com.robson.robssohex.datasources.EmailConfirmacaoComHtmlRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class EmailRequest {
    private String destino;
    private String nomeUsuario;
    private String sistemaOrigem;
    private List<EmailHtmlElemento> conteudoHtml;

    // Getters e setters

    public EmailConfirmacaoComHtmlRequest toFeignFormat() {
        List<Map<String, Object>> html = conteudoHtml.stream()
                .map(e -> {
                    // Garante que o mapa seja do tipo Map<String, Object>
                    Map<String, Object> map = Map.of("tag", e.getTag(), "conteudo", e.getConteudo());
                    return map;
                })
                .collect(Collectors.toList());

        EmailConfirmacaoComHtmlRequest request = new EmailConfirmacaoComHtmlRequest();
        request.setDestino(destino);
        request.setNomeUsuario(nomeUsuario);
        request.setSistemaOrigem(sistemaOrigem);
        request.setConteudoHtml(html);
        return request;
    }
}
