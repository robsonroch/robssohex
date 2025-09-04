package br.com.robson.robssohex.datasources;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "emailClient",
        url = "http://localhost:9001/mail-service"
)
public interface EmailClient {

    @PostMapping("/emails/confirmacao-html")
    ResponseEntity<Void> enviarEmailHtml(@RequestBody EmailConfirmacaoComHtmlRequest request);
}
