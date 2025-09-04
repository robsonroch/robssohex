package br.com.robson.robssohex.datasources;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class EmailConfirmacaoComHtmlRequest {
    private String destino;
    private String nomeUsuario;
    private String sistemaOrigem;
    private List<Map<String, Object>> conteudoHtml;
}