# Anleitung: Wie erstelle ich einen Pull Request (PR)?

In dieser Anleitung erfährst du, wie du Ergebnisse in das Hauptrepository einbringst, indem du einen Pull Request (PR) erstellst.

---

## 1. Repository forken

1. Gehe zum Hauptrepository  [https://github.com/eclipse-tractusx/tutorial-resources](https://github.com/eclipse-tractusx/tutorial-resources).
2. Klicke oben rechts auf **Fork**.
3. Nach dem Fork befindet sich eine Kopie des Repositories in deinem eigenen GitHub-Account.

---

## 2. Ergebnisse in den eigenen Fork hochladen

1. **Klone dein geforktes Repository:**
   - Öffne ein Terminal oder deine Git-Umgebung.
   - Führe den folgenden Befehl aus (ersetze `<dein-github-benutzername>` mit deinem GitHub-Benutzernamen):
     ```bash
     git clone https://github.com/<dein-github-benutzername>/tutorial-resources.git
     cd tutorial-resources
     ```

2. **Füge deine Ergebnisse hinzu:**
   - Erstelle in der entsprechenden Struktur einen neuen Ordner für deine Ergebnisse, z. B.:
     ```bash
     mkdir results/<dein-name>
     ```
   - Lade deine Dateien (z. B. Screenshots und Metadaten) in diesen Ordner.
   - Beispiel:
     ```bash
     cp /pfad/zu/deinem/screenshot.png results/<dein-name>/ID001_screenshot.png
     echo "ID: ID001\nName: Max Mustermann\nBeschreibung: Mein Screenshot" > results/<dein-name>/metadata.txt
     ```

3. **Änderungen committen und pushen:**
   - Füge die Änderungen dem lokalen Git-Repository hinzu:
     ```bash
     git add .
     git commit -m "Meine Ergebnisse hinzugefügt (ID001)"
     ```
   - Lade die Änderungen in deinen Fork hoch:
     ```bash
     git push origin main
     ```

---

## 3. Pull Request zum Hauptrepository erstellen

1. Gehe zu deinem geforkten Repository auf GitHub.
2. Klicke auf den Button **Compare & pull request**, der oben erscheint, nachdem du deine Änderungen gepusht hast.
3. Gib in der PR-Beschreibung folgende Informationen an:
   - Eine kurze Beschreibung deiner Änderungen (z. B. „Ergebnisse von Max Mustermann hinzugefügt“).
   - Details zu deinen Dateien (z. B. „Screenshot mit ID001 und Metadaten hinzugefügt“).
4. Klicke auf **Create pull request**, um den PR zu erstellen.

---

## 4. Überprüfung und Feedback

- Dein PR wird vom Projektteam überprüft.
- Falls Änderungen erforderlich sind, hinterlässt das Team Feedback direkt im PR.
- Nimm Änderungen vor und pushe sie erneut in deinen Fork:
  ```bash
  git add .
  git commit -m "Feedback umgesetzt"
  git push origin main
 
