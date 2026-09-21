#include <DHT.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>

// DHT11: Monitora temperatura e umidade do ar para contexto ambiental
#define DHT_PIN 20
#define DHT_TYPE DHT11
DHT dht(DHT_PIN, DHT_TYPE);
float temperaturaAr;
float umidadeAr;

// Sensor capacitivo: Mede quanto de água há no solo (0-4095)
// A calibração define os extremos: VSECO (solo seco) e VMOLHADO (solo molhado)
#define SOLO_PIN 4
const int VSECO = 0;
const int VMOLHADO = 4095;

// Limites de decisão: quando irrigar (SECO) e quando parar (UMIDO)
const int LSECO = 30;
const int LUMIDO = 80;

int vBrutoSolo = 0;
int umidadeSolo = 0;
String estadoSolo = "SEM";

// Exibe em tempo real: modo, sensores e status da bomba
#define SCREEN_WIDTH 128
#define SCREEN_HEIGHT 64
Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, -1);
#define OLEDPIN1 21
#define OLEDPIN2 47

// Relay: Ativa/desativa fisicamente a bomba via GPIO
// Sistema dual: AUTOMATICO (por umidade) ou MANUAL (por botão)
#define RELAY_PIN 15
String statusBomba = "SEM";
String modoOperacao = "AUTOMATICO";

// Alterna entre automático e manual
// Usa debounce para evitar leitura múltipla de um único clique
#define BOTAO_PIN 48
int estadoBotao = 0;
int valorAnterior = LOW;
unsigned long ultimoTempoBotao = 0;
const int DEBOUNCE_DELAY = 50;

// Ligando/desligando a bomba manualmente (só funciona em MANUAL)
// Evita conflito com controle automático
#define BOTAO_BOMBA_PIN 45
int estadoBombaManual = 0;
int valorAnteriorBomba = LOW;
unsigned long ultimoTempoBotaoBomba = 0;

void setup() {
    Serial.begin(9600);
    
    pinMode(BOTAO_PIN, INPUT);
    pinMode(BOTAO_BOMBA_PIN, INPUT);
    
    dht.begin();
    
    analogReadResolution(12);
    
    Wire.begin(OLEDPIN1, OLEDPIN2);
    
    pinMode(RELAY_PIN, OUTPUT);
    digitalWrite(RELAY_PIN, LOW);

    Serial.println("Sistema iniciado!");
    Serial.println("Botao 1 (GPIO 48): Alterna AUTOMATICO <-> MANUAL");
    Serial.println("Botao 2 (GPIO 45): Controla BOMBA ON <-> OFF");
    Serial.println("--------------------------");
}

// Gerencia mudança de modo (automático ↔ manual)
// Lógica: Lê entrada → debounce → inverte estado → executa ação
// Ao voltar para automático, reseta controle manual da bomba
void botaoController() {
    int valorEntrada = digitalRead(BOTAO_PIN);
    unsigned long tempoAgora = millis();

    if ((tempoAgora - ultimoTempoBotao) < DEBOUNCE_DELAY) {
        return;
    }

    if (valorEntrada != valorAnterior) {
        valorAnterior = valorEntrada;
        ultimoTempoBotao = tempoAgora;

        if (valorEntrada == HIGH) {
            estadoBotao = 1 - estadoBotao;
            delay(10);

            if (estadoBotao == 1) {
                modoOperacao = "MANUAL";
                Serial.println(">>> MODO MANUAL ATIVADO");
                Serial.println("    Use o botão 2 para controlar a bomba!");
                // Segurança: desliga bomba ao retornar para manual
                estadoBombaManual = 0;
                digitalWrite(RELAY_PIN, LOW);
                statusBomba = "DESLIGADA";
            } else {
                modoOperacao = "AUTOMATICO";
                Serial.println(">>> MODO AUTOMATICO ATIVADO");
                Serial.println("    Bomba será controlada pela umidade do solo");
                // Segurança: desliga bomba ao retornar para automático
                estadoBombaManual = 0;
                digitalWrite(RELAY_PIN, LOW);
                statusBomba = "DESLIGADA";
            }
        }
    }
}

// Controle manual da bomba (seguro contra automático)
// Lógica: Verifica modo → se automático, nega ação → se manual, alterna bomba
// Proteção: Botão 2 só funciona em modo MANUAL
void botaoBombaController() {
    int valorEntrada = digitalRead(BOTAO_BOMBA_PIN);
    unsigned long tempoAgora = millis();

    if ((tempoAgora - ultimoTempoBotaoBomba) < DEBOUNCE_DELAY) {
        return;
    }

    if (valorEntrada != valorAnteriorBomba) {
        valorAnteriorBomba = valorEntrada;
        ultimoTempoBotaoBomba = tempoAgora;

        if (valorEntrada == HIGH) {
            // Proteção: impede comando manual em modo automático
            if (estadoBotao == 1) {
                estadoBombaManual = 1 - estadoBombaManual;
                delay(10);

                if (estadoBombaManual == 1) {
                    digitalWrite(RELAY_PIN, HIGH);
                    statusBomba = "LIGADA";
                    Serial.println(">>> BOMBA LIGADA (Manual)");
                } else {
                    digitalWrite(RELAY_PIN, LOW);
                    statusBomba = "DESLIGADA";
                    Serial.println(">>> BOMBA DESLIGADA (Manual)");
                }
            } else {
                Serial.println("!!! Botão 2 desabilitado em modo AUTOMATICO");
                Serial.println("    Pressione o Botão 1 para ativar MANUAL");
            }
        }
    }

    Serial.print("Status da bomba: ");
    Serial.println(statusBomba);
}

// Controle automático da bomba (por umidade do solo)
// Lógica: Se automático → liga quando seco, desliga quando úmido
// Implementa histerese: SECO (ligar) e UMIDO (desligar) previnem oscilação
void bombaController() {
    if (estadoBotao == 0) { // Só funciona em modo AUTOMATICO
        if (estadoSolo == "SECO") {
            digitalWrite(RELAY_PIN, HIGH);
            statusBomba = "LIGADA";
        } else {
            digitalWrite(RELAY_PIN, LOW);
            statusBomba = "DESLIGADA";
        }
    }

    Serial.print("Status da bomba: ");
    Serial.println(statusBomba);
}

// Interface visual (Tela OLED)
// Estrutura: Modo (topo) | Sensores ambientais | Umidade solo | Status bomba (destaque)
// Atualiza a cada leitura para feedback em tempo real
void displayOLED() {
    if (!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) {
        Serial.println("OLED nao encontrado!");
        while (true);
    }

    display.clearDisplay();
    display.setTextColor(SSD1306_WHITE);

    display.setTextSize(1);
    display.setCursor(5, 0);
    display.print(modoOperacao == "MANUAL" ? "[M]" : "[A]");
    display.setCursor(40, 4);
    display.println(modoOperacao);

    display.drawLine(0, 18, 128, 18, SSD1306_WHITE);

    display.setTextSize(1);
    display.setCursor(0, 22);
    display.print("Temp: ");
    display.print(temperaturaAr, 1);
    display.println("C");

    display.setCursor(0, 32);
    display.print("Umid: ");
    display.print(umidadeAr, 0);
    display.println("%");

    display.setCursor(72, 22);
    display.print("Solo:");
    display.print(umidadeSolo);
    display.println("%");

    display.setCursor(72, 32);
    display.print("Estado:");
    if (estadoSolo == "IDEAL") display.println("OK");
    else if (estadoSolo == "SECO") display.println("SECO");
    else if (estadoSolo == "UMIDO") display.println("WET");

    display.drawLine(0, 44, 128, 44, SSD1306_WHITE);

    display.setTextSize(1);
    display.setCursor(0, 48);
    display.print("Bomba: ");
    display.setTextSize(1);
    display.println(statusBomba == "LIGADA" ? "ON" : "OFF");

    display.display();
}

// DHT: Umidade do ar (contexto ambiental)
void DHTumidade() {
    umidadeAr = dht.readHumidity();
    if (isnan(umidadeAr)) {
        Serial.println("Erro ao ler umidade do ar!");
        return;
    }
    Serial.print("Umidade do ar: ");
    Serial.print(umidadeAr);
    Serial.println("%");
}

// DHT: Temperatura do ar (monitoramento)
void DHTemperatura() {
    temperaturaAr = dht.readTemperature();
    if (isnan(temperaturaAr)) {
        Serial.println("Erro ao ler temperatura do ar!");
        return;
    }
    Serial.print("Temperatura do ar: ");
    Serial.print(temperaturaAr);
    Serial.println(" C");
}

// Leitura e interpretação do solo
// Amostras múltiplas (10x) reduzem ruído de leitura analógica
// Mapeamento: valor bruto (0-4095) → percentual (0-100%)
// Classificação: SECO (≤30%) | IDEAL (30-80%) | UMIDO (≥80%)
void lerUmidadeSolo() {
    long soma = 0;
    const int AMOSTRAS = 10;
    for (int i = 0; i < AMOSTRAS; i++) {
        soma += analogRead(SOLO_PIN);
        delay(5);
    }
    vBrutoSolo = soma / AMOSTRAS;

    umidadeSolo = map(vBrutoSolo, VSECO, VMOLHADO, 100, 0);
    umidadeSolo = constrain(umidadeSolo, 0, 100);

    if (vBrutoSolo < min(VSECO, VMOLHADO) || vBrutoSolo > max(VSECO, VMOLHADO)) {
        Serial.println("Aviso: leitura fora da faixa de calibracao; ajuste VSECO/VMOLHADO.");
    }

    if (umidadeSolo <= LSECO) {
        estadoSolo = "SECO";
    } else if (umidadeSolo >= LUMIDO) {
        estadoSolo = "UMIDO";
    } else {
        estadoSolo = "IDEAL";
    }

    Serial.print("Umidade do solo: ");
    Serial.print(umidadeSolo);
    Serial.println("%");
    Serial.print("Estado do solo: ");
    Serial.println(estadoSolo);
    Serial.print("Modo: ");
    Serial.println(modoOperacao);
}

// Ciclo principal (2 segundos entre leituras)
// Ordem: entrada (botões) → sensores → controle → saída (display)
void loop() {
    botaoController();      // Verifica mudança de modo
    botaoBombaController(); // Verifica comando manual
    DHTemperatura();        // Lê temperatura
    DHTumidade();           // Lê umidade do ar
    lerUmidadeSolo();       // Lê e classifica solo
    bombaController();      // Executa lógica de controle (automático ou manual)
    displayOLED();          // Exibe estado no display
    Serial.println("--------------------------");
}