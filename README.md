# Remote Arena (Kotlin)

Projeto Android independente que demonstra uma interface estilo remote/mod menu, com:
- Arena de treino desenhada localmente;
- ESP visual para alvos fictícios;
- mira/FOV e seleção de alvo apenas dentro da arena;
- botões para ligar/desligar as simulações e reiniciar.

**Não se conecta ao Free Fire, não lê memória, não injeta código e não interage com partidas reais.**

## Requisitos
- Android Studio recente;
- JDK 17;
- Android SDK Platform 35 instalado no SDK Manager.

## Abrir e gerar APK
1. Extraia o ZIP.
2. No Android Studio, selecione **Open** e escolha a pasta `RemoteArenaKotlin`.
3. Aguarde o Gradle Sync. Se solicitado, instale o Android SDK Platform 35.
4. Para testar, conecte um Android com depuração USB ativada ou inicie um emulador e pressione **Run**.
5. Para gerar APK de depuração: **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
6. O arquivo normalmente ficará em `app/build/outputs/apk/debug/app-debug.apk`. Clique em **locate** na notificação do Android Studio para abrir a pasta.

## Linha de comando
Na pasta do projeto, se o Gradle estiver instalado:
```bash
gradle assembleDebug
```
Ou, depois de configurar um Gradle Wrapper válido:
```bash
./gradlew assembleDebug
```
No Windows:
```bat
gradlew.bat assembleDebug
```

## Observações
Este ZIP contém os arquivos de projeto, não um APK já compilado. A primeira sincronização pode precisar de internet para baixar Gradle e os plugins.
