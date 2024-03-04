package com.tunde.GKwebhook.Public.infra.providers;

import com.tunde.GKwebhook.Public.domain.order.dto.ProductDTO;
import com.tunde.GKwebhook.Public.domain.order.dto.VerifyOrderDTO;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MailSenderProvider {
    @Autowired
    private Environment env;


    public void sendEmail(VerifyOrderDTO order) throws Exception {
        HtmlEmail email = new HtmlEmail();
        email.setCharset("UTF-8");
        email.setHostName("email-ssl.com.br");
        email.setSmtpPort(465);
        email.setAuthentication(this.env.getProperty("my.email"), this.env.getProperty("my.password"));
        email.setSSLOnConnect(true);

        List<String> productNames = new ArrayList<>();

        for (ProductDTO item : order.itens()) {
            productNames.add(item.nome());
        }

        String htmlBody = this.generateDocumentValidation(
                order.numero(),
                productNames,
                order.pagamentos().get(0).valor(),
                order.pagamentos().get(0).numero_parcelas(),
                order.pagamentos().get(0).valor_parcela(),
                order.cliente().nome()
                );

        try {
            email.setFrom(this.env.getProperty("my.email"));
            email.setSubject("Verificação de Documentação PED: " + order.numero());
            email.setHtmlMsg(htmlBody);
            email.addTo("niinbus@gmail.com");
            email.send();

        } catch (Exception err) {
            err.printStackTrace();
            throw new Exception("Error while trying to send email");
        }
    }

    private String generateDocumentValidation(int order, List<String> products, String price, int installments, double installmentsValue, String name) {
        return "<!doctype html>\n" +
                "<html lang=\"pt-BR\">\n" +
                "  <head>\n" +
                "    <meta charset=\"UTF-8\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                "    <title>Email de Contato</title>\n" +
                "    <style>\n" +
                "      body {\n" +
                "        font-family: Arial, sans-serif;\n" +
                "        line-height: 1.6;\n" +
                "      }\n" +
                "      .container {\n" +
                "        max-width: 600px;\n" +
                "        margin: 0 auto;\n" +
                "        padding: 20px;\n" +
                "        background-color: #f9f9f9;\n" +
                "        border: 1px solid #ddd;\n" +
                "        border-radius: 5px;\n" +
                "      }\n" +
                "      p {\n" +
                "        margin-bottom: 10px;\n" +
                "      }    \n" +
                "      img {\n" +
                "        width: 40%; /* Faça a imagem ocupar 100% da largura da div */\n" +
                "        height: auto;\n" +
                "        display: block;\n" +
                "        margin-top: 20px;\n" +
                "        margin-left: auto; /* Centraliza a imagem horizontalmente */\n" +
                "        margin-right: auto; /* Centraliza a imagem horizontalmente */\n" +
                "      }\n" +
                "    </style>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <div class=\"container\">\n" +
                "      <p>\n" +
                "        Prezado(a) <strong>" + name + "</strong>, tudo bem? Estamos entrando em \n" +
                "        contato em razão do pedido de número: <strong>" + order + "</strong>, \n" +
                "        onde consta o produto: <strong>" + String.join(", ", products) + "</strong>, \n" +
                "        parcelado em <strong>" + installments + "x</strong> de <strong>R$ " + installmentsValue + "</strong>, \n" +
                "        totalizando <strong>R$ " + price + "</strong>. \n" +
                "        Ressaltamos que adotamos novas práticas de segurança para a primeira compra via cartão de crédito, \n" +
                "        iremos prosseguir com os procedimentos de emissão da nota fiscal e envio, assim que os documentos seguintes forem confirmados, \n" +
                "        são eles:\n" +
                "        ressaltamos que adotamos novas práticas de segurança para a primeira\n" +
                "        compra via cartão de crédito, iremos prosseguir com os procedimentos de\n" +
                "        emissão da nota fiscal e envio, assim que os documentos seguintes forem\n" +
                "        confirmados, são eles:\n" +
                "      </p>\n" +
                "      <ul>\n" +
                "        <li>Foto frente e verso do RG ou CNH</li>\n" +
                "        <li>Foto APENAS da frente do cartão (apenas nome do titular)</li>\n" +
                "      </ul>\n" +
                "      <p>\n" +
                "        Ressaltamos que a compra no cartão precisa ser no mesmo nome do cliente\n" +
                "        cadastrado, o setor de segurança NÃO possui interesse em dados\n" +
                "        financeiros como os números do cartão, desejamos apenas informações que\n" +
                "        comprovem a identidade (RG) e titularidade do cartão (nome do cliente no\n" +
                "        cartão). Desde já agradecemos a atenção, caso possua dúvidas sobre o\n" +
                "        requerimento de dados indicamos a checagem do seguinte site, pois nele\n" +
                "        há informações cedidas pelo Procon a respeito da solicitação de dados\n" +
                "        para compras via cartão de crédito:\n" +
                "        <a\n" +
                "          href=\"https://www.terra.com.br/economia/direitos-do-consumidor/procon-esclarece-duvidas-nas-compras-com-cartao-de-credito,ec48e6edbf17a410VgnVCM4000009bcceb0aRCRD.html#:~:text=%3A%3A%3A%20A%20loja%20tem%20direito,consumidor%20n%C3%A3o%20pode%20ser%20obrigado\"\n" +
                "          target=\"_blank\"\n" +
                "          >Procon</a\n" +
                "        >.\n" +
                "      </p>\n" +
                "      <P>\n" +
                "          Caso já tenha enviado a documentação por favor desconsiderar esse email.\n" +
                "      </P>\n" +
                "      <p>Att Equipe GK INFO STORE</p>\n" +
                "    </div>\n" +
                "    <img src=\"https://imgur.com/gXdgnn4.png\" alt=\"Assinatura\">\n" +
                "  </body>\n" +
                "</html>\n";
    }
}
