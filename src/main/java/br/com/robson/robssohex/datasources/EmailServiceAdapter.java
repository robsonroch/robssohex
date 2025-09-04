package br.com.robson.robssohex.datasources;

import br.com.robson.robssohex.interactors.EmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailServiceAdapter {
    private final EmailClient emailClient;

    public void enviarEmail(EmailRequest request) {
        emailClient.enviarEmailHtml(request.toFeignFormat());
    }
}

