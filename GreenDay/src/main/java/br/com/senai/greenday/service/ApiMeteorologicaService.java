package br.com.senai.greenday.service;

import br.com.senai.greenday.model.Clima;
import br.com.senai.greenday.model.Propriedade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

/** Consulta meteorológica atual sem necessidade de chave, usando Open-Meteo. */
@Service
@RequiredArgsConstructor
public class ApiMeteorologicaService {
    private final RestClient restClient;
    private final ClimaService climaService;

    @SuppressWarnings("unchecked")
    public Clima buscarEArmazenarClimaAtual(Propriedade propriedade) {
        if (propriedade.getLatitude() == null || propriedade.getLongitude() == null) {
            throw new IllegalArgumentException("Cadastre latitude e longitude da propriedade para consultar o clima em tempo real.");
        }
        String url = "https://api.open-meteo.com/v1/forecast?latitude=" + propriedade.getLatitude()
                + "&longitude=" + propriedade.getLongitude()
                + "&current=temperature_2m,relative_humidity_2m,wind_speed_10m,weather_code"
                + "&timezone=America%2FSao_Paulo";
        Map<String, Object> resposta = restClient.get().uri(url).retrieve().body(Map.class);
        Map<String, Object> current = (Map<String, Object>) resposta.get("current");
        int code = current.get("weather_code") instanceof Number n ? n.intValue() : -1;
        Clima clima = Clima.builder()
                .propriedade(propriedade)
                .temperaturaCelsius(((Number) current.get("temperature_2m")).doubleValue())
                .umidadeRelativa(((Number) current.get("relative_humidity_2m")).doubleValue())
                .velocidadeVentoKmh(((Number) current.get("wind_speed_10m")).doubleValue())
                .descricao(descricao(code))
                .build();
        return climaService.registrar(propriedade.getId(), clima);
    }

    private String descricao(int code) {
        return switch (code) {
            case 0 -> "Céu limpo"; case 1,2 -> "Parcialmente nublado"; case 3 -> "Nublado";
            case 45,48 -> "Neblina"; case 51,53,55,56,57 -> "Garoa";
            case 61,63,65,66,67,80,81,82 -> "Chuva";
            case 71,73,75,77,85,86 -> "Neve"; case 95,96,99 -> "Trovoadas";
            default -> "Condições atuais";
        };
    }
}
